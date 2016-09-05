package com.zsg.jx.lightcontrol.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zsg.jx.lightcontrol.R;

@SuppressLint("ValidFragment")
public class SplashFragment extends Fragment {

    ImageView mivSplashImageView;
    int mImageId;
    public void initial(int id) {
        mImageId = id;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_splash_fragment, container, false);

        mivSplashImageView = (ImageView) rootView.findViewById(R.id.ivSplashImageView);
        mivSplashImageView.setImageResource(mImageId);
        return rootView;
    }
}
