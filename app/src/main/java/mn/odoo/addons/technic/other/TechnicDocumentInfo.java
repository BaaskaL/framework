//package mn.odoo.addons.technic.other;
//
//import android.database.Cursor;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.support.v4.content.CursorLoader;
//import android.support.v4.content.Loader;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.Toast;
//
//import com.odoo.R;
//import com.odoo.addons.technic.models.TechnicDocument;
//import com.odoo.core.orm.ODataRow;
//import com.odoo.core.support.list.OCursorListAdapter;
//import com.odoo.core.utils.OControls;
//
//import java.util.ArrayList;
//import java.util.List;
//
//
//public class TechnicDocumentInfo extends Fragment {
//
//    public static final String TAG = TechnicDocumentInfo.class.getSimpleName();
//    private View mView;
//    private Boolean mSyncRequested = false;
//    private ListView mList;
//    private OCursorListAdapter mAdapter;
//    public static final int RETURN_ACTIVITY = 1525;
//    private ListView dashboardListView;
//    public static Integer TechnicId;
//
//    @Override
//    public Class<TechnicDocument> database() {
//        return TechnicDocument.class;
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        setHasOptionsMenu(true);
//        mView = inflater.inflate(R.layout.main_list_view, container, false);
//        mList = (ListView) mView.findViewById(R.id.listview_mn);
//        LinearLayout llContainer = (LinearLayout) mView.findViewById(R.id.ll_header);
//        LinearLayout llHeader = (LinearLayout) inflater.inflate(R.layout.header_technic_document_info, container, false);
//        llContainer.addView(llHeader);
//        mView.setEnabled(false);
//        mView.findViewById(R.id.fabButton).setVisibility(View.GONE);
//        return mView;
//    }
//
//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        mView = view;
//        dashboardListView = (ListView) mView.findViewById(R.id.listview_mn);
//        setHasFloatingButton(mView, R.id.fabButton, dashboardListView, this);
//        initAdapter();
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        mAdapter.changeCursor(data);
//        if (data != null && data.getCount() > 0) {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    OControls.setGone(mView, com.odoo.R.id.loadingProgress);
//                    OControls.setVisible(mView, com.odoo.R.id.swipe_container_mn);
//                    OControls.setGone(mView, com.odoo.R.id.customer_no_items_mn);
//                    setHasSwipeRefreshView(mView, com.odoo.R.id.swipe_container_mn, TechnicDocumentInfo.this);
//                }
//            }, 500);
//        } else {
//            if (db().isEmptyTable() && !mSyncRequested) {
//                mSyncRequested = true;
//                onRefresh();
//            }
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    OControls.setGone(mView, com.odoo.R.id.loadingProgress);
//                    OControls.setGone(mView, com.odoo.R.id.swipe_container_mn);
//                    OControls.setVisible(mView, com.odoo.R.id.customer_no_items_mn);
//                    setHasSwipeRefreshView(mView, com.odoo.R.id.customer_no_items_mn, TechnicDocumentInfo.this);
//                    OControls.setText(mView, com.odoo.R.id.title, "Мэдээлэл байхгүй байна.");
//                    OControls.setText(mView, com.odoo.R.id.subTitle, "");
//                }
//            }, 500);
//        }
//    }
//
//    private void initAdapter() {
//        mAdapter = new OCursorListAdapter(getActivity(), null, R.layout.technic_document_info);
//        mAdapter.setOnViewBindListener(this);
//        mList.setAdapter(mAdapter);
//        mAdapter.handleItemClickListener(mList, this);
//        setHasSyncStatusObserver(TAG, this, db());
//        getLoaderManager().initLoader(0, null, this);
//    }
//
//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle data) {
//        try {
//            String where;
//            String[] whereArgs;
//            List<String> args = new ArrayList();
//            where = " (technic_id = ? )";
//            args.add(String.valueOf(TechnicId));
//            whereArgs = args.toArray(new String[args.size()]);
//            return new CursorLoader(getActivity(), db().uri(), null, where, whereArgs, null);
//        } catch (Exception ex) {
//            Log.d(TAG, "ERROR : " + ex);
//            return null;
//        }
//    }
//
//    @Override
//    public boolean onBackPressed() {
//        return true;
//    }
//
//    @Override
//    public void onRefresh() {
//        if (inNetwork()) {
//            setSwipeRefreshing(true);
//        } else {
//            hideRefreshingProgress();
//            Toast.makeText(getActivity(), _s(com.odoo.R.string.toast_network_required_mn), Toast.LENGTH_LONG).show();
//        }
//    }
//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        menu.clear();
//        inflater.inflate(com.odoo.R.menu.menu_sales_order, menu);
//        setHasSearchView(this, menu, com.odoo.R.id.menu_sales_search);
//    }
//
//    @Override
//    public void onViewBind(View view, Cursor cursor, ODataRow row) {
//        OControls.setText(view, R.id.name, row.getString("document_type_name"));
//        OControls.setText(view, R.id.number, row.getString("document_name"));
//        OControls.setText(view, R.id.date, row.getString("expiry_date"));
//        OControls.setText(view, R.id.owner, row.getString("respondent_name"));
//    }
//}