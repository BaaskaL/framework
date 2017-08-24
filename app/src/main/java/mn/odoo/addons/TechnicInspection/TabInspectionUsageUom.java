package mn.odoo.addons.TechnicInspection;

/**
 * Created by baaska on 7/24/17.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.odoo.R;

public class TabInspectionUsageUom extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View View = inflater.inflate(R.layout.technic_inspection_usage_uom_header, container, false);
        mRecyclerView = (RecyclerView) View.findViewById(R.id.my_recycler_view_uom);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new AdapterUsageUom(TechnicsInspectionDetails.linesUom);
        mRecyclerView.setAdapter(mAdapter);
        return View;
    }
}