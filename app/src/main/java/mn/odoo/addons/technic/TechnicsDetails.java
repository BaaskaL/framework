package mn.odoo.addons.technic;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.odoo.R;
import com.odoo.addons.technic.models.TechnicDocument;
import com.odoo.addons.technic.models.TechnicsModel;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.support.OdooCompatActivity;

import java.util.ArrayList;
import java.util.List;

import mn.odoo.addons.technic.tabs.TechnicDocuments;
import mn.odoo.addons.technic.tabs.TechnicInfo;
import mn.odoo.addons.technic.tabs.TechnicNorms;
import mn.odoo.addons.technic.tabs.TechnicStateStorys;
import mn.odoo.addons.technic.tabs.TechnicUsages;
import odoo.controls.OForm;

/**
 * Created by baaska on 5/30/17.
 */

public class TechnicsDetails extends OdooCompatActivity {

    public static final String TAG = TechnicsDetails.class.getSimpleName();

    private Bundle extras;
    private OForm mForm;
    private TechnicsModel technic;
    private ODataRow record = null;
    private Toolbar toolbar;
    private TechnicDocument technicDocument;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private int TechnicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.technic_detail);
        extras = getIntent().getExtras();
        TechnicId = extras.getInt(OColumn.ROW_ID);
        OnTechnicChangeUpdate onTechnicChangeUpdate = new OnTechnicChangeUpdate();
        ODomain d = new ODomain();
        d.add("technic_id", "=", TechnicId);
        technicDocument = new TechnicDocument(getApplicationContext(), null);
        onTechnicChangeUpdate.execute(d);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Техникийн дэлгэрэнгүй");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TechnicInfo(TechnicId), "Техник");
        adapter.addFragment(new TechnicNorms(TechnicId), "Эзэмшлийн мэдээлэл");
        adapter.addFragment(new TechnicUsages(TechnicId), "Ашиглалтын түүх");
        adapter.addFragment(new TechnicDocuments(TechnicId), "Бичиг баримт");
        adapter.addFragment(new TechnicStateStorys(TechnicId), "Төлвийн түүх");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class OnTechnicChangeUpdate extends AsyncTask<ODomain, Void, Void> {

        @Override
        protected Void doInBackground(ODomain... params) {
            ODomain domain = params[0];
            technicDocument.quickSyncRecords(null);
            return null;
        }
    }

}
