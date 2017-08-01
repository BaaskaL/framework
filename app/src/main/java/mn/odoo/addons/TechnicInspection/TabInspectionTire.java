package mn.odoo.addons.TechnicInspection;

/**
 * Created by baaska on 7/24/17.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

public class TabInspectionTire extends Fragment implements ExpandableListControl.ExpandableListAdapterGetViewListener {

    private ExpandableListControl mList;
    private ExpandableListControl.ExpandableListAdapter mAdapter;
    private List<Object> tireObj = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View View = inflater.inflate(R.layout.technic_inspection_tire_header, container, false);
        mList = (ExpandableListControl) View.findViewById(R.id.expListTire);

        tireObj.clear();
        tireObj.addAll(TechnicsInspectionDetails.tireLines);
        mAdapter = mList.getAdapter(R.layout.technic_inspection_tire_item, tireObj, this);
        mAdapter.notifyDataSetChanged(tireObj);
        return View;
    }

    @Override
    public View getView(final int position, View mView, ViewGroup parent) {

        final EditText serial = (EditText) mView.findViewById(R.id.serial);
        serial.setEnabled(true);
        TextView name = (TextView) mView.findViewById(R.id.name);
        TextView date_record = (TextView) mView.findViewById(R.id.date_record);
        TextView current_position = (TextView) mView.findViewById(R.id.current_position);
        ODataRow row = (ODataRow) mAdapter.getItem(position);
        name.setText((position + 1) + ". " + row.getString("name"));
        date_record.setText(row.getString("date_record"));
        current_position.setText(row.getString("current_position"));
        serial.setText(row.getString("serial"));
        serial.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() != 0) {
                    Log.i("serial_change==", "   WORK");
//                    TechnicsInspectionDetails.tireLines.get(position).put("serial", serial.getText().toString());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
        Log.i("val====", mAdapter.getItem(position) + "");
        return mView;
    }
}