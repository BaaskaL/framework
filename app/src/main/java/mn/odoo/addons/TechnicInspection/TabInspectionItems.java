package mn.odoo.addons.TechnicInspection;

/**
 * Created by baaska on 7/24/17.
 */

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.odoo.R;
import com.odoo.core.orm.ODataRow;

import java.util.ArrayList;
import java.util.List;

import odoo.controls.ExpandableListControl;

public class TabInspectionItems extends Fragment implements ExpandableListControl.ExpandableListAdapterGetViewListener {

    private ExpandableListControl mList;
    private ExpandableListControl.ExpandableListAdapter mAdapter;
    private List<Object> inspectionItemObjects = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View View = inflater.inflate(R.layout.technic_inspection_line_header, container, false);
        mList = (ExpandableListControl) View.findViewById(R.id.expListInspectionItem);

        inspectionItemObjects.clear();
        inspectionItemObjects.addAll(TechnicsInspectionDetails.inspectionItemLines);
        mAdapter = mList.getAdapter(R.layout.technic_inspection_line_item, inspectionItemObjects, this);
        mAdapter.notifyDataSetChanged(inspectionItemObjects);
        return View;
    }

    @Override
    public View getView(int position, final View mView, ViewGroup parent) {
        Log.i("Line_position==", position + "");
        Log.i("Line_getItem===", mAdapter.getItem(position).toString());

        final EditText description = (EditText) mView.findViewById(R.id.edtDescription);
        EditText view = (EditText) mView.findViewById(R.id.edtDescription);

        final RadioGroup radioGroup = (RadioGroup) mView.findViewById(R.id.edtRadioGroup);
        RadioButton inspection_isitnormal = (RadioButton) mView.findViewById(R.id.inspection_isitnormal);
        RadioButton inspection_check = (RadioButton) mView.findViewById(R.id.inspection_check);
        TextView category = (TextView) mView.findViewById(R.id.edtCategory);
        TextView edtName = (TextView) mView.findViewById(R.id.edtName);
        description.setEnabled(TechnicsInspectionDetails.mEditMode);
        inspection_isitnormal.setEnabled(TechnicsInspectionDetails.mEditMode);
        inspection_check.setEnabled(TechnicsInspectionDetails.mEditMode);

        ODataRow row = (ODataRow) mAdapter.getItem(position);
        edtName.setText((position + 1) + ". " + row.getString("item_name"));
        category.setText(row.getString("categ_name"));
        inspection_isitnormal.setChecked(row.getBoolean("inspection_isitnormal"));
        inspection_check.setChecked(row.getBoolean("inspection_check"));
        Log.i("row.getString====", row.getString("description"));
        if (!row.getString("description").equals("false")) {
            Log.i("WORK===", row.getString("description"));
            description.setText(row.getString("description"));
        }
        if (TechnicsInspectionDetails.mEditMode) {
            description.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() != 0) {
                        Log.i("111111", "   work");
                        TechnicsInspectionDetails.inspectionItemLines.get(1).put("description", description.getText().toString());
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });
            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                    TechnicsInspectionDetails.inspectionItemLines.get(1).put("inspection_isitnormal", (checkedId == R.id.inspection_isitnormal) ? true : false);
                    TechnicsInspectionDetails.inspectionItemLines.get(1).put("inspection_check", (checkedId == R.id.inspection_check) ? true : false);
                }
            });
        }
        return mView;
    }
}