package com.codeworm.barkapp;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by Harvie Marcelino on 03/11/2018.
 */

public class LoadingDialog extends Dialog implements View.OnClickListener{
    public Activity activity;
    public Dialog dialog;
    ImageView imageView;
    AnimationDrawable animationDrawable;

    public LoadingDialog(Activity activity){
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);

        imageView = (ImageView) findViewById(R.id.img_loading);
        imageView.setBackgroundResource(R.drawable.animation_loading);

        animationDrawable = (AnimationDrawable)imageView.getBackground();
        animationDrawable.start();
    }

    @Override
    public void onClick(View view) {

    }
}
