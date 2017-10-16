package mn.odoo.addons.TechnicInspection;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.odoo.App;
import com.odoo.R;
import com.odoo.addons.TechnicInsoection.Models.ProductUom;
import com.odoo.addons.TechnicInsoection.Models.TechnicInspectionAccumulators;
import com.odoo.addons.TechnicInsoection.Models.TechnicInspectionCategory;
import com.odoo.addons.TechnicInsoection.Models.TechnicInspectionCheckList;
import com.odoo.addons.TechnicInsoection.Models.TechnicInspectionNorm;
import com.odoo.addons.TechnicInsoection.Models.TechnicInspectionPack;
import com.odoo.addons.TechnicInsoection.Models.TechnicInspectionPhoto;
import com.odoo.addons.TechnicInsoection.Models.TechnicInspectionTires;
import com.odoo.addons.TechnicInsoection.Models.TechnicInspectionType;
import com.odoo.addons.TechnicInsoection.Models.TechnicInspectionUsage;
import com.odoo.addons.TechnicInsoection.Models.TechnicsInspectionModel;
import com.odoo.addons.TechnicInsoection.Models.UsageUom;
import com.odoo.addons.employees.models.Employee;
import com.odoo.addons.technic.models.TechnicNorm;
import com.odoo.addons.technic.models.TechnicsModel;
import com.odoo.base.addons.ir.feature.OFileManager;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.RelValues;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.support.OdooCompatActivity;
import com.odoo.core.utils.OAlert;
import com.odoo.core.utils.OAppBarUtils;
import com.odoo.core.utils.ODateUtils;
import com.odoo.core.utils.OResource;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import mn.odoo.addons.otherClass.ImageFragmentAdapter;
import mn.odoo.addons.otherClass.InkPageIndicator;
import odoo.controls.OField;
import odoo.controls.OForm;

/**
 * Created by baaska on 5/30/17.
 */

