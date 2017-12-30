package com.example.lzk.mobilehutubill.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.ViewGroup;

import com.example.lzk.mobilehutubill.MainActivity;
import com.example.lzk.mobilehutubill.fragment.OverviewFragment;
import com.example.lzk.mobilehutubill.fragment.RecordFragment;
import com.example.lzk.mobilehutubill.fragment.CategoryFragment;
import com.example.lzk.mobilehutubill.fragment.ChartsFragment;

/**
 * Created by Lzk on 2017/11/16.
 */
public class FragmentPagerAdapter extends android.support.v4.app.FragmentPagerAdapter {
    private final int PAGER_COUNT = 4;
    private OverviewFragment overviewFragment = null;
    private RecordFragment recordFragment = null;
    private CategoryFragment categoryFragment = null;
    private ChartsFragment chartsFragment = null;

    public FragmentPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
        overviewFragment = new OverviewFragment();
        recordFragment = new RecordFragment();
        categoryFragment = new CategoryFragment();
        chartsFragment = new ChartsFragment();
    }

    @Override
    public int getCount() {
        return PAGER_COUNT;
    }

    @Override
    public Object instantiateItem(ViewGroup vg, int position) {
        return super.instantiateItem(vg, position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch (position) {
            case MainActivity.PAGE_ONE:
                fragment = overviewFragment;
                break;
            case MainActivity.PAGE_TWO:
                fragment = recordFragment;
                break;
            case MainActivity.PAGE_THREE:
                fragment = categoryFragment;
                break;
            case MainActivity.PAGE_FOUR:
                fragment = chartsFragment;
                break;
        }
        return fragment;
    }

}

