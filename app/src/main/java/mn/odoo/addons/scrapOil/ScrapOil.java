package mn.odoo.addons.scrapOil;

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
import com.odoo.addons.scrapOil.models.ScrapOils;
import com.odoo.addons.scrapOil.models.ShTMScrapPhotos;
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
import com.odoo.core.utils.OResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baaska on 7/16/17.
 */

public class ScrapOil extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        ISyncStatusObserverListener, SwipeRefreshLayout.OnRefreshListener, OCursorListAdapter.OnViewBindListener, IOnSearchViewChangeListener,
        AdapterView.OnItemClickListener, View.OnClickListener {

    public static final String KEY = ScrapOil.class.getSimpleName();
    private String mCurFilter = null;
    private View mView;
    private OCursorListAdapter mAdapter = null;
    private boolean syncRequested = false;
    private ScrapOils scrapOil;
    private ShTMScrapPhotos shTMScrapPhotos;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        setHasSyncStatusObserver(KEY, this, db());
        mView = inflater.inflate(R.layout.scrap_listview, container, false);
        LinearLayout HeaderContainer = (LinearLayout) mView.findViewById(R.id.scrap_header);
        LinearLayout Header = (LinearLayout) inflater.inflate(R.layout.header_accumulator_scrap_list, container, false);
        HeaderContainer.addView(Header);
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView = view;
        mContext = this.getContext();
        scrapOil = new ScrapOils(mContext, null);
        shTMScrapPhotos = new ShTMScrapPhotos(mContext, null);
        ListView mListViewScrap = (ListView) mView.findViewById(R.id.listview_scrap);
        mAdapter = new OCursorListAdapter(getActivity(), null, R.layout.scrap_oil_row_item);
        mAdapter.setOnViewBindListener(this);

        mListViewScrap.setAdapter(mAdapter);
        mListViewScrap.setFastScrollAlwaysVisible(true);
        mListViewScrap.setOnItemClickListener(this);
        setHasFloatingButton(view, R.id.fabButton, mListViewScrap, this);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onStatusChange(Boolean changed) {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onViewBind(View view, Cursor cursor, ODataRow row) {
        String state = "";
        switch (row.getString("state")) {
            case "request":
                state = "Хүсэлт";
                break;
            case "waiting_approval":
                state = "Баталгаа хүлээгдсэн";
                break;
            case "approved":
                state = "Баталсан";
                break;
            case "refused":
                state = "Татгалзсан";
                break;
            case "done":
                state = "Дууссан";
                break;

        }
        OControls.setText(view, R.id.tvOilSequence, (row.getString("origin").equals("-") ? "Илгээгдээгүй" : row.getString("origin")));
        OControls.setText(view, R.id.tvOilTechnicName, (row.getString("technic_name")));
        OControls.setText(view, R.id.tvOilDate, (row.getString("date")));
        OControls.setText(view, R.id.tvOilState, state);
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
                    setHasSwipeRefreshView(mView, R.id.swipe_container_scrap, ScrapOil.this);
                }
            }, 500);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    OControls.setGone(mView, R.id.loadingProgress);
                    OControls.setGone(mView, R.id.swipe_container_scrap);
                    OControls.setVisible(mView, R.id.data_list_no_item);
                    setHasSwipeRefreshView(mView, R.id.data_list_no_item, ScrapOil.this);
                    OControls.setImage(mView, R.id.icon, R.drawable.ic_action_customers);
                    OControls.setText(mView, R.id.title, _s(R.string.label_no_scrap_oil_found));
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
//            parent().sync().requestSync(ScrapOils.AUTHORITY);
            OnOilScrapChangeUpdate onTireScrapChangeUpdate = new OnOilScrapChangeUpdate();
            ODomain d = new ODomain();
            /*swipe хийхэд бүх үзлэгийг update хйих*/
            onTireScrapChangeUpdate.execute(d);
            setSwipeRefreshing(true);
        } else {
            hideRefreshingProgress();
            Toast.makeText(getActivity(), _s(R.string.toast_network_required), Toast.LENGTH_LONG).show();
        }
    }

    private class OnOilScrapChangeUpdate extends AsyncTask<ODomain, Void, Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setTitle(R.string.title_please_wait_mn);
            progressDialog.setMessage("Мэдээлэл шинэчилж байна.");
//            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//            progressDialog.setProgress(1);
            progressDialog.setMax(mAdapter.getCount());
            progressDialog.setCancelable(true);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(ODomain... params) {
            if (inNetwork()) {
                ODomain domain = params[0];
                List<ODataRow> rows = scrapOil.select(null, "id = ?", new String[]{"0"});
                List<ODataRow> photoRows = shTMScrapPhotos.select(null, "id = ?", new String[]{"0"});
                for (ODataRow row : rows) {
                    scrapOil.quickCreateRecord(row);
                }
                for (ODataRow row : photoRows) {
                    shTMScrapPhotos.quickCreateRecord(row);
                }
                /*Бусад бичлэгүүдийг update хийж байна*/
                scrapOil.quickSyncRecords(domain);
                shTMScrapPhotos.quickSyncRecords(domain);
            } else {
                Toast.makeText(mContext, OResource.string(mContext, R.string.toast_network_required), Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            hideRefreshingProgress();
            progressDialog.dismiss();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        List<ODrawerItem> items = new ArrayList<>();
        items.add(new ODrawerItem(KEY).setTitle("ШТМ актлах хүсэлт")
                .setIcon(R.drawable.ic_action_customers)
                .setInstance(new ScrapOil()));
        return items;
    }

    @Override
    public Class<ScrapOils> database() {
        return ScrapOils.class;
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
        IntentUtils.startActivity(getActivity(), ScrapOilDetails.class, data);
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