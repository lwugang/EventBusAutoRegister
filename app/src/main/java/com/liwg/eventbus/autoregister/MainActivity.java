package com.liwg.eventbus.autoregister;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.JavascriptInterface;

import org.greenrobot.eventbus.Subscribe;

public class MainActivity extends AppCompatActivity {


    @Subscribe
    public void onEvent(Object object){

    }
}
