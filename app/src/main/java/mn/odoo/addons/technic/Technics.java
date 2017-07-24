package mn.odoo.addons.technic;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.odoo.addons.technic.models.TechnicsModel;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.support.addons.fragment.BaseFragment;
import com.odoo.core.support.addons.fragment.IOnSearchViewChangeListener;
import com.odoo.core.support.addons.fragment.ISyncStatusObserverListener;
import com.odoo.core.support.drawer.ODrawerItem;
import com.odoo.core.support.list.OCursorListAdapter;
import com.odoo.core.utils.BitmapUtils;
import com.odoo.core.utils.IntentUtils;
import com.odoo.core.utils.OControls;

import java.util.ArrayList;
import java.util.List;

import com.odoo.R;
import com.odoo.core.utils.OCursorUtils;
import com.odoo.core.utils.OStringColorUtil;

import odoo.controls.OControlHelper;

/**
 * Created by baaska on 5/30/17.
 */

public class Technics extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        ISyncStatusObserverListener, SwipeRefreshLayout.OnRefreshListener, OCursorListAdapter.OnViewBindListener, IOnSearchViewChangeListener,
        AdapterView.OnItemClickListener {

    public static final String KEY = Technics.class.getSimpleName();
    public static final String EXTRA_KEY_TYPE = "extra_key_type";
    private String mCurFilter = null;

    private View mView;
    private OCursorListAdapter mAdapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        setHasSyncStatusObserver(KEY, this, db());

        return inflater.inflate(R.layout.technic_listview, container, false);
//        mView = inflater.inflate(R.layout.main_list_view, container, false);
//
//        LinearLayout llContainer = (LinearLayout) mView.findViewById(R.id.ll_header);
//        LinearLayout llHeader = (LinearLayout) inflater.inflate(R.layout.header_technic_list, container, false);
//        llContainer.addView(llHeader);
//        return mView;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mView = view;
        ListView mTechnicList = (ListView) view.findViewById(R.id.listview_technic);
        mAdapter = new OCursorListAdapter(getActivity(), null, R.layout.technic_row_item);

        mAdapter.setOnViewBindListener(this);

        mTechnicList.setAdapter(mAdapter);
        mTechnicList.setFastScrollAlwaysVisible(true);
        mTechnicList.setOnItemClickListener(this);

        setHasSyncStatusObserver(KEY, this, db());

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onStatusChange(Boolean changed) {
        if (changed) {
            getLoaderManager().restartLoader(0, null, this);
        }
    }

    public static Bitmap getAlphabetImageTechnic(Context context, String content) {
        Resources res = context.getResources();
        Bitmap mDefaultBitmap = BitmapFactory.decodeResource(res, android.R.drawable.sym_def_app_icon);
        int width = mDefaultBitmap.getWidth();
        int height = mDefaultBitmap.getHeight();
        TextPaint mPaint = new TextPaint();
        mPaint.setTypeface(OControlHelper.boldFont());
        mPaint.setColor(Color.WHITE);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setAntiAlias(true);
        int textSize = res.getDimensionPixelSize(R.dimen.text_size_large);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        Rect mBounds = new Rect();
        canvas.setBitmap(bitmap);
        canvas.drawColor(OStringColorUtil.getStringColor(context, content));

        mPaint.setTextSize(textSize);
        mPaint.getTextBounds(content, 0, 1, mBounds);
        int size = content.length();
        canvas.drawText(content, 0, size, width / 2, height / 2 + (mBounds.bottom - mBounds.top) / 2, mPaint);
        return bitmap;
    }

    @Override
    public void onViewBind(View view, Cursor cursor, ODataRow row) {
        Bitmap img;
        int temp = cursor.getPosition() + 1;
        img = getAlphabetImageTechnic(getActivity(), temp + "");
        OControls.setImage(view, R.id.image_small, img);
        OControls.setText(view, R.id.technic_name, (row.getString("technic_name").equals("false"))
                ? "Хоосон" : row.getString("technic_name"));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle data) {
        String where = "";
        String order_by = "";
        String[] whereArgs = null;
        List<String> args = new ArrayList<>();

        if (mCurFilter != null) {
            where += " technic_name like ? ";
            args.add("%" + mCurFilter + "%");
            order_by = "technic_name ASC";
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
                    OControls.setVisible(mView, R.id.swipe_container_technic);
                    OControls.setGone(mView, R.id.data_list_no_item);
                    setHasSwipeRefreshView(mView, R.id.swipe_container_technic, Technics.this);
                }
            }, 500);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    OControls.setGone(mView, R.id.loadingProgress);
                    OControls.setGone(mView, R.id.swipe_container_technic);
                    OControls.setVisible(mView, R.id.data_list_no_item);
                    setHasSwipeRefreshView(mView, R.id.data_list_no_item, Technics.this);
                    OControls.setImage(mView, R.id.icon, R.drawable.ic_action_suppliers);
                    OControls.setText(mView, R.id.title, _s(R.string.label_no_technic_found));
                    OControls.setText(mView, R.id.subTitle, "");
                }
            }, 500);
            if (db().isEmptyTable()) {
                onRefresh();
            }
        }
    }

    @Override
    public void onRefresh() {
        if (inNetwork()) {
            parent().sync().requestSync(TechnicsModel.AUTHORITY);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        List<ODrawerItem> items = new ArrayList<>();

        items.add(new ODrawerItem(KEY).setTitle("Техник")
                .setIcon(R.drawable.ic_action_suppliers)
                .setInstance(new Technics()));
        return items;
    }

    @Override
    public Class<TechnicsModel> database() {
        return TechnicsModel.class;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_technics, menu);
        setHasSearchView(this, menu, R.id.menu_technics_search);
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
        IntentUtils.startActivity(getActivity(), TechnicsDetails.class, data);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ODataRow row = OCursorUtils.toDatarow((Cursor) mAdapter.getItem(position));
        loadActivity(row);
    }
}