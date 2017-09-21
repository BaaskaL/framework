package mn.odoo.addons.scrapTechnic;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.odoo.App;
import com.odoo.R;
import com.odoo.addons.scrapTechnic.models.ScrapTechnic;
import com.odoo.addons.scrapTechnic.models.TechnicScrapPhoto;
import com.odoo.addons.technic.models.TechnicsModel;
import com.odoo.base.addons.ir.feature.OFileManager;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.RelValues;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.support.OdooCompatActivity;
import com.odoo.core.utils.OAlert;
import com.odoo.core.utils.ODateUtils;
import com.odoo.core.utils.OResource;

import java.util.ArrayList;
import java.util.List;

import mn.odoo.addons.otherClass.ImageFragmentAdapter;
import mn.odoo.addons.otherClass.InkPageIndicator;
import odoo.controls.OField;
import odoo.controls.OForm;

/**
 * Created by baaska on 7/30/17.
 */

public class ScrapTechnicsDetail extends OdooCompatActivity implements View.OnClickListener {

    public static final String TAG = ScrapTechnicsDetail.class.getSimpleName();
    private TechnicsModel technic;
    private ScrapTechnic scrapTechnic;
    private TechnicScrapPhoto technicScrapPhoto;

    private Bundle extra;
    private OForm mForm;
    private OField oState, date, technicId;
    private ODataRow record = null;
    private Boolean mEditMode = false;
    private Menu mMenu;
    private Toolbar toolbar;
    private OFileManager fileManager;
    App app;
    /*picture*/
    private ViewPager mPager;
    private ImageFragmentAdapter mAdapter;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    public List<ODataRow> recScrapTechImages = new ArrayList<>();
    private int ScrapId = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scrap_technic_detail);

        extra = getIntent().getExtras();
        ScrapId = extra.getInt(OColumn.ROW_ID);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.scrap_technic_collapsing_toolbar);
        mPager = (ViewPager) findViewById(R.id.pager);
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
            imageTheard(recScrapTechImages);
            mForm.initForm(null);
            ((OField) mForm.findViewById(R.id.dateTechnicScrap)).setValue(ODateUtils.getDate());
        } else {
            setTitle("Техникийн үзлэг дэлгэрэнгүй");
            record = scrapTechnic.browse(ScrapId);
            recScrapTechImages = record.getO2MRecord("scrap_photos").browseEach();
            imageTheard(recScrapTechImages);
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
                OValues values = mForm.getValues();
                if (values != null) {
                    List<OValues> imgValuene = new ArrayList();
                    for (int i = 0; i < mAdapter.getCount(); i++) {
                        ODataRow row = mAdapter.getRow(i);
                        if (row.getString("id").equals("0")) {
                            imgValuene.add(row.toValues());
                        }
                    }
                    values.put("scrap_photos", new RelValues().append(imgValuene.toArray(new OValues[imgValuene.size()])).delete(mAdapter.getDeleteIds()));
                    if (record != null) {
                        scrapTechnic.update(record.getInt(OColumn.ROW_ID), values);
                        onTechnicScrapChangeUpdate.execute(domain);
                        mEditMode = !mEditMode;
                        mForm.setEditable(mEditMode);
                        setMode(mEditMode);
                        Toast.makeText(this, R.string.tech_toast_information_saved, Toast.LENGTH_LONG).show();
                    } else {
                        values.put("technic_name", technic.browse(values.getInt("technic")).getString("name"));
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


    private void imageTheard(final List<ODataRow> rows) {
        new Thread(new Runnable() {
            public void run() {
                mAdapter = new ImageFragmentAdapter(getSupportFragmentManager(), rows);
                mPager.setAdapter(null);
                mPager.setAdapter(mAdapter);
                InkPageIndicator mIndicator = (InkPageIndicator) findViewById(R.id.indicator);
                mIndicator.setViewPager(mPager);
            }
        }).start();
    }

    /*private class OnTechnicScrapImageSync extends AsyncTask<List<ODataRow>, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(List<ODataRow>... params) {
            try {
                Thread.sleep(500);
                mAdapter = new ImageFragmentAdapter(getSupportFragmentManager(), params[0]);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mPager.setAdapter(null);
            mPager.setAdapter(mAdapter);
            InkPageIndicator mIndicator;
            mIndicator = (InkPageIndicator) findViewById(R.id.indicator);
            mIndicator.setViewPager(mPager);
        }
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        OValues values = fileManager.handleResult(requestCode, resultCode, data);
        if (values != null && !values.contains("size_limit_exceed")) {
            ODataRow row = new ODataRow();
            row.put("photo", values.getString("datas"));
            row.put("scrap_id", ScrapId);
            row.put("id", 0);
            if (!mAdapter.update(row)) {
                Toast.makeText(this, "Уг зураг аль хэдийн орсон байна!!!", Toast.LENGTH_LONG).show();
            }
        } else if (values != null) {
            Toast.makeText(this, R.string.toast_image_size_too_large, Toast.LENGTH_LONG).show();
        }
    }
}