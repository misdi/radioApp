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

package com.ypyglobal.xradio;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.ypyglobal.xradio.constants.IXRadioConstants;
import com.ypyglobal.xradio.dataMng.TotalDataManager;
import com.ypyglobal.xradio.dataMng.XRadioNetUtils;
import com.ypyglobal.xradio.model.ConfigureModel;
import com.ypyglobal.xradio.ypylibs.activity.YPYSplashActivity;
import com.ypyglobal.xradio.ypylibs.executor.YPYExecutorSupplier;
import com.ypyglobal.xradio.ypylibs.utils.ShareActionUtils;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * @author:YPY Global

 * @Email: bl911vn@gmail.com
 * @Website: www.ypyglobal.com
 * @Date:Oct 20, 2017
 */

public class XRadioGrantActivity extends YPYSplashActivity implements IXRadioConstants,View.OnClickListener {

    @BindView(R.id.tv_info)
    TextView mTvInfo;

    private TotalDataManager mTotalMng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isNeedCheckGoogleService=false;
        super.onCreate(savedInstanceState);
        setUpOverlayBackground(true);

        mTotalMng = TotalDataManager.getInstance(getApplicationContext());

        String data=String.format(getString(R.string.format_request_permission),getString(R.string.app_name));
        mTvInfo.setText(Html.fromHtml(data));

    }

    @Override
    public int getResId() {
        return R.layout.activity_grant_permission;
    }

    @Override
    public void onInitData() {
        startCheckData();
    }

    @Override
    public File getDirectoryCached() {
        return mTotalMng.getDirectoryCached(getApplicationContext());
    }

    @Override
    public String[] getListPermissionNeedGrant() {
        return LIST_PERMISSIONS;
    }


    private void startCheckData() {
        YPYExecutorSupplier.getInstance().forBackgroundTasks().execute(() -> {
            mTotalMng.readConfigure(this);
            mTotalMng.readAllCache(this);
            runOnUiThread(this::goToMainActivity);
        });
    }



    public void goToMainActivity() {
        try {
            Intent mIntent = new Intent(this, XMultiRadioMainActivity.class);
            startActivity(mIntent);
            finish();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }


    @OnClick({R.id.tv_policy, R.id.tv_tos, R.id.btn_allow})
    @Override
    public void onClick(View view) {
        ConfigureModel model=mTotalMng.getConfigureModel();
        switch (view.getId()){
            case R.id.tv_policy:
                String url= model!=null && !TextUtils.isEmpty(model.getUrlEndPoint()) ?model.getUrlEndPoint()+ XRadioNetUtils.METHOD_PRIVACY_POLICY:URL_PRIVACY_POLICY;
                //goToUrl(getString(R.string.title_privacy_policy),url);
                ShareActionUtils.goToUrl(this,url);
                break;
            case R.id.tv_tos:
                String url1= model!=null && !TextUtils.isEmpty(model.getUrlEndPoint()) ?model.getUrlEndPoint()+ XRadioNetUtils.METHOD_TERM_OF_USE:URL_TERM_OF_USE;
                //goToUrl(getString(R.string.title_term_of_use),url1);
                ShareActionUtils.goToUrl(this,url1);
                break;
            case R.id.btn_allow:
                startGrantPermission();
                break;
        }
    }

    @Override
    public void onPermissionDenied() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backToHome();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

//    public void goToUrl(String name, String url) {
//        Intent mIntent = new Intent(this, XRadioShowUrlActivity.class);
//        mIntent.putExtra(KEY_HEADER, name);
//        mIntent.putExtra(KEY_SHOW_URL, url);
//        mIntent.putExtra(KEY_SHOW_ADS, false);
//        startActivity(mIntent);
//    }

    @Override
    public void onUpdateUIWhenSupportRTL() {
        super.onUpdateUIWhenSupportRTL();
        mTvInfo.setGravity(Gravity.END);
    }
}
