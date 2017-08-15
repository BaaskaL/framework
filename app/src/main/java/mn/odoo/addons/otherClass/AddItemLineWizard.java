package mn.odoo.addons.otherClass;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.odoo.R;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.support.list.OListAdapter;
import com.odoo.core.utils.OControls;
import com.odoo.core.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import odoo.controls.IOnQuickRecordCreateListener;

public class AddItemLineWizard extends ActionBarActivity implements
        AdapterView.OnItemClickListener, TextWatcher, View.OnClickListener,
        OListAdapter.OnSearchChange, IOnQuickRecordCreateListener, AdapterView.OnItemLongClickListener {

    private EditText edt_searchable_input;
    private ListView mList = null;
    private OListAdapter mAdapter;
    private List<Object> objects = new ArrayList<>();
    private int selected_position = -1;
    //    private LiveSearch mLiveDataLoader = null;
    private OColumn mCol = null;
    private HashMap<String, Boolean> lineValues = new HashMap<>();
    private Boolean mLongClicked = false;
    private List<String> oilIds = new ArrayList<>();
    public static OModel mModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_add_base);
//        setResult(RESULT_CANCELED);

        edt_searchable_input = (EditText) findViewById(R.id.edt_searchable_input);
        edt_searchable_input.addTextChangedListener(this);
        findViewById(R.id.done).setOnClickListener(this);
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            mList = (ListView) findViewById(R.id.searchable_items);
            mList.setOnItemClickListener(this);
            for (String key : extra.keySet()) {
                lineValues.put(key, extra.getBoolean(key));//baigaa baraanuudiig haruulna
                oilIds.add(key);
            }
            List<ODataRow> oilRows = mModel.select(null, "_id IN (" + StringUtils.repeat(" ?, ", oilIds.size() - 1) + " ?)", oilIds.toArray(new String[oilIds.size()]));
            objects.addAll(oilRows);
            mAdapter = new OListAdapter(this, R.layout.item_line_base, objects) {
                @Override
                public View getView(final int position, View convertView, ViewGroup parent) {
                    View v = convertView;
                    if (v == null) {
                        v = getLayoutInflater().inflate(getResource(), parent, false);
                        ODataRow row = (ODataRow) objects.get(position);
                        OControls.setText(v, R.id.itemName, row.getString("name"));
                        final CheckBox chBox = (CheckBox) v.findViewById(R.id.itemCheck);
                        if (lineValues.get(row.getString("_id"))) {
                            chBox.setChecked(lineValues.get(row.getString("_id")));
                        }
                        chBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                ODataRow row = (ODataRow) objects.get(position);
                                if (!chBox.isChecked()) {
                                    lineValues.put(row.getString("_id"), false);
                                } else {
                                    lineValues.put(row.getString("_id"), true);
                                }
                            }
                        });
                        return v;
                    }
                    return v;
                }
            };
            mList.setAdapter(mAdapter);
        } else {
            finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//        ODataRow data = (ODataRow) objects.get(position);
//        int row_id = productProduct.selectRowId(data.getInt("id"));
//        if (row_id != -1) {
//            data.put(OColumn.ROW_ID, row_id);
//        }
//        if (!data.contains(OColumn.ROW_ID)) {
//            QuickCreateRecordProcess quickCreateRecordProcess = new QuickCreateRecordProcess(this);
//            quickCreateRecordProcess.execute(data);
//        } else {
//            onRecordCreated(data);
//        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        ODataRow data = (ODataRow) objects.get(position);
//        mLongClicked = true;
//        int row_id = productProduct.selectRowId(data.getInt("id"));
//        if (row_id != -1) {
//            data.put(OColumn.ROW_ID, row_id);
//        }
//        if (!data.contains(OColumn.ROW_ID)) {
//            QuickCreateRecordProcess quickCreateRecordProcess = new QuickCreateRecordProcess(this);
//            quickCreateRecordProcess.execute(data);
//        } else {
//            onLongClicked(data);
//        }
        return true;
    }

    private void onLongClicked(final ODataRow row) {
//        mLongClicked = false;
//        final Float count = ((lineValues.containsKey(row.getString("id")))
//                ? lineValues.get(row.getString("id")) : 0);
//        OAlert.inputDialog(this, "Quantity", new OAlert.OnUserInputListener() {
//            @Override
//            public void onViewCreated(EditText inputView) {
//                inputView.setInputType(InputType.TYPE_CLASS_NUMBER
//                        | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
//                inputView.setText(count + "");
//            }
//
//            @Override
//            public void onUserInputted(Object value) {
//                float userData = Float.parseFloat(value.toString());
//                lineValues.put(row.getString("id"), userData);
//                mAdapter.notifiyDataChange(objects);
//            }
//        });
    }

    @Override
    public void onRecordCreated(ODataRow row) {
//        if (!mLongClicked) {
//            Float count = ((lineValues.containsKey(row.getString("id")))
//                    ? lineValues.get(row.getString("id")) : 0);
//            lineValues.put(row.getString("id"), ++count);
//            mAdapter.notifiyDataChange(objects);
//        } else {
//            onLongClicked(row);
//        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count,
                                  int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mAdapter.getFilter().filter(s);
        ImageView imgView = (ImageView) findViewById(R.id.search_icon);
        if (s.length() > 0) {
            imgView.setImageResource(R.drawable.ic_action_navigation_close);
            imgView.setOnClickListener(this);
            imgView.setClickable(true);
        } else {
            imgView.setClickable(false);
            imgView.setImageResource(R.drawable.ic_action_search);
            imgView.setOnClickListener(null);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.done:
                Bundle data = new Bundle();
                for (String key : lineValues.keySet()) {
                    data.putBoolean(key, lineValues.get(key));
                }
                Intent intent = new Intent();
                intent.putExtras(data);
                setResult(RESULT_OK, intent);
                finish();
                break;
            default:
                setResult(RESULT_CANCELED);
                finish();
        }
    }

    @Override
    public void onSearchChange(List<Object> newRecords) {
        if (newRecords.size() <= 2) {
//            if (mLiveDataLoader != null)
//                mLiveDataLoader.cancel(true);
//            if (edt_searchable_input.getText().length() >= 2) {
//                mLiveDataLoader = new LiveSearch();
//                mLiveDataLoader.execute(edt_searchable_input.getText()
//                        .toString());
//            }
        }
    }


