package cs4912.g4907.rather.Utilities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;

import java.util.ArrayList;

/**
 * Created by Eli on 4/15/2015.
 */
public class MyPagerAdapter extends FragmentPagerAdapter
{
    ArrayList<Fragment> mList = null;

    public MyPagerAdapter(FragmentManager fm)
    {
        super(fm);
        mList = new ArrayList<Fragment>();
    }

    public void add(Fragment fragment)
    {
        mList.add(fragment);
    }

    public void removeLast()
    {
        if(mList.size() > 1)
            mList.remove(getCount() - 1);
    }

    @Override
    public Fragment getItem(int i)
    {
        return mList.get(i);
    }

    @Override
    public int getCount()
    {
        return mList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        int position = mList.indexOf(object);
        return position == -1 ? POSITION_NONE : position;
    }
}
