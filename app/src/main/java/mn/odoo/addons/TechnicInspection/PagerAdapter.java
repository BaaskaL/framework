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
        try {
            switch (position) {
                case 0:
                    return new TabInspectionItems();
                case 1:
                    return new TabInspectionTire();
                case 2:
                    return new TabInspectionAccumulator();
                case 3:
                    return new TabInspectionUsageUom();
                default:
                    return null;
            }
        } catch (Exception ex) {
            Log.e("Exception: ", ex.getMessage());
            return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}