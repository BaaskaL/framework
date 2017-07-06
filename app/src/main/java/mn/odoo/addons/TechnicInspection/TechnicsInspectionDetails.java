package mn.odoo.addons.TechnicInspection;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
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
import com.odoo.addons.TechnicInsoection.Models.TechnicInspectionUsage;
import com.odoo.addons.TechnicInsoection.Models.TechnicsInspectionModel;
import com.odoo.addons.TechnicInsoection.Models.UsageUom;
import com.odoo.addons.scrapTire.models.TechnicTire;
import com.odoo.addons.technic.models.TechnicNorm;
import com.odoo.addons.technic.models.TechnicsModel;
import com.odoo.base.addons.ir.feature.OFileManager;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.support.OdooCompatActivity;
import com.odoo.core.utils.BitmapUtils;
import com.odoo.core.utils.OAlert;
import com.odoo.core.utils.OAppBarUtils;
import com.odoo.core.utils.ODateUtils;
import com.odoo.core.utils.OResource;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import odoo.controls.ExpandableListControl;
import odoo.controls.OField;
import odoo.controls.OForm;

/**
 * Created by baaska on 5/30/17.
 */

public class TechnicsInspectionDetails extends OdooCompatActivity implements OField.IOnFieldValueChangeListener,
        View.OnClickListener, ViewPagerEx.OnPageChangeListener, BaseSliderView.OnSliderClickListener {

    public static final String TAG = TechnicsInspectionDetails.class.getSimpleName();

    private Bundle extra;
    private OForm mForm;
    private final String KEY_MODE = "key_edit_mode";
    private TechnicsInspectionModel technicIns;
    private TechnicsModel technic;
    private ODataRow record = null;
    private Boolean mEditMode = false;
    private Menu mMenu;
    private List<ODataRow> resultInspectionPack;
    private TechnicInspectionCheckList inspectionLines;
    private TechnicInspectionUsage inspectionUsage;
    private TechnicInspectionNorm norm_obj;
    private TechnicInspectionPack isectionPack;
    private TechnicInspectionCategory inspectionCategory;
    private UsageUom usageUom;
    private ProductUom productUom;
    private OField typeField;
    private int myId;
    private Uri mCapturedImageURI;

    private ExpandableListControl mList;
    private ExpandableListControl.ExpandableListAdapter mAdapter;
    private ExpandableListControl mUsageList;
    private ExpandableListControl.ExpandableListAdapter mUsageAdapter;
    private ExpandableListControl mTireList;
    private ExpandableListControl.ExpandableListAdapter mTireAdapter;
    private List<Object> objects = new ArrayList<>();
    private List<ODataRow> lines = null;
    private List<Object> UsageObjects = new ArrayList<>();
    private List<ODataRow> linesUom = new ArrayList<>();
    private List<Object> TireObjects = new ArrayList<>();
    private List<ODataRow> tireLines = new ArrayList<>();
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView userImage = null;
    private Toolbar toolbar;
    private OFileManager fileManager;
    private SliderLayout mDemoSlider;
    private String newImage = null;
    private HashMap<String, File> file_maps = new HashMap<>();
    private HashMap<String, String> url_maps;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.technic_inspection_detail);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.technic_collapsing_toolbar);

        mDemoSlider = (SliderLayout) findViewById(R.id.slider);
        toolbar = (Toolbar) findViewById(R.id.toolbarTechnic);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        userImage = (ImageView) findViewById(R.id.technic_image);
        findViewById(R.id.captureImage).setOnClickListener(this);

        fileManager = new OFileManager(this);

        OAppBarUtils.setAppBar(this, true);
        if (savedInstanceState != null) {
            mEditMode = savedInstanceState.getBoolean(KEY_MODE);
        }
        extra = getIntent().getExtras();
        technicIns = new TechnicsInspectionModel(this, null);
        if (!hasRecordInExtra())
            mEditMode = true;
        inspectionLines = new TechnicInspectionCheckList(this, null);
        inspectionUsage = new TechnicInspectionUsage(this, null);
        norm_obj = new TechnicInspectionNorm(this, null);
        isectionPack = new TechnicInspectionPack(this, null);
        inspectionCategory = new TechnicInspectionCategory(this, null);
        technic = new TechnicsModel(this, null);
        usageUom = new UsageUom(this, null);
        productUom = new ProductUom(this, null);
        typeField = (OField) findViewById(R.id.inspection_type_id);

        url_maps = new HashMap<>();
        url_maps.put("Hannibal", "http://static2.hypable.com/wp-content/uploads/2013/12/hannibal-season-2-release-date.jpg");
//        url_maps.put("Big Bang Theory", "http://tvfiles.alphacoders.com/100/hdclearart-10.png");
//        url_maps.put("House of Cards", "http://cdn3.nflximg.net/images/3093/2043093.jpg");
//        url_maps.put("Game of Thrones", "http://images.boomsbeat.com/data/images/full/19640/game-of-thrones-season-4-jpg.jpg");


//        file_maps = new HashMap<>();
//        file_maps.put("Hannibal", R.drawable.hannibal);
//        file_maps.put("Big Bang Theory", R.drawable.bigbang);
//        file_maps.put("House of Cards", R.drawable.house);
//        file_maps.put("Game of Thrones", R.drawable.game_of_thrones);

        for (String name : url_maps.keySet()) {
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView.description(name).image(url_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            //add your extra information setImageBitmap
//            userImage.setImageBitmap(BitmapUtils.getBitmapImage(this, newImage));
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle().
                    putString("extra", name);

            mDemoSlider.addSlider(textSliderView);
        }
        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);

        setupToolbar();
        initAdapter();
        getUsageUom();
        getTire();
    }

    private void setMode(Boolean edit) {
        if (mMenu != null) {
            mMenu.findItem(R.id.menu_technic_detail_more).setVisible(!edit);
            mMenu.findItem(R.id.menu_technic_edit).setVisible(!edit);
            mMenu.findItem(R.id.menu_technic_save).setVisible(edit);
            mMenu.findItem(R.id.menu_technic_cancel).setVisible(edit);
        }
        if (edit) {
            mForm = (OForm) findViewById(R.id.technic_inspection_edit_form);
            findViewById(R.id.technic_inspection_view_layout).setVisibility(View.GONE);
            findViewById(R.id.technic_inspection_edit_layout).setVisibility(View.VISIBLE);
            OField technicField = (OField) findViewById(R.id.inspection_technic_id);
            mList = (ExpandableListControl) findViewById(R.id.expListOrderLineEdit);
            mList.setVisibility(View.VISIBLE);
            mUsageList = (ExpandableListControl) findViewById(R.id.expListUsageUomEdit);
            mUsageList.setVisibility(View.VISIBLE);
            mTireList = (ExpandableListControl) findViewById(R.id.expListTireEdit);
            mTireList.setVisibility(View.VISIBLE);
            technicField.setOnValueChangeListener(this);
            typeField.setOnValueChangeListener(this);
        } else {
            mForm = (OForm) findViewById(R.id.technic_inspection_form);
            findViewById(R.id.technic_inspection_edit_layout).setVisibility(View.GONE);
            findViewById(R.id.technic_inspection_view_layout).setVisibility(View.VISIBLE);
            mList = (ExpandableListControl) findViewById(R.id.expListOrderLine);
            mList.setVisibility(View.VISIBLE);
            mUsageList = (ExpandableListControl) findViewById(R.id.expListUsageUom);
            mUsageList.setVisibility(View.VISIBLE);
            mTireList = (ExpandableListControl) findViewById(R.id.expListTire);
            mTireList.setVisibility(View.VISIBLE);
        }
    }

    private void initAdapter() {
        if (extra != null && record != null) {
            lines = record.getO2MRecord("technic_inspection_check_list_ids").browseEach();
        }
        if (lines != null) {
            objects.clear();
            objects.addAll(lines);
            mAdapter = mList.getAdapter(R.layout.sale_order_line_item, objects,
                    new ExpandableListControl.ExpandableListAdapterGetViewListener() {
                        @Override
                        public View getView(final int position, View mView, ViewGroup parent) {
                            if (mView == null) {
                                mView = getLayoutInflater().inflate(R.layout.sale_order_line_item, parent, false);
                            }
                            final EditText description = (EditText) mView.findViewById(R.id.edtDescription);
                            RadioGroup radioGroup = (RadioGroup) mView.findViewById(R.id.edtRadioGroup);
                            RadioButton inspection_isitnormal = (RadioButton) mView.findViewById(R.id.inspection_isitnormal);
                            RadioButton inspection_check = (RadioButton) mView.findViewById(R.id.inspection_check);
                            TextView category = (TextView) mView.findViewById(R.id.edtCategory);
                            TextView edtName = (TextView) mView.findViewById(R.id.edtName);
                            description.setEnabled(mEditMode);
                            inspection_isitnormal.setEnabled(mEditMode);
                            inspection_check.setEnabled(mEditMode);

                            ODataRow row = (ODataRow) mAdapter.getItem(position);
                            edtName.setText((position + 1) + ". " + row.getString("item_name"));
                            category.setText(row.getString("categ_name"));
                            inspection_isitnormal.setChecked(row.getBoolean("inspection_isitnormal"));
                            inspection_check.setChecked(row.getBoolean("inspection_check"));
                            if (!row.getString("description").equals("false")) {
                                description.setText(row.getString("description"));
                            }
                            description.addTextChangedListener(new TextWatcher() {

                                @Override
                                public void afterTextChanged(Editable s) {
                                    if (s.length() != 0) {
                                        lines.get(position).put("description", description.getText().toString());
                                    }
                                }

                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                }
                            });

                            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                                    lines.get(position).put("inspection_isitnormal", (checkedId == R.id.inspection_isitnormal) ? true : false);
                                    lines.get(position).put("inspection_check", (checkedId == R.id.inspection_check) ? true : false);
                                }
                            });

                            return mView;
                        }
                    });
            objects.clear();
            objects.addAll(lines);
            mAdapter.notifyDataSetChanged(objects);
        }
    }

    private boolean saveLine(OValues values) throws JSONException {
        if (record != null) {
            for (Object line : objects) {
                ODataRow row = (ODataRow) line;
                inspectionLines.update(row.getInt("id"), row.toValues());
            }
            Log.i("UsageObjects===", UsageObjects.toString());
            for (Object line : UsageObjects) {
                ODataRow row = (ODataRow) line;
                OValues update = new OValues();
                update.put("usage_value", row.getFloat("usage_value"));
                inspectionUsage.update(row.getInt("_id"), update);
                Log.i("UPDATEDDDD++++", "ss");

//                ORecordValues datas = new ORecordValues();
//                datas.put("usage_value", row.getFloat("usage_value"));
//                Log.i("datasss", datas.toString());
//                inspectionUsage.getServerDataHelper().updateOnServer(datas, row.getInt("id"));
//                inspectionUsage.quickCreateRecord(row);
            }
            return true;
        } else {
            int row_id = technicIns.insert(values);
            for (Object line : objects) {
                ODataRow row = (ODataRow) line;
                row.put("inspection_id", row_id);
                inspectionLines.insert(row.toValues());
            }

            for (Object line : UsageObjects) {
                ODataRow row = (ODataRow) line;
                row.put("inspection_id", row_id);
                inspectionUsage.insert(row.toValues());
                OValues update = new OValues();
                update.put("last_km", row.getFloat("usage_value"));
                ODataRow technicid = technic.select(new String[]{}, "_id = ? ", new String[]{values.getString("inspection_technic_id")}).get(0);
                Log.i("technicid", technicid.toString());
                Log.i("update val", update.toString());
                technic.update(values.getInt("inspection_technic_id"), update);
//                ORecordValues datas = new ORecordValues();
//                datas.put("last_km", row.getFloat("usage_value"));
//                Log.i("datasss", datas.toString());
//                technic.getServerDataHelper().updateOnServer(datas, technicid.getInt("id"));
//                inspectionUsage.quickCreateRecord(row);
            }
            for (Object line : TireObjects) {
                ODataRow row = (ODataRow) line;
                row.put("inspection_id", row_id);
                inspectionLines.insert(row.toValues());
            }

            if (row_id != OModel.INVALID_ROW_ID) {
                finish();
            }
            return true;
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

    private void setupToolbar() {
        if (!hasRecordInExtra()) {
            setTitle("New");
            setMode(mEditMode);
            mForm.setEditable(mEditMode);
            myId = technicIns.myId();
            mForm.initForm(null);
            ((OField) mForm.findViewById(R.id.inspection_registrar_id)).setValue(myId);
            ((OField) mForm.findViewById(R.id.inspection_date)).setValue(ODateUtils.getDate());
        } else {
            int rowId = extra.getInt(OColumn.ROW_ID);
            setTitle("Technic inspection detail");
            record = technicIns.browse(rowId);
            technicIns.setTechnicNorm(record.getInt("inspection_technic_id"));
            setMode(mEditMode);
            mForm.setEditable(mEditMode);
            resultInspectionPack = record.getO2MRecord("technic_inspection_check_list_ids").browseEach();
            mForm.initForm(record);
        }
    }

    private boolean hasRecordInExtra() {
        return extra != null && extra.containsKey(OColumn.ROW_ID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_technic_inspection_detail, menu);
        mMenu = menu;
        setMode(mEditMode);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        App app = (App) getApplicationContext();
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_technic_save:
                Intent intent = new Intent(this, TechnicsInspectionSignature.class);
                startActivityForResult(intent, 1);
                OValues values = mForm.getValues();
                if (values != null) {
//                    if (record != null) {
//                        try {
//                            saveLine(values);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        technicIns.update(record.getInt(OColumn.ROW_ID), values);
//                        Toast.makeText(this, R.string.toast_information_saved, Toast.LENGTH_LONG).show();
//                        mEditMode = !mEditMode;
//                        setupToolbar();
//                        finish();
//                    } else {
//                        values.put("origin", "/");
//                        if (app.inNetwork()) {
//                            try {
//                                saveLine(values);
//                                Toast.makeText(this, R.string.toast_save, Toast.LENGTH_LONG).show();
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        } else {
//                            try {
//                                saveLine(values);
//                                Toast.makeText(this, R.string.toast_offline_save, Toast.LENGTH_LONG).show();
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
                }
                break;
            case R.id.menu_technic_cancel:
            case R.id.menu_technic_edit:
                if (hasRecordInExtra()) {
                    mEditMode = !mEditMode;
                    setMode(mEditMode);
                    mForm.setEditable(mEditMode);
                    mForm.initForm(record);
                    initAdapter();
                    getUsageUom();
                } else {
                    finish();
                }
                break;
            case R.id.menu_technic_delete:
                OAlert.showConfirm(this, OResource.string(this,
                        R.string.confirm_are_you_sure_want_to_delete),
                        new OAlert.OnAlertConfirmListener() {
                            @Override
                            public void onConfirmChoiceSelect(OAlert.ConfirmType type) {
                                if (type == OAlert.ConfirmType.POSITIVE) {
                                    // Deleting record and finishing activity if success.
                                    if (technicIns.delete(record.getInt(OColumn.ROW_ID))) {
                                        Toast.makeText(TechnicsInspectionDetails.this, R.string.toast_record_deleted,
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
    public void onFieldValueChange(OField field, Object value) {
        ODataRow row = ((ODataRow) value);
        if (field.getFieldName().equals("inspection_type_id")) {
            if (record != null && record.getInt("inspection_type_id") != 0) {
            } else {
                inspection_items(row);
            }
        }
        if (field.getFieldName().equals("inspection_technic_id")) {
            typeField.setValue(false);
            technisUsageUoms(row);
            technisTire(row);
        }
    }

    public void inspection_items(ODataRow rows) {
        try {
            List<ODataRow> val = norm_obj.select(new String[]{"inspection_pack_id"}, "inspection_type_id = ? and norm_id = ?", new String[]{"" + rows.getInt(OColumn.ROW_ID), "" + technicIns.getTechnicNorm()});
            if (val.size() > 0) {
                ODataRow result = norm_obj.select(new String[]{"inspection_pack_id"}, "inspection_type_id = ? and norm_id = ?", new String[]{"" + rows.getInt(OColumn.ROW_ID), "" + technicIns.getTechnicNorm()}).get(0);
                ODataRow inspectionPack = isectionPack.select(new String[]{"inspection_items"}, "id = ? ", new String[]{result.getString("inspection_pack_id")}).get(0);
                resultInspectionPack = isectionPack.selectManyToManyRecords(new String[]{"name", "inspection_category_id"}, "inspection_items", inspectionPack.getInt("id"));
                lines = new ArrayList<>();
                for (ODataRow row : resultInspectionPack) {
                    ODataRow newRow = new ODataRow();
                    newRow.put("item_name", row.getString("name"));
                    newRow.put("technic_inspection_category_id", row.get("inspection_category_id"));
                    if (!row.get("inspection_category_id").equals("false")) {
                        ODataRow inspectionCateg = inspectionCategory.select(new String[]{"name"}, "_id = ? ", new String[]{row.getString("inspection_category_id")}).get(0);
                        newRow.put("categ_name", inspectionCateg.getString("name"));
                    } else {
                        newRow.put("categ_name", "Хоосон");
                    }
                    newRow.put(("technic_inspection_item_id"), row.get("_id"));
//                    newRow.put("inspection_id", row.get("id"));
                    newRow.put("inspection_isitnormal", true);
                    newRow.put("inspection_check", false);
                    newRow.put("description", "");
                    lines.add(newRow);
                }
                initAdapter();
                getUsageUom();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void technisUsageUoms(ODataRow rows) {
        try {
            TechnicNorm norm = new TechnicNorm(this, null);
            List<ODataRow> normRow = norm.select(new String[]{"usage_uom_ids", "name"}, "_id = ? ", new String[]{rows.getString("technic_norm_id")});
            if (normRow.size() > 0) {
                ODataRow normRows = norm.select(new String[]{"usage_uom_ids", "name"}, "_id = ? ", new String[]{rows.getString("technic_norm_id")}).get(0);
                List<ODataRow> lines = normRows.getO2MRecord("usage_uom_ids").browseEach();
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
                    newRow.put("usage_uom_id", row.get("usage_uom_id"));
                    newRow.put("usage_value", row.getString("last_km"));//last_motohour
                    linesUom.add(newRow);
                }
                getUsageUom();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getUsageUom() {
        if (extra != null && record != null) {
            linesUom = record.getO2MRecord("inspection_usage_ids").browseEach();
            Log.i("o2m get val ===", linesUom.toString());
        }
        if (linesUom != null) {
            UsageObjects.clear();
            UsageObjects.addAll(linesUom);

            final int template = R.layout.technic_inspection_usage_uom_row;

            mUsageAdapter = mUsageList.getAdapter(template, UsageObjects,
                    new ExpandableListControl.ExpandableListAdapterGetViewListener() {
                        @Override
                        public View getView(final int position, View mView, ViewGroup parent) {
                            if (mView == null) {
                                mView = getLayoutInflater().inflate(template, parent, false);
                            }
                            final EditText usageValue = (EditText) mView.findViewById(R.id.usageValue);
                            usageValue.setEnabled(mEditMode);
                            TextView usageUom = (TextView) mView.findViewById(R.id.usageUom);
                            TextView productUom = (TextView) mView.findViewById(R.id.productUom);
                            ODataRow row = (ODataRow) mUsageAdapter.getItem(position);
                            usageUom.setText((position + 1) + ". " + row.getString("usage_uom_name"));
                            productUom.setText(row.getString("product_uom_name"));
                            usageValue.setText(row.getString("usage_value"));

                            usageValue.addTextChangedListener(new TextWatcher() {

                                @Override
                                public void afterTextChanged(Editable s) {
                                    if (s.length() != 0) {
                                        linesUom.get(position).put("usage_value", usageValue.getText().toString());
                                    }
                                    Log.i("linesUom=====", linesUom.toString());
                                }

                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                }
                            });
                            UsageObjects.clear();
                            UsageObjects.addAll(linesUom);
                            return mView;
                        }
                    });
            Log.i("UsageObjects========", UsageObjects.toString());
            mUsageAdapter.notifyDataSetChanged(UsageObjects);
        }
    }

    private void getTire() {
        if (extra != null && record != null) {
            List<ODataRow> tech = technic.select(new String[]{"tires"}, "_id = ? ", new String[]{record.getString("inspection_technic_id")});
            if (tech.size() > 0) {
                ODataRow techs = technic.select(new String[]{"tires"}, "_id = ? ", new String[]{record.getString("inspection_technic_id")}).get(0);
                Log.i("techs====", techs.toString());
                tireLines = techs.getO2MRecord("tires").browseEach();
                Log.i("tire_o2m get val ===", tireLines.toString());
            }
        }
        if (tireLines != null) {
            TireObjects.clear();
            TireObjects.addAll(tireLines);

            final int template = R.layout.technic_inspection_tire_row;

            mTireAdapter = mTireList.getAdapter(template, TireObjects,
                    new ExpandableListControl.ExpandableListAdapterGetViewListener() {
                        @Override
                        public View getView(final int position, View mView, ViewGroup parent) {
                            if (mView == null) {
                                mView = getLayoutInflater().inflate(template, parent, false);
                            }

                            final EditText serial = (EditText) mView.findViewById(R.id.serial);
                            serial.setEnabled(mEditMode);
                            TextView name = (TextView) mView.findViewById(R.id.name);
                            TextView date_record = (TextView) mView.findViewById(R.id.date_record);
//                            TextView tread_depreciation_percent = (TextView) mView.findViewById(R.id.tread_depreciation_percent);
                            TextView current_position = (TextView) mView.findViewById(R.id.current_position);

                            ODataRow row = (ODataRow) mTireAdapter.getItem(position);
                            name.setText((position + 1) + ". " + row.getString("name"));
                            date_record.setText(row.getString("date_record"));
//                            tread_depreciation_percent.setText(row.getString("tread_depreciation_percent"));
                            current_position.setText(row.getString("current_position"));
                            serial.setText(row.getString("serial"));
                            serial.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void afterTextChanged(Editable s) {
                                    if (s.length() != 0) {
                                        tireLines.get(position).put("usage_value", serial.getText().toString());
                                    }
                                    Log.i("linesUom=====", tireLines.toString());
                                }

                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                }
                            });

                            return mView;
                        }
                    });
            TireObjects.clear();
            TireObjects.addAll(tireLines);
            Log.i("TireObjects========", TireObjects.toString());
            mTireAdapter.notifyDataSetChanged(TireObjects);
        }
    }

    public void technisTire(ODataRow rows) {
        try {
            TechnicTire tire = new TechnicTire(this, null);
            List<ODataRow> tireRow = tire.select(new String[]{}, "technic_id = ? ", new String[]{rows.getString("_id")});
            if (tireRow.size() > 0) {
                for (ODataRow row : tireRow) {
                    ODataRow newRow = new ODataRow();
                    newRow.put("name", row.getString("name"));
                    newRow.put("date_record", row.getString("date_record"));
                    newRow.put("securrent_positionrial", row.getString("current_position"));
                    newRow.put("serial", row.getString("serial"));
                    tireLines.add(newRow);
                }
                getTire();
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

            ImageView userImages = null;
            userImages.setScaleType(ImageView.ScaleType.CENTER_CROP);
            userImages.setColorFilter(null);
            userImages.setImageBitmap(BitmapUtils.getBitmapImage(this, newImage));

            Uri aa = userImages.getim;

            Bitmap img = BitmapUtils.getBitmapImage(this, newImage);
            Matrix matrix = new Matrix();
            matrix.postRotate(270);
//            x + width must be <= bitmap.width()
            Bitmap b = BitmapFactory.decodeByteArray(data.getByteArrayExtra("byteArray"), 0, data.getByteArrayExtra("byteArray").length);
            Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, false);
            String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), rotatedImg, "Title", null);

            Uri tempUri = Uri.parse(path);
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
                        .setScaleType(BaseSliderView.ScaleType.Fit)
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
}