public class TechnicsInspectionDetails extends OdooCompatActivity implements OField.IOnFieldValueChangeListener,
        View.OnClickListener, ViewPagerEx.OnPageChangeListener, BaseSliderView.OnSliderClickListener {

    public static final String TAG = TechnicsInspectionDetails.class.getSimpleName();

    public static List<ODataRow> inspectionItemLines = new ArrayList<>();
    public static List<ODataRow> linesUom = new ArrayList<>();
    public static List<ODataRow> tireLines = new ArrayList<>();
    public static List<ODataRow> accumulatorLines = new ArrayList<>();
    public List<ODataRow> recInsImages = new ArrayList<>();

    private HashMap<Integer, String> inspectionImages = new HashMap<>();

    private Bundle extra;
    private OForm mForm;
    private final String KEY_MODE = "key_edit_mode";

    private TechnicsInspectionModel technicIns;
    private TechnicInspectionType techInsType;
    private TechnicsModel technic;
    private Employee employee;

    private TechnicInspectionPhoto inspectionPhoto;
    private TechnicInspectionCheckList inspectionLines;
    private TechnicInspectionTires inspectionTires;
    private TechnicInspectionAccumulators inspectionAccumulators;
    private TechnicInspectionUsage inspectionUsage;
    private TechnicInspectionNorm inspectionNorm;
    private TechnicInspectionPack isectionPack;
    private TechnicInspectionCategory inspectionCategory;
    private UsageUom usageUom;
    private ProductUom productUom;

    private ODataRow record = null;
    public static Boolean mEditMode = false;
    private Menu mMenu;

    public int myId;
    private Uri mCapturedImageURI;
    private Toolbar toolbar;
    public static OFileManager fileManager;
    private String newImage = null;
    private HashMap<String, File> file_maps = new HashMap<>();
    private int inspectionItemId;
    private OField oState, oOrigin, date, technicId, typeField, respondent_worker, registrar_worker, inspection_commis;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PagerAdapter adapter;
    private int InspectionId;
    private List<TextSliderView> textSlider = new ArrayList<>();
    private List<String> indicator = new ArrayList<>();
    public static final int REQUEST_ADD_ITEMS = 323;
    private Boolean saveData = false;
    App app;
    /*picture*/
    private ViewPager mPager;
    private InkPageIndicator mIndicator;
    private ImageFragmentAdapter mAdapter;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    public List<ODataRow> recTechInspectionImages = new ArrayList<>();
    private int inspectionId = 0;
    private static String imgName = "";
    private List<ODataRow> technic_inspection = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.technic_inspection_detail);

        extra = getIntent().getExtras();
        inspectionId = extra.getInt(OColumn.ROW_ID);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.technic_collapsing_toolbar);
        mPager = (ViewPager) findViewById(R.id.pagerInspection);
        toolbar = (Toolbar) findViewById(R.id.toolbarTechnic);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEditMode = (!hasRecordInExtra() ? true : false);
        fileManager = new OFileManager(this);
        OAppBarUtils.setAppBar(this, true);
        if (savedInstanceState != null) {
            mEditMode = savedInstanceState.getBoolean(KEY_MODE);
        }

        technicIns = new TechnicsInspectionModel(this, null);
        techInsType = new TechnicInspectionType(this, null);
        employee = new Employee(this, null);
        inspectionLines = new TechnicInspectionCheckList(this, null);
        inspectionPhoto = new TechnicInspectionPhoto(this, null);
        inspectionTires = new TechnicInspectionTires(this, null);
        inspectionAccumulators = new TechnicInspectionAccumulators(this, null);
        inspectionUsage = new TechnicInspectionUsage(this, null);
        inspectionNorm = new TechnicInspectionNorm(this, null);
        isectionPack = new TechnicInspectionPack(this, null);
        inspectionCategory = new TechnicInspectionCategory(this, null);

        technic = new TechnicsModel(this, null);
        usageUom = new UsageUom(this, null);
        productUom = new ProductUom(this, null);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.pageViewer);

        tabLayout.addTab(tabLayout.newTab().setText("Техникийн үзлэгийн зүйлc"));
        tabLayout.addTab(tabLayout.newTab().setText("Дугуйн үзлэг"));
        tabLayout.addTab(tabLayout.newTab().setText("Аккумулятор үзлэг"));
        tabLayout.addTab(tabLayout.newTab().setText("Ашиглалт"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        //This is test
        app = (App) getApplicationContext();
        mForm = (OForm) findViewById(R.id.technic_inspection_form);
        technicId = (OField) mForm.findViewById(R.id.inspection_technic_id);
        respondent_worker = (OField) mForm.findViewById(R.id.inspection_respondent_id);
        registrar_worker = (OField) mForm.findViewById(R.id.inspection_registrar_id);
        inspection_commis = (OField) mForm.findViewById(R.id.inspection_commis);
        oOrigin = (OField) mForm.findViewById(R.id.originTechIns);
        oState = (OField) mForm.findViewById(R.id.stateTechIns);
        date = (OField) mForm.findViewById(R.id.inspection_date);
        typeField = (OField) findViewById(R.id.inspection_type_id);
        mAdapter = new ImageFragmentAdapter(getSupportFragmentManager(), recInsImages);
        mPager.setAdapter(null);
        mPager.setAdapter(mAdapter);
        mIndicator = (InkPageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);

        setupToolbar();
    }

    private void setupToolbar() {
        OnInspectionImageSync onInspectionImageSync = new OnInspectionImageSync();
        if (!hasRecordInExtra()) {
            setTitle("Үүсгэх");
            myId = technicIns.myId();
            mForm.initForm(null);
            onInspectionImageSync.execute(recInsImages);
            ((OField) mForm.findViewById(R.id.inspection_registrar_id)).setValue(myId);
            ((OField) mForm.findViewById(R.id.inspection_date)).setValue(ODateUtils.getDate());
        } else {
            int rowId = extra.getInt(OColumn.ROW_ID);
            setTitle("Техникийн үзлэг дэлгэрэнгүй");
            record = technicIns.browse(rowId);
            recInsImages = record.getO2MRecord("ins_photo").browseEach();
            onInspectionImageSync.execute(recInsImages);
            mForm.initForm(record);
            technicIns.setTechnicNorm(record.getInt("inspection_technic_id"));
            inspectionItemLines = record.getO2MRecord("technic_inspection_check_list_ids").browseEach();
            linesUom = record.getO2MRecord("inspection_usage_ids").browseEach();
            tireLines = record.getO2MRecord("tire_ids").browseEach();
            accumulatorLines = record.getO2MRecord("accumulator_ids").browseEach();
            Log.i("accumulatorLines==111==", accumulatorLines.toString());
        }
        mForm.setEditable(mEditMode);
        setMode(mEditMode);
    }

    private void ToolbarMenuSetVisible(Boolean Visibility) {
        if (mMenu != null) {
            mMenu.findItem(R.id.menu_technic_detail_more).setVisible(!Visibility);
            mMenu.findItem(R.id.menu_technic_edit).setVisible(!Visibility);
            mMenu.findItem(R.id.menu_technic_save).setVisible(Visibility);
            mMenu.findItem(R.id.menu_technic_cancel).setVisible(Visibility);
        }
    }

    private void setMode(Boolean edit) {
        oOrigin.setEditable(false);
        oState.setEditable(false);
        registrar_worker.setEditable(false);
        ToolbarMenuSetVisible(edit);
        findViewById(R.id.captureImage).setVisibility(View.GONE);
        if (edit) {
            technicId.setOnValueChangeListener(this);
            typeField.setOnValueChangeListener(this);
            findViewById(R.id.captureImage).setOnClickListener(this);
            findViewById(R.id.captureImage).setVisibility(View.VISIBLE);
        }
        if (record != null) {
            date.setEditable(false);
            technicId.setEditable(false);
        }
    }

    @Override
    public void onFieldValueChange(OField field, Object value) {
        ODataRow row = ((ODataRow) value);
        if (field.getFieldName().equals("inspection_type_id")) {
            if (record != null && record.getInt("inspection_type_id") != 0) {
            } else {
                inspection_items(row);
                viewPager.setAdapter(adapter);
            }
        }
        if (field.getFieldName().equals("inspection_technic_id")) {
//            start sync
            OnTechnicSync sync = new OnTechnicSync();
            List<Object> params = new ArrayList<>();
            ODomain domain = new ODomain();
            domain.add("id", "=", row.getString("id"));
            params.add(domain);
            params.add(row.getString("_id"));
            if (app.inNetwork()) {
                sync.execute(params);
            } else {
                Toast.makeText(this, R.string.toast_network_required, Toast.LENGTH_SHORT).show();
            }
//          end sync
            technisUsageUoms(row);
            technisTire(row);
            technisAccumulator(row);
            inspectionItemLines.clear();
            typeField.setValue(false);
            viewPager.setAdapter(adapter);
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
            inspectionItemLines.clear();
            linesUom.clear();
            tireLines.clear();
            accumulatorLines.clear();
            super.finish();
        }
    }

    public void inspection_items(ODataRow rows) {
        try {
            List<ODataRow> vals = inspectionNorm.select(new String[]{"inspection_pack_id"}, "inspection_type_id = ? and norm_id = ?", new String[]{"" + rows.getInt(OColumn.ROW_ID), "" + technicIns.getTechnicNorm()});
            inspectionItemLines.clear();
            for (ODataRow val : vals) {
                List<ODataRow> inspectionPack = isectionPack.select(null, "_id = ? ", new String[]{val.getString("inspection_pack_id")});
                for (ODataRow pack : inspectionPack) {
                    List<ODataRow> resultInspectionPack = isectionPack.selectManyToManyRecords(new String[]{"name", "inspection_category_id"}, "inspection_items", pack.getInt("_id"));
                    for (ODataRow row : resultInspectionPack) {
                        List<ODataRow> lines = new ArrayList<>();
                        ODataRow newRow = new ODataRow();
                        newRow.put("item_name", row.getString("name"));
                        newRow.put("_id", row.getString("_id"));
                        newRow.put("technic_inspection_category_id", row.get("inspection_category_id"));
                        newRow.put("categ_name", "");
                        if (!row.get("inspection_category_id").equals("false"))
                            newRow.put("categ_name", inspectionCategory.browse(row.getInt("inspection_category_id")).getString("name"));
                        newRow.put(("technic_inspection_item_id"), row.get("_id"));
                        newRow.put("inspection_isitnormal", true);
                        newRow.put("inspection_check", false);
                        newRow.put("description", "");
                        lines.add(newRow);
                        inspectionItemLines.addAll(lines);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.captureImage:
                if (record == null) {
                    if ((Integer) technicId.getValue() == -1) {
                        Toast.makeText(this, "Техник сонгоно уу!!!", Toast.LENGTH_LONG).show();
                        break;
                    }
                    imgName = technic.browse((Integer) technicId.getValue()).getString("state_number");
                } else {
                    imgName = technic.browse(record.getInt("inspection_technic_id")).getString("state_number");
                }
                imgName = imgName.equals("false") ? "" : imgName;
                fileManager.requestForFile(OFileManager.RequestType.IMAGE_OR_CAPTURE_IMAGE);
                break;
        }
    }

    private boolean hasRecordInExtra() {
        return extra != null && extra.containsKey(OColumn.ROW_ID);
    }

    public static void captureOfLine(String name) {
        imgName = name;
        fileManager.requestForFile(OFileManager.RequestType.IMAGE_OR_CAPTURE_IMAGE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_technic_inspection_detail, menu);
        mMenu = menu;
        ToolbarMenuSetVisible(mEditMode);
        return true;
    }

    private class OnTechnicInspecinChangeUpdate extends AsyncTask<ODomain, Void, Void> {

        @Override
        protected Void doInBackground(ODomain... params) {
            if (app.inNetwork()) {
                ODomain domain = params[0];
                List<ODataRow> rows = technicIns.select(null, "id = ?", new String[]{"0"});
                List<ODataRow> rowsImg = inspectionPhoto.select(null, "id = ?", new String[]{"0"});
                List<ODataRow> rowsAccum = inspectionAccumulators.select(null, "id = ?", new String[]{"0"});
                List<ODataRow> rowsTire = inspectionTires.select(null, "id = ?", new String[]{"0"});
                for (ODataRow row : rows)
                    technicIns.quickCreateRecord(row);
                for (ODataRow row : rowsImg)
                    inspectionPhoto.quickCreateRecord(row);
                for (ODataRow row : rowsAccum)
                    inspectionAccumulators.quickCreateRecord(row);
                for (ODataRow row : rowsTire)
                    inspectionTires.quickCreateRecord(row);
                /*Бусад бичлэгүүдийг update хийж байна*/
                technicIns.quickSyncRecords(domain);
                JSONArray jDomain = domain.getArray();
                try {
                    ODomain d = new ODomain();
                    if (jDomain.length() > 0) {
                        jDomain = jDomain.getJSONArray(0);
                        d.add("inspection_id", "=", jDomain.getString(2));
                    }
                    inspectionPhoto.quickSyncRecords(d);
                    inspectionAccumulators.quickSyncRecords(d);
                    inspectionTires.quickSyncRecords(d);
                    inspectionLines.quickSyncRecords(d);
                    inspectionUsage.quickSyncRecords(d);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (!app.inNetwork())
                Toast.makeText(getApplicationContext(), OResource.string(getApplicationContext(), R.string.toast_network_required), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final OnTechnicInspecinChangeUpdate onInsChangeUpdate = new OnTechnicInspecinChangeUpdate();
        final ODomain domain = new ODomain();
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_technic_save:
                if (!saveData && (Integer) technicId.getValue() != -1 && (Integer) typeField.getValue() != -1) {
                    Intent intent = new Intent(this, TechnicsInspectionSignature.class);
                    Bundle extra = new Bundle();
                    extra.putInt("respondent_worker", (Integer) respondent_worker.getValue());
                    extra.putInt("registrar_worker", (Integer) registrar_worker.getValue());
                    extra.putInt("inspection_commis", (Integer) inspection_commis.getValue());
                    intent.putExtras(extra);

                    startActivityForResult(intent, REQUEST_ADD_ITEMS);
                }
                OValues values = mForm.getValues();
                if (values != null && saveData) {
                    List<Integer> inspectionItemIds = new ArrayList<>();
                    List<Integer> uoomIds = new ArrayList<>();
                    List<Integer> tireIds = new ArrayList<>();

                    List<OValues> imgValue = new ArrayList();
                    for (int i = 0; i < mAdapter.getCount(); i++) {
                        ODataRow row = mAdapter.getRow(i);
                        if (row.getString("id").equals("0")) {
                            imgValue.add(row.toValues());
                        }
                    }
                    List<Integer> deleteIds = new ArrayList<>();
                    for (int i = 0; i < mAdapter.getCount(); i++) {
                        ODataRow row = mAdapter.getRow(i);
                        if (!row.getString("id").equals("0") && row.getString("name").equals("registrar_worker")) {
                            deleteIds.add(row.getInt("_id"));
                        } else if (!row.getString("id").equals("0") && row.getString("name").equals("respondent_worker")) {
                            deleteIds.add(row.getInt("_id"));
                        } else if (!row.getString("id").equals("0") && row.getString("name").equals("inspection_commis")) {
                            deleteIds.add(row.getInt("_id"));
                        }
                    }

                    values.put("ins_photo", new RelValues().append(imgValue.toArray(new OValues[imgValue.size()])).delete(deleteIds));

                    if (record != null) {
                        for (ODataRow row : accumulatorLines) {
                            OValues value = new OValues();
                            value.put("serial", row.getString("serial"));
                            inspectionAccumulators.update(row.getInt("_id"), value);
                        }

                        for (ODataRow row : tireLines) {
                            OValues value = new OValues();
                            value.put("serial", row.getString("serial"));
                            inspectionTires.update(row.getInt("_id"), value);
                        }

                        for (ODataRow row : inspectionItemLines) {
                            OValues value = new OValues();
                            value.put("inspection_isitnormal", row.getBoolean("inspection_isitnormal"));
                            value.put("inspection_check", row.getBoolean("inspection_check"));
                            value.put("description", row.getString("description"));
                            inspectionLines.update(row.getInt("_id"), value);
                        }

                        for (ODataRow row : linesUom) {
                            OValues value = new OValues();
                            value.put("usage_value", row.getString("usage_value"));
                            inspectionUsage.update(row.getInt("_id"), value);
                            Log.i("inspectionUsage===", inspectionUsage.select(null, "_id=?", new String[]{row.getString("_id")}).toString());
                        }

                        ODataRow employObj = employee.browse(values.getInt("inspection_respondent_id"));
                        values.put("inspection_respondent_name", employObj.get("name"));
                        ODataRow insType = techInsType.browse(values.getInt("inspection_type_id"));
                        values.put("inspection_type_name", insType.get("name"));

                        technicIns.update(record.getInt(OColumn.ROW_ID), values);
                        record.getString("id");
                        domain.add("id", "=", record.getString("id"));
                        onInsChangeUpdate.execute(domain);
                        Toast.makeText(this, R.string.tech_toast_information_created, Toast.LENGTH_LONG).show();
                        mEditMode = !mEditMode;
                        finish();
                    } else {
                        for (ODataRow row : inspectionItemLines) {
                            OValues insNewVal = new OValues();
                            insNewVal.put("technic_inspection_category_id", row.getInt("technic_inspection_category_id"));
                            insNewVal.put("technic_inspection_item_id", row.getInt("technic_inspection_item_id"));
                            insNewVal.put("inspection_isitnormal", row.getBoolean("inspection_isitnormal"));
                            insNewVal.put("inspection_check", row.getBoolean("inspection_check"));
                            insNewVal.put("description", row.getString("description"));
                            insNewVal.put("item_name", row.getString("item_name"));
                            insNewVal.put("categ_name", row.getString("categ_name"));
                            int newId = inspectionLines.insert(insNewVal);
                            inspectionItemIds.add(newId);
                        }
                        for (ODataRow row : linesUom) {
                            OValues insNewVal = new OValues();
                            insNewVal.put("product_uom_id", row.getInt("product_uom_id"));
                            insNewVal.put("usage_uom_id", row.getInt("usage_uom_id"));
                            insNewVal.put("usage_value", row.getString("usage_value"));
                            int newId = inspectionUsage.insert(insNewVal);
                            uoomIds.add(newId);
                        }

                        for (ODataRow row : tireLines) {
                            OValues insNewVal = new OValues();
                            insNewVal.put("name", row.getString("name"));
                            insNewVal.put("date_record", row.getString("date_record"));
                            insNewVal.put("serial", row.getString("serial"));
                            insNewVal.put("current_position", row.getString("current_position"));
                            int newId = inspectionTires.insert(insNewVal);
                            tireIds.add(newId);
                        }

                        List<OValues> accumValue = new ArrayList();
                        for (ODataRow row : accumulatorLines) {
                            accumValue.add(row.toValues());
                        }

                        values.put("accumulator_ids", new RelValues().append(accumValue.toArray(new OValues[accumValue.size()])));
                        values.put("technic_inspection_check_list_ids", inspectionItemIds);
                        values.put("tire_ids", tireIds);
                        values.put("inspection_usage_ids", uoomIds);

                        values.put("technic_name", technic.browse(values.getInt("inspection_technic_id")).getString("name"));
                        ODataRow employObj = employee.browse(values.getInt("inspection_respondent_id"));
                        if (employObj != null)
                            values.put("inspection_respondent_name", employObj.get("name"));
                        ODataRow insType = techInsType.browse(values.getInt("inspection_type_id"));
                        values.put("inspection_type_name", insType.get("name"));

                        int row_id = technicIns.insert(values);
                        if (row_id != technicIns.INVALID_ROW_ID) {
                            onInsChangeUpdate.execute(domain);
                            Toast.makeText(this, R.string.tech_toast_information_created, Toast.LENGTH_LONG).show();
                            mEditMode = !mEditMode;
                            finish();
                        }
                    }
                }
                break;
            case R.id.menu_technic_cancel:
                OAlert.showConfirm(this, OResource.string(this, R.string.close_activity),
                        new OAlert.OnAlertConfirmListener() {
                            @Override
                            public void onConfirmChoiceSelect(OAlert.ConfirmType type) {
                                if (type == OAlert.ConfirmType.POSITIVE) {
                                    mEditMode = !mEditMode;
                                    setupToolbar();
                                    viewPager.setAdapter(adapter);
                                } else {
                                    mForm.setEditable(true);
                                    setMode(mEditMode);
                                }
                            }
                        });
                break;
            case R.id.menu_technic_edit:
                if (hasRecordInExtra()) {
                    mEditMode = !mEditMode;
                    mForm.setEditable(mEditMode);
                    setMode(mEditMode);
                    viewPager.setAdapter(adapter);
                } else {
                    finish();
                }
                break;
            case R.id.menu_technic_delete:
                OAlert.showConfirm(this, OResource.string(this,
                        R.string.to_delete),
                        new OAlert.OnAlertConfirmListener() {
                            @Override
                            public void onConfirmChoiceSelect(OAlert.ConfirmType type) {
                                if (type == OAlert.ConfirmType.POSITIVE) {
                                    if (technicIns.delete(record.getInt(OColumn.ROW_ID))) {
                                        onInsChangeUpdate.execute(domain);
                                        Toast.makeText(TechnicsInspectionDetails.this, R.string.tech_toast_information_deleted,
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

    private class OnTechnicSync extends AsyncTask<List<Object>, Void, Void> {
        private String id = "0";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.InspectionProgress).setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(List<Object>... params) {
            try {
                List parameter = params[0];
                ODomain domain = (ODomain) parameter.get(0);
                id = parameter.get(1).toString();
                technic.quickSyncRecords(domain);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            if (!id.equals("0")) {
//               /* ODataRow technic = technicIns.browse(Integer.parseInt(id));
//                Log.i("technic====", technic.toString());
////                inspectionItemLines = technic.getO2MRecord("technic_inspection_check_list_ids").browseEach();
//                linesUom = technic.getO2MRecord("inspection_usage_ids").browseEach();
//                tireLines = technic.getO2MRecord("tire_ids").browseEach();
//                accumulatorLines = technic.getO2MRecord("accumulator_ids").browseEach();*/
//            }
            findViewById(R.id.InspectionProgress).setVisibility(View.GONE);
        }
    }

    public void technisUsageUoms(ODataRow technicObj) {
        try {
            TechnicNorm norm = new TechnicNorm(this, null);
            List<ODataRow> normRow = norm.select(new String[]{"usage_uom_ids", "name"}, "_id = ? ", new String[]{technicObj.getString("technic_norm_id")});
            linesUom.clear();
            if (normRow.size() > 0) {
                ODataRow normRows = norm.select(new String[]{"usage_uom_ids", "name"}, "_id = ? ", new String[]{technicObj.getString("technic_norm_id")}).get(0);
                List<ODataRow> lines = normRows.getO2MRecord("usage_uom_ids").browseEach();
                for (ODataRow row : lines) {
                    ODataRow newRow = new ODataRow();
                    newRow.put("product_uom_name", "");
                    newRow.put("usage_uom_name", "");
                    newRow.put("usage_value", "");//last_motohour
                    if (!row.getString("product_uom_name").equals("false")) {
                        newRow.put("product_uom_name", row.getString("product_uom_name"));
                        if (row.getString("product_uom_name").equals("км"))
                            newRow.put("usage_value", technicObj.getString("last_km"));
                        else
                            newRow.put("usage_value", technicObj.getString("last_motohour"));
                    }
                    if (!row.getString("usage_uom_name").equals("false"))
                        newRow.put("usage_uom_name", row.getString("usage_uom_name"));
                    newRow.put("product_uom_id", row.getString("product_uom_id"));
                    newRow.put("_id", row.getString("_id"));
                    newRow.put("usage_uom_id", row.get("usage_uom_id"));
                    linesUom.add(newRow);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void technisTire(ODataRow rows) {
        try {
            tireLines.clear();
            List<ODataRow> techs = technic.select(new String[]{"tires"}, "_id = ? ", new String[]{rows.getString("_id")});
            for (ODataRow tech : techs) {
                tireLines = tech.getO2MRecord("tires").browseEach();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void technisAccumulator(ODataRow rows) {
        try {
            accumulatorLines.clear();
            List<ODataRow> techs = technic.select(new String[]{"accumulators"}, "_id = ? ", new String[]{rows.getString("_id")});
            for (ODataRow tech : techs) {
                for (ODataRow row : tech.getO2MRecord("accumulators").browseEach()) {
                    ODataRow newRow = new ODataRow();
                    newRow.put("name", row.getString("name"));
                    newRow.put("date", row.getString("date"));
                    newRow.put("serial", row.getString("serial"));
                    newRow.put("usage_percent", row.getString("usage_percent"));
                    accumulatorLines.add(newRow);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_ITEMS && resultCode == Activity.RESULT_OK) {
            saveData = true;
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    ODataRow row = new ODataRow();
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
                    row.put("inspection_id", inspectionId);
                    row.put("photo", bundle.getString(key));
                    row.put("name", key);
                    row.put("id", 0);
                    if (!mAdapter.update(row)) {
                        Toast.makeText(this, "Уг зураг аль хэдийн орсон байна!!!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, key + "_" + timeStamp + "-нэртэй зураг нэмэгдлээ.", Toast.LENGTH_LONG).show();
                    }
                }
            }
            MenuItem item = mMenu.findItem(R.id.menu_technic_save);
            onOptionsItemSelected(item);
        } else {
            OValues values = fileManager.handleResult(requestCode, resultCode, data);
            if (values != null && !values.contains("size_limit_exceed")) {
                ODataRow row = new ODataRow();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
                row.put("inspection_id", inspectionId);
                row.put("photo", values.getString("datas"));
                row.put("name", imgName + "_" + timeStamp);
                row.put("id", 0);
                if (!mAdapter.update(row)) {
                    Toast.makeText(this, "Уг зураг аль хэдийн орсон байна!!!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, imgName + "_" + timeStamp + "-нэртэй зураг нэмэгдлээ.", Toast.LENGTH_LONG).show();
                }
            } else if (values != null) {
                Toast.makeText(this, R.string.toast_image_size_too_large, Toast.LENGTH_LONG).show();
            }
        }
    }

    private class OnInspectionImageSync extends AsyncTask<List<ODataRow>, Void, Void> {
        @Override
        protected Void doInBackground(List<ODataRow>... params) {
            try {
                mAdapter = new ImageFragmentAdapter(getSupportFragmentManager(), params[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mPager.setAdapter(null);
            mPager.setAdapter(mAdapter);
            mIndicator.setViewPager(mPager);
        }
    }


/*    private class OnInspectionImageSync extends AsyncTask<List<ODataRow>, Void, Void> {
        @Override
        protected Void doInBackground(List<ODataRow>... params) {
            mAdapter = new ImageFragmentAdapter(getSupportFragmentManager(), params[0]);
            return null;
        }

        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mPager.setAdapter(null);
            mPager.setAdapter(mAdapter);
//            InkPageIndicator mIndicator;
//            mIndicator = (InkPageIndicator) findViewById(R.id.indicator);
//            mIndicator.setViewPager(mPager);
        }
    }*/
}