//    private class LiveSearch extends AsyncTask<String, Void, List<ODataRow>> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            findViewById(R.id.loading_progress).setVisibility(View.VISIBLE);
//            mList.setVisibility(View.GONE);
//        }
//
////        @Override
////        protected List<ODataRow> doInBackground(String... params) {
////            try {
////                ServerDataHelper helper = productProduct.getServerDataHelper();
//////                ODomain domain = new ODomain();
////////                domain.add(productProduct.getDefaultNameColumn(), "ilike", params[0]);
//////                domain.add("id", "not in", productProduct.getServerIds());
//////                if (mCol != null) {
//////                    for (String key : mCol.getDomains().keySet()) {
//////                        // domain.add("sale_ok", "=", true);
//////                    }
//////                }
//////                OdooFields fields = new OdooFields(productProduct.getColumns());
//////                //return helper.searchRecords(fields, domain, 10);
////                return helper.nameSearch(params[0], domain, 10);
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////            return null;
////        }
//
//        @Override
//        protected void onPostExecute(List<ODataRow> result) {
//            super.onPostExecute(result);
//            findViewById(R.id.loading_progress).setVisibility(View.GONE);
//            mList.setVisibility(View.VISIBLE);
//            if (result != null && result.size() > 0) {
//                objects.clear();
//                objects.addAll(localItems);
//                objects.addAll(result);
//                mAdapter.notifiyDataChange(objects);
//            }
//        }
//
//        @Override
//        protected void onCancelled() {
//            super.onCancelled();
//            findViewById(R.id.loading_progress).setVisibility(View.GONE);
//            mList.setVisibility(View.VISIBLE);
//        }
//    }

//    private class QuickCreateRecordProcess extends AsyncTask<ODataRow, Void, ODataRow> {
//
//        private ProgressDialog progressDialog;
//        IOnQuickRecordCreateListener mOnQuickRecordCreateListener = null;
//
//        public QuickCreateRecordProcess(IOnQuickRecordCreateListener listener) {
//            mOnQuickRecordCreateListener = listener;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            progressDialog = new ProgressDialog(AddProductLineWizard.this);
//            progressDialog.setTitle(R.string.title_please_wait);
//            progressDialog.setMessage(OResource.string(AddProductLineWizard.this, R.string.title_working));
//            progressDialog.setCancelable(false);
//            progressDialog.show();
//        }
//
//        @Override
//        protected ODataRow doInBackground(ODataRow... params) {
//            try {
//                Thread.sleep(700);
//                return productProduct.quickCreateRecord(params[0]);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(ODataRow data) {
//            super.onPostExecute(data);
//            if (data != null && mOnQuickRecordCreateListener != null) {
//                mOnQuickRecordCreateListener.onRecordCreated(data);
//            }
//            progressDialog.dismiss();
//        }
//    }

}
