package mn.odoo.addons.scrapTire;

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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.odoo.App;
import com.odoo.R;
import com.odoo.addons.scrapTire.models.ScrapTires;
import com.odoo.addons.scrapTire.models.TechnicTire;
import com.odoo.addons.technic.models.TechnicsModel;
import com.odoo.base.addons.ir.feature.OFileManager;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.support.OdooCompatActivity;
import com.odoo.core.utils.BitmapUtils;
import com.odoo.core.utils.OAlert;
import com.odoo.core.utils.ODateUtils;
import com.odoo.core.utils.OResource;

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
    private OField oState, oOrigin, date, technicId, isPaybale;
    private ODataRow record = null;
    private Boolean mEditMode = false;
    private Menu mMenu;
    private TechnicsModel technic;
    private TechnicTire technicTire;
    private ScrapTires scrapTires;
    private ExpandableListControl mList;
    private ExpandableListControl.ExpandableListAdapter mAdapter;
    private List<Object> TireObjects = new ArrayList<>();
    private List<ODataRow> tireLines = new ArrayList<>();
    private List<ODataRow> tireRow = new ArrayList<>();
    private Toolbar toolbar;
    private OFileManager fileManager;
    private HashMap<Integer, String> tireImages = new HashMap<>();
    private int TireLocalId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scrap_tire_detail);

        extra = getIntent().getExtras();
        toolbar = (Toolbar) findViewById(R.id.toolbarScrapTire);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mEditMode = (!hasRecordInExtra() ? true : false);
        technic = new TechnicsModel(this, null);
        scrapTires = new ScrapTires(this, null);
        technicTire = new TechnicTire(this, null);
        fileManager = new OFileManager(this);

        mList = (ExpandableListControl) findViewById(R.id.ExpandListTireLine);
        mForm = (OForm) findViewById(R.id.OFormTireScrap);
        mForm.setModel("tire.scrap");

        oState = (OField) mForm.findViewById(R.id.StateTireScrap);
        oOrigin = (OField) mForm.findViewById(R.id.OriginTireScrap);
        date = (OField) mForm.findViewById(R.id.DateTireScrap);
        technicId = (OField) mForm.findViewById(R.id.TechnicTireScrap);
        isPaybale = (OField) mForm.findViewById(R.id.IsPayableTireScrap);
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
            OField technicField = (OField) findViewById(R.id.TechnicTireScrap);
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
            ((OField) mForm.findViewById(R.id.DateTireScrap)).setValue(ODateUtils.getDate());
            setMode(mEditMode);
        } else {
            setTitle("Дугуйн акт дэлгэрэнгүй");
            int ScrapId = extra.getInt(OColumn.ROW_ID);
            record = scrapTires.browse(ScrapId);
            mForm.initForm(record);
            mForm.setEditable(mEditMode);
            setMode(mEditMode);
            tireLines = record.getO2MRecord("tire_ids").browseEach();
            setTireImage(tireLines);
        }
    }

    private void setTireImage(List<ODataRow> lines) {
        for (ODataRow row : lines) {
            if (!row.getString("tire_image").equals("false")) {
                tireImages.put(row.getInt("_id"), row.getString("tire_image"));
            }
        }
        getTire();
    }

    private void setTireImage(ODataRow row) {
        TireLocalId = row.getInt("_id");
        tireImages.put(TireLocalId, row.getString("tire_image"));
        getTire();
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
        OnTireScrapChangeUpdate onTireScrapChangeUpdate = new OnTireScrapChangeUpdate();
        ODomain domain = new ODomain();
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_save:
                OValues values = mForm.getValues();
                if (values != null) {
                    List<Integer> TireIds = new ArrayList<>();
                    if (record != null) {
                        for (ODataRow row : tireLines) {
                            OValues tireImage = new OValues();
                            int tireId = row.getInt("_id");
                            TireIds.add(tireId);
                            if (tireImages.get(tireId) != null) {
                                tireImage.put("tire_image", tireImages.get(tireId));
                            } else {
                                tireImage.put("tire_image", false);
                            }
                            technicTire.update(tireId, tireImage);
                        }
                        values.put("tire_ids", TireIds);
                        scrapTires.update(record.getInt(OColumn.ROW_ID), values);
                        onTireScrapChangeUpdate.execute(domain);
                        mEditMode = !mEditMode;
                        mForm.setEditable(mEditMode);
                        setMode(mEditMode);
                        Toast.makeText(this, R.string.tech_toast_information_saved, Toast.LENGTH_LONG).show();
                    } else {
                        for (ODataRow row : tireLines) {
                            /*tire line like [6,false,[1,2,3,..]]*/
                            OValues tireImage = new OValues();
                            int tireId = row.getInt("_id");
                            TireIds.add(tireId);
                            if (tireImages.get(tireId) != null) {
                                tireImage.put("tire_image", tireImages.get(tireId));
                            }
                            tireImage.put("in_scrap", true);
                            technicTire.update(tireId, tireImage);
                        }
                        values.put("tire_ids", TireIds);
                        int row_id = scrapTires.insert(values);
                        if (row_id != scrapTires.INVALID_ROW_ID) {
                            onTireScrapChangeUpdate.execute(domain);
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
                    /*tire line buttons show*/
                    getTire();
                }
                break;
            case R.id.menu_delete:
                OAlert.showConfirm(this, OResource.string(this,
                        R.string.to_delete),
                        new OAlert.OnAlertConfirmListener() {
                            @Override
                            public void onConfirmChoiceSelect(OAlert.ConfirmType type) {
                                if (type == OAlert.ConfirmType.POSITIVE) {
                                    if (scrapTires.delete(record.getInt(OColumn.ROW_ID))) {
                                        Toast.makeText(ScrapTireDetails.this, R.string.tech_toast_information_deleted,
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

    private class OnTireScrapChangeUpdate extends AsyncTask<ODomain, Void, Void> {

        @Override
        protected Void doInBackground(ODomain... params) {
            ODomain domain = params[0];
            scrapTires.quickSyncRecords(domain);
            //required 2 call
            scrapTires.quickSyncRecords(domain);
            return null;
        }
    }

    @Override
    public void onFieldValueChange(OField field, Object value) {
        if (record == null && field.getFieldName().equals("technic_id")) {
            technisTire((ODataRow) value);
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
        private int key;

        public TireImageMenuItemClickListener(int positon) {
            this.key = positon;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            tireImages.remove(key);
            getTire();
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

    private void getTire() {
        if (tireLines != null) {
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
                            mImageButton.setVisibility(mEditMode && record == null ? View.VISIBLE : View.GONE);
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

                            final ODataRow row = (ODataRow) mAdapter.getItem(position);
                            Log.i("row====ss", row.toString());
                            TireLocalId = row.getInt("_id");
                            name.setText((position + 1) + ". " + row.getString("name"));
                            date_record.setText(row.getString("date_record"));
//                            tread_depreciation_percent.setText(row.getString("tread_depreciation_percent"));
                            current_position.setText(row.getString("current_position"));
                            serial.setText(row.getString("serial"));
                            state.setText(row.getString("state"));
                            if (tireImages.get(TireLocalId) != null) {
                                tireImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                tireImage.setColorFilter(null);
//                                Matrix matrix = new Matrix();
//                                matrix.postRotate(90);
                                Bitmap image = BitmapUtils.getBitmapImage(ScrapTireDetails.this, tireImages.get(TireLocalId));
                                Bitmap scaledBitmap = Bitmap.createScaledBitmap(image, 256, 160, true);//screen resolution 16:10
//                                Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
                                tireImage.setImageBitmap(scaledBitmap);
                                tireImage.setVisibility(View.VISIBLE);
                            }
                            if (mEditMode) {
                                tireImage.setOnLongClickListener(new View.OnLongClickListener() {
                                    @Override
                                    public boolean onLongClick(View v) {
                                        TireLocalId = row.getInt("_id");
                                        TireShowPopupMenu(v, TireLocalId);
                                        return true;
                                    }
                                });
                            }
                            captureImageTire.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    TireLocalId = row.getInt("_id");
                                    fileManager.requestForFile(OFileManager.RequestType.IMAGE_OR_CAPTURE_IMAGE);
                                }
                            });
                            return mView;
                        }
                    });
        }
        TireObjects.clear();
        TireObjects.addAll(tireLines);
        mAdapter.notifyDataSetChanged(TireObjects);
    }

    public void technisTire(ODataRow row) {
        try {
            updateTire(row.getString("_id"));
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

    private void updateTire(String technic_id) {
        tireRow = technicTire.select(new String[]{"name", "date_record", "current_position", "serial", "state", "tire_image"}, "technic_id = ? and in_scrap = ? ", new String[]{technic_id, "false"});
        tireLines.clear();
        if (tireRow.size() > 0) {
            for (ODataRow row : tireRow) {
                ODataRow newRow = new ODataRow();
                newRow.put("_id", row.getString("_id"));
                newRow.put("id", row.getString("id"));
                newRow.put("name", row.getString("name"));
                newRow.put("date_record", row.getString("date_record"));
                newRow.put("current_position", row.getString("current_position"));
                newRow.put("serial", row.getString("serial"));
                newRow.put("state", row.getString("state"));
                newRow.put("tire_image", row.getString("tire_image"));
                tireLines.add(newRow);
            }
        }
        setTireImage(tireLines);
    }

    private class OnTechnicSync extends AsyncTask<List<Object>, Void, Void> {
        private String technicId;
        App app = (App) getApplicationContext();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            findViewById(R.id.TireScrapProgress).setVisibility(View.VISIBLE);
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
            findViewById(R.id.TireScrapProgress).setVisibility(View.GONE);
            if (app.inNetwork()) {
                updateTire(technicId);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        OValues values = fileManager.handleResult(requestCode, resultCode, data);
        if (values != null && !values.contains("size_limit_exceed")) {
            String CapturedImage = values.getString(("datas"));
            ODataRow row = new ODataRow();
            row.put("_id", TireLocalId);
            row.put("tire_image", CapturedImage);
            setTireImage(row);
        } else if (values != null) {
            Toast.makeText(this, R.string.toast_image_size_too_large, Toast.LENGTH_LONG).show();
        }
    }
}