package com.liwg.eventbus.autoregister;


import android.app.Fragment;
import android.os.Bundle;

import org.greenrobot.eventbus.Subscribe;

public class FragmentTest2 extends Fragment {

    @Subscribe
    public void onEvent(Object object){

    }
}
