package mn.odoo.addons.workOrder;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.odoo.R;
import com.odoo.addons.workOrder.Models.WorkOrder;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.support.addons.fragment.BaseFragment;
import com.odoo.core.support.addons.fragment.IOnSearchViewChangeListener;
import com.odoo.core.support.addons.fragment.ISyncStatusObserverListener;
import com.odoo.core.support.drawer.ODrawerItem;
import com.odoo.core.support.list.OCursorListAdapter;
import com.odoo.core.utils.IntentUtils;
import com.odoo.core.utils.OControls;
import com.odoo.core.utils.OCursorUtils;

import java.util.ArrayList;
import java.util.List;

import mn.odoo.addons.scrapOil.ScrapOilDetails;

/**
 * Created by baaska on 2017-08-29.
 */

public class WorkOrders extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        ISyncStatusObserverListener, SwipeRefreshLayout.OnRefreshListener, OCursorListAdapter.OnViewBindListener, IOnSearchViewChangeListener,
        AdapterView.OnItemClickListener, View.OnClickListener {

    public static final String KEY = WorkOrder.class.getSimpleName();
    private String mCurFilter = null;
    private View mView;
    private OCursorListAdapter mAdapter = null;
    private boolean syncRequested = false;
    private WorkOrder workOrder;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        setHasSyncStatusObserver(KEY, this, db());
        mView = inflater.inflate(R.layout.wo_listview, container, false);
        mContext = this.getContext();
        workOrder = new WorkOrder(mContext, null);
        LinearLayout HeaderContainer = (LinearLayout) mView.findViewById(R.id.wo_header_container);
        LinearLayout Header = (LinearLayout) inflater.inflate(R.layout.header_work_order_list, container, false);
        HeaderContainer.addView(Header);
        return mView;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView = view;
        ListView mListViewWorkOrder = (ListView) mView.findViewById(R.id.listview_wo);
        mAdapter = new OCursorListAdapter(getActivity(), null, R.layout.work_order_row_item);
        mAdapter.setOnViewBindListener(this);
        mListViewWorkOrder.setAdapter(mAdapter);
        mListViewWorkOrder.setFastScrollAlwaysVisible(true);
        mListViewWorkOrder.setOnItemClickListener(this);
        setHasFloatingButton(view, R.id.fabButton, mListViewWorkOrder, this);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle data) {
        String where = "";
        String order_by = "";
        String[] whereArgs = null;
        List<String> args = new ArrayList<>();

        if (mCurFilter != null) {
            where += " origin like ? ";
            where += " or name like ? ";
            where += " or assigned_to_name like ? ";
            args.add("%" + mCurFilter + "%");
            args.add("%" + mCurFilter + "%");
            args.add("%" + mCurFilter + "%");
        }
        order_by = "origin DESC";
        where = (args.size() > 0) ? where : null;
        order_by = (args.size() > 0) ? order_by : null;
        whereArgs = (args.size() > 0) ? args.toArray(new String[args.size()]) : null;
        return new CursorLoader(getActivity(), db().uri(), null, where, whereArgs, order_by);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
        if (data.getCount() > 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    OControls.setGone(mView, R.id.loadingProgress);
                    OControls.setVisible(mView, R.id.swipe_container_wo);
                    OControls.setGone(mView, R.id.data_list_no_item);
                    setHasSwipeRefreshView(mView, R.id.swipe_container_wo, WorkOrders.this);
                }
            }, 500);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    OControls.setGone(mView, R.id.loadingProgress);
                    OControls.setGone(mView, R.id.swipe_container_wo);
                    OControls.setVisible(mView, R.id.data_list_no_item);
                    setHasSwipeRefreshView(mView, R.id.data_list_no_item, WorkOrders.this);
                    OControls.setImage(mView, R.id.icon, R.drawable.ic_action_customers);
                    OControls.setText(mView, R.id.title, _s(R.string.label_no_work_order_found));
                    OControls.setText(mView, R.id.subTitle, "");
                }
            }, 500);
            if (db().isEmptyTable() && !syncRequested) {
                syncRequested = true;
                onRefresh();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    @Override
    public void onRefresh() {
        if (inNetwork()) {
            parent().sync().requestSync(WorkOrder.AUTHORITY);
            setSwipeRefreshing(true);
            OnWorkOrderChangeUpdate onWorkOrderChangeUpdate = new OnWorkOrderChangeUpdate();
            ODomain d = new ODomain();
            /*swipe хийхэд бүх засварын ажилбарыг update хйих*/
            onWorkOrderChangeUpdate.execute(d);
            setSwipeRefreshing(true);
        } else {
            hideRefreshingProgress();
            Toast.makeText(getActivity(), _s(R.string.toast_network_required), Toast.LENGTH_LONG).show();
        }
    }

    private class OnWorkOrderChangeUpdate extends AsyncTask<ODomain, Void, Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setCancelable(false);
            progressDialog.setTitle(R.string.title_please_wait);
            progressDialog.setMessage("Update");
            progressDialog.hide();
        }

        @Override
        protected Void doInBackground(ODomain... params) {
            ODomain domain = params[0];
            workOrder.quickSyncRecords(domain);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabButton:
                loadActivity(null);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ODataRow row = OCursorUtils.toDatarow((Cursor) mAdapter.getItem(position));
        loadActivity(row);
    }

    @Override
    public void onStatusChange(Boolean refreshing) {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_technic_inspections, menu);
        setHasSearchView(this, menu, R.id.menu_technic_inspection_search);
    }

    @Override
    public boolean onSearchViewTextChange(String newFilter) {
        mCurFilter = newFilter;
        getLoaderManager().restartLoader(0, null, this);
        return true;
    }

    @Override
    public void onSearchViewClose() {

    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        List<ODrawerItem> items = new ArrayList<>();
        items.add(new ODrawerItem(KEY).setTitle("Засварын ажилбар")
                .setIcon(R.drawable.ic_action_customers)
                .setInstance(new WorkOrders()));
        return items;
    }

    @Override
    public Class<WorkOrder> database() {
        return WorkOrder.class;
    }

    @Override
    public void onViewBind(View view, Cursor cursor, ODataRow row) {
        OControls.setText(view, R.id.tvWoSequence, (row.getString("origin").equals("-")) ? "Илгээгдээгүй" : row.getString("origin"));
        OControls.setText(view, R.id.tvWoName, (row.getString("name")));
        OControls.setText(view, R.id.tvWoPlannedDate, (row.getString("planned_date")));
        OControls.setText(view, R.id.tvWoPlannedEndDate, (row.getString("planned_date")));
        OControls.setText(view, R.id.tvAssigned, (row.getString("assigned_to_name")));
        OControls.setText(view, R.id.tvStage, (row.getString("stage_name")));
    }

    private void loadActivity(ODataRow row) {
        Bundle data = new Bundle();
        if (row != null) {
            data = row.getPrimaryBundleData();
        }
        IntentUtils.startActivity(getActivity(), WorkOrderDetails.class, data);
    }

}
