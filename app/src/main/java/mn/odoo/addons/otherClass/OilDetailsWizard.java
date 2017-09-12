package mn.odoo.addons.otherClass;

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
import com.odoo.addons.scrapOil.models.ScrapOilReason;
import com.odoo.addons.scrapOil.models.TechnicOil;
import com.odoo.base.addons.ir.feature.OFileManager;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.support.OdooCompatActivity;
import com.odoo.core.support.list.OListAdapter;
import com.odoo.core.utils.BitmapUtils;
import com.odoo.core.utils.OAlert;
import com.odoo.core.utils.OResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import odoo.controls.OField;
import odoo.controls.OForm;


public class OilDetailsWizard extends OdooCompatActivity implements View.OnClickListener {

    private TechnicOil technicOil;
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
    private List<ODataRow> scrap_photos = new ArrayList<>();
    private Button takePic;
    private OFileManager fileManager;
    private ArrayList<Bitmap> imageItems = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extra = getIntent().getExtras();
        int oilId = extra.getInt(OColumn.ROW_ID);
        setContentView(R.layout.oil_detail_wizard);
        setResult(RESULT_CANCELED);

        mForm = (OForm) findViewById(R.id.OFormOilScrapWizard);
        fileManager = new OFileManager(this);
        technicOil = new TechnicOil(this, null);
        toolbar = (Toolbar) findViewById(R.id.toolbarOilWizard);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        record = technicOil.browse(oilId);
        mForm.initForm(record);
        setTitle(record.getString("name"));
        oReason = (OField) mForm.findViewById(R.id.oilReason);
        scrap_photos = record.getO2MRecord("scrap_photos").browseEach();
        getData();
//        Bitmap img = BitmapUtils.getBitmapImage(ScrapTechnicsDetail.this, row.getString("photo"));
        takePic = (Button) findViewById(R.id.takePicture);
        takePic.setOnClickListener(this);
        gridView = (GridView) findViewById(R.id.gridViewOilImage);

        scrapOilReason = new ScrapOilReason(this, null);
        Log.i("scrapOilReason======", scrapOilReason.select().toString());

        adaperRun();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bitmap item = (Bitmap) parent.getItemAtPosition(position);
                Intent intent = new Intent(OilDetailsWizard.this, DetailsActivity.class);
                DetailsActivity.image = item;
                startActivity(intent);
            }
        });
    }


    private void adaperRun() {
//        gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, imageItems);
        gridView.setAdapter(gridAdapter);
    }

    private void getData() {
        for (ODataRow row : scrap_photos) {
            Bitmap img = BitmapUtils.getBitmapImage(OilDetailsWizard.this, row.getString("photo"));
            imageItems.add(img);
        }
    }

    private void setMode(Boolean edit) {
        ToolbarMenuSetVisibl(edit);
        oReason.setEditable(edit);
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
                if (values != null) {
                    List<Integer> oilIds = new ArrayList<>();
                    if (record != null) {
                        technicOil.update(record.getInt(OColumn.ROW_ID), values);
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
            imageItems.add(scaledBitmap);
            adaperRun();
        } else if (values != null) {
            Toast.makeText(this, R.string.toast_image_size_too_large, Toast.LENGTH_LONG).show();
        }
    }

}
