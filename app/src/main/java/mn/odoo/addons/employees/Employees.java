package mn.odoo.addons.employees;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import com.odoo.addons.employees.models.Employee;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.support.addons.fragment.BaseFragment;
import com.odoo.core.support.addons.fragment.IOnSearchViewChangeListener;
import com.odoo.core.support.addons.fragment.ISyncStatusObserverListener;
import com.odoo.core.support.drawer.ODrawerItem;
import com.odoo.core.support.list.OCursorListAdapter;
import com.odoo.core.utils.BitmapUtils;
import com.odoo.core.utils.IntentUtils;
import com.odoo.core.utils.OControls;
import com.odoo.core.utils.OCursorUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baaska on 7/20/17.
 */

public class Employees extends BaseFragment implements LoaderManager.LoaderCallbacks<Cursor>,
        ISyncStatusObserverListener, SwipeRefreshLayout.OnRefreshListener, OCursorListAdapter.OnViewBindListener, IOnSearchViewChangeListener,
        AdapterView.OnItemClickListener, View.OnClickListener {

    public static final String KEY = Employees.class.getSimpleName();
    private String mCurFilter = null;
    private Employee employee;
    private View mView;
    private OCursorListAdapter mAdapter = null;
    private boolean syncRequested = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        setHasSyncStatusObserver(KEY, this, db());
        employee = new Employee(getContext(), null);
        mView = inflater.inflate(R.layout.employee_listview, container, false);
        LinearLayout HeaderContainer = (LinearLayout) mView.findViewById(R.id.employee_header_container);
        LinearLayout Header = (LinearLayout) inflater.inflate(R.layout.header_employee_list, container, false);
        HeaderContainer.addView(Header);
        return mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasSwipeRefreshView(view, R.id.swipe_container_employee, this);
        mView = view;
        ListView mEmployeeList = (ListView) mView.findViewById(R.id.listview_employee);
        mAdapter = new OCursorListAdapter(getActivity(), null, R.layout.employee_row_item);
        mAdapter.setOnViewBindListener(this);

        mEmployeeList.setAdapter(mAdapter);
        mEmployeeList.setFastScrollAlwaysVisible(true);
        mEmployeeList.setOnItemClickListener(this);
        setHasFloatingButton(view, R.id.fabButton, mEmployeeList, this);
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
        Bitmap img;
        if (row.getString("image_small").equals("false")) {
            img = BitmapUtils.getAlphabetImage(getActivity(), row.getString("name"));
        } else {
            img = BitmapUtils.getBitmapImage(getActivity(), row.getString("image_small"));
        }
        OControls.setImage(view, R.id.employeeImageSmall, img);
        OControls.setText(view, R.id.employeeName, row.getString("name"));
        OControls.setText(view, R.id.employeeLname, (row.getString("last_name").equals("false") ? " "
                : row.getString("last_name")));

        OControls.setText(view, R.id.employeeSSIND, (row.getString("ssnid").equals("false"))
                ? "" : row.getString("ssnid"));

        OControls.setText(view, R.id.employeeWorkPhone, (row.getString("work_phone").equals("false"))
                ? "" : row.getString("work_phone"));

        OControls.setText(view, R.id.employeeJob, (row.getString("job_name").equals("false"))
                ? "" : row.getString("job_name"));
        OControls.setText(view, R.id.employeeCompany, (row.getString("company_name").equals("false"))
                ? "" : row.getString("company_name"));
        OControls.setText(view, R.id.employeeDepartment, (row.getString("department_name").equals("false"))
                ? "" : row.getString("department_name"));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle data) {
        String where = "";
        String order_by = "";
        String[] whereArgs = null;
        List<String> args = new ArrayList<>();

        if (mCurFilter != null) {
            where += " name like ? ";
            args.add("%" + mCurFilter + "%");
            order_by = "name ASC";
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
                    OControls.setVisible(mView, R.id.swipe_container_employee);
                    OControls.setGone(mView, R.id.data_list_no_item);
                    setHasSwipeRefreshView(mView, R.id.swipe_container_employee, Employees.this);
                }
            }, 500);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    OControls.setGone(mView, R.id.loadingProgress);
                    OControls.setGone(mView, R.id.swipe_container_employee);
                    OControls.setVisible(mView, R.id.data_list_no_item);
                    setHasSwipeRefreshView(mView, R.id.data_list_no_item, Employees.this);
                    OControls.setImage(mView, R.id.icon, R.drawable.ic_action_customers);
                    OControls.setText(mView, R.id.title, _s(R.string.label_no_employee_found));
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
            parent().sync().requestSync(Employee.AUTHORITY);
            OnEmployeeChangeUpdate onEmployeeChangeUpdate = new OnEmployeeChangeUpdate();
            ODomain d = new ODomain();
            /*swipe хийхэд бүх ажилтанг update хйих*/
            onEmployeeChangeUpdate.execute(d);
            setSwipeRefreshing(true);
        } else {
            hideRefreshingProgress();
            Toast.makeText(getActivity(), _s(R.string.toast_network_required), Toast.LENGTH_LONG).show();
        }
    }

    private class OnEmployeeChangeUpdate extends AsyncTask<ODomain, Void, Void> {
        @Override
        protected Void doInBackground(ODomain... params) {
            ODomain domain = params[0];
            employee.quickSyncRecords(domain);
            employee.quickSyncRecords(domain);
            return null;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        List<ODrawerItem> items = new ArrayList<>();
        items.add(new ODrawerItem(KEY).setTitle("Ажилчид")
                .setIcon(R.drawable.ic_action_customers)
                .setInstance(new Employees()));
        return items;
    }

    @Override
    public Class<Employee> database() {
        return Employee.class;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_partners, menu);
        setHasSearchView(this, menu, R.id.menu_partner_search);
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
        IntentUtils.startActivity(getActivity(), EmployeeDetails.class, data);
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