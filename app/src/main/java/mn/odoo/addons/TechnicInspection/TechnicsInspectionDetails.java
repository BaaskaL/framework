package mn.odoo.addons.TechnicInspection;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;
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

    private int myId;
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
    private CustomerViewPager viewPager;
    private PagerAdapter adapter;
    private int InspectionId;
    private List<TextSliderView> textSlider = new ArrayList<>();
    private List<String> indicator = new ArrayList<>();
    private ImageView image;
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
        tabLayout.addTab(tabLayout.newTab().setText("Техникийн үзлэгийн зүйлc"));
        tabLayout.addTab(tabLayout.newTab().setText("Дугуйн үзлэг"));
        tabLayout.addTab(tabLayout.newTab().setText("Ашиглалт"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager = (CustomerViewPager) findViewById(R.id.pageViewer);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.i("Selected_position=", tab.getPosition() + "");
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
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
// gej bolh uuuuuuu
//
//                Intent intent = new Intent(Intent.ACTION_PICK,
//                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(intent, 1);
//
//                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));
//                startActivityForResult(intent, REQUEST_IMAGE);

                fileManager.requestForFile(OFileManager.RequestType.IMAGE_OR_CAPTURE_IMAGE);
//                Intent takePictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
//
//                String fileName = "temp.jpg";
//                ContentValues values = new ContentValues();
//                values.put(MediaStore.Images.Media.TITLE, fileName);
//                mCapturedImageURI = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
//                startActivityForResult(takePictureIntent, 2);
//
//                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(intent, 1);

//                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//                    String fileName = "technic_inspection_01.jpg";
//                    ContentValues values = new ContentValues();
//                    values.put(MediaStore.Images.ImageColumns.TITLE, fileName);
//                    mCapturedImageURI = getContentResolver()
//                            .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//                    takePictureIntent
//                            .putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
//                    startActivityForResult(takePictureIntent, 2);
//                }
//                break;
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
                OValues values = mForm.getValues();
                if (values != null) {
                    List<Integer> inspectionItemIds = new ArrayList<>();
                    List<Integer> uoomIds = new ArrayList<>();
                    List<Integer> tireIds = new ArrayList<>();
                    List<Integer> photoIds = new ArrayList<>();
                    if (record != null) {
                        technicIns.update(record.getInt(OColumn.ROW_ID), values);
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
//        private String technicId;

        @Override
        protected Void doInBackground(List<Object>... params) {
            if (app.inNetwork()) {
                List parameter = params[0];
                ODomain domain = (ODomain) parameter.get(0);
                technic.quickSyncRecords(domain);
//                technicId = parameter.get(1).toString();
            }
            return null;
        }
    }

    public void technisUsageUoms(ODataRow rows) {
        try {
            TechnicNorm norm = new TechnicNorm(this, null);
            Log.i("technic_norm_id====", rows.getString("technic_norm_id"));

            List<ODataRow> normRow = norm.select(new String[]{"usage_uom_ids", "name"}, "_id = ? ", new String[]{rows.getString("technic_norm_id")});
            Log.i("normRow====", normRow.toString());
            linesUom.clear();
            if (normRow.size() > 0) {
                ODataRow normRows = norm.select(new String[]{"usage_uom_ids", "name"}, "_id = ? ", new String[]{rows.getString("technic_norm_id")}).get(0);
                Log.i("normRows====", normRows.toString());
                List<ODataRow> lines = normRows.getO2MRecord("usage_uom_ids").browseEach();
                Log.i("lines=====kk", lines.toString());

                for (ODataRow row : lines) {
                    ODataRow newRow = new ODataRow();
                    if (!row.getString("product_uom_id").equals("false")) {
                        ODataRow product_uom = productUom.select(new String[]{"name"}, "_id = ? ", new String[]{row.getString("product_uom_id")}).get(0);
                        newRow.put("product_uom_name", product_uom.getString("name"));
                    } else {
                        newRow.put("product_uom_name", "Хоосон");
                    }

                    if (!row.getString("usage_uom_id").equals("false")) {
                        ODataRow usage_uom = usageUom.select(new String[]{"name"}, "_id = ? ", new String[]{row.getString("usage_uom_id")}).get(0);
                        newRow.put("usage_uom_name", usage_uom.getString("name"));
                    } else {
                        newRow.put("usage_uom_name", "Хоосон");
                    }
                    newRow.put("product_uom_id", row.getString("product_uom_id"));
                    newRow.put("_id", row.getString("_id"));
                    newRow.put("usage_uom_id", row.get("usage_uom_id"));
                    newRow.put("usage_value", row.getString("last_km"));//last_motohour
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Uri uri = data.getData();
//        String uriString = uri.toString();
//        File file = null;

        OValues values = fileManager.handleResult(requestCode, resultCode, data);
        if (values != null && !values.contains("size_limit_exceed")) {
            newImage = values.getString("datas");
//            Uri targetUri = data.getData();
            ODataRow aa = new ODataRow();
            aa.put("photo", newImage);
            recInsImages.add(aa);
//            ImageView userImages = null;
////            userImages.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            userImages.setColorFilter(null);
//            userImages.setImageBitmap(BitmapUtils.getBitmapImage(this, newImage));

//            Uri aa = userImages.getim;
            Bitmap img = BitmapUtils.getBitmapImage(this, newImage);
//            Matrix matrix = new Matrix();
//            matrix.postRotate(90);
//            x + width must be <= bitmap.width()
//            Bitmap b = BitmapFactory.decodeByteArray(data.getByteArrayExtra("byteArray"), 0, data.getByteArrayExtra("byteArray").length);
//            Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, false);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(img, 2560, 1600, true);//screen resolution 16:10
            String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), img, "Title", null);
            Log.i("path====", path);
            Uri tempUri = Uri.parse(path);
            Log.i("tempUri====", tempUri.toString());
//            profileImage.setImageBitmap(Bitmap.createScaledBitmap(b, 120, 120, false));
            Cursor cursor = getContentResolver().query(tempUri, null, null, null, null);
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
            File finalFile = new File(cursor.getString(idx));
            file_maps.put(finalFile.getName(), finalFile);
            mDemoSlider.removeAllSliders();
            for (String name : file_maps.keySet()) {
                TextSliderView textSliderView = new TextSliderView(this);
                textSliderView.description(name).image(file_maps.get(name))
                        .setScaleType(BaseSliderView.ScaleType.CenterInside)
                        .setOnSliderClickListener(this);
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle().putString("extra", name);
                mDemoSlider.addSlider(textSliderView);
            }
        } else if (values != null) {
            Toast.makeText(this, R.string.toast_image_size_too_large, Toast.LENGTH_LONG).show();
        }

//        if (requestCode == 2 && resultCode == RESULT_OK) {
//            String[] projection = {MediaStore.Images.Media.DATA};
//            Cursor cursor = getContentResolver().query(mCapturedImageURI, projection, null, null, null);
//            int column_index_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            cursor.moveToFirst();
//            String picturePath = cursor.getString(column_index_data);
//            Log.i("picturePath====", picturePath);
//            file = new File(picturePath);
//            url_maps.put("aaaaaaaaa", "dd");
//            for (String name : url_maps.keySet()) {
//                TextSliderView textSliderView = new TextSliderView(this);
//                // initialize a SliderLayout
//                textSliderView.description(name).image(file)
//                        .setScaleType(BaseSliderView.ScaleType.Fit)
//                        .setOnSliderClickListener(this);
//                textSliderView.bundle(new Bundle());
//                textSliderView.getBundle()
//                        .putString("extra", name);
//                mDemoSlider.addSlider(textSliderView);
//            }
//
//        }

    }

    private class OnInspectionImageSync extends AsyncTask<List<ODataRow>, Void, Void> {
//        HashMap<String, String> business = new HashMap<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(List<ODataRow>... params) {
            try {
                file_maps.clear();
                for (ODataRow row : params[0]) {
                    Bitmap img = BitmapUtils.getBitmapImage(TechnicsInspectionDetails.this, row.getString("photo"));
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(img, 2560, 1600, true);//screen resolution 16:10

                    String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), scaledBitmap, "Title", null);
                    Uri tempUri = Uri.parse(path);
                    String aaaaaa = Environment.getExternalStorageDirectory().toString() + "Pictures/1501385646043.jpg";

                    Log.i("ExternalStorageDirect", aaaaaa);
                    Log.i("tempUri====", tempUri.toString());
//                    captureImage8.setImageBitmap(BitmapUtils.getBitmapImage(this, row.getString("photo"));
                    Cursor cursor = getContentResolver().query(tempUri, null, null, null, null);
                    cursor.moveToFirst();
                    int idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    File finalFile = new File(cursor.getString(idx));
                    cursor.close();

//                    file_maps.put(finalFile.getName(), finalFile);
//                    ContextWrapper cw = new ContextWrapper(getApplicationContext());
//                    File directory = cw.getDir("imageDir", getApplicationContext().MODE_PRIVATE);
//                    // Create imageDir
//                    File mypath = new File(directory, "profile.jpg");
//                    FileOutputStream fos = null;
//
//                    try {
//                        fos = new FileOutputStream(mypath);
//                        // Use the compress method on the BitMap object to write image to the OutputStream
//                        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    } finally {
//                        try {
//                            fos.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                    Log.i(directory.getAbsolutePath(), "========");
//                    Uri tempUri = Uri.parse(directory.getAbsolutePath());

                    TextSliderView textSliderView = new TextSliderView(TechnicsInspectionDetails.this);
                    textSliderView.description("ZZZZZ")
                            .image(tempUri)
                            .setScaleType(BaseSliderView.ScaleType.CenterInside)
                            .setOnSliderClickListener(TechnicsInspectionDetails.this);
                    textSlider.add(textSliderView);
                    Log.i("path=====", path.toString());
                    File fdelete = new File(path);
//                    finalFile.delete();
                }
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

//            String strLogoURL = business.get("aaa");
//            image.setMaxHeight(20);
//            image.setMaxWidth(20);
            mDemoSlider.removeAllSliders();
//            Picasso.with(getApplicationContext()).load(strLogoURL).into(image);
            for (TextSliderView slide : textSlider) {
                mDemoSlider.addSlider(slide);
            }
            mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            mDemoSlider.stopAutoCycle();
        }
    }
}
