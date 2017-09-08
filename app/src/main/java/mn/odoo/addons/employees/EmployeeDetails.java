package mn.odoo.addons.employees;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.odoo.App;
import com.odoo.R;
import com.odoo.addons.customers.utils.ShareUtil;
import com.odoo.addons.employees.models.Employee;
import com.odoo.base.addons.ir.feature.OFileManager;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.rpc.helper.OdooFields;
import com.odoo.core.rpc.helper.utils.gson.OdooResult;
import com.odoo.core.support.OdooCompatActivity;
import com.odoo.core.utils.BitmapUtils;
import com.odoo.core.utils.OAlert;
import com.odoo.core.utils.OResource;
import com.odoo.core.utils.OStringColorUtil;

import java.util.ArrayList;
import java.util.List;

import odoo.controls.ExpandableListControl;
import odoo.controls.OField;
import odoo.controls.OForm;

/**
 * Created by baaska on 7/21/17.
 */

public class EmployeeDetails extends OdooCompatActivity
        implements View.OnClickListener, OField.IOnFieldValueChangeListener {

    public static final String TAG = EmployeeDetails.class.getSimpleName();

    private Bundle extras;
    private OForm mForm;
    private final String KEY_MODE = "key_edit_mode";
    private final String KEY_NEW_IMAGE = "key_new_image";
    private Employee employ;
    private ODataRow record = null;
    private Boolean mEditMode = false;
    private Menu mMenu;
    private List<ODataRow> resultInspectionPack;
    private int myId;

    private ExpandableListControl mList;
    private ExpandableListControl.ExpandableListAdapter mAdapter;
    private ExpandableListControl mUsageList;
    private ExpandableListControl.ExpandableListAdapter mUsageAdapter;
    private List<Object> objects = new ArrayList<>();
    private List<ODataRow> lines = null;
    private List<Object> UsageObjects = new ArrayList<>();
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView userImage = null;
    private Toolbar toolbar;
    private OFileManager fileManager;
    private String newImage = null;
    private TextInputLayout inputLayoutPass, inputLayoutEditPass;
    private EditText editPass;
    private TextView showPass;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.employee_detail);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.employee_collapsing_toolbar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        editPass = (EditText) findViewById(R.id.editPass);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        userImage = (ImageView) findViewById(R.id.user_image);
        findViewById(R.id.captureImage).setOnClickListener(this);

        fileManager = new OFileManager(this);
        if (toolbar != null)
            collapsingToolbarLayout.setTitle("");
        if (savedInstanceState != null) {
            mEditMode = savedInstanceState.getBoolean(KEY_MODE);
            newImage = savedInstanceState.getString(KEY_NEW_IMAGE);
        }
        App app = (App) getApplicationContext();
        employ = new Employee(this, null);
        extras = getIntent().getExtras();
        if (!hasRecordInExtra())
            mEditMode = true;
        setupToolbar();
    }

    private boolean hasRecordInExtra() {
        return extras != null && extras.containsKey(OColumn.ROW_ID);
    }

    private void setMode(Boolean edit) {
        findViewById(R.id.captureImage).setVisibility(edit ? View.VISIBLE : View.GONE);
        if (mMenu != null) {
            mMenu.findItem(R.id.menu_customer_detail_more).setVisible(!edit);
            mMenu.findItem(R.id.menu_customer_edit).setVisible(!edit);
            mMenu.findItem(R.id.menu_customer_save).setVisible(edit);
            mMenu.findItem(R.id.menu_customer_cancel).setVisible(edit);
        }
        int color = Color.DKGRAY;
        if (record != null) {
            color = OStringColorUtil.getStringColor(this, record.getString("name"));
        }
        if (edit) {
            if (!hasRecordInExtra()) {
                collapsingToolbarLayout.setTitle("Үүсгэх");
            }
            mForm = (OForm) findViewById(R.id.employeeFormEdit);
            findViewById(R.id.employee_view_layout).setVisibility(View.GONE);
            findViewById(R.id.employee_edit_layout).setVisibility(View.VISIBLE);
        } else {
            mForm = (OForm) findViewById(R.id.employeeForm);
            showPass = (TextView) findViewById(R.id.showPass);
            if (!record.getString("confirm_code").equals("false")) {
                showPass.setText(record.getString("confirm_code"));
            }
            findViewById(R.id.employee_edit_layout).setVisibility(View.GONE);
            findViewById(R.id.employee_view_layout).setVisibility(View.VISIBLE);
        }
        if (record != null && edit) {

            if (!record.getString("confirm_code").equals("false")) {
                editPass.setText(record.getString("confirm_code"));
            }
        }
        setColor(color);
    }

    private void setupToolbar() {
        if (!hasRecordInExtra()) {
            setMode(mEditMode);
            userImage.setColorFilter(Color.parseColor("#ffffff"));
            userImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            mForm.setEditable(mEditMode);
            mForm.initForm(null);
        } else {
            int rowId = extras.getInt(OColumn.ROW_ID);
            record = employ.browse(rowId);
            checkControls();
            setMode(mEditMode);
            mForm.setEditable(mEditMode);
            mForm.initForm(record);
            collapsingToolbarLayout.setTitle(record.getString("name"));
            setCustomerImage();
            if (record.getInt("id") != 0 && record.getString("image_medium").equals("false")) {
                EmployeeDetails.BigImageLoader bigImageLoader = new EmployeeDetails.BigImageLoader();
                bigImageLoader.execute(record.getInt("id"));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.captureImage:
                fileManager.requestForFile(OFileManager.RequestType.IMAGE_OR_CAPTURE_IMAGE);
                break;
        }
    }

    private void checkControls() {

    }

    private void setCustomerImage() {

        if (record != null && !record.getString("image_small").equals("false")) {
            userImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            String base64 = newImage;
            if (newImage == null) {
                if (!record.getString("image_medium").equals("false")) {
                    base64 = record.getString("image_medium");
                } else {
                    base64 = record.getString("image_small");
                }
            }
            userImage.setImageBitmap(BitmapUtils.getBitmapImage(this, base64));
        } else {
            userImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            userImage.setColorFilter(Color.WHITE);
            int color = OStringColorUtil.getStringColor(this, record.getString("name"));
            userImage.setBackgroundColor(color);
        }
    }

    private void setColor(int color) {
        mForm.setIconTintColor(color);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_customer_save:
                OValues values = mForm.getValues();
                if (values != null) {
                    if (newImage != null) {
                        values.put("image_small", newImage);
                        values.put("image_medium", newImage);
                    }
                    values.put("confirm_code", editPass.getText());
                    if (record != null) {
                        employ.update(record.getInt(OColumn.ROW_ID), values);
                        Toast.makeText(this, R.string.tech_toast_information_saved, Toast.LENGTH_LONG).show();
                        mEditMode = !mEditMode;
                        setupToolbar();
                    } else {
                        final int row_id = employ.insert(values);
                        if (row_id != OModel.INVALID_ROW_ID) {
                            finish();
                        }
                    }
                }
                break;
            case R.id.menu_customer_cancel:
            case R.id.menu_customer_edit:
                if (hasRecordInExtra()) {
                    mEditMode = !mEditMode;
                    setMode(mEditMode);
                    mForm.setEditable(mEditMode);
                    mForm.initForm(record);
                    setCustomerImage();
                } else {
                    finish();
                }
                break;
            case R.id.menu_customer_share:
                ShareUtil.shareContact(this, record, true);
                break;
            case R.id.menu_customer_import:
                ShareUtil.shareContact(this, record, false);
                break;
            case R.id.menu_customer_delete:
                OAlert.showConfirm(this, OResource.string(this,
                        R.string.confirm_are_you_sure_want_to_delete),
                        new OAlert.OnAlertConfirmListener() {
                            @Override
                            public void onConfirmChoiceSelect(OAlert.ConfirmType type) {
                                if (type == OAlert.ConfirmType.POSITIVE) {
                                    // Deleting record and finishing activity if success.
                                    if (employ.delete(record.getInt(OColumn.ROW_ID))) {
                                        Toast.makeText(EmployeeDetails.this, R.string.tech_toast_information_deleted,
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_customer_detail, menu);
        mMenu = menu;
        setMode(mEditMode);
        return true;
    }

    @Override
    public void onFieldValueChange(OField field, Object value) {
        if (field.getFieldName().equals("is_company")) {
            Boolean checked = Boolean.parseBoolean(value.toString());
            int view = (checked) ? View.GONE : View.VISIBLE;
            findViewById(R.id.parent_id).setVisibility(view);
        }
    }

    private class BigImageLoader extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... params) {
            String image = null;
            try {
                Thread.sleep(300);
                OdooFields fields = new OdooFields();
                fields.addAll(new String[]{"image_small"});
                OdooResult record = employ.getServerDataHelper().read(null, params[0]);
                if (record != null && !record.getString("image_small").equals("false")) {
                    image = record.getString("image_small");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return image;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                if (!result.equals("false")) {
                    OValues values = new OValues();
                    values.put("image_medium", result);
                    employ.update(record.getInt(OColumn.ROW_ID), values);
                    record.put("image_medium", result);
                    setCustomerImage();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_MODE, mEditMode);
        outState.putString(KEY_NEW_IMAGE, newImage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        OValues values = fileManager.handleResult(requestCode, resultCode, data);
        if (values != null && !values.contains("size_limit_exceed")) {
            newImage = values.getString("datas");
            userImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            userImage.setColorFilter(null);
            userImage.setImageBitmap(BitmapUtils.getBitmapImage(this, newImage));
        } else if (values != null) {
            Toast.makeText(this, R.string.toast_image_size_too_large, Toast.LENGTH_LONG).show();
        }
    }
}
