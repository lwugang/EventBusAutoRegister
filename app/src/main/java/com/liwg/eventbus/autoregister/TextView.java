package com.liwg.eventbus.autoregister;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import org.greenrobot.eventbus.Subscribe;

public class TextView extends View {
    public TextView(Context context) {
        super(context);
    }

    public TextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextView(Context context,AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Subscribe
    public void onEvent(Object object){

    }
}
