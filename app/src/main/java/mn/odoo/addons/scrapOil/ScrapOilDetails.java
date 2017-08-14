package mn.odoo.addons.scrapOil;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.odoo.App;
import com.odoo.R;
import com.odoo.addons.scrapOil.models.ScrapOils;
import com.odoo.addons.scrapOil.models.ShTMScrapPhotos;
import com.odoo.addons.scrapOil.models.TechnicOil;
import com.odoo.addons.technic.models.TechnicsModel;
import com.odoo.base.addons.ir.feature.OFileManager;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mn.odoo.addons.otherClass.AddItemLineWizard;
import mn.odoo.addons.otherClass.OilDetailsWizard;
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
    private List<Object> oilObjects = new ArrayList<>();
    private List<ODataRow> oilLines = new ArrayList<>();
    private List<ODataRow> technicOilLine = new ArrayList<>();
    private List<ODataRow> oilRow = new ArrayList<>();
    private Toolbar toolbar;
    private OFileManager fileManager;
    private HashMap<Integer, String> oilImages = new HashMap<>();
    private LinearLayout layoutAddItem = null;
    private int oilLocalId;
    /*Зүйлс оруулж ирэх*/
    private HashMap<String, Boolean> lineValues = new HashMap<>();
    private HashMap<String, Integer> lineIds = new HashMap<>();
    private List<Object> objects = new ArrayList<>();
    public static final int REQUEST_ADD_ITEMS = 323;
