package mn.odoo.addons.scrapTire;

import android.content.Context;
import android.database.Cursor;
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
import com.odoo.addons.scrapTire.models.ScrapTires;
import com.odoo.core.orm.ODataRow;
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

/**
 * Created by baaska on 6/29/17.
 */

public class ScrapTire extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        ISyncStatusObserverListener, SwipeRefreshLayout.OnRefreshListener, OCursorListAdapter.OnViewBindListener, IOnSearchViewChangeListener,
        AdapterView.OnItemClickListener, View.OnClickListener {

    public static final String KEY = ScrapTire.class.getSimpleName();
    private String mCurFilter = null;
    private View mView;
    private OCursorListAdapter mAdapter = null;
    private boolean syncRequested = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        setHasSyncStatusObserver(KEY, this, db());
        mView = inflater.inflate(R.layout.scrap_listview, container, false);
        LinearLayout HeaderContainer = (LinearLayout) mView.findViewById(R.id.scrap_header);
        LinearLayout Header = (LinearLayout) inflater.inflate(R.layout.header_tire_scrap_list, container, false);
        HeaderContainer.addView(Header);
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView = view;
        ListView mListViewScrap = (ListView) mView.findViewById(R.id.listview_scrap);
        mAdapter = new OCursorListAdapter(getActivity(), null, R.layout.scrap_tire_row_item);
        mAdapter.setOnViewBindListener(this);

        mListViewScrap.setAdapter(mAdapter);
        mListViewScrap.setFastScrollAlwaysVisible(true);
        mListViewScrap.setOnItemClickListener(this);
        setHasFloatingButton(view, R.id.fabButton, mListViewScrap, this);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onStatusChange(Boolean changed) {
//        if (changed) {
//            getLoaderManager().restartLoader(0, null, this);
//        }
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onViewBind(View view, Cursor cursor, ODataRow row) {
        OControls.setText(view, R.id.tvTireName, (row.getString("origin").equals("false") ? "" : row.getString("origin")));
        OControls.setText(view, R.id.tvTechnicName, (row.getString("technic_name")));
        OControls.setText(view, R.id.tvDate, (row.getString("date")));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle data) {
        String where = "";
        String order_by = "";
        String[] whereArgs = null;
        List<String> args = new ArrayList<>();

        if (mCurFilter != null) {
            where += " origin like ? ";
            args.add("%" + mCurFilter + "%");
            order_by = "origin ASC";
        }

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
                    OControls.setVisible(mView, R.id.swipe_container_scrap);
                    OControls.setGone(mView, R.id.data_list_no_item);
                    setHasSwipeRefreshView(mView, R.id.swipe_container_scrap, ScrapTire.this);
                }
            }, 500);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    OControls.setGone(mView, R.id.loadingProgress);
                    OControls.setGone(mView, R.id.swipe_container_scrap);
                    OControls.setVisible(mView, R.id.data_list_no_item);
                    setHasSwipeRefreshView(mView, R.id.data_list_no_item, ScrapTire.this);
                    OControls.setImage(mView, R.id.icon, R.drawable.ic_action_customers);
                    OControls.setText(mView, R.id.title, _s(R.string.label_no_scrap_tires_found));
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
    public void onRefresh() {
        if (inNetwork()) {
            parent().sync().requestSync(ScrapTires.AUTHORITY);
            setSwipeRefreshing(true);
        } else {
            hideRefreshingProgress();
            Toast.makeText(getActivity(), _s(R.string.toast_network_required), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        List<ODrawerItem> items = new ArrayList<>();
        items.add(new ODrawerItem(KEY).setTitle("Дугуй актлах хүсэлт")
                .setIcon(R.drawable.ic_action_tire)
                .setInstance(new ScrapTire()));
        return items;
    }

    @Override
    public Class<ScrapTires> database() {
        return ScrapTires.class;
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

    private void loadActivity(ODataRow row) {
        Bundle data = new Bundle();
        if (row != null) {
            data = row.getPrimaryBundleData();
        }
        IntentUtils.startActivity(getActivity(), ScrapTireDetails.class, data);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ODataRow row = OCursorUtils.toDatarow((Cursor) mAdapter.getItem(position));
        loadActivity(row);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabButton:
                loadActivity(null);
                break;
        }
    }
}