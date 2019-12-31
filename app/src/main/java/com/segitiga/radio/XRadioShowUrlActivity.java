/*
 * Copyright (c) 2017. YPY Global - All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at.
 *
 *         http://ypyglobal.com/sourcecode/policy
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.segitiga.radio;

import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.segitiga.radio.ypylibs.utils.ApplicationUtils;
import com.segitiga.radio.ypylibs.utils.YPYLog;

import butterknife.BindView;

/**
 * @author:YPY Global

 * @Email: bl911vn@gmail.com
 * @Website: http://segitiga.com
 * Created by YPY Global on 10/19/17.
 */
public class XRadioShowUrlActivity extends XRadioFragmentActivity {

    public static final String TAG = XRadioShowUrlActivity.class.getSimpleName();
    public static final String KEY_HEADER = "KEY_HEADER";
    public static final String KEY_SHOW_URL = "KEY_SHOW_URL";
    public static final String KEY_SHOW_ADS = "KEY_SHOW_ADS";

    @BindView(R.id.progressBar1)
    ProgressBar mProgressBar;

    @BindView(R.id.webview)
    WebView mWebViewShowPage;

    private String mUrl;
    private String mNameHeader;

    private boolean isShowAds;

    @Override
    protected void onDoBeforeSetView() {
        super.onDoBeforeSetView();
    }

    @Override
    public int getResId() {
        return R.layout.activity_show_url;
    }


    @Override
    public void onDoWhenDone() {
        Intent args = getIntent();
        if (args != null) {
            mUrl = args.getStringExtra(KEY_SHOW_URL);
            mNameHeader = args.getStringExtra(KEY_HEADER);
            isShowAds = args.getBooleanExtra(KEY_SHOW_ADS,true);
            YPYLog.d(TAG, "===========>url=" + mUrl);
        }
        if(TextUtils.isEmpty(mUrl)){
            backToHome();
            return;
        }
        super.onDoWhenDone();
        setUpCustomizeActionBar(Color.TRANSPARENT,true);
        if (!TextUtils.isEmpty(mNameHeader)) {
            setActionBarTitle(mNameHeader);
        }
        mWebViewShowPage.getSettings().setJavaScriptEnabled(true);
        mWebViewShowPage.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(mProgressBar!=null){
                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });

        if(ApplicationUtils.isOnline(this)){
            if(!mUrl.startsWith("http")){
                mUrl = "http://"+mUrl;
            }
            mWebViewShowPage.loadUrl(mUrl);
        }
    }
    @Override
    public void setUpLayoutBanner() {
        if(isShowAds){
            super.setUpLayoutBanner();
        }
        else{
            mLayoutAds=findViewById(R.id.layout_ads);
            if(mLayoutAds!=null){
                mLayoutAds.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onDoWhenNetworkOn() {
        super.onDoWhenNetworkOn();
        if(mWebViewShowPage!=null){
            mWebViewShowPage.loadUrl(mUrl);
        }
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mWebViewShowPage != null) {
            mWebViewShowPage.destroy();
        }
    }

    @Override
    public boolean backToHome() {
        finish();
        return true;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebViewShowPage.canGoBack()) {
                mWebViewShowPage.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
