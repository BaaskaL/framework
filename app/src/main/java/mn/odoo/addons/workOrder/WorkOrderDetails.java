package mn.odoo.addons.workOrder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.odoo.App;
import com.odoo.R;
import com.odoo.addons.employees.models.Employee;
import com.odoo.addons.scrapOil.models.ShTMScrapPhotos;
import com.odoo.addons.scrapOil.models.TechnicOil;
import com.odoo.addons.technic.models.TechnicsModel;
import com.odoo.addons.workOrder.Models.WorkOrder;
import com.odoo.base.addons.ir.feature.OFileManager;
import com.odoo.base.addons.res.ResUsers;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.support.OdooCompatActivity;
import com.odoo.core.utils.BitmapUtils;
import com.odoo.core.utils.OAlert;
import com.odoo.core.utils.OControls;
import com.odoo.core.utils.ODateUtils;
import com.odoo.core.utils.OResource;
import com.odoo.core.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mn.odoo.addons.scrapOil.ScrapOilDetails;
import mn.odoo.addons.workOrder.wizards.repairTeam.AddEmployeeWizard;
import odoo.controls.ExpandableListControl;
import odoo.controls.OField;
import odoo.controls.OForm;

/**
 * Created by baaska on 2017-08-29.
 */

public class WorkOrderDetails extends OdooCompatActivity implements OField.IOnFieldValueChangeListener, View.OnClickListener {

    public static final String TAG = ScrapOilDetails.class.getSimpleName();
    private Bundle extra;
    private OForm mForm;
    private OField name, oState, oOrigin, date, technicId, norm, priority;
    private ODataRow record = null;
    private Boolean mEditMode = false;
    private Menu mMenu;
    private TechnicsModel technic;
    private Employee employee;
    private ShTMScrapPhotos shTMScrapPhotos;
    private WorkOrder workOrder;
    private ExpandableListControl mList;
    private ExpandableListControl.ExpandableListAdapter mAdapter;
    private List<Object> oilObjects = new ArrayList<>();
    private List<ODataRow> repairTeam = new ArrayList<>();

    private List<ODataRow> oilRow = new ArrayList<>();
    private Toolbar toolbar;
    private OFileManager fileManager;
    private HashMap<Integer, String> oilImages = new HashMap<>();
    private LinearLayout layoutAddEmployee = null;
    private int woId;

    private HashMap<String, Boolean> toWizardEmployees = new HashMap<>();
    private HashMap<String, Integer> lineIds = new HashMap<>();
    public static final int REQUEST_ADD_ITEMS = 323;
    private List<Object> objects = new ArrayList<>();
    App app;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.work_order_detail);

        extra = getIntent().getExtras();
        toolbar = (Toolbar) findViewById(R.id.toolbarWorkOrder);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        app = (App) getApplicationContext();

        mEditMode = (!hasRecordInExtra() ? true : false);
        technic = new TechnicsModel(this, null);
        employee = new Employee(this, null);
        shTMScrapPhotos = new ShTMScrapPhotos(this, null);
        workOrder = new WorkOrder(this, null);
        fileManager = new OFileManager(this);

        mList = (ExpandableListControl) findViewById(R.id.ExpandListRepairTeam);
        mList.setOnClickListener(this);
        mForm = (OForm) findViewById(R.id.OFormWOScrap);

        name = (OField) mForm.findViewById(R.id.NameWO);
        oState = (OField) mForm.findViewById(R.id.StageWO);
        oOrigin = (OField) mForm.findViewById(R.id.OriginWO);
        date = (OField) mForm.findViewById(R.id.CreateDateWO);
        technicId = (OField) mForm.findViewById(R.id.TechnicWO);
        norm = (OField) mForm.findViewById(R.id.NormWO);
        priority = (OField) mForm.findViewById(R.id.PriorityWO);
        layoutAddEmployee = (LinearLayout) findViewById(R.id.layoutAddEmployee);

        setupToolbar();
    }

    private void ToolbarMenuSetVisibl(Boolean Visibility) {
        if (mMenu != null) {
            mMenu.findItem(R.id.menu_more).setVisible(!Visibility);
            mMenu.findItem(R.id.menu_edit).setVisible(!Visibility);
            mMenu.findItem(R.id.menu_save).setVisible(Visibility);
            mMenu.findItem(R.id.menu_cancel).setVisible(Visibility);
        }
    }

    private void setMode(Boolean edit) {
        ToolbarMenuSetVisibl(edit);
        oOrigin.setEditable(false);
        oState.setEditable(false);
        if (edit && record == null) {
            technicId.setOnValueChangeListener(this);
            layoutAddEmployee.setOnClickListener(this);
        }
        if (edit && record != null) {
            layoutAddEmployee.setOnClickListener(this);
            name.setEditable(false);
            date.setEditable(false);
            technicId.setEditable(false);
            norm.setEditable(false);
            priority.setEditable(false);
        }
    }

    private void setupToolbar() {
        Log.i("mEditMode====", mEditMode.toString());
        if (!hasRecordInExtra()) {
            setTitle("Үүсгэх");
            mForm.setEditable(mEditMode);
            mForm.initForm(null);
            ResUsers user = new ResUsers(this, null);
            ((OField) mForm.findViewById(R.id.CreateDateWO)).setValue(ODateUtils.getDate());
            ((OField) mForm.findViewById(R.id.PlannedDateWO)).setValue(ODateUtils.getDate());
//            ((OField) mForm.findViewById(R.id.PriorityWO)).setValue(ODateUtils.getDate());
            ((OField) mForm.findViewById(R.id.AssignedWO)).setValue(user.myId(this));
            ((OField) mForm.findViewById(R.id.StageWO)).setValue(1);
        } else {
            setTitle("Засварын ажилбар дэлгэрэнгүй");
            woId = extra.getInt(OColumn.ROW_ID);
            record = workOrder.browse(woId);
            mForm.initForm(record);
            mForm.setEditable(mEditMode);
            int technic_id = record.getInt("technic_id");
            getEmployee(woId);
            repairTeam = record.getM2MRecord("repair_team").browseEach();
            drawTeamEmployees(repairTeam);
        }
        setMode(mEditMode);
    }

    private boolean hasRecordInExtra() {
        return extra != null && extra.containsKey(OColumn.ROW_ID);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutAddEmployee:
                if (woId > 0) {
                    getEmployee(woId);
                }
                Intent intent = new Intent(this, AddEmployeeWizard.class);
                Bundle extra = new Bundle();
                for (String key : toWizardEmployees.keySet()) {
                    extra.putBoolean(key, toWizardEmployees.get(key));
                }
                AddEmployeeWizard.mModel = employee;
                intent.putExtras(extra);
                startActivityForResult(intent, REQUEST_ADD_ITEMS);
                break;
        }
    }

    @Override
    public void onFieldValueChange(OField field, Object value) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        mMenu = menu;
        ToolbarMenuSetVisibl(mEditMode);
        return true;
    }

    private void getEmployee(int techId) {
        List<ODataRow> Employees = new ArrayList<>();
        List<String> job_name = new ArrayList<>();
        job_name.add("Засварчин");
        job_name.add("Туслах ажилтан");
        job_name.add("Техникч");
        job_name.add("Засвар үйлчилгээний ажилтан");
        job_name.add("жолооч");
        job_name.add("Инженер, өрөм");
        job_name.add("Өрмийн мастер");
        job_name.add("Моторчин");
        Employees = employee.select(null, "job_name IN (" + StringUtils.repeat(" ?, ", job_name.size() - 1) + " ?)", job_name.toArray(new String[job_name.size()]));
        Log.i("Employees====", Employees.toString());
        for (ODataRow line : Employees) {
            toWizardEmployees.put(line.getString("_id"), false);
        }
        for (ODataRow line : repairTeam) {
            if (toWizardEmployees.containsKey(line.getString("_id"))) {
                toWizardEmployees.put(line.getString("_id"), true);
            }
        }
    }

    private void drawTeamEmployees(List<ODataRow> oils) {
        objects.clear();
        objects.addAll(oils);
        mAdapter = mList.getAdapter(R.layout.work_order_employee_item, objects,
                new ExpandableListControl.ExpandableListAdapterGetViewListener() {
                    @Override
                    public View getView(final int position, View mView, ViewGroup parent) {
                        ODataRow row = (ODataRow) mAdapter.getItem(position);
                        OControls.setText(mView, R.id.last_name, (position + 1) + ". " + row.getString("last_name"));
                        OControls.setText(mView, R.id.name, row.getString("name"));
                        OControls.setText(mView, R.id.job_name, row.getString("job_name"));
                        return mView;
                    }
                });
        mAdapter.notifyDataSetChanged(objects);
    }

    private void loadActivity(ODataRow row) {
        Intent intent = new Intent(this, AddEmployeeWizard.class);
        Bundle extra = new Bundle();
        if (row != null) {
            extra = row.getPrimaryBundleData();
        }
        intent.putExtras(extra);
        startActivityForResult(intent, REQUEST_ADD_ITEMS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADD_ITEMS && resultCode == Activity.RESULT_OK) {
            repairTeam.clear();
            for (String key : data.getExtras().keySet()) {
                if (data.getExtras().getBoolean(key)) {
                    repairTeam.add(employee.select(null, "_id = ?", new String[]{key}).get(0));
                }
            }
            drawTeamEmployees(repairTeam);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        OnWorkOrderChangeUpdate onWorkOrderChangeUpdate = new OnWorkOrderChangeUpdate();
        ODomain domain = new ODomain();
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_save:
                OValues values = mForm.getValues();
                if (values != null) {
                    List<Integer> employIds = new ArrayList<>();
                    if (record != null) {
                        for (ODataRow row : repairTeam) {
                            int employId = row.getInt("_id");
                            employIds.add(employId);
//                            technicOil.update(oilId, oilImage);
                        }
//                        values.put("repair_team", oilId);
                        workOrder.update(record.getInt(OColumn.ROW_ID), values);
                        onWorkOrderChangeUpdate.execute(domain);
                        mEditMode = !mEditMode;
                        mForm.setEditable(mEditMode);
                        setMode(mEditMode);
                        Toast.makeText(this, R.string.tech_toast_information_saved, Toast.LENGTH_LONG).show();
                    } else {
//                        HashMap<RelCommands, List<Object>> aa=new HashMap<>();
                        for (ODataRow row : repairTeam) {
                            int employId = row.getInt("_id");
                            employIds.add(employId);
//                            m2mtable inset hiine.insert(employIds);
                        }
                        float hour = 0;
                        try {
                            hour = Float.parseFloat(values.getString("planned_hours"));
                        } catch (NumberFormatException EX) {
                            Toast.makeText(WorkOrderDetails.this, "Төлөвлөсөн цагт тоо оруулана уу!!!", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        if (hour > 0) {
                            values.put("stage_name", "Ноорог");
                            int row_id = workOrder.insert(values);
                            if (row_id != workOrder.INVALID_ROW_ID) {
                                onWorkOrderChangeUpdate.execute(domain);
                                Toast.makeText(this, R.string.tech_toast_information_created, Toast.LENGTH_LONG).show();
                                mEditMode = !mEditMode;
                                finish();
                            }
                        } else {
                            Toast.makeText(WorkOrderDetails.this, "Төлөвлөсөн цаг 0-с их байх ёстой!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                break;
            case R.id.menu_cancel:
                OAlert.showConfirm(this, OResource.string(this, R.string.close_activity),
                        new OAlert.OnAlertConfirmListener() {
                            @Override
                            public void onConfirmChoiceSelect(OAlert.ConfirmType type) {
                                if (type == OAlert.ConfirmType.POSITIVE) {
                                    mEditMode = !mEditMode;
                                    setupToolbar();
                                } else {
                                    mForm.setEditable(true);
                                    setMode(mEditMode);
                                }
                            }
                        });
                break;
            case R.id.menu_edit:
                if (hasRecordInExtra()) {
                    mEditMode = !mEditMode;
                    mForm.setEditable(mEditMode);
                    setMode(mEditMode);
                    /*Oil line buttons show*/
//                    getOil();
                }
                break;
            case R.id.menu_delete:
                OAlert.showConfirm(this, OResource.string(this,
                        R.string.to_delete),
                        new OAlert.OnAlertConfirmListener() {
                            @Override
                            public void onConfirmChoiceSelect(OAlert.ConfirmType type) {
                                if (type == OAlert.ConfirmType.POSITIVE) {
                                    if (workOrder.delete(record.getInt(OColumn.ROW_ID))) {
                                        Toast.makeText(WorkOrderDetails.this, R.string.tech_toast_information_deleted,
                                                Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            }
                        });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class OnWorkOrderChangeUpdate extends AsyncTask<ODomain, Void, Void> {

        @Override
        protected Void doInBackground(ODomain... params) {
            if (app.inNetwork()) {
                ODomain domain = params[0];
                List<ODataRow> rows = workOrder.select(null, "id = ?", new String[]{"0"});
                for (ODataRow row : rows) {
                    workOrder.quickCreateRecord(row);
                }
                workOrder.quickSyncRecords(domain);
            }
            return null;
        }
    }
}
