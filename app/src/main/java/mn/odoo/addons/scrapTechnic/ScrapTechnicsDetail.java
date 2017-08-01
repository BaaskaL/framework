package mn.odoo.addons.scrapTechnic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.odoo.App;
import com.odoo.R;
import com.odoo.addons.scrapTechnic.models.ScrapTechnic;
import com.odoo.addons.scrapTechnic.models.TechnicScrapPhoto;
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
import java.util.List;

import odoo.controls.OField;
import odoo.controls.OForm;

/**
 * Created by baaska on 7/30/17.
 */

public class ScrapTechnicsDetail extends OdooCompatActivity implements View.OnClickListener, BaseSliderView.OnSliderClickListener {

    public static final String TAG = ScrapTechnicsDetail.class.getSimpleName();
    private Bundle extra;
    private OForm mForm;
    private OField oState, date, technicId;
    private ODataRow record = null;
    private Boolean mEditMode = false;
    private Menu mMenu;
    private TechnicsModel technic;
    private ScrapTechnic scrapTechnic;
    private TechnicScrapPhoto technicScrapPhoto;
    private Toolbar toolbar;
    private OFileManager fileManager;
    App app;
    /*picture*/
    private SliderLayout mSlider;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    public List<ODataRow> recScrapTechImages = new ArrayList<>();
    private List<TextSliderView> textSlider = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scrap_technic_detail);

        extra = getIntent().getExtras();

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.scrap_technic_collapsing_toolbar);
        mSlider = (SliderLayout) findViewById(R.id.slider);
        toolbar = (Toolbar) findViewById(R.id.toolbarTechnicScrap);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEditMode = (!hasRecordInExtra() ? true : false);
        technic = new TechnicsModel(this, null);
        scrapTechnic = new ScrapTechnic(this, null);
        technicScrapPhoto = new TechnicScrapPhoto(this, null);
        fileManager = new OFileManager(this);
        app = (App) getApplicationContext();


        mForm = (OForm) findViewById(R.id.OFormTechnicScrap);

        oState = (OField) mForm.findViewById(R.id.stateTechnicScrap);
        date = (OField) mForm.findViewById(R.id.dateTechnicScrap);
        technicId = (OField) mForm.findViewById(R.id.technicTechnicScrap);

        TextSliderView textSliderView = new TextSliderView(this);
        textSliderView.description("Default")
                .image(R.drawable.user_xlarge)
                .setScaleType(BaseSliderView.ScaleType.CenterInside);
        mSlider.addSlider(textSliderView);
        mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mSlider.stopAutoCycle();
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
        oState.setEditable(false);
        findViewById(R.id.scrapCaptureImage).setVisibility(View.GONE);
        if (edit) {
            findViewById(R.id.scrapCaptureImage).setOnClickListener(this);
            findViewById(R.id.scrapCaptureImage).setVisibility(View.VISIBLE);
        }
        if (record != null) {
            date.setEditable(false);
            technicId.setEditable(false);
        }
    }

    private void setupToolbar() {
        if (!hasRecordInExtra()) {
            setTitle("Үүсгэх");
            mForm.setEditable(mEditMode);
            mForm.initForm(null);
            ((OField) mForm.findViewById(R.id.dateTechnicScrap)).setValue(ODateUtils.getDate());
        } else {
            OnTechnicScrapImageSync imageSync = new OnTechnicScrapImageSync();
            setTitle("Техник акт дэлгэрэнгүй");
            int ScrapId = extra.getInt(OColumn.ROW_ID);
            record = scrapTechnic.browse(ScrapId);
            recScrapTechImages = record.getO2MRecord("scrap_photos").browseEach();
            if (recScrapTechImages.size() > 0) {
                imageSync.execute(recScrapTechImages);
            }
            mForm.initForm(record);
            mForm.setEditable(mEditMode);
        }
        setMode(mEditMode);
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
        OnTechnicScrapChangeUpdate onTechnicScrapChangeUpdate = new OnTechnicScrapChangeUpdate();
        ODomain domain = new ODomain();
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_save:
                List<Integer> photoIds = new ArrayList<>();
                OValues values = mForm.getValues();
                if (values != null) {
                    if (record != null) {
                        for (ODataRow row : recScrapTechImages) {
                            if (row.getString("scrap_id").equals("false")) {
                                OValues photo = new OValues();
                                photo.put("scrap_id", record.getInt(OColumn.ROW_ID));
                                photo.put("photo", row.get("photo"));
                                technicScrapPhoto.insert(photo);
                            }
                        }
                        scrapTechnic.update(record.getInt(OColumn.ROW_ID), values);
                        onTechnicScrapChangeUpdate.execute(domain);
                        mEditMode = !mEditMode;
                        mForm.setEditable(mEditMode);
                        setMode(mEditMode);
                        Toast.makeText(this, R.string.tech_toast_information_saved, Toast.LENGTH_LONG).show();
                    } else {
                        for (ODataRow row : recScrapTechImages) {
                            OValues photo = new OValues();
                            photo.put("photo", row.get("photo"));
                            int newId = technicScrapPhoto.insert(photo);
                            photoIds.add(newId);
                        }
                        ODataRow techObj = technic.browse(values.getInt("technic"));
                        values.put("technic_name", techObj.get("name"));
                        values.put("scrap_photos", photoIds);
                        int row_id = scrapTechnic.insert(values);
                        if (row_id != scrapTechnic.INVALID_ROW_ID) {
                            onTechnicScrapChangeUpdate.execute(domain);
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
                                    if (scrapTechnic.delete(record.getInt(OColumn.ROW_ID))) {
                                        Toast.makeText(ScrapTechnicsDetail.this, R.string.tech_toast_information_deleted,
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
            case R.id.scrapCaptureImage:
                fileManager.requestForFile(OFileManager.RequestType.IMAGE_OR_CAPTURE_IMAGE);
                break;
        }
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

    }

    private class OnTechnicScrapChangeUpdate extends AsyncTask<ODomain, Void, Void> {

        @Override
        protected Void doInBackground(ODomain... params) {

            if (app.inNetwork()) {
                ODomain domain = params[0];
                List<ODataRow> rows = scrapTechnic.select(null, "id = ?", new String[]{"0"});
                for (ODataRow row : rows) {
                    scrapTechnic.quickCreateRecord(row);
                }
                /*Бусад бичлэгүүдийг update хийж байна*/
                scrapTechnic.quickSyncRecords(domain);
            }
            return null;
        }
    }

    class technicImageMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        private int key;

        public technicImageMenuItemClickListener(int positon) {
            this.key = positon;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
//            oilImages.remove(key);
            return true;
        }
    }

    private void technicScrapShowPopupMenu(View view, int position) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.card_view_menu, popup.getMenu());
        popup.getMenu().clear();
        popup.getMenu().add("Зураг устгах");
        popup.setOnMenuItemClickListener(new technicImageMenuItemClickListener(position));
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

    private class OnTechnicScrapImageSync extends AsyncTask<List<ODataRow>, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(List<ODataRow>... params) {
            try {
                for (ODataRow row : params[0]) {
                    Bitmap img = BitmapUtils.getBitmapImage(ScrapTechnicsDetail.this, row.getString("photo"));
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(img, 2560, 1600, true);//screen resolution 16:10
                    String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), scaledBitmap, "Title", null);
                    Uri tempUri = Uri.parse(path);
                    TextSliderView textSliderView = new TextSliderView(ScrapTechnicsDetail.this);
                    textSliderView.description("ZZZZZ")
                            .image(tempUri)
                            .setScaleType(BaseSliderView.ScaleType.CenterInside)
                            .setOnSliderClickListener(ScrapTechnicsDetail.this);
                    textSlider.add(textSliderView);
                }
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mSlider.removeAllSliders();
            for (TextSliderView slide : textSlider) {
                mSlider.addSlider(slide);
            }
            mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            mSlider.startAutoCycle();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        OValues values = fileManager.handleResult(requestCode, resultCode, data);
        if (values != null && !values.contains("size_limit_exceed")) {
            String newImage = values.getString("datas");
            ODataRow image = new ODataRow();
            image.put("photo", newImage);
            recScrapTechImages.add(image);

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
            if (mSlider.getCurrentSlider().getDescription().equals("Default")) {
                mSlider.removeSliderAt(0);
            }
            mSlider.addSlider(textSliderView);
            mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            mSlider.startAutoCycle();
        } else if (values != null) {
            Toast.makeText(this, R.string.toast_image_size_too_large, Toast.LENGTH_LONG).show();
        }
    }
}