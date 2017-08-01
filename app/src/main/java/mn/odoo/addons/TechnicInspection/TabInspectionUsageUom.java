package mn.odoo.addons.TechnicInspection;

/**
 * Created by baaska on 7/24/17.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.odoo.R;
import com.odoo.core.orm.ODataRow;

import java.util.ArrayList;
import java.util.List;

import odoo.controls.ExpandableListControl;

public class TabInspectionUsageUom extends Fragment implements ExpandableListControl.ExpandableListAdapterGetViewListener {

    private ExpandableListControl mList;
    private ExpandableListControl.ExpandableListAdapter mAdapter;
    private List<Object> uomObj = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View View = inflater.inflate(R.layout.technic_inspection_usage_uom_header, container, false);
        mList = (ExpandableListControl) View.findViewById(R.id.expListUsageUom);

        uomObj.clear();
        uomObj.addAll(TechnicsInspectionDetails.linesUom);
        mAdapter = mList.getAdapter(R.layout.technic_inspection_usage_uom_item, uomObj, this);
        mAdapter.notifyDataSetChanged(uomObj);
        return View;
    }

    @Override
    public View getView(final int position, View mView, ViewGroup parent) {
        final EditText usageValue = (EditText) mView.findViewById(R.id.usageValue);
        usageValue.setEnabled(true);
        TextView usageUom = (TextView) mView.findViewById(R.id.usageUom);
        TextView productUom = (TextView) mView.findViewById(R.id.productUom);
        ODataRow row = (ODataRow) mAdapter.getItem(position);
        usageUom.setText((position + 1) + ". " + row.getString("usage_uom_name"));
        productUom.setText(row.getString("product_uom_name"));
        usageValue.setText(row.getString("usage_value"));
        usageValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    TechnicsInspectionDetails.linesUom.get(position).put("usage_value", usageValue.getText().toString());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        return mView;
    }
}