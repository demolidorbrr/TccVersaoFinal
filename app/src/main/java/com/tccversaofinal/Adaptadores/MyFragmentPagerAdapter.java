package com.tccversaofinal.Adaptadores;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.tccversaofinal.Fragments.FragmentA;
import com.tccversaofinal.Fragments.FragmentB;
import com.tccversaofinal.Fragments.FragmentC;
import com.tccversaofinal.Fragments.FragmentD;
import com.tccversaofinal.Fragments.FragmentE;
import com.tccversaofinal.Fragments.FragmentF;

/**
 * Created by Lucas on 17/10/2017.
 */

public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
    private String[] mTabTitles;


    public MyFragmentPagerAdapter(FragmentManager fm, String[] mTabTitles) {
        super(fm);
        this.mTabTitles = mTabTitles;
    }

    @Override
    public Fragment getItem(int position) {
         switch (position){
            case 0:
                return new FragmentA();
            case 1:
                return new FragmentB();
            case 2:
                return new FragmentC();
            case 3:
                return new FragmentD();
            case 4:
                return new FragmentE();
            case 5:
                return new FragmentF();
           default:
               return null;
         }
    }

    @Override
    public int getCount() {
        return this.mTabTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return this.mTabTitles[position];
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        try{
            super.finishUpdate(container);
        } catch (NullPointerException nullPointerException){
            System.out.println("Catch the NullPointerException in FragmentPagerAdapter.finishUpdate");
        }
    }


}