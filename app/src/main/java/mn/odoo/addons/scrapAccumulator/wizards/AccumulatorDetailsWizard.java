package mn.odoo.addons.scrapAccumulator.wizards;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.odoo.R;
import com.odoo.addons.scrapAccumulator.Accumulator;
import com.odoo.addons.scrapAccumulator.ScrapAccumulatorPhotos;
import com.odoo.addons.scrapOil.models.ScrapOilReason;
import com.odoo.addons.scrapOil.models.TechnicOil;
import com.odoo.base.addons.ir.feature.OFileManager;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.RelValues;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.support.OdooCompatActivity;
import com.odoo.core.support.list.OListAdapter;
import com.odoo.core.utils.BitmapUtils;
import com.odoo.core.utils.OAlert;
import com.odoo.core.utils.OResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import mn.odoo.addons.otherClass.DetailsActivity;
import mn.odoo.addons.otherClass.GridViewAdapter;
import odoo.controls.OField;
import odoo.controls.OForm;


public class AccumulatorDetailsWizard extends OdooCompatActivity implements View.OnClickListener {

    private Accumulator accumulator;
    private ScrapAccumulatorPhotos scrapAccumulatorPhotos;
    private ScrapOilReason scrapOilReason;

    private EditText edt_searchable_input;
    private ListView mList = null;
    private OListAdapter mAdapter;
    private Toolbar toolbar;

    private List<Object> objects = new ArrayList<>();
    private int selected_position = -1;
    //    private LiveSearch mLiveDataLoader = null;
    private OColumn mCol = null;
    private HashMap<String, Boolean> lineValues = new HashMap<>();

    private OField oReason;
    private Menu mMenu;
    private Boolean mEditMode = false;
    private Bundle extra;
    private ODataRow record = null;
    private OForm mForm;

    private GridView gridView;
    private GridViewAdapter gridAdapter;
    //    private List<ODataRow> scrap_photos = new ArrayList<>();
    private Button takePic;
    private OFileManager fileManager;
    private ArrayList<Bitmap> imageItems = new ArrayList<>();
    private ArrayList<String> imageItemsString = new ArrayList<>();
    private Context mContext;
    private String scrap_id;
    private String rowId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extra = getIntent().getExtras();
        Log.i("extra=====", extra.toString());
        scrap_id = extra.getString("scrap_id");
        rowId = String.valueOf(extra.getInt(OColumn.ROW_ID));
        setContentView(R.layout.accumulator_detail_wizard);
        setResult(RESULT_CANCELED);
        mContext = getApplicationContext();

        mForm = (OForm) findViewById(R.id.OFormAccumulatorScrapWizard);
        gridView = (GridView) findViewById(R.id.gridViewAccumImage);
        toolbar = (Toolbar) findViewById(R.id.toolbarOilWizard);
        oReason = (OField) mForm.findViewById(R.id.accumReason);
        takePic = (Button) findViewById(R.id.takePicture);

        fileManager = new OFileManager(this);
        accumulator = new Accumulator(this, null);
        scrapOilReason = new ScrapOilReason(this, null);
        scrapAccumulatorPhotos = new ScrapAccumulatorPhotos(this, null);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        record = accumulator.browse(Integer.parseInt(rowId));
        mForm.initForm(record);
        setTitle(record.getString("name"));

        List<ODataRow> scrapPhotos = new ArrayList<>();
        Log.i("Picture=======", scrapAccumulatorPhotos.select().toString());
            scrapPhotos = scrapAccumulatorPhotos.select(null, "scrap_id = ? and accumulator_id = ?", new String[]{scrap_id, rowId});
        gridView.setAdapter(adapterFill(scrapPhotos));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bitmap item = (Bitmap) parent.getItemAtPosition(position);
                Intent intent = new Intent(AccumulatorDetailsWizard.this, DetailsActivity.class);
                DetailsActivity.image = item;
                startActivity(intent);
            }
        });
    }

    private GridViewAdapter adapterFill(List<ODataRow> scrapPhotos) {
        for (ODataRow row : scrapPhotos)
            imageItems.add(BitmapUtils.getBitmapImage(mContext, row.getString("photo")));
        gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, imageItems);
        return gridAdapter;
    }

    private void setMode(Boolean edit) {
        ToolbarMenuSetVisibl(edit);
        oReason.setEditable(edit);
        if (edit) {
            takePic.setOnClickListener(this);
        } else {
            takePic.setClickable(false);
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
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_save:
                OValues values = mForm.getValues();
                Log.i(":values====", values.toString());
                if (values != null) {
                    if (record != null) {
                        List<OValues> imgValuene = new ArrayList();
                        for (String img : imageItemsString) {
                            OValues row = new OValues();
                            row.put("scrap_id", scrap_id);
                            row.put("photo", img);
                            row.put("accumulator_id", rowId);
                            imgValuene.add(row);
                        }
                        values.put("scrap_photos", new RelValues().append(imgValuene.toArray(new OValues[imgValuene.size()])));
//                        values.put("scrap_photos", new RelValues().append(imgValuene));
                        accumulator.update(record.getInt(OColumn.ROW_ID), values);

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
            case R.id.takePicture:
                fileManager.requestForFile(OFileManager.RequestType.IMAGE_OR_CAPTURE_IMAGE);
                break;
//            case R.id.done:
//                Bundle data = new Bundle();
//                for (String key : lineValues.keySet()) {
//                    data.putBoolean(key, lineValues.get(key));
//                }
//                Intent intent = new Intent();
//                intent.putExtras(data);
//                setResult(RESULT_OK, intent);
//                finish();
//                break;
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
            String newImage = values.getString("datas");
            Bitmap img = BitmapUtils.getBitmapImage(this, newImage);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(img, 2560, 1600, true);//screen resolution 16:10
            imageItemsString.add(newImage);
            imageItems.add(scaledBitmap);
            gridAdapter.updateContent(imageItems);
        } else if (values != null) {
            Toast.makeText(this, R.string.toast_image_size_too_large, Toast.LENGTH_LONG).show();
        }
    }

}
