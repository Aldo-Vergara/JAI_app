package com.movil.jaiapp.ui.register.member;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.movil.jaiapp.ui.register.client.ClientFragment;

public class AdapterRegister extends FragmentPagerAdapter {

    private Context myContext;
    int totalTabs;

    public AdapterRegister(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
    }

    // this is for fragment tabs
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                MemberFragment memberFragment = new MemberFragment();
                return memberFragment;
            case 1:
                ClientFragment clientFragment = new ClientFragment();
                return clientFragment;
            default:
                return null;
        }
    }
    // this counts total number of tabs
    @Override
    public int getCount() {
        return totalTabs;
    }
}
