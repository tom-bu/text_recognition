package com.tom.ml_demo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Tom on 5/12/18.
 */

public class PagerAdapter extends FragmentStatePagerAdapter{
    private int mNoOfTabs;
    public PagerAdapter(FragmentManager fm, int NumberOfTabs){
        super(fm);
        this.mNoOfTabs = NumberOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                //First fragment conatins the Camera Preview
                CameraFragment cameraFragment = new CameraFragment();
                return cameraFragment;
            case 1:
                //Second fragment contains the extracted text
                TextExtractionFragment textExtractionFragment = new TextExtractionFragment();
                return textExtractionFragment;
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return mNoOfTabs;
    }
}
