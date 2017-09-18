package mn.odoo.addons.scrapAccumulator.wizards;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.odoo.App;
import com.odoo.R;
import com.odoo.addons.scrapAccumulator.Accumulator;
import com.odoo.addons.scrapAccumulator.ScrapAccumulatorPhotos;
import com.odoo.base.addons.ir.feature.OFileManager;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.RelValues;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.support.OdooCompatActivity;
import com.odoo.core.utils.BitmapUtils;
import com.odoo.core.utils.OAlert;
import com.odoo.core.utils.OResource;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import mn.odoo.addons.otherClass.DetailsActivity;
import mn.odoo.addons.otherClass.GridViewAdapter;
import odoo.controls.OField;
import odoo.controls.OForm;


public class AccumulatorDetailsWizard extends OdooCompatActivity implements View.OnClickListener {

    private Accumulator accumulator;
    private ScrapAccumulatorPhotos scrapAccumulatorPhotos;

    private Toolbar toolbar;
    private OField oReason;
    private Menu mMenu;
    private Boolean mEditMode = false;
    private Bundle extra;
    private ODataRow record = null;
    private OForm mForm;
    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private Context mContext;

    private Button takePic;
    private OFileManager fileManager;
    private ArrayList<String> imageItemsString = new ArrayList<>();
    private String scrap_id;
    private String scrap_name = "";
    private String rowId;
    App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extra = getIntent().getExtras();
        scrap_id = extra.getString("scrap_id");
        scrap_name = extra.getString("scrap_name");

        setTitle(scrap_name);
        rowId = String.valueOf(extra.getInt(OColumn.ROW_ID));
        setContentView(R.layout.accumulator_detail_wizard);
        setResult(RESULT_CANCELED);
        mContext = getApplicationContext();

        mForm = (OForm) findViewById(R.id.OFormAccumulatorScrapWizard);
        gridView = (GridView) findViewById(R.id.gridViewAccumImage);
        toolbar = (Toolbar) findViewById(R.id.toolbarOilWizard);
        oReason = (OField) mForm.findViewById(R.id.accumReason);
        takePic = (Button) findViewById(R.id.takePictureAccum);
        app = (App) getApplicationContext();

        fileManager = new OFileManager(this);
        accumulator = new Accumulator(this, null);
        scrapAccumulatorPhotos = new ScrapAccumulatorPhotos(this, null);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        record = accumulator.browse(Integer.parseInt(rowId));
        mForm.initForm(record);

        List<ODataRow> scrapPhotos = new ArrayList<>();
        scrapPhotos = scrapAccumulatorPhotos.select(null, "scrap_id = ? and accumulator_id = ?", new String[]{scrap_id, rowId});
        gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, scrapPhotos);
        gridView.setAdapter(gridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView image = (ImageView) view.findViewById(R.id.image);
                ODataRow accumRow = (ODataRow) gridAdapter.getItem(position);
                Bitmap item = BitmapUtils.getBitmapImage(mContext, accumRow.getString("photo"));
                Intent intent = new Intent(AccumulatorDetailsWizard.this, DetailsActivity.class);
                DetailsActivity.image = item;
                startActivity(intent);
            }
        });
    }

    private void ShowPopupMenu(View view, int position) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.card_view_menu, popup.getMenu());
        popup.getMenu().clear();
        popup.getMenu().add("Зураг устгах");
        popup.setOnMenuItemClickListener(new ImageMenuItemClickListener(position));
        popup.show();
    }

    class ImageMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        private int key;

        public ImageMenuItemClickListener(int positon) {
            this.key = positon;
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            gridAdapter.deleteContent(key);
            return true;
        }
    }

    private void setMode(Boolean edit) {
        ToolbarMenuSetVisibl(edit);
        oReason.setEditable(edit);
        if (edit) {
            takePic.setOnClickListener(this);
            gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    ShowPopupMenu(view, position);
                    return true;
                }
            });
        } else {
            takePic.setClickable(false);
            gridView.setLongClickable(false);
        }
    }

    private void ToolbarMenuSetVisibl(Boolean Visibility) {
        if (mMenu != null) {
            mMenu.findItem(R.id.menu_more).setVisible(!Visibility);
            mMenu.findItem(R.id.menu_edit).setVisible(!Visibility);
            mMenu.findItem(R.id.menu_save).setVisible(Visibility);
            mMenu.findItem(R.id.menu_cancel).setVisible(Visibility);
        }
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
        OnAccumuScrapPhotoChangeUpdate onAccumuScrapPhotoChangeUpdate = new OnAccumuScrapPhotoChangeUpdate();
        ODomain domain = new ODomain();
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_save:
                OValues values = mForm.getValues();
                if (values != null) {
                    if (record != null) {
                        List<OValues> imgValuene = new ArrayList();
                        for (int i = 0; i < gridAdapter.getCount(); i++) {
                            ODataRow row = (ODataRow) gridAdapter.getItem(i);
                            if (row.getString("id").equals("0")) {
                                imgValuene.add(row.toValues());
                            }
                        }
                        values.put("scrap_photos", new RelValues().append(imgValuene.toArray(new OValues[imgValuene.size()])).delete(gridAdapter.deleteIds));
                        accumulator.update(record.getInt(OColumn.ROW_ID), values);
                        onAccumuScrapPhotoChangeUpdate.execute(domain);
                        mEditMode = !mEditMode;
                        setMode(mEditMode);
                        Toast.makeText(this, R.string.tech_toast_information_saved, Toast.LENGTH_LONG).show();
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
                                    finish();
                                }
                            }
                        });
                break;
            case R.id.menu_edit:
                mEditMode = !mEditMode;
                setMode(mEditMode);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class OnAccumuScrapPhotoChangeUpdate extends AsyncTask<ODomain, Void, Void> {

        @Override
        protected Void doInBackground(ODomain... params) {
            if (app.inNetwork()) {
                ODomain domain = params[0];
                List<ODataRow> rows = scrapAccumulatorPhotos.select(null, "id = ?", new String[]{"0"});
                for (ODataRow row : rows) {
                    scrapAccumulatorPhotos.quickCreateRecord(row);
                }
                /*Бусад бичлэгүүдийг update хийж байна*/
                scrapAccumulatorPhotos.quickSyncRecords(domain);
            } else {
                Toast.makeText(mContext, OResource.string(mContext, R.string.toast_network_required), Toast.LENGTH_LONG).show();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.takePictureAccum:
                fileManager.requestForFile(OFileManager.RequestType.IMAGE_OR_CAPTURE_IMAGE);
                break;
            default:
                setResult(RESULT_CANCELED);
                finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        OValues values = fileManager.handleResult(requestCode, resultCode, data);
        if (values != null && !values.contains("size_limit_exceed")) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
            ODataRow row = new ODataRow();
            row.put("scrap_id", scrap_id);
            row.put("photo", values.getString("datas"));
            row.put("accumulator_id", rowId);
            row.put("name", "(" + record.getString("name") + ")_" + timeStamp);
            row.put("id", 0);
            if (!gridAdapter.updateContent(row)) {
                Toast.makeText(this, "Уг зураг аль хэдийн орсон байна!!!", Toast.LENGTH_LONG).show();
            }
        } else if (values != null) {
            Toast.makeText(this, R.string.toast_image_size_too_large, Toast.LENGTH_LONG).show();
        }
    }

}