//    private HashMap<String, Float> lineValues = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scrap_oil_detail);

        extra = getIntent().getExtras();
        toolbar = (Toolbar) findViewById(R.id.toolbarScrapOil);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEditMode = (!hasRecordInExtra() ? true : false);
        technic = new TechnicsModel(this, null);
        technicOil = new TechnicOil(this, null);
        shTMScrapPhotos = new ShTMScrapPhotos(this, null);
        scrapOil = new ScrapOils(this, null);
        fileManager = new OFileManager(this);

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
        initAdapter();
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
            OField technicField = (OField) findViewById(R.id.TechnicOilScrap);
            technicField.setOnValueChangeListener(this);
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
//            oilLines = record.getO2MRecord("oil_photos").browseEach();
            int technic_id = record.getInt("technic_id");
            ODataRow techRecord = technic.browse(technic_id);
            technicOilLine = techRecord.getO2MRecord("oils").browseEach();
            oilLines = record.getM2MRecord("oil_ids").browseEach();
            setOilImage(oilLines);
        }
        setMode(mEditMode);
    }

    private void initAdapter() {
        if (extra != null && record != null) {
            for (ODataRow line : technicOilLine) {
                lineValues.put(line.getString("_id"), false);
            }
            Log.i("lineValues==", lineValues.toString());
            for (ODataRow line : oilLines) {
                if (lineValues.containsKey(line.getString("_id"))) {
                    lineValues.put(line.getString("_id"), true);
                }
            }
            Log.i("lineValues==2222", lineValues.toString());
        }
        Log.i("oilLines==2222", oilLines.toString());
        objects.clear();
        objects.addAll(oilLines);
        mAdapter = mList.getAdapter(R.layout.scrap_oil_oil_item, objects,
                new ExpandableListControl.ExpandableListAdapterGetViewListener() {
                    @Override
                    public View getView(final int position, View mView, ViewGroup parent) {
                        ODataRow row = (ODataRow) mAdapter.getItem(position);
                        OControls.setText(mView, R.id.name, (position + 1) + ". " + row.getString("name"));
                        OControls.setText(mView, R.id.date, row.getString("date"));
                        if (row.getString("date").equals("false"))
                            OControls.setText(mView, R.id.date, "Огноо");
                        OControls.setText(mView, R.id.capacity, row.getString("capacity"));
                        OControls.setText(mView, R.id.product, row.getString("product_name"));
                        OControls.setText(mView, R.id.reason, row.getString("reason_name"));
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

    private void setOilImage(List<ODataRow> lines) {
        for (ODataRow row : lines) {
            if (!row.getString("photo").equals("false")) {
                oilImages.put(row.getInt("shtm_id"), row.getString("photo"));
            }
        }
        getOil();
    }

    private void setOilImage(ODataRow row) {
        oilLocalId = row.getInt("_id");
        oilImages.put(oilLocalId, row.getString("oil_image"));
        getOil();
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
        OnOilScrapChangeUpdate onOilScrapChangeUpdate = new OnOilScrapChangeUpdate();
        ODomain domain = new ODomain();
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_save:
                OValues values = mForm.getValues();
                if (values != null) {
                    List<Integer> oilIds = new ArrayList<>();
                    if (record != null) {
                        for (ODataRow row : oilLines) {
                            OValues oilImage = new OValues();
                            int oilId = row.getInt("_id");
                            oilIds.add(oilId);
                            if (oilImages.get(oilId) != null) {
                                oilImage.put("tire_image", oilImages.get(oilId));
                            } else {
                                oilImage.put("tire_image", false);
                            }
                            technicOil.update(oilId, oilImage);
                        }
                        values.put("oil_ids", oilIds);
                        scrapOil.update(record.getInt(OColumn.ROW_ID), values);
                        onOilScrapChangeUpdate.execute(domain);
                        mEditMode = !mEditMode;
                        mForm.setEditable(mEditMode);
                        setMode(mEditMode);
                        Toast.makeText(this, R.string.tech_toast_information_saved, Toast.LENGTH_LONG).show();
                    } else {
                        for (ODataRow row : oilLines) {
                            /*oil line like [6,false,[1,2,3,..]]*/
                            OValues oilImage = new OValues();
                            int oilId = row.getInt("_id");
                            oilIds.add(oilId);
                            if (oilImages.get(oilId) != null) {
                                oilImage.put("tire_image", oilImages.get(oilId));
                            }
                            oilImage.put("in_scrap", true);
                            technicOil.update(oilId, oilImage);
                        }
                        values.put("oil_ids", oilIds);
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
                    /*Oil line buttons show*/
                    getOil();
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
                if (mForm.getValues() != null) {
                    Intent intent = new Intent(this, AddItemLineWizard.class);
                    Bundle extra = new Bundle();
                    for (String key : lineValues.keySet()) {
                        extra.putBoolean(key, lineValues.get(key));
                        AddItemLineWizard.mModel = technicOil;
                    }
                    intent.putExtras(extra);
                    startActivityForResult(intent, REQUEST_ADD_ITEMS);
                }
                break;
            case R.id.gridViewOilImage:

//            case R.id.ExpandListOilLine:
//                if (mForm.getValues() != null) {
//                    Intent intent = new Intent(this, OilDetailsWizard.class);
//                    Bundle extra = new Bundle();
//                    for (String key : lineValues.keySet()) {
//                        extra.putBoolean(key, lineValues.get(key));
//                    }
//                    intent.putExtras(extra);
//                    startActivityForResult(intent, REQUEST_ADD_ITEMS);
//
//                }
//                break;
        }
    }

    private void loadActivity(ODataRow row) {
        Intent intent = new Intent(this, OilDetailsWizard.class);
        Bundle extra = new Bundle();
        if (row != null) {
            extra = row.getPrimaryBundleData();
        }
        intent.putExtras(extra);
        startActivityForResult(intent, REQUEST_ADD_ITEMS);
    }

    private class OnOilScrapChangeUpdate extends AsyncTask<ODomain, Void, Void> {

        @Override
        protected Void doInBackground(ODomain... params) {
            ODomain domain = params[0];
            scrapOil.quickSyncRecords(domain);
            //required 2 call
            scrapOil.quickSyncRecords(domain);
            return null;
        }
    }

    @Override
    public void onFieldValueChange(OField field, Object value) {
        if (record == null && field.getFieldName().equals("technic_id")) {
            technisOil((ODataRow) value);
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
                    oilLines.remove(position);
                    oilObjects.remove(position);
                    mAdapter.notifyDataSetChanged(oilObjects);
                    return true;
                default:
            }
            return false;
        }
    }

    class oilImageMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        private int key;

        public oilImageMenuItemClickListener(int positon) {
            this.key = positon;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            oilImages.remove(key);
            getOil();
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

    private void oilShowPopupMenu(View view, int position) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.card_view_menu, popup.getMenu());
        popup.getMenu().clear();
        popup.getMenu().add("Зураг устгах");
        popup.setOnMenuItemClickListener(new oilImageMenuItemClickListener(position));
        popup.show();
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

    private void getOil() {
        if (oilLines != null) {
            Log.i("oilLines=====", oilLines.toString());
            final int template = R.layout.scrap_oil_oil_item;
            mAdapter = mList.getAdapter(template, oilObjects,
                    new ExpandableListControl.ExpandableListAdapterGetViewListener() {
                        @Override
                        public View getView(final int position, View mView, ViewGroup parent) {
                            if (mView == null) {
                                mView = getLayoutInflater().inflate(template, parent, false);
                            }
                            TextView product = (TextView) mView.findViewById(R.id.product);
                            TextView name = (TextView) mView.findViewById(R.id.name);
                            ImageButton mImageButton = (ImageButton) mView.findViewById(R.id.btn_detail);
                            mImageButton.setVisibility(mEditMode && record == null ? View.VISIBLE : View.GONE);
                            mImageButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    showPopupMenu(view, position);
                                }
                            });
                            TextView date = (TextView) mView.findViewById(R.id.date);
                            TextView capacity = (TextView) mView.findViewById(R.id.capacity);
                            TextView state = (TextView) mView.findViewById(R.id.state);
                            ImageView oilImage = (ImageView) mView.findViewById(R.id.oilImage);
                            FloatingActionButton captureImageTire = (FloatingActionButton) mView.findViewById(R.id.captureImageOil);
                            captureImageTire.setVisibility(mEditMode ? View.VISIBLE : View.GONE);

                            final ODataRow row = (ODataRow) mAdapter.getItem(position);
                            oilLocalId = row.getInt("_id");
                            name.setText((position + 1) + ". " + row.getString("name"));
                            date.setText(row.getString("date"));
                            capacity.setText(row.getString("capacity"));
                            product.setText(row.getString("product"));
                            state.setText(row.getString("state"));
                            if (oilImages.get(oilLocalId) != null) {
                                oilImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                oilImage.setColorFilter(null);
                                Bitmap image = BitmapUtils.getBitmapImage(ScrapOilDetails.this, oilImages.get(oilLocalId));
                                Bitmap scaledBitmap = Bitmap.createScaledBitmap(image, 256, 160, true);//screen resolution 16:10
                                oilImage.setImageBitmap(scaledBitmap);
                                oilImage.setVisibility(View.VISIBLE);
                            }
                            if (mEditMode) {
                                oilImage.setOnLongClickListener(new View.OnLongClickListener() {
                                    @Override
                                    public boolean onLongClick(View v) {
                                        oilLocalId = row.getInt("_id");
                                        oilShowPopupMenu(v, oilLocalId);
                                        return true;
                                    }
                                });
                            }
                            captureImageTire.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    oilLocalId = row.getInt("_id");
                                    fileManager.requestForFile(OFileManager.RequestType.IMAGE_OR_CAPTURE_IMAGE);
                                }
                            });
                            return mView;
                        }
                    });
        }
        oilObjects.clear();
        oilObjects.addAll(oilLines);
        mAdapter.notifyDataSetChanged(oilObjects);
    }

    public void technisOil(ODataRow row) {
        try {
            updateOil(row.getString("_id"));
            ODomain domain = new ODomain();
            domain.add("id", "=", row.getString("id"));
            OnTechnicSync sync = new OnTechnicSync();
            List<Object> params = new ArrayList<>();
            params.add(domain);
            params.add(row.getString("_id"));
            sync.execute(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateOil(String technic_id) {
        oilRow = technicOil.select(null, "technic_id = ? and in_scrap = ? ", new String[]{technic_id, "false"});
        oilLines.clear();
        if (oilRow.size() > 0) {
            for (ODataRow row : oilRow) {
                ODataRow newRow = new ODataRow();
                newRow.put("_id", row.getString("_id"));
                newRow.put("id", row.getString("id"));
                newRow.put("name", row.getString("name"));
                newRow.put("product_id", row.getString("product_id"));
                newRow.put("capacity", row.getString("capacity"));
                newRow.put("state", row.getString("state"));
                newRow.put("oil_image", row.getString("oil_image"));
                oilLines.add(newRow);
            }
        }
        setOilImage(oilLines);
    }

    private class OnTechnicSync extends AsyncTask<List<Object>, Void, Void> {
        private String technicId;
        App app = (App) getApplicationContext();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.oilScrapProgress).setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(List<Object>... params) {
            try {
                if (app.inNetwork()) {
                    Thread.sleep(500);
                    List parameter = params[0];
                    ODomain domain = (ODomain) parameter.get(0);
                    technic.quickSyncRecords(domain);
                    technicId = parameter.get(1).toString();
                }
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            findViewById(R.id.oilScrapProgress).setVisibility(View.GONE);
            if (app.inNetwork()) {
                updateOil(technicId);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ADD_ITEMS && resultCode == Activity.RESULT_OK) {
            oilLines.clear();
            for (String key : data.getExtras().keySet()) {
                Log.i("key===", key);
                Log.i("VAL===", "" + data.getExtras().getBoolean(key));
                if (data.getExtras().getBoolean(key)) {
                    oilLines.add(technicOil.select(null, "_id = ?", new String[]{key}).get(0));
                }
            }
            Log.i("oilLines===111", oilLines.toString());
            initAdapter();
        }
//        OValues values = fileManager.handleResult(requestCode, resultCode, data);
//        if (values != null && !values.contains("size_limit_exceed")) {
//            String CapturedImage = values.getString(("datas"));
//            ODataRow row = new ODataRow();
//            row.put("_id", oilLocalId);
//            row.put("oil_image", CapturedImage);
//            setOilImage(row);
//        } else if (values != null) {
//            Toast.makeText(this, R.string.toast_image_size_too_large, Toast.LENGTH_LONG).show();
//        }
    }
}