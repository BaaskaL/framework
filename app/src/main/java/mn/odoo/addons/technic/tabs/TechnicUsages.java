package mn.odoo.addons.technic.tabs;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.odoo.R;
import com.odoo.addons.technic.models.TechnicsModel;
import com.odoo.core.orm.ODataRow;

import java.util.ArrayList;
import java.util.List;

import odoo.controls.ExpandableListControl;
import odoo.controls.OForm;

/**
 * Created by baaska on 8/13/17.
 */

public class TechnicUsages extends Fragment {

    private TechnicsModel technicsModel;
    private OForm mForm;
    private Context mContext;
    private int TechnicId;
    private ExpandableListControl mList;
    private ExpandableListControl.ExpandableListAdapter mAdapter;
    private List<Object> mObjects = new ArrayList<>();
    private List<ODataRow> rows = new ArrayList<>();

    public TechnicUsages(int TechnicId) {
        this.TechnicId = TechnicId;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.technic_usage_history, container, false);
        mForm = (OForm) mView.findViewById(R.id.technicUsageHistoryForm);
        mList = (ExpandableListControl) mView.findViewById(R.id.ExpandListUsageHistoryLine);
        technicsModel = new TechnicsModel(mContext, null);
        rows = technicsModel.browse(TechnicId).getO2MRecord("technic_usage_history_ids").browseEach();
        getUsageHistory();
        return mView;
    }

    private void getUsageHistory() {
        if (rows != null) {
            final int template = R.layout.technic_usage_history_line;
            mAdapter = mList.getAdapter(template, mObjects,
                    new ExpandableListControl.ExpandableListAdapterGetViewListener() {
                        @Override
                        public View getView(final int position, View mView, ViewGroup parent) {
                            TextView change_measurement = (TextView) mView.findViewById(R.id.change_measurement_name);
                            TextView uom = (TextView) mView.findViewById(R.id.uom_name);
                            TextView change_value = (TextView) mView.findViewById(R.id.change_value);
                            TextView change_date = (TextView) mView.findViewById(R.id.change_date);
                            TextView change_user_name = (TextView) mView.findViewById(R.id.change_user_name);
                            TextView description = (TextView) mView.findViewById(R.id.description);
                            final ODataRow row = (ODataRow) mAdapter.getItem(position);
                            change_measurement.setText((position + 1) + ". " + row.getString("change_measurement_name"));
                            uom.setText(row.getString("uom_name"));
                            change_value.setText(row.getString("change_value"));
                            change_date.setText(row.getString("change_date"));
                            change_user_name.setText(row.getString("change_user_name"));
                            if(!row.getString("description").equals("false")){
                                description.setText(row.getString("description"));
                            }
                            return mView;
                        }
                    });
        }
        mObjects.clear();
        mObjects.addAll(rows);
        mAdapter.notifyDataSetChanged(mObjects);
    }
}
