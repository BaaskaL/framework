package mn.odoo.addons.TechnicInspection;

/**
 * Created by baaska on 7/24/17.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        Log.i("imput_position==", position + "");
        switch (position) {

            case 0:
                Log.i("case_0====", position + "");
                TabInspectionItems tab1 = new TabInspectionItems();
                return tab1;
            case 1:
                Log.i("case_1====", position + "");
                TabInspectionTire tab2 = new TabInspectionTire();
                return tab2;
            case 2:
                Log.i("case_2====", position + "");
                TabInspectionUsageUom tab3 = new TabInspectionUsageUom();
                return tab3;
            default:
                Log.i("case_default==", position + "");
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}