package mn.odoo.addons.scrapOil;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.odoo.App;
import com.odoo.R;
import com.odoo.addons.scrapOil.models.ScrapOils;
import com.odoo.addons.scrapOil.models.ShTMScrapPhotos;
import com.odoo.addons.scrapOil.models.TechnicOil;
import com.odoo.addons.technic.models.TechnicsModel;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.RelValues;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.support.OdooCompatActivity;
import com.odoo.core.utils.OAlert;
import com.odoo.core.utils.OControls;
import com.odoo.core.utils.ODateUtils;
import com.odoo.core.utils.OResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mn.odoo.addons.otherClass.AddItemLineWizard;
import mn.odoo.addons.scrapOil.wizards.OilDetailsWizard;
import odoo.controls.ExpandableListControl;
import odoo.controls.OField;
import odoo.controls.OForm;

/**
 * Created by baaska on 5/30/17.
 */

public class ScrapOilDetails extends OdooCompatActivity implements OField.IOnFieldValueChangeListener, View.OnClickListener {

    public static final String TAG = ScrapOilDetails.class.getSimpleName();
    private Bundle extra;
    private OForm mForm;
    private OField oState, oOrigin, date, technicId, isPaybale;
    private ODataRow record = null;
    private Boolean mEditMode = false;
    private Menu mMenu;
    private TechnicsModel technic;
    private TechnicOil technicOil;
    private ShTMScrapPhotos shTMScrapPhotos;
    private ScrapOils scrapOil;
    private ExpandableListControl mList;
    private ExpandableListControl.ExpandableListAdapter mAdapter;
    private List<ODataRow> scrapOilLines = new ArrayList<>();
    private List<ODataRow> technicOilLines = new ArrayList<>();
    private List<ODataRow> oilRow = new ArrayList<>();
    private Toolbar toolbar;
    private LinearLayout layoutAddItem = null;
    private Context mContext;
    App app;
    /*Зүйлс оруулж ирэх*/
    private HashMap<String, Boolean> toWizardTechOils = new HashMap<>();
    private List<Object> objects = new ArrayList<>();
    public static final int REQUEST_ADD_ITEMS = 323;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scrap_oil_detail);

        extra = getIntent().getExtras();
        app = (App) getApplicationContext();
        mContext = getApplicationContext();
        toolbar = (Toolbar) findViewById(R.id.toolbarScrapOil);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEditMode = (!hasRecordInExtra() ? true : false);
        technic = new TechnicsModel(this, null);
        technicOil = new TechnicOil(this, null);
        shTMScrapPhotos = new ShTMScrapPhotos(this, null);
        scrapOil = new ScrapOils(this, null);

        mList = (ExpandableListControl) findViewById(R.id.ExpandListOilLine);
        mList.setOnClickListener(this);
        mForm = (OForm) findViewById(R.id.OFormOilScrap);

        oState = (OField) mForm.findViewById(R.id.StateOilScrap);
        oOrigin = (OField) mForm.findViewById(R.id.OriginOilScrap);
        date = (OField) mForm.findViewById(R.id.DateOilScrap);
        technicId = (OField) mForm.findViewById(R.id.TechnicOilScrap);
        isPaybale = (OField) mForm.findViewById(R.id.IsPayableOilScrap);
        layoutAddItem = (LinearLayout) findViewById(R.id.layoutAddItem);
        layoutAddItem.setOnClickListener(this);

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
        }
        if (record != null) {
            date.setEditable(false);
            technicId.setEditable(false);
            isPaybale.setEditable(false);
        }
    }

    private void setupToolbar() {
        if (!hasRecordInExtra()) {
            setTitle("Үүсгэх");
            mForm.setEditable(mEditMode);
            mForm.initForm(null);
            ((OField) mForm.findViewById(R.id.DateOilScrap)).setValue(ODateUtils.getDate());
        } else {
            setTitle("ШТМ акт дэлгэрэнгүй");
            int ScrapId = extra.getInt(OColumn.ROW_ID);
            record = scrapOil.browse(ScrapId);
            mForm.initForm(record);
            mForm.setEditable(mEditMode);
            scrapOilLines = record.getM2MRecord("oil_ids").browseEach();
            drawOil(scrapOilLines);
        }
        setMode(mEditMode);
    }


    private void drawOil(List<ODataRow> oils) {
        objects.clear();
        objects.addAll(oils);
        mAdapter = mList.getAdapter(R.layout.scrap_accumulator_accum_item, objects,
                new ExpandableListControl.ExpandableListAdapterGetViewListener() {
                    @Override
                    public View getView(final int position, View mView, ViewGroup parent) {
                        ODataRow row = (ODataRow) mAdapter.getItem(position);
                        OControls.setText(mView, R.id.name, (position + 1) + ". " + row.getString("name"));
                        OControls.setText(mView, R.id.date, row.getString("date"));
                        if (row.getString("date").equals("false"))
                            OControls.setText(mView, R.id.date, "");
                        OControls.setText(mView, R.id.product, row.getString("product_name"));
                        OControls.setText(mView, R.id.capacity, row.getString("capacity"));
                        OControls.setText(mView, R.id.usage_percent, row.getString("usage_percent"));

                        if (row.getString("state").equals("draft"))
                            OControls.setText(mView, R.id.state, "Ноорог");
                        else if (row.getString("state").equals("using"))
                            OControls.setText(mView, R.id.state, "Хэрэглэж буй");
                        else if (row.getString("state").equals("inactive"))
                            OControls.setText(mView, R.id.state, "Нөөцөнд");
                        else OControls.setText(mView, R.id.state, "Акталсан");

                        mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ODataRow row = (ODataRow) mAdapter.getItem(position);
                                loadActivity(row);
                            }
                        });
                        return mView;
                    }
                });
        mAdapter.notifyDataSetChanged(objects);
    }

    private boolean hasRecordInExtra() {
        return extra != null && extra.containsKey(OColumn.ROW_ID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        mMenu = menu;
        ToolbarMenuSetVisibl(mEditMode);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final OnOilScrapChangeUpdate onOilScrapChangeUpdate = new OnOilScrapChangeUpdate();
        final ODomain domain = new ODomain();
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_save:
                OValues values = mForm.getValues();
                if (values != null) {
                    List oilIds = new ArrayList();
                    for (ODataRow row : scrapOilLines) {
                        OValues oValues = new OValues();
                        oilIds.add(row.getInt("_id"));
                        oValues.put("in_scrap", true);
                        technicOil.update(row.getInt("_id"), oValues);
                    }
                    if (oilIds.isEmpty()) {
                        OAlert.showError(this, "ШТМ сонгон уу?");
                        break;
                    }

                    if (record != null) {
                        values.put("oil_ids", new RelValues().replace(oilIds));
                        scrapOil.update(record.getInt(OColumn.ROW_ID), values);
                        onOilScrapChangeUpdate.execute(domain);
                        mEditMode = !mEditMode;
                        mForm.setEditable(mEditMode);
                        setMode(mEditMode);
                        Toast.makeText(this, R.string.tech_toast_information_saved, Toast.LENGTH_LONG).show();
                    } else {
                        values.put("oil_ids", new RelValues().append(oilIds));
                        values.put("technic_name", technic.browse(values.getInt("technic")).getString("name"));
                        int row_id = scrapOil.insert(values);
                        if (row_id != scrapOil.INVALID_ROW_ID) {
                            onOilScrapChangeUpdate.execute(domain);
                            Toast.makeText(this, R.string.tech_toast_information_created, Toast.LENGTH_LONG).show();
                            mEditMode = !mEditMode;
                            finish();
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
                }
                break;
            case R.id.menu_delete:
                OAlert.showConfirm(this, OResource.string(this,
                        R.string.to_delete),
                        new OAlert.OnAlertConfirmListener() {
                            @Override
                            public void onConfirmChoiceSelect(OAlert.ConfirmType type) {
                                if (type == OAlert.ConfirmType.POSITIVE) {
                                    if (scrapOil.delete(record.getInt(OColumn.ROW_ID))) {
                                        onOilScrapChangeUpdate.execute(domain);
                                        Toast.makeText(ScrapOilDetails.this, R.string.tech_toast_information_deleted,
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutAddItem:
                int techId = (Integer) technicId.getValue();
                if (techId > 0) {
                    getTechnicOils(techId);
                    Intent intent = new Intent(this, AddItemLineWizard.class);
                    Bundle extra = new Bundle();
                    for (String key : toWizardTechOils.keySet()) {
                        extra.putBoolean(key, toWizardTechOils.get(key));
                    }
                    AddItemLineWizard.mModel = technicOil;
                    intent.putExtras(extra);
                    startActivityForResult(intent, REQUEST_ADD_ITEMS);
                }
                break;
        }
    }

    private void getTechnicOils(int techId) {
        ODataRow techRecord = technic.browse(techId);
        technicOilLines = techRecord.getO2MRecord("oils").browseEach();
        toWizardTechOils.clear();
        for (ODataRow line : technicOilLines) {
            toWizardTechOils.put(line.getString("_id"), false);
        }
        for (ODataRow line : scrapOilLines) {
            if (toWizardTechOils.containsKey(line.getString("_id"))) {
                toWizardTechOils.put(line.getString("_id"), true);
            }
        }
    }

    private void loadActivity(ODataRow row) {
        if (record != null) {
            Intent intent = new Intent(this, OilDetailsWizard.class);
            Bundle extra = new Bundle();
            if (row != null) {
                extra = row.getPrimaryBundleData();
                extra.putString("scrap_id", record.getString("_id"));
                extra.putString("scrap_name", record.getString("origin"));
            }
            intent.putExtras(extra);
            startActivityForResult(intent, REQUEST_ADD_ITEMS);
        } else {
            OAlert.showAlert(this, OResource.string(this, R.string.required_save));
        }
    }

    private class OnOilScrapChangeUpdate extends AsyncTask<ODomain, Void, Void> {

        @Override
        protected Void doInBackground(ODomain... params) {
            if (app.inNetwork()) {
                ODomain domain = params[0];
                List<ODataRow> rows = scrapOil.select(null, "id = ?", new String[]{"0"});
                List<ODataRow> photoRows = shTMScrapPhotos.select(null, "id = ?", new String[]{"0"});
                for (ODataRow row : rows) {
                    scrapOil.quickCreateRecord(row);
                }
                for (ODataRow row : photoRows) {
                    shTMScrapPhotos.quickCreateRecord(row);
                }
                /*Бусад бичлэгүүдийг update хийж байна*/
                scrapOil.quickSyncRecords(domain);
                shTMScrapPhotos.quickSyncRecords(domain);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!app.inNetwork())
                Toast.makeText(mContext, OResource.string(mContext, R.string.toast_network_required), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFieldValueChange(OField field, Object value) {
        if (record == null && field.getFieldName().equals("technic_id")) {
            ODataRow techVal = (ODataRow) value;
            technicSync(techVal.getString("id"));
        }
    }

    @Override
    public void finish() {
        if (mEditMode) {
            OAlert.showConfirm(this, OResource.string(this, R.string.close_activity),
                    new OAlert.OnAlertConfirmListener() {
                        @Override
                        public void onConfirmChoiceSelect(OAlert.ConfirmType type) {
                            if (type == OAlert.ConfirmType.POSITIVE) {
                                mEditMode = !mEditMode;
                                finish();
                            }
                        }
                    });
        } else {
            super.finish();
        }
    }

    public void technicSync(String serverTechId) {
        try {
            if (app.inNetwork()) {
                ODomain domain = new ODomain();
                domain.add("id", "=", serverTechId);
                OnTechnicSync sync = new OnTechnicSync();
                sync.execute(domain);
            } else {
                Toast.makeText(this, R.string.toast_network_required, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class OnTechnicSync extends AsyncTask<ODomain, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.oilScrapProgress).setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(ODomain... params) {
            try {
                technic.quickSyncRecords(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            findViewById(R.id.oilScrapProgress).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADD_ITEMS && resultCode == Activity.RESULT_OK) {
            scrapOilLines.clear();
            for (String key : data.getExtras().keySet()) {
                if (data.getExtras().getBoolean(key)) {
                    scrapOilLines.add(technicOil.select(null, "_id = ?", new String[]{key}).get(0));
                }
            }
            drawOil(scrapOilLines);
        }
    }
}