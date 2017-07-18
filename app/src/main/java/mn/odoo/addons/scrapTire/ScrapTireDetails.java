package mn.odoo.addons.scrapTire;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.odoo.App;
import com.odoo.R;
import com.odoo.addons.TechnicInsoection.Models.TechnicInspectionNorm;
import com.odoo.addons.scrapTire.models.ScrapTires;
import com.odoo.addons.scrapTire.models.TechnicTire;
import com.odoo.addons.technic.models.TechnicsModel;
import com.odoo.base.addons.ir.IrAttachment;
import com.odoo.base.addons.ir.feature.OFileManager;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.rpc.helper.ORecordValues;
import com.odoo.core.support.OdooCompatActivity;
import com.odoo.core.utils.BitmapUtils;
import com.odoo.core.utils.OAlert;
import com.odoo.core.utils.OAppBarUtils;
import com.odoo.core.utils.ODateUtils;
import com.odoo.core.utils.OResource;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import odoo.controls.ExpandableListControl;
import odoo.controls.OField;
import odoo.controls.OForm;

/**
 * Created by baaska on 5/30/17.
 */

public class ScrapTireDetails extends OdooCompatActivity implements OField.IOnFieldValueChangeListener {

    public static final String TAG = ScrapTireDetails.class.getSimpleName();

    private Bundle extra;
    private OForm mForm;
    private final String KEY_MODE = "key_edit_mode";
    private TechnicsModel technic;
    private ODataRow record = null;
    private Boolean mEditMode = false;
    private Menu mMenu;
    private TechnicInspectionNorm norm_obj;
    private int myId;
    private ScrapTires scrapTires;
    private ExpandableListControl mList;
    private ExpandableListControl.ExpandableListAdapter mAdapter;
    private List<ODataRow> tireLines = new ArrayList<>();
    private List<Object> TireObjects = new ArrayList<>();
    private Toolbar toolbar;
    private OFileManager fileManager;
    private List<OValues> attachments;
    private String newImage = null;
    private int selectedCapture = 0;
    private IrAttachment irAttachment;
    private int TireScrapId;//record.getInt("id");
    private HashMap<Integer, Bitmap> images = new HashMap<>();
    private int pos = -1;
    private ImageView aaaa = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scrap_tire_detail);

        toolbar = (Toolbar) findViewById(R.id.toolbarScrapTire);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        scrapTires = new ScrapTires(this, null);
        OAppBarUtils.setAppBar(this, true);
        if (savedInstanceState != null) {
            mEditMode = savedInstanceState.getBoolean(KEY_MODE);
        }
        extra = getIntent().getExtras();
        if (!hasRecordInExtra())
            mEditMode = true;
        norm_obj = new TechnicInspectionNorm(this, null);
        technic = new TechnicsModel(this, null);
        fileManager = new OFileManager(this);
        attachments = new ArrayList<OValues>();

        irAttachment = new IrAttachment(this, null);

        setupToolbar();
        getTire();
    }

    private void setMode(Boolean edit) {
        if (mMenu != null) {
            mMenu.findItem(R.id.menu_more).setVisible(!edit);
            mMenu.findItem(R.id.menu_edit).setVisible(!edit);
            mMenu.findItem(R.id.menu_save).setVisible(edit);
            mMenu.findItem(R.id.menu_cancel).setVisible(edit);
        }
        if (edit) {
            mForm = (OForm) findViewById(R.id.scrap_tire_edit_form);
            findViewById(R.id.scrap_tire_view_layout).setVisibility(View.GONE);
            findViewById(R.id.scrap_tire_edit_layout).setVisibility(View.VISIBLE);
            OField technicField = (OField) findViewById(R.id.technic_id);
            technicField.setOnValueChangeListener(this);
            mList = (ExpandableListControl) findViewById(R.id.expListTireLineEdit);
        } else {
            mForm = (OForm) findViewById(R.id.scrap_tire_form);
            findViewById(R.id.scrap_tire_edit_layout).setVisibility(View.GONE);
            findViewById(R.id.scrap_tire_view_layout).setVisibility(View.VISIBLE);
            mList = (ExpandableListControl) findViewById(R.id.expListTireLine);
        }
    }

//    private void initAdapter() {
//        if (extra != null && record != null) {
//            lines = record.getO2MRecord("technic_inspection_check_list_ids").browseEach();
//        }
//        if (lines != null) {
//            objects.clear();
//            objects.addAll(lines);
//            mAdapter = mList.getAdapter(R.layout.sale_order_line_item, objects,
//                    new ExpandableListControl.ExpandableListAdapterGetViewListener() {
//                        @Override
//                        public View getView(final int position, View mView, ViewGroup parent) {
//                            if (mView == null) {
//                                mView = getLayoutInflater().inflate(R.layout.sale_order_line_item, parent, false);
//                            }
//                            final EditText description = (EditText) mView.findViewById(R.id.edtDescription);
//                            RadioGroup radioGroup = (RadioGroup) mView.findViewById(R.id.edtRadioGroup);
//                            RadioButton inspection_isitnormal = (RadioButton) mView.findViewById(R.id.inspection_isitnormal);
//                            RadioButton inspection_check = (RadioButton) mView.findViewById(R.id.inspection_check);
//                            TextView category = (TextView) mView.findViewById(R.id.edtCategory);
//                            TextView edtName = (TextView) mView.findViewById(R.id.edtName);
//                            description.setEnabled(mEditMode);
//                            inspection_isitnormal.setEnabled(mEditMode);
//                            inspection_check.setEnabled(mEditMode);
//
//                            ODataRow row = (ODataRow) mAdapter.getItem(position);
//                            edtName.setText((position + 1) + ". " + row.getString("item_name"));
//                            category.setText(row.getString("categ_name"));
//                            inspection_isitnormal.setChecked(row.getBoolean("inspection_isitnormal"));
//                            inspection_check.setChecked(row.getBoolean("inspection_check"));
//                            if (!row.getString("description").equals("false")) {
//                                description.setText(row.getString("description"));
//                            }
//                            description.addTextChangedListener(new TextWatcher() {
//
//                                @Override
//                                public void afterTextChanged(Editable s) {
//                                    if (s.length() != 0) {
//                                        lines.get(position).put("description", description.getText().toString());
//                                    }
//                                }
//
//                                @Override
//                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                                }
//
//                                @Override
//                                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                                }
//                            });
//
//                            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//                                @Override
//                                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
//                                    lines.get(position).put("inspection_isitnormal", (checkedId == R.id.inspection_isitnormal) ? true : false);
//                                    lines.get(position).put("inspection_check", (checkedId == R.id.inspection_check) ? true : false);
//                                }
//                            });
//
//                            return mView;
//                        }
//                    });
//            objects.clear();
//            objects.addAll(lines);
//            mAdapter.notifyDataSetChanged(objects);
//        }
//    }

//    private boolean saveLine(OValues values) throws JSONException {
//        if (record != null) {
//            for (Object line : objects) {
//                ODataRow row = (ODataRow) line;
//                inspectionLines.update(row.getInt("id"), row.toValues());
//            }
//            Log.i("UsageObjects===", UsageObjects.toString());
//            for (Object line : UsageObjects) {
//                ODataRow row = (ODataRow) line;
//                OValues update = new OValues();
//                update.put("usage_value", row.getFloat("usage_value"));
//                inspectionUsage.update(row.getInt("_id"), update);
//                Log.i("UPDATEDDDD++++", "ss");
//
////                ORecordValues datas = new ORecordValues();
////                datas.put("usage_value", row.getFloat("usage_value"));
////                Log.i("datasss", datas.toString());
////                inspectionUsage.getServerDataHelper().updateOnServer(datas, row.getInt("id"));
////                inspectionUsage.quickCreateRecord(row);
//            }
//            return true;
//        } else {
//            int row_id = technicIns.insert(values);
//            for (Object line : objects) {
//                ODataRow row = (ODataRow) line;
//                row.put("inspection_id", row_id);
//                inspectionLines.insert(row.toValues());
//            }
//
//            for (Object line : UsageObjects) {
//                ODataRow row = (ODataRow) line;
//                row.put("inspection_id", row_id);
//                inspectionUsage.insert(row.toValues());
//                OValues update = new OValues();
//                update.put("last_km", row.getFloat("usage_value"));
//                ODataRow technicid = technic.select(new String[]{}, "_id = ? ", new String[]{values.getString("inspection_technic_id")}).get(0);
//                Log.i("technicid", technicid.toString());
//                Log.i("update val", update.toString());
//                technic.update(values.getInt("inspection_technic_id"), update);
////                ORecordValues datas = new ORecordValues();
////                datas.put("last_km", row.getFloat("usage_value"));
////                Log.i("datasss", datas.toString());
////                technic.getServerDataHelper().updateOnServer(datas, technicid.getInt("id"));
////                inspectionUsage.quickCreateRecord(row);
//            }
//            for (Object line : TireObjects) {
//                ODataRow row = (ODataRow) line;
//                row.put("inspection_id", row_id);
//                inspectionLines.insert(row.toValues());
//            }
//
//            if (row_id != OModel.INVALID_ROW_ID) {
//                finish();
//            }
//            return true;
//        }
//    }

    private void setupToolbar() {
        if (!hasRecordInExtra()) {
            setTitle("Үүсгэх");
            setMode(mEditMode);
            mForm.setEditable(mEditMode);
            mForm.initForm(null);
            ((OField) mForm.findViewById(R.id.tire_date)).setValue(ODateUtils.getDate());
            OField oState = (OField) mForm.findViewById(R.id.tire_state);
            oState.setValue("Ноорог");
            oState.setEditable(false);
        } else {
            int rowId = extra.getInt(OColumn.ROW_ID);
            setTitle("Дугуйн акт дэлгэрэнгүй");
            record = scrapTires.browse(rowId);
            setMode(mEditMode);
            tireLines = record.getO2MRecord("tire_ids").browseEach();
            getTire();
            Log.i("browse_value==", record.toString());
            mForm.setEditable(mEditMode);
            mForm.initForm(record);
        }
    }

//    private void setCustomerImage() {
//
//        if (record != null && !record.getString("image_small").equals("false")) {
//            tireImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            String base64 = newImage;
//            if (tireImage == null) {
//                if (!record.getString("large_image").equals("false")) {
//                    base64 = record.getString("large_image");
//                } else {
//                    base64 = record.getString("image_small");
//                }
//            }
//            tireImage.setImageBitmap(BitmapUtils.getBitmapImage(this, base64));
//        } else {
//            tireImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//            tireImage.setColorFilter(Color.WHITE);
//            int color = OStringColorUtil.getStringColor(this, record.getString("name"));
//            tireImage.setBackgroundColor(color);
//        }
//    }


    private boolean hasRecordInExtra() {
        return extra != null && extra.containsKey(OColumn.ROW_ID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        mMenu = menu;
        setMode(mEditMode);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        App app = (App) getApplicationContext();
        OnCustomerChangeUpdate onCustomerChangeUpdate = new OnCustomerChangeUpdate();
        ODomain ccc = new ODomain();
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_save:
                OValues values = mForm.getValues();
                if (values != null) {
                    if (record != null) {
                        scrapTires.update(record.getInt(OColumn.ROW_ID), values);
                        Toast.makeText(this, R.string.tech_toast_information_saved, Toast.LENGTH_LONG).show();
                        mEditMode = !mEditMode;
                        onCustomerChangeUpdate.execute(ccc);
                        setupToolbar();
                    } else {
                        JSONArray order_line = new JSONArray();
                        JSONArray o_line = new JSONArray();
                        o_line.put(6);
                        o_line.put(false);

                        values.put("origin", "-");
                        values.put("state", "request");

                        TechnicTire tire = new TechnicTire(this, null);

                        List line_data = new ArrayList();
                        List<Integer> order_line1 = new ArrayList<>();
                        List<ODataRow> order_line2 = new ArrayList<>();
                        for (ODataRow row : tireLines) {
                            Log.i("row==", row.toString());
                            line_data.add(row.getInt("id"));
                            order_line1.add(row.getInt("_id"));
                            Log.i("IDSDSDSDSDSD==", row.getInt("_id") + "");
                        }
                        o_line.put(line_data);
//                        order_line.put(o_line);
                        values.put("tire_ids", order_line1);
                        Log.i("values===", values.toString());
                        int row_id = scrapTires.insert(values);

                        if (row_id != scrapTires.INVALID_ROW_ID) {
                            onCustomerChangeUpdate.execute(ccc);
//                            OnCustomerChangeUpdate.execute(getSystemService());
                            Toast.makeText(this, "AMJILTTAI", Toast.LENGTH_LONG).show();
                            finish();
//                            ODataRow record = new ODataRow();
//                            record.put("id", new_id);
//                            record.put("id", new_id);
//                            scrapTires.quickCreateRecord(record);
                        }
                    }
                }
//                finishAndRemoveTask();
                break;
//                Intent intent = new Intent(this, TechnicsInspectionSignature.class);
//                startActivityForResult(intent, 1);
//                OValues values = mForm.getValues();
//                if (values != null) {
//                    CreateAttachments createAttachments = new CreateAttachments();
//                    createAttachments.execute(attachments);
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
//                }
//                break;
            case R.id.menu_cancel:
            case R.id.menu_edit:
                if (hasRecordInExtra()) {
                    mEditMode = !mEditMode;
                    setMode(mEditMode);
                    mForm.setEditable(mEditMode);
                    mForm.initForm(record);
                    getTire();
                } else {
                    finish();
                }
                break;
            case R.id.menu_delete:
                OAlert.showConfirm(this, OResource.string(this,
                        R.string.confirm_are_you_sure_want_to_delete),
                        new OAlert.OnAlertConfirmListener() {
                            @Override
                            public void onConfirmChoiceSelect(OAlert.ConfirmType type) {
                                if (type == OAlert.ConfirmType.POSITIVE) {
                                    // Deleting record and finishing activity if success.
                                    if (scrapTires.delete(record.getInt(OColumn.ROW_ID))) {
                                        Toast.makeText(ScrapTireDetails.this, R.string.toast_record_deleted,
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

    private class OnCustomerChangeUpdate extends AsyncTask<ODomain, Void, Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(ODomain... params) {
            ODomain aa = params[0];
            scrapTires.quickSyncRecords(aa);
            //required 2 call
            scrapTires.quickSyncRecords(aa);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
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
        if (field.getFieldName().equals("technic_id")) {
            tireLines.clear();
            TireObjects.clear();
            TireObjects.addAll(tireLines);
            mAdapter.notifyDataSetChanged(TireObjects);
            technisTire(row);
        }
    }

    public void inspection_items(ODataRow rows) {
        try {
//            List<ODataRow> val = norm_obj.select(new String[]{"inspection_pack_id"}, "inspection_type_id = ? and norm_id = ?", new String[]{"" + rows.getInt(OColumn.ROW_ID), "" + technicIns.getTechnicNorm()});
            if (true) {
//                ODataRow result = norm_obj.select(new String[]{"inspection_pack_id"}, "inspection_type_id = ? and norm_id = ?", new String[]{"" + rows.getInt(OColumn.ROW_ID), "" + technicIns.getTechnicNorm()}).get(0);
//                ODataRow inspectionPack = insectionPack.select(new String[]{"inspection_items"}, "id = ? ", new String[]{result.getString("inspection_pack_id")}).get(0);
//                resultInspectionPack = insectionPack.selectManyToManyRecords(new String[]{"name", "inspection_category_id"}, "inspection_items", inspectionPack.getInt("id"));
//                lines = new ArrayList<>();
//                for (ODataRow row : resultInspectionPack) {
//                    ODataRow newRow = new ODataRow();
//                    newRow.put("item_name", row.getString("name"));
//                    newRow.put("technic_inspection_category_id", row.get("inspection_category_id"));
//                    if (!row.get("inspection_category_id").equals("false")) {
//                        ODataRow inspectionCateg = inspectionCategory.select(new String[]{"name"}, "_id = ? ", new String[]{row.getString("inspection_category_id")}).get(0);
//                        newRow.put("categ_name", inspectionCateg.getString("name"));
//                    } else {
//                        newRow.put("categ_name", "Хоосон");
//                    }
//                    newRow.put(("technic_inspection_item_id"), row.get("_id"));
////                    newRow.put("inspection_id", row.get("id"));
//                    newRow.put("inspection_isitnormal", true);
//                    newRow.put("inspection_check", false);
//                    newRow.put("description", "");
//                    lines.add(newRow);
//                }
//                initAdapter();
            }
        } catch (
                Exception e)

        {
            e.printStackTrace();
        }

    }

    class CardViewMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        private int position;

        public CardViewMenuItemClickListener(int positon) {
            this.position = positon;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.menu_delete:
                    tireLines.remove(position);
                    TireObjects.remove(position);
                    mAdapter.notifyDataSetChanged(TireObjects);
                    return true;
                default:
            }
            return false;
        }
    }

    class TireImageMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        private int position;

        public TireImageMenuItemClickListener(int positon) {
            this.position = positon;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            images.remove(position);
            mAdapter.notifyDataSetChanged(TireObjects);
            return true;
        }
    }

    private void showPopupMenu(View view, int position) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.card_view_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new CardViewMenuItemClickListener(position));
        popup.show();
    }

    private void TireShowPopupMenu(View view, int position) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.card_view_menu, popup.getMenu());
        popup.getMenu().clear();
        popup.getMenu().add("Зураг устгах");
        popup.setOnMenuItemClickListener(new TireImageMenuItemClickListener(position));
        popup.show();
    }


    private void getTire() {
        if (extra != null && record != null) {

//            List<ODataRow> crapTire = scrapTires.select(new String[]{"tires"}, "_id = ? ", new String[]{record.getString("technic_id")});

            List<ODataRow> tech = technic.select(new String[]{"tires"}, "_id = ? ", new String[]{record.getString("technic_id")});
            if (tech.size() > 0) {
                ODataRow techs = technic.select(new String[]{"tires"}, "_id = ? ", new String[]{record.getString("technic_id")}).get(0);
                Log.i("techs====", techs.toString());
//                tireLines = techs.getO2MRecord("tires").browseEach();
                tireLines = record.getO2MRecord("tire_ids").browseEach();
                Log.i("tire_o2m get val ===", tireLines.toString());
            }
        }
        if (tireLines != null) {
            TireObjects.clear();
            TireObjects.addAll(tireLines);

            final int template = R.layout.technic_inspection_tire_row;
            mAdapter = mList.getAdapter(template, TireObjects,
                    new ExpandableListControl.ExpandableListAdapterGetViewListener() {
                        @Override
                        public View getView(final int position, View mView, ViewGroup parent) {
                            if (mView == null) {
                                mView = getLayoutInflater().inflate(template, parent, false);
                            }
                            EditText serial = (EditText) mView.findViewById(R.id.serial);
                            serial.setEnabled(false);
                            TextView name = (TextView) mView.findViewById(R.id.name);
                            ImageButton mImageButton = (ImageButton) mView.findViewById(R.id.btn_detail);
                            mImageButton.setVisibility(mEditMode ? View.VISIBLE : View.GONE);
                            mImageButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showPopupMenu(view, position);
                                }
                            });
                            TextView date_record = (TextView) mView.findViewById(R.id.date_record);
//                            TextView tread_depreciation_percent = (TextView) mView.findViewById(R.id.tread_depreciation_percent);
                            TextView current_position = (TextView) mView.findViewById(R.id.current_position);
                            TextView state = (TextView) mView.findViewById(R.id.state);
                            ImageView tireImage = (ImageView) mView.findViewById(R.id.tireImage);
                            FloatingActionButton captureImageTire = (FloatingActionButton) mView.findViewById(R.id.captureImageTire);
                            captureImageTire.setVisibility(mEditMode ? View.VISIBLE : View.GONE);

                            ODataRow row = (ODataRow) mAdapter.getItem(position);
                            name.setText((position + 1) + ". " + row.getString("name"));
                            date_record.setText(row.getString("date_record"));
//                            tread_depreciation_percent.setText(row.getString("tread_depreciation_percent"));
                            current_position.setText(row.getString("current_position"));
                            serial.setText(row.getString("serial"));
                            state.setText(row.getString("state"));
                            pos = position;
                            if (images.get(pos) != null) {
                                tireImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                tireImage.setColorFilter(null);
                                tireImage.setImageBitmap(images.get(pos));
                                tireImage.setVisibility(View.VISIBLE);
                            }
                            tireImage.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    TireShowPopupMenu(v, position);
                                    return true;
                                }
                            });
                            captureImageTire.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    pos = position;
                                    fileManager.requestForFile(OFileManager.RequestType.IMAGE_OR_CAPTURE_IMAGE);
                                }
                            });
                            return mView;
                        }

//                        @Override
//                        public void onBindViewHolder(final MyViewHolder holder, int position) {
//                            Album album = albumList.get(position);
//                            holder.title.setText(album.getName());
//                            holder.count.setText(album.getNumOfSongs() + " songs");
//
//                            // loading album cover using Glide library
//                            Glide.with(mContext).load(album.getThumbnail()).into(holder.thumbnail);
//
//                            holder.overflow.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    showPopupMenu(holder.overflow);
//                                }
//                            });
//                        }
                    });
            TireObjects.clear();
            TireObjects.addAll(tireLines);
            Log.i("TireObjects========", TireObjects.toString());
            mAdapter.notifyDataSetChanged(TireObjects);
        }
    }

    public void technisTire(ODataRow rows) {
        try {
            TechnicTire tire = new TechnicTire(this, null);
            List<ODataRow> tireRow = tire.select(new String[]{"name", "date_record", "current_position", "serial", "state"}, "technic_id = ? ", new String[]{rows.getString("_id")});
            if (tireRow.size() > 0) {
                for (ODataRow row : tireRow) {
                    Log.i("tire_row====", row.toString());
                    ODataRow newRow = new ODataRow();
                    newRow.put("_id", row.getString("_id"));
                    newRow.put("id", row.getString("id"));
                    newRow.put("name", row.getString("name"));
                    newRow.put("date_record", row.getString("date_record"));
                    newRow.put("current_position", row.getString("current_position"));
                    newRow.put("serial", row.getString("serial"));
                    newRow.put("state", row.getString("state"));
                    tireLines.add(newRow);
                }
                getTire();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        OValues values = fileManager.handleResult(requestCode, resultCode, data);
        if (values != null && !values.contains("size_limit_exceed")) {
            newImage = values.getString("datas");
            Bitmap image = BitmapUtils.getBitmapImage(this, newImage);
            images.put(pos, image);
            getTire();
            Log.i("images=======", images.toString());
        } else if (values != null) {
            Toast.makeText(this, R.string.toast_image_size_too_large, Toast.LENGTH_LONG).show();
        }
    }

    private class CreateAttachments extends AsyncTask<List<OValues>, Void, List<Integer>> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ScrapTireDetails.this);
            progressDialog.setTitle(com.odoo.R.string.title_working);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage("Uploading attachments...");
            progressDialog.setCancelable(false);
            progressDialog.setProgress(1);
            progressDialog.show();
        }

        @Override
        protected List<Integer> doInBackground(final List<OValues>... params) {
            try {
                List<Integer> ids = new ArrayList<Integer>();
                for (final OValues value : params[0]) {
                    boolean isImage = (value.getString("file_type").contains("image"));
                    if (value.getString("name").contains("signature"))
                        value.put("datas", value.getString("datas"));
                    else
                        value.put("datas", BitmapUtils.uriToBase64(Uri.parse(value.getString("file_uri")), getContentResolver(), isImage));
                    value.put("res_model", scrapTires.getModelName());
//                    TireScrapId=====!!!! hooson bgaa
                    value.put("res_id", TireScrapId);
                    ORecordValues data = IrAttachment.valuesToData(irAttachment, value);
                    if (data != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.setProgress(params[0].indexOf(value) + 1);
                            }
                        });
                        int newId = irAttachment.getServerDataHelper().createOnServer(data);
                        value.put("id", newId);
                        irAttachment.createAttachment(value, scrapTires.getModelName(), 0);
                        ids.add(newId);
                    }
                }
                return ids;
            } catch (Exception e) {
                Log.e(TAG, "ERROR", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Integer> ids) {
            super.onPostExecute(ids);
            progressDialog.dismiss();
            ScrapTireDetails.this.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(ScrapTireDetails.this, "Successful :)", Toast.LENGTH_LONG).show();
                }
            });
            finish();
        }
    }
}
