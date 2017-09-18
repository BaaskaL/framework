package mn.odoo.addons.scrapAccumulator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.odoo.App;
import com.odoo.R;
import com.odoo.addons.scrapAccumulator.Accumulator;
import com.odoo.addons.scrapAccumulator.ScrapAccumulator;
import com.odoo.addons.scrapAccumulator.ScrapAccumulatorPhotos;
import com.odoo.addons.technic.models.TechnicsModel;
import com.odoo.base.addons.ir.feature.OFileManager;
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
import mn.odoo.addons.scrapAccumulator.wizards.AccumulatorDetailsWizard;
import odoo.controls.ExpandableListControl;
import odoo.controls.OField;
import odoo.controls.OForm;

/**
 * Created by baaska on 5/30/17.
 */

public class ScrapAccumulatorDetails extends OdooCompatActivity implements OField.IOnFieldValueChangeListener, View.OnClickListener {

    public static final String TAG = ScrapAccumulatorDetails.class.getSimpleName();
    private Bundle extra;
    private OForm mForm;
    private OField oState, oOrigin, date, technicId, isPaybale;
    private ODataRow record = null;
    private Boolean mEditMode = false;
    private Menu mMenu;
    private TechnicsModel technic;
    private Accumulator accumulator;
    private ScrapAccumulatorPhotos scrapAccumulatorPhotos;
    private ScrapAccumulator scrapAccumulator;
    private ExpandableListControl mList;
    private ExpandableListControl.ExpandableListAdapter mAdapter;
    private List<ODataRow> scrapAccumulatorLines = new ArrayList<>();
    private List<ODataRow> technicAccumulatorLines = new ArrayList<>();
    private List<ODataRow> oilRow = new ArrayList<>();
    private Toolbar toolbar;
    private OFileManager fileManager;
    private LinearLayout layoutAddItem = null;
    private Context mContext;
    App app;
    /*Зүйлс оруулж ирэх*/
    private HashMap<String, Boolean> toWizardTechAccums = new HashMap<>();
    private List<Object> objects = new ArrayList<>();
    public static final int REQUEST_ADD_ITEMS = 323;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scrap_accumulator_detail);

        extra = getIntent().getExtras();
        app = (App) getApplicationContext();
        mContext = getApplicationContext();
        toolbar = (Toolbar) findViewById(R.id.toolbarScrapAccumulator);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEditMode = (!hasRecordInExtra() ? true : false);
        technic = new TechnicsModel(this, null);
        accumulator = new Accumulator(this, null);
        scrapAccumulatorPhotos = new ScrapAccumulatorPhotos(this, null);
        scrapAccumulator = new ScrapAccumulator(this, null);
        fileManager = new OFileManager(this);

        mList = (ExpandableListControl) findViewById(R.id.ExpandListAccumulatorLine);
        mList.setOnClickListener(this);
        mForm = (OForm) findViewById(R.id.OFormAccumulatorScrap);

        oState = (OField) mForm.findViewById(R.id.StateAccumulatorScrap);
        oOrigin = (OField) mForm.findViewById(R.id.OriginAccumulatorScrap);
        date = (OField) mForm.findViewById(R.id.DateAccumulatorScrap);
        technicId = (OField) mForm.findViewById(R.id.TechnicAccumulatorScrap);
        isPaybale = (OField) mForm.findViewById(R.id.IsPayableAccumulatorScrap);
        layoutAddItem = (LinearLayout) findViewById(R.id.layoutAddItemAccumulator);
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
            Log.i("tech====", technicId.toString());
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
            ((OField) mForm.findViewById(R.id.DateAccumulatorScrap)).setValue(ODateUtils.getDate());
        } else {
            setTitle("Аккумулятор акт дэлгэрэнгүй");
            int ScrapId = extra.getInt(OColumn.ROW_ID);
            record = scrapAccumulator.browse(ScrapId);
            mForm.initForm(record);
            mForm.setEditable(mEditMode);
            int technic_id = record.getInt("technic");
            getTechnicAccumulators(technic_id);
            scrapAccumulatorLines = record.getM2MRecord("accumulators").browseEach();
            drawAccumulator(scrapAccumulatorLines);
        }
        setMode(mEditMode);
    }

    private void getTechnicAccumulators(int techId) {
        ODataRow techRecord = technic.browse(techId);
        technicAccumulatorLines = techRecord.getO2MRecord("accumulators").browseEach();
        toWizardTechAccums.clear();
        for (ODataRow line : technicAccumulatorLines) {
            toWizardTechAccums.put(line.getString("_id"), false);
        }
        for (ODataRow line : scrapAccumulatorLines) {
            if (toWizardTechAccums.containsKey(line.getString("_id"))) {
                toWizardTechAccums.put(line.getString("_id"), true);
            }
        }
    }

    private void drawAccumulator(List<ODataRow> accumIds) {
        objects.clear();
        objects.addAll(accumIds);
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
                        OControls.setText(mView, R.id.reason, row.getString("reason_name"));
                        if (row.getString("state").equals("draft"))
                            OControls.setText(mView, R.id.state, "Ноорог");
                        else if (row.getString("state").equals("in_using"))
                            OControls.setText(mView, R.id.state, "Ашиглаж буй");
                        else if (row.getString("state").equals("in_reserve"))
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
                    List accumIds = new ArrayList();
                    for (ODataRow row : scrapAccumulatorLines) {
                        OValues oValues = new OValues();
                        accumIds.add(row.getInt("_id"));
                        oValues.put("in_scrap", true);
                        accumulator.update(row.getInt("_id"), oValues);
                    }
                    if (accumIds.isEmpty()) {
                        OAlert.showError(this, "Аккумулятор сонгон уу?");
                        break;
                    }
                    if (record != null) {
                        values.put("accumulators", new RelValues().replace(accumIds));
                        scrapAccumulator.update(record.getInt(OColumn.ROW_ID), values);
                        onOilScrapChangeUpdate.execute(domain);
                        mEditMode = !mEditMode;
                        mForm.setEditable(mEditMode);
                        setMode(mEditMode);
                        Toast.makeText(this, R.string.tech_toast_information_saved, Toast.LENGTH_LONG).show();
                    } else {
                        values.put("accumulators", new RelValues().append(accumIds));
                        values.put("technic_name", technic.browse(values.getInt("technic")).getString("name"));
                        int row_id = scrapAccumulator.insert(values);
                        if (row_id != scrapAccumulator.INVALID_ROW_ID) {
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
                                    if (scrapAccumulator.delete(record.getInt(OColumn.ROW_ID))) {
                                        onOilScrapChangeUpdate.execute(domain);
                                        Toast.makeText(ScrapAccumulatorDetails.this, R.string.tech_toast_information_deleted,
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
            case R.id.layoutAddItemAccumulator:
                int techId = (Integer) technicId.getValue();
                if (techId > 0) {
                    getTechnicAccumulators(techId);
                    Intent intent = new Intent(this, AddItemLineWizard.class);
                    Bundle extra = new Bundle();
                    for (String key : toWizardTechAccums.keySet()) {
                        extra.putBoolean(key, toWizardTechAccums.get(key));
                    }
                    AddItemLineWizard.mModel = accumulator;
                    intent.putExtras(extra);
                    startActivityForResult(intent, REQUEST_ADD_ITEMS);
                }
                break;
        }
    }

    private void loadActivity(ODataRow row) {
        if (record != null) {
            Intent intent = new Intent(this, AccumulatorDetailsWizard.class);
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
                List<ODataRow> rows = scrapAccumulator.select(null, "id = ?", new String[]{"0"});
                List<ODataRow> photoRows = scrapAccumulatorPhotos.select(null, "id = ?", new String[]{"0"});
                for (ODataRow row : rows) {
                    scrapAccumulator.quickCreateRecord(row);
                }
                for (ODataRow row : photoRows) {
                    scrapAccumulatorPhotos.quickCreateRecord(row);
                }
                /*Бусад бичлэгүүдийг update хийж байна*/
                scrapAccumulator.quickSyncRecords(domain);
                scrapAccumulatorPhotos.quickSyncRecords(domain);
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
        Log.i("field===", field.getFieldName().toString());
        if (record == null && field.getFieldName().equals("technic")) {
            ODataRow techVal = (ODataRow) value;
            Log.i("techVal.getString==", techVal.getString("id").toString());
            technicSync(techVal.getString("id"));
            scrapAccumulatorLines.clear();
            drawAccumulator(scrapAccumulatorLines);
            getTechnicAccumulators((Integer) technicId.getValue());
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
                Log.i("techVal.getString==", "work====");
                sync.execute(domain);
            } else {
                Toast.makeText(this, R.string.toast_network_required, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class OnTechnicSync extends AsyncTask<ODomain, Void, Void> {
        App app = (App) getApplicationContext();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.accumulatorScrapProgress).setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(ODomain... params) {
            try {
                if (app.inNetwork()) {
                    Thread.sleep(500);
                    technic.quickSyncRecords(params[0]);
                }
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            findViewById(R.id.accumulatorScrapProgress).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADD_ITEMS && resultCode == Activity.RESULT_OK) {
            scrapAccumulatorLines.clear();
            for (String key : data.getExtras().keySet()) {
                if (data.getExtras().getBoolean(key)) {
                    scrapAccumulatorLines.add(accumulator.select(null, "_id = ?", new String[]{key}).get(0));
                }
            }
            drawAccumulator(scrapAccumulatorLines);
        }
    }
}