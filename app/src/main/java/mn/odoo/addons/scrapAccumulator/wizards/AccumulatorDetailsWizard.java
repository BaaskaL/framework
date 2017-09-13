package mn.odoo.addons.scrapAccumulator.wizards;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
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
import com.odoo.core.rpc.helper.ODomain;
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
    private Toolbar toolbar;

    private OField oReason;
    private Menu mMenu;
    private Boolean mEditMode = false;
    private Bundle extra;
    private ODataRow record = null;
    private OForm mForm;

    private GridView gridView;
    private GridViewAdapter gridAdapter;
    private Button takePic;
    private OFileManager fileManager;
    private ArrayList<String> imageItemsString = new ArrayList<>();
    private Context mContext;
    private String scrap_id;
    private String rowId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extra = getIntent().getExtras();
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
        scrapAccumulatorPhotos = new ScrapAccumulatorPhotos(this, null);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        record = accumulator.browse(Integer.parseInt(rowId));
        mForm.initForm(record);
        setTitle(record.getString("name"));

        List<ODataRow> scrapPhotos = new ArrayList<>();
        scrapPhotos = scrapAccumulatorPhotos.select(null, "scrap_id = ? and accumulator_id = ?", new String[]{scrap_id, rowId});
        gridView.setAdapter(adapterFill(scrapPhotos));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("click========", position + "");
                ImageView image = (ImageView) view.findViewById(R.id.image);
                Bitmap item = BitmapUtils.getBitmapImage(mContext, gridAdapter.getItem(position).toString());
                Intent intent = new Intent(AccumulatorDetailsWizard.this, DetailsActivity.class);
                DetailsActivity.image = item;
                startActivity(intent);
            }
        });


        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                gridAdapter.remove(gridAdapter.getItem(position).toString());
//                imageItemsString.remove(position);
//                gridAdapter.updateContent(imageItemsString);

//                gridAdapter.remove(gridAdapter.getItem(position));
//                gridAdapter.notifyDataSetChanged();
//                Toast.makeText(getActivity(), "You selected : " + item, Toast.LENGTH_SHORT).show();
//                gridAdapter.delete(position);
//                ShowPopupMenu(view, position);
                return true;
            }
        });
    }

    public static void detailsss() {
//        ImageView image = (ImageView) view.findViewById(R.id.image);
//        Bitmap item = BitmapUtils.getBitmapImage(mContext, gridAdapter.getItem(2).toString());
//        Intent intent = new Intent(AccumulatorDetailsWizard.this, DetailsActivity.class);
//        DetailsActivity.image = item;
//        startActivity(intent);
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
            gridAdapter.remove(key);
            return true;
        }
    }

    private GridViewAdapter adapterFill(List<ODataRow> scrapPhotos) {
        for (ODataRow row : scrapPhotos)
            imageItemsString.add(row.getString("photo"));
        gridAdapter = new GridViewAdapter(this, R.layout.grid_item_layout, imageItemsString);
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
        OnAccumuScrapPhotoChangeUpdate onAccumuScrapPhotoChangeUpdate = new OnAccumuScrapPhotoChangeUpdate();
        ODomain domain = new ODomain();
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
                        for (int i = 0; i < gridAdapter.getCount(); i++) {
                            OValues row = new OValues();
                            row.put("scrap_id", scrap_id);
                            row.put("photo", gridAdapter.getItem(i));
                            row.put("accumulator_id", rowId);
                            imgValuene.add(row);
                        }
                        List<ODataRow> rows = scrapAccumulatorPhotos.select(null, "scrap_id = ? and accumulator_id = ?", new String[]{scrap_id, rowId});
                        List<Integer> ids = new ArrayList<>();
                        for (ODataRow row : rows) {
                            ids.add(row.getInt("_id"));
                        }
                        Log.i("delete_ids====", ids.toString());
                        values.put("scrap_photos", new RelValues().append(imgValuene.toArray(new OValues[imgValuene.size()])).delete(ids));
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
            ODomain domain = params[0];
            scrapAccumulatorPhotos.quickSyncRecords(domain);
            //required 2 call
//            scrapAccumulatorPhotos.quickSyncRecords(domain);
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
            imageItemsString.add(newImage);
            Bitmap img = BitmapUtils.getBitmapImage(this, newImage);
            gridAdapter.updateContent(imageItemsString);
        } else if (values != null) {
            Toast.makeText(this, R.string.toast_image_size_too_large, Toast.LENGTH_LONG).show();
        }
    }

}
