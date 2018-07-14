package vlimv.taxi;

/**
 * Created by HP on 17-Mar-18.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class DriverTabsAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public DriverTabsAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                NewOrdersFragment tab1 = new NewOrdersFragment();
                return tab1;
            case 1:
                CompletedOrdersFragment tab2 = new CompletedOrdersFragment();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
