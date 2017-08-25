package mn.odoo.addons.TechnicInspection;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.odoo.App;
import com.odoo.R;
import com.odoo.addons.TechnicInsoection.Models.ProductUom;
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
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.support.OdooCompatActivity;
import com.odoo.core.utils.BitmapUtils;
import com.odoo.core.utils.OAlert;
import com.odoo.core.utils.OAppBarUtils;
import com.odoo.core.utils.ODateUtils;
import com.odoo.core.utils.OResource;
import com.odoo.core.utils.OStringColorUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

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
    private TechnicInspectionUsage inspectionUsage;
    private TechnicInspectionNorm norm_obj;
    private TechnicInspectionPack isectionPack;
    private TechnicInspectionCategory inspectionCategory;
    private UsageUom usageUom;
    private ProductUom productUom;

    private ODataRow record = null;
    public static Boolean mEditMode = false;
    private Menu mMenu;
    private OField typeField;

    public int myId;
    private Uri mCapturedImageURI;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView userImage = null;
    private Toolbar toolbar;
    private OFileManager fileManager;
    private SliderLayout mDemoSlider;
    private String newImage = null;
    private HashMap<String, File> file_maps = new HashMap<>();
    private int inspectionItemId;
    private OField oState, oOrigin, date;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PagerAdapter adapter;
    private int InspectionId;
    private List<TextSliderView> textSlider = new ArrayList<>();
    private List<String> indicator = new ArrayList<>();
    private ImageView image;
    public static final int REQUEST_ADD_ITEMS = 323;
    private Boolean saveData = false;
    App app;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.technic_inspection_detail);

        extra = getIntent().getExtras();
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.technic_collapsing_toolbar);
        mDemoSlider = (SliderLayout) findViewById(R.id.slider);
        toolbar = (Toolbar) findViewById(R.id.toolbarTechnic);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEditMode = (!hasRecordInExtra() ? true : false);
        userImage = (ImageView) findViewById(R.id.technic_image);
        findViewById(R.id.captureImage).setOnClickListener(this);
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
        inspectionUsage = new TechnicInspectionUsage(this, null);
        norm_obj = new TechnicInspectionNorm(this, null);
        isectionPack = new TechnicInspectionPack(this, null);
        inspectionCategory = new TechnicInspectionCategory(this, null);
        technic = new TechnicsModel(this, null);
        usageUom = new UsageUom(this, null);
        productUom = new ProductUom(this, null);

        typeField = (OField) findViewById(R.id.inspection_type_id);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        viewPager = (ViewPager) findViewById(R.id.pageViewer);

        tabLayout.addTab(tabLayout.newTab().setText("Техникийн үзлэгийн зүйлc"));
        tabLayout.addTab(tabLayout.newTab().setText("Дугуйн үзлэг"));
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
        //This is testcircleci
        app = (App) getApplicationContext();

        TextSliderView textSliderView = new TextSliderView(this);
        textSliderView.description("Default")
                .image(R.drawable.user_xlarge)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);
        mDemoSlider.addSlider(textSliderView);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.stopAutoCycle();
        indicator.addAll(Arrays.asList("Default",
                "Accordion",
                "Background2Foreground",
                "CubeIn",
                "DepthPage",
                "Fade",
                "FlipHorizontal",
                "FlipPage",
                "Foreground2Background",
                "RotateDown",
                "RotateUp",
                "Stack",
                "Tablet",
                "ZoomIn",
                "ZoomOutSlide",
                "ZoomOut"));

        mForm = (OForm) findViewById(R.id.technic_inspection_form);
        oOrigin = (OField) mForm.findViewById(R.id.originTechIns);
        oState = (OField) mForm.findViewById(R.id.stateTechIns);
        setupToolbar();
        image = new ImageView(this);
    }

    private void setupToolbar() {
        if (!hasRecordInExtra()) {
            setTitle("Үүсгэх");
            myId = technicIns.myId();
            mForm.initForm(null);
            ((OField) mForm.findViewById(R.id.inspection_registrar_id)).setValue(myId);
            ((OField) mForm.findViewById(R.id.inspection_date)).setValue(ODateUtils.getDate());
        } else {
            int rowId = extra.getInt(OColumn.ROW_ID);
            setTitle("Техникийн үзлэг дэлгэрэнгүй");
            record = technicIns.browse(rowId);
            recInsImages = record.getO2MRecord("ins_photo").browseEach();
            OnInspectionImageSync imageSync = new OnInspectionImageSync();
            imageSync.execute(recInsImages);
            Random rn = new Random();
            mDemoSlider.setPresetTransformer(rn.nextInt(indicator.size()) - 1);

            mForm.initForm(record);
            technicIns.setTechnicNorm(record.getInt("inspection_technic_id"));
            inspectionItemLines = record.getO2MRecord("technic_inspection_check_list_ids").browseEach();
            linesUom = record.getO2MRecord("inspection_usage_ids").browseEach();
            tireLines = record.getO2MRecord("tire_ids").browseEach();
        }
        mForm.setEditable(mEditMode);
        setMode(mEditMode);
    }

    private void ToolbarMenuSetVisibl(Boolean Visibility) {
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
        ToolbarMenuSetVisibl(edit);
        if (edit) {
            OField technicField = (OField) findViewById(R.id.inspection_technic_id);
            technicField.setOnValueChangeListener(this);
            typeField.setOnValueChangeListener(this);
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
            sync.execute(params);
//          end sync

            technisUsageUoms(row);
            technisTire(row);
            inspectionItemLines.clear();
            typeField.setValue(false);
            viewPager.setAdapter(adapter);
        }
    }

    private void setInsImage(ODataRow row) {
        InspectionId = row.getInt("_id");
        inspectionImages.put(InspectionId, row.getString("photo"));
//        getTire();
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
            super.finish();
        }
    }

    public void inspection_items(ODataRow rows) {
        try {
            List<ODataRow> val = norm_obj.select(new String[]{"inspection_pack_id"}, "inspection_type_id = ? and norm_id = ?", new String[]{"" + rows.getInt(OColumn.ROW_ID), "" + technicIns.getTechnicNorm()});
            inspectionItemLines.clear();
            if (val.size() > 0) {
                ODataRow result = norm_obj.select(new String[]{"inspection_pack_id"}, "inspection_type_id = ? and norm_id = ?", new String[]{"" + rows.getInt(OColumn.ROW_ID), "" + technicIns.getTechnicNorm()}).get(0);
                ODataRow inspectionPack = isectionPack.select(new String[]{"inspection_items"}, "id = ? ", new String[]{result.getString("inspection_pack_id")}).get(0);
                List<ODataRow> resultInspectionPack = isectionPack.selectManyToManyRecords(new String[]{"name", "inspection_category_id"}, "inspection_items", inspectionPack.getInt("id"));
                Log.i("resultInspectionPack=", resultInspectionPack.toString());
                List<ODataRow> lines = new ArrayList<>();
                for (ODataRow row : resultInspectionPack) {
                    Log.i("row==ll=", row.toString());
                    ODataRow newRow = new ODataRow();
                    newRow.put("item_name", row.getString("name"));
                    newRow.put("_id", row.getString("_id"));
                    newRow.put("technic_inspection_category_id", row.get("inspection_category_id"));
                    if (!row.get("inspection_category_id").equals("false")) {
                        List<ODataRow> inspectionCateg = inspectionCategory.select(new String[]{"name"}, "_id = ? ", new String[]{row.getString("inspection_category_id")});
                        Log.i("inspectionCateg===", inspectionCateg.toString());
                        for (ODataRow categ : inspectionCateg) {
                            newRow.put("categ_name", categ.getString("name"));
                        }
                    } else {
                        newRow.put("categ_name", "Хоосон");
                    }
                    newRow.put(("technic_inspection_item_id"), row.get("_id"));
                    newRow.put("inspection_isitnormal", true);
                    newRow.put("inspection_check", false);
                    newRow.put("description", "");
                    lines.add(newRow);
                }
                inspectionItemLines.addAll(lines);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCustomerImage() {

        if (record != null && !record.getString("image_small").equals("false")) {
            userImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            String base64 = newImage;
            if (newImage == null) {
                if (!record.getString("image_medium").equals("false")) {
                    base64 = record.getString("image_medium");
                } else {
                    base64 = record.getString("image_small");
                }
            }
            userImage.setImageBitmap(BitmapUtils.getBitmapImage(this, base64));
        } else {
            userImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            userImage.setColorFilter(Color.WHITE);
            int color = OStringColorUtil.getStringColor(this, record.getString("name"));
            userImage.setBackgroundColor(color);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.captureImage:
                fileManager.requestForFile(OFileManager.RequestType.IMAGE_OR_CAPTURE_IMAGE);
                break;
        }
    }

    private boolean hasRecordInExtra() {
        return extra != null && extra.containsKey(OColumn.ROW_ID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_technic_inspection_detail, menu);
        mMenu = menu;
        ToolbarMenuSetVisibl(mEditMode);
        return true;
    }

    private class OnTechnicInspecinChangeUpdate extends AsyncTask<ODomain, Void, Void> {

        @Override
        protected Void doInBackground(ODomain... params) {
            if (app.inNetwork()) {
                ODomain domain = params[0];
                List<ODataRow> rows = technicIns.select(null, "id = ?", new String[]{"0"});
                for (ODataRow row : rows) {
                    technicIns.quickCreateRecord(row);
                }
                Log.i("domain-=====", domain.toString());
                technicIns.quickSyncRecords(domain);
            }
            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        OnTechnicInspecinChangeUpdate onInsChangeUpdate = new OnTechnicInspecinChangeUpdate();
        ODomain domain = new ODomain();
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_technic_save:
                if (!saveData) {
                    Intent intent = new Intent(this, TechnicsInspectionSignature.class);
                    startActivityForResult(intent, REQUEST_ADD_ITEMS);
                }
                OValues values = mForm.getValues();
                if (values != null && saveData) {
                    List<Integer> inspectionItemIds = new ArrayList<>();
                    List<Integer> uoomIds = new ArrayList<>();
                    List<Integer> tireIds = new ArrayList<>();
                    List<Integer> photoIds = new ArrayList<>();
                    if (record != null) {
                        for (ODataRow row : inspectionItemLines) {
                            OValues insNewVal = new OValues();
                            insNewVal.put("inspection_isitnormal", row.getBoolean("inspection_isitnormal"));
                            insNewVal.put("inspection_check", row.getBoolean("inspection_check"));
                            if (!row.getString("description").equals("false")) {
                                insNewVal.put("description", row.getString("description"));
                            }
                            inspectionLines.update(row.getInt(OColumn.ROW_ID), insNewVal);
                        }
                        technicIns.update(record.getInt(OColumn.ROW_ID), values);
                        domain.add("id", "=", record.getInt(OColumn.ROW_ID));
                        onInsChangeUpdate.execute(domain);
                        Toast.makeText(this, R.string.toast_information_saved, Toast.LENGTH_LONG).show();
                        mEditMode = !mEditMode;
                        setupToolbar();
                        finish();
                    } else {
                        for (ODataRow row : inspectionItemLines) {
                            OValues insNewVal = new OValues();
                            insNewVal.put("technic_inspection_category_id", row.getInt("technic_inspection_category_id"));
                            insNewVal.put("technic_inspection_item_id", row.getInt("technic_inspection_item_id"));
                            insNewVal.put("inspection_isitnormal", row.getBoolean("inspection_isitnormal"));
                            insNewVal.put("inspection_check", row.getBoolean("inspection_check"));
                            insNewVal.put("description", row.getString("description"));
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
                        for (ODataRow row : recInsImages) {
                            OValues insNewVal = new OValues();
                            insNewVal.put("photo", row.getString("photo"));
                            int newId = inspectionPhoto.insert(insNewVal);
                            photoIds.add(newId);
                        }
                        values.put("technic_inspection_check_list_ids", inspectionItemIds);
                        values.put("tire_ids", tireIds);
                        values.put("ins_photo", photoIds);
                        values.put("technic_name", photoIds);
                        values.put("inspection_usage_ids", uoomIds);

                        ODataRow employObj = employee.browse(values.getInt("inspection_respondent_id"));
                        values.put("inspection_respondent_name", employObj.get("name"));

                        ODataRow techObj = technic.browse(values.getInt("inspection_technic_id"));
                        values.put("technic_name", techObj.get("name"));

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
//                    getUsageUom();
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
        @Override
        protected Void doInBackground(List<Object>... params) {
            if (app.inNetwork()) {
                List parameter = params[0];
                ODomain domain = (ODomain) parameter.get(0);
                technic.quickSyncRecords(domain);
            }
            return null;
        }
    }

    public void technisUsageUoms(ODataRow technicObj) {
        try {
            TechnicNorm norm = new TechnicNorm(this, null);
            Log.i("technicId====", technicObj.toString());
            Log.i("technic_norm_id====", technicObj.getString("technic_norm_id"));

            List<ODataRow> normRow = norm.select(new String[]{"usage_uom_ids", "name"}, "_id = ? ", new String[]{technicObj.getString("technic_norm_id")});
            Log.i("normRow====", normRow.toString());
            linesUom.clear();
            if (normRow.size() > 0) {
                ODataRow normRows = norm.select(new String[]{"usage_uom_ids", "name"}, "_id = ? ", new String[]{technicObj.getString("technic_norm_id")}).get(0);
                Log.i("normRows====", normRows.toString());
                List<ODataRow> lines = normRows.getO2MRecord("usage_uom_ids").browseEach();
                Log.i("lines=====kk", lines.toString());
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

    public String BitMapToString(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ADD_ITEMS && resultCode == Activity.RESULT_OK) {
            saveData = true;
            MenuItem item = mMenu.findItem(R.id.menu_technic_save);
            Bitmap img = BitmapFactory.decodeByteArray(
                    data.getByteArrayExtra("byteArray"), 0,
                    data.getByteArrayExtra("byteArray").length);

            String newImage = BitMapToString(img);
            ODataRow image = new ODataRow();
            image.put("photo", newImage);
//            recInsImages.add(image);

            onOptionsItemSelected(item);

        } else {
            OValues values = fileManager.handleResult(requestCode, resultCode, data);
            if (values != null && !values.contains("size_limit_exceed")) {
                String newImage = values.getString("datas");
                ODataRow image = new ODataRow();
                image.put("photo", newImage);
                recInsImages.add(image);
                Bitmap img = BitmapUtils.getBitmapImage(this, newImage);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(img, 2560, 1600, true);//screen resolution 16:10
                String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), img, "Title", null);
                Uri tempUri = Uri.parse(path);
                TextSliderView textSliderView = new TextSliderView(this);
                textSliderView.description("aa")
                        .image(tempUri)
                        .setScaleType(BaseSliderView.ScaleType.CenterInside)
                        .setOnSliderClickListener(this);
                textSliderView.bundle(new Bundle());
                if (mDemoSlider.getCurrentSlider().getDescription().equals("Default")) {
                    mDemoSlider.removeSliderAt(0);
                }
                mDemoSlider.addSlider(textSliderView);
                mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                mDemoSlider.startAutoCycle();
            } else if (values != null) {
                Toast.makeText(this, R.string.toast_image_size_too_large, Toast.LENGTH_LONG).show();
            }
        }
    }

    private class OnInspectionImageSync extends AsyncTask<List<ODataRow>, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(List<ODataRow>... params) {
            try {
                for (ODataRow row : params[0]) {
                    Bitmap img = BitmapUtils.getBitmapImage(TechnicsInspectionDetails.this, row.getString("photo"));
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(img, 2560, 1600, true);//screen resolution 16:10
                    String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), scaledBitmap, "Title", null);
                    Uri tempUri = Uri.parse(path);
                    TextSliderView textSliderView = new TextSliderView(TechnicsInspectionDetails.this);
                    textSliderView.description("ZZZZZ")
                            .image(tempUri)
                            .setScaleType(BaseSliderView.ScaleType.CenterInside)
                            .setOnSliderClickListener(TechnicsInspectionDetails.this);
                    textSlider.add(textSliderView);
                }
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mDemoSlider.removeAllSliders();
            for (TextSliderView slide : textSlider) {
                mDemoSlider.addSlider(slide);
            }
            mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            mDemoSlider.stopAutoCycle();
        }
    }
}
