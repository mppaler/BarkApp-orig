package com.codeworm.barkapp;

import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class LoadingActivity extends AppCompatActivity {
    ImageView imageView;
    AnimationDrawable animationDrawable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        imageView = (ImageView) findViewById(R.id.img_loading);
        imageView.setBackgroundResource(R.drawable.animation_loading);

        animationDrawable = (AnimationDrawable)imageView.getBackground();
        animationDrawable.start();
    }
}
