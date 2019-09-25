package com.liwg.eventbus.autoregister;

import android.support.v4.app.Fragment;

import org.greenrobot.eventbus.Subscribe;

public class FragmentTest extends Fragment {

    @Subscribe
    public void onEvent(Object object){

    }
}
