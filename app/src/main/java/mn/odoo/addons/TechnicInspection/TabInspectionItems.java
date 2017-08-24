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

public class TabInspectionItems extends Fragment {

    private RecyclerView mRecycleView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View View = inflater.inflate(R.layout.technic_inspection_line_header, container, false);
        mRecycleView = (RecyclerView) View.findViewById(R.id.my_recycler_view_item);
        mRecycleView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecycleView.setLayoutManager(mLayoutManager);

        mAdapter = new AdapterItem(TechnicsInspectionDetails.inspectionItemLines);
        mRecycleView.setAdapter(mAdapter);
        return View;
    }
}