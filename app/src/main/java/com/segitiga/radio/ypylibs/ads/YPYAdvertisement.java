package com.segitiga.radio.ypylibs.ads;

import android.content.Context;
import android.os.Handler;
import android.view.ViewGroup;

import com.segitiga.radio.ypylibs.task.IYPYCallback;


/**
 * @author:YPY Global
 * @Email: bl911vn@gmail.com
 * @Website: http://segitiga.com
 * Created by YPY Global on 2/22/18.
 */

public abstract class YPYAdvertisement {

    private static final long DEFAULT_TIME_OUT_LOAD_ADS = 15000;

    public Context mContext;
    private String adTypes;

    String testId;
    String bannerId;
    String mediumId;
    String interstitialId;

    Handler mHandlerAds = new Handler();
    long timeOutLoadAds=DEFAULT_TIME_OUT_LOAD_ADS;
    boolean isDestroy;


    YPYAdvertisement(Context mContext, String adTypes,
                     String bannerId, String interstitialId, String testId) {
        this.mContext = mContext;
        this.adTypes=adTypes;
        this.bannerId=bannerId;
        this.interstitialId=interstitialId;
        this.testId=testId;
    }

    YPYAdvertisement(Context mContext, String adTypes, String bannerId,
                     String interstitialId, String testId, long timeOutLoadAds) {
        this.mContext = mContext;
        this.adTypes = adTypes;
        this.timeOutLoadAds = timeOutLoadAds;
        this.bannerId=bannerId;
        this.interstitialId=interstitialId;
        this.testId=testId;
    }

    public String getAdTypes() {
        return adTypes;
    }

    public void setAdTypes(String adTypes) {
        this.adTypes = adTypes;
    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId = testId;
    }

    public String getBannerId() {
        return bannerId;
    }

    public void setBannerId(String bannerId) {
        this.bannerId = bannerId;
    }

    public String getMediumId() {
        return mediumId;
    }

    public void setMediumId(String mediumId) {
        this.mediumId = mediumId;
    }

    public abstract void setUpAdBanner(ViewGroup mLayoutAds, boolean isAllowShowAds);
    public abstract void setUpMediumBanner(ViewGroup mLayoutAds, boolean isAllowShowAds);
    public abstract void showInterstitialAd(boolean isAllowShowAds,IYPYCallback mCallback);
    public abstract void showLoopInterstitialAd(IYPYCallback mCallback);
    public abstract void setUpLoopInterstitial();

    public void onDestroy(){
        isDestroy=true;
        mHandlerAds.removeCallbacksAndMessages(null);
    }

}
