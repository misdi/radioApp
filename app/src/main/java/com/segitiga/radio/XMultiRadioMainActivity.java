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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.behavior.model.FixAppBarLayoutBehavior;
import com.behavior.model.YPYBottomSheetBehavior;
import com.google.ads.consent.ConsentInformation;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;
import com.segitiga.radio.dataMng.XRadioNetUtils;
import com.segitiga.radio.fragment.FragmentDetailList;
import com.segitiga.radio.fragment.FragmentDragDrop;
import com.segitiga.radio.fragment.FragmentFavorite;
import com.segitiga.radio.fragment.FragmentGenre;
import com.segitiga.radio.fragment.FragmentTheme;
import com.segitiga.radio.fragment.FragmentTopChart;
import com.segitiga.radio.gdpr.GDPRManager;
import com.segitiga.radio.model.ConfigureModel;
import com.segitiga.radio.model.GenreModel;
import com.segitiga.radio.model.RadioModel;
import com.segitiga.radio.model.UIConfigModel;
import com.segitiga.radio.setting.XRadioSettingManager;
import com.segitiga.radio.stream.constant.IYPYStreamConstants;
import com.segitiga.radio.stream.manager.YPYStreamManager;
import com.segitiga.radio.stream.mediaplayer.YPYMediaPlayer;
import com.segitiga.radio.ypylibs.fragment.YPYFragment;
import com.segitiga.radio.ypylibs.fragment.YPYFragmentAdapter;
import com.segitiga.radio.ypylibs.imageloader.GlideImageLoader;
import com.segitiga.radio.ypylibs.listener.IYPYSearchViewInterface;
import com.segitiga.radio.ypylibs.task.IYPYCallback;
import com.segitiga.radio.ypylibs.utils.ApplicationUtils;
import com.segitiga.radio.ypylibs.utils.ShareActionUtils;
import com.segitiga.radio.ypylibs.view.CircularProgressBar;
import com.segitiga.radio.ypylibs.view.YPYViewPager;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author:YPY Global

 * @Email: bl911vn@gmail.com
 * @Website: http://segitiga.com
 * Created by YPY Global on 10/19/17.
 */
public class XMultiRadioMainActivity extends XRadioFragmentActivity implements IYPYStreamConstants, View.OnClickListener {

    public static final String TAG = XMultiRadioMainActivity.class.getSimpleName();
    private static final String KEY_TOP_INDEX = "view_pager_index";

    @BindView(R.id.tab_layout)
    TabLayout mTabLayout;

    @BindView(R.id.app_bar)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.view_pager)
    YPYViewPager mViewpager;

    @BindView(R.id.container)
    FrameLayout mLayoutContainer;

    @BindView(R.id.btn_small_play)
    ImageView mBtnSmallPlay;

    @BindView(R.id.btn_small_next)
    ImageView mBtnSmallNext;

    @BindView(R.id.btn_small_prev)
    ImageView mBtnSmallPrev;

    @BindView(R.id.img_song)
    ImageView mImgSmallSong;

    @BindView(R.id.tv_radio_name)
    TextView mTvRadioName;

    @BindView(R.id.tv_info)
    TextView mTvSmallInfo;

    @BindView(R.id.img_status_loading)
    CircularProgressBar mProgressBar;

    @BindView(R.id.layout_small_control)
    RelativeLayout mLayoutSmallControl;

    @BindView(R.id.drag_drop_container)
    FrameLayout mLayoutDragDropContainer;

    @BindView(R.id.layout_total_drag_drop)
    View mLayoutTotalDragDrop;

    private int mStartHeight;

    private ConfigureModel mConfigureModel;
    private UIConfigModel mUIConfigModel;

    private ArrayList<Fragment> mListHomeFragments = new ArrayList<>();

    private FragmentTopChart mFragmentTopChart;
    private FragmentFavorite mFragmentFavorite;
    private YPYBottomSheetBehavior<View> mBottomSheetBehavior;

    public String mUrlHost;
    public String mApiKey;
    private ApplicationBroadcast mApplicationBroadcast;
    private FragmentDragDrop mFragmentDragDrop;
    private int countInterstitial;

    public boolean isAllCheckNetWorkOff;
    private int mCurrentIndex=-1;

    @Override
    public int getResId() {
        return R.layout.activity_app_bar_main;
    }


    @Override
    public void onDoWhenDone() {
        super.onDoWhenDone();

        XRadioSettingManager.setOnline(this, true);
        if(mSavedInstance!=null){
            mCurrentIndex=mSavedInstance.getInt(KEY_TOP_INDEX,-1);
        }

        resetTimer();
        setUpActionBar();

        ((CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams()).setBehavior(new FixAppBarLayoutBehavior());
        setIsAllowPressMoreToExit(true);
        setUpDragDropLayout();

        mFragmentDragDrop = (FragmentDragDrop) getSupportFragmentManager().findFragmentById(R.id.fragment_drag_drop);
        findViewById(R.id.img_touch).setOnTouchListener((v, event) -> true);

        setUpTab();
        showAppRate();

        setUpColorWidget();
        registerApplicationBroadcastReceiver();

        //TODO WHEN SAVED INSTANCE !=NULL
        if(mListFragments!=null && mListFragments.size()>0){
            showHideLayoutContainer(true);
            YPYFragment mFragment = (YPYFragment) mListFragments.get(mListFragments.size()-1);
            if(!TextUtils.isEmpty(mFragment.getScreenName())){
                setActionBarTitle(mFragment.getScreenName());
            }
        }
        if(mSavedInstance!=null){
            boolean b=isHavingListStream();
            showLayoutListenMusic(b);
            if(b){
                updateInfoOfPlayingTrack(true);
                updateStatePlayer(YPYStreamManager.getInstance().isPlaying());
                YPYMediaPlayer.StreamInfo mStrInfo = YPYStreamManager.getInstance().getStreamInfo();
                processUpdateImage(mStrInfo!=null?mStrInfo.imgUrl:null);
            }
        }


    }

    private void setUpDragDropLayout() {
        findViewById(R.id.img_fake_touch).setOnTouchListener((v, event) -> true);
        mStartHeight = getResources().getDimensionPixelOffset(R.dimen.size_img_big);
        mLayoutSmallControl.setOnClickListener(view -> expandLayoutListenMusic());

        mBottomSheetBehavior = (YPYBottomSheetBehavior<View>) BottomSheetBehavior.from(mLayoutTotalDragDrop);
        mBottomSheetBehavior.setPeekHeight(mStartHeight);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            boolean isHidden;
            float mSlideOffset;
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                try {
                    if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                        showAppBar(false);
                        showHeaderMusicPlayer(true);
                        enableDragForBottomSheet(ALLOW_DRAG_DROP_WHEN_EXPAND);
                    }
                    else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                        isHidden = false;
                        showAppBar(true);
                        enableDragForBottomSheet(true);
                        showHeaderMusicPlayer(false);
                        if (!isHavingListStream()) {
                            showLayoutListenMusic(false);
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                try {
                    if (mSlideOffset > 0 && slideOffset > mSlideOffset && !isHidden) {
                        showAppBar(false);
                        isHidden = true;
                    }
                    mSlideOffset = slideOffset;
                    mLayoutSmallControl.setVisibility(View.VISIBLE);
                    mLayoutDragDropContainer.setVisibility(View.VISIBLE);
                    mLayoutSmallControl.setAlpha(1f - slideOffset);
                    mLayoutDragDropContainer.setAlpha(slideOffset);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        showLayoutListenMusic(false);
    }

    private boolean isHavingListStream(){
        try{
            ArrayList<RadioModel> mListObjects = YPYStreamManager.getInstance().getListMusicRadio();
            return mListObjects!=null && mListObjects.size()>0;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;

    }

    private void showHeaderMusicPlayer(boolean b) {
        mLayoutSmallControl.setVisibility(!b ? View.VISIBLE : View.GONE);
        mLayoutDragDropContainer.setVisibility(b ? View.VISIBLE : View.INVISIBLE);
    }

    public void showAppBar(boolean b) {
        if (mAppBarLayout != null) {
            mAppBarLayout.setExpanded(b);
        }
    }

    private void showLayoutListenMusic(boolean b) {
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED && !b) {
            return;
        }
        mLayoutTotalDragDrop.setVisibility(b ? View.VISIBLE : View.GONE);
        mViewpager.setPadding(0, 0, 0, b ? mStartHeight : 0);
        mLayoutContainer.setPadding(0, 0, 0, b ? mStartHeight : 0);
        if (!b) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private void expandLayoutListenMusic() {
        if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            if (mFragmentDragDrop != null) {
                mFragmentDragDrop.updateVolume();
            }
            enableDragForBottomSheet(ALLOW_DRAG_DROP_WHEN_EXPAND);
        }
    }

    public boolean collapseListenMusic() {
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            enableDragForBottomSheet(ALLOW_DRAG_DROP_WHEN_EXPAND);
            return true;
        }
        return false;
    }

    public void enableDragForBottomSheet(boolean b) {
        mBottomSheetBehavior.setAllowUserDragging(b);
    }


    private void setUpActionBar() {
        mConfigureModel = mTotalMng.getConfigureModel();
        mUIConfigModel = mTotalMng.getUiConfigModel();

        removeElevationActionBar();
        setUpCustomizeActionBar(Color.TRANSPARENT);
        setActionBarTitle(R.string.title_home_screen);

        mUrlHost = mConfigureModel != null ? mConfigureModel.getUrlEndPoint() : null;
        mApiKey = mConfigureModel != null ? mConfigureModel.getApiKey() : null;
    }

    private void setUpColorWidget() {
        try {
            int typeBg = mUIConfigModel != null ? mUIConfigModel.getIsFullBg() : UI_BG_JUST_ACTIONBAR;
            if (typeBg == UI_BG_FULL) {
                mLayoutContainer.setBackgroundColor(Color.TRANSPARENT);
                mViewpager.setBackgroundColor(Color.TRANSPARENT);
                mTabLayout.setBackgroundColor(getResources().getColor(R.color.tab_overlay_color));
                if (mLayoutAds != null) {
                    mLayoutAds.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateBackground() {
        super.updateBackground();
        if (mLayoutSmallControl != null) {
            int startColor = parseColor(XRadioSettingManager.getStartColor(this));
            int endColor = parseColor(XRadioSettingManager.getEndColor(this));
            if (startColor != 0 || endColor != 0) {
                GradientDrawable gradientDrawable = getGradientDrawable(startColor, 0, endColor);
                mLayoutSmallControl.setBackground(gradientDrawable);
            }
        }
        if (mFragmentDragDrop != null) {
            mFragmentDragDrop.updateBackground();
        }
    }

    private void setUpTab() {
        if (mTabLayout == null) {
            mTabLayout = findViewById(R.id.tab_layout);
        }
        if (mViewpager == null) {
            mViewpager = findViewById(R.id.view_pager);
        }
        if (mTabLayout == null || mViewpager == null) return;

        mTabLayout.setTabTextColors(getResources().getColor(R.color.tab_text_normal_color),
                getResources().getColor(R.color.tab_text_focus_color));
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mTabLayout.setTabRippleColor(null);
        ViewCompat.setElevation(mTabLayout, 0f);
        mViewpager.setPagingEnabled(true);

        int uiGenre = mUIConfigModel!=null?mUIConfigModel.getUiGenre():UI_MAGIC_GRID;
        int uiTheme = mUIConfigModel!=null?mUIConfigModel.getUiThemes():UI_CARD_GRID;

        boolean isOnlineApp = mConfigureModel != null && mConfigureModel.isOnlineApp();
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.title_tab_top_chart));
        if(uiGenre>UI_HIDDEN){
            mTabLayout.addTab(mTabLayout.newTab().setText(R.string.title_tab_discover));
        }
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.title_tab_favorite));

        if(uiTheme>UI_HIDDEN){
            mTabLayout.addTab(mTabLayout.newTab().setText(R.string.title_tab_themes));
        }

        Bundle mBundle1 = new Bundle();
        mBundle1.putInt(KEY_TYPE_FRAGMENT, TYPE_TAB_FEATURED);
        mBundle1.putBoolean(KEY_IS_TAB, true);
        mBundle1.putBoolean(KEY_ALLOW_READ_CACHE, true);
        mBundle1.putBoolean(KEY_ALLOW_MORE, isOnlineApp);
        mBundle1.putBoolean(KEY_ALLOW_REFRESH, isOnlineApp);
        mBundle1.putBoolean(KEY_READ_CACHE_WHEN_NO_DATA, true);
        mFragmentTopChart = (FragmentTopChart) Fragment.instantiate(this, FragmentTopChart.class.getName(), mBundle1);
        mListHomeFragments.add(mFragmentTopChart);

        if(uiGenre>UI_HIDDEN){
            Bundle mBundle2 = new Bundle();
            mBundle2.putInt(KEY_TYPE_FRAGMENT, TYPE_TAB_GENRE);
            mBundle2.putBoolean(KEY_IS_TAB, true);
            mBundle2.putBoolean(KEY_ALLOW_READ_CACHE, true);
            mBundle2.putBoolean(KEY_ALLOW_REFRESH, isOnlineApp);
            mBundle2.putBoolean(KEY_READ_CACHE_WHEN_NO_DATA, true);
            FragmentGenre mFragmentGenre = (FragmentGenre) Fragment.instantiate(this, FragmentGenre.class.getName(), mBundle2);
            mListHomeFragments.add(mFragmentGenre);
        }

        Bundle mBundle3 = new Bundle();
        mBundle3.putInt(KEY_TYPE_FRAGMENT, TYPE_TAB_FAVORITE);
        mBundle3.putBoolean(KEY_IS_TAB, true);
        mBundle3.putBoolean(KEY_OFFLINE_DATA, true);
        mBundle3.putBoolean(KEY_ALLOW_REFRESH, false);
        mBundle3.putBoolean(KEY_ALLOW_SHOW_NO_DATA, false);
        mFragmentFavorite = (FragmentFavorite) Fragment.instantiate(this, FragmentFavorite.class.getName(), mBundle3);
        mListHomeFragments.add(mFragmentFavorite);

        if(uiTheme>UI_HIDDEN){
            Bundle mBundle4 = new Bundle();
            mBundle4.putInt(KEY_TYPE_FRAGMENT, TYPE_TAB_THEMES);
            mBundle4.putBoolean(KEY_IS_TAB, true);
            mBundle4.putBoolean(KEY_ALLOW_MORE, true);
            mBundle4.putBoolean(KEY_ALLOW_READ_CACHE, true);
            mBundle4.putBoolean(KEY_READ_CACHE_WHEN_NO_DATA, true);
            FragmentTheme mFragmentThemes = (FragmentTheme) Fragment.instantiate(this, FragmentTheme.class.getName(), mBundle4);
            mListHomeFragments.add(mFragmentThemes);
        }

        if(mCurrentIndex>=0){
            ((YPYFragment)mListHomeFragments.get(mCurrentIndex)).setFirstInTab(true);
        }
        else{
            if (!ApplicationUtils.isOnline(this)) {
                mFragmentFavorite.setFirstInTab(true);
            }
            else {
                mFragmentTopChart.setFirstInTab(true);
            }
        }

        YPYFragmentAdapter mTabAdapters = new YPYFragmentAdapter(getSupportFragmentManager(), mListHomeFragments, mViewpager);
        mViewpager.setAdapter(mTabAdapters);
        mViewpager.setOffscreenPageLimit(mListHomeFragments.size());

        mViewpager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout) {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                hiddenKeyBoardForSearchView();
                int pos = tab.getPosition();
                mAppBarLayout.setExpanded(true);
                mViewpager.setCurrentItem(pos);
                ((YPYFragment) mListHomeFragments.get(pos)).startLoadData();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        if(mCurrentIndex>=0){
            mViewpager.setCurrentItem(mCurrentIndex);
        }
        else{
            if (!ApplicationUtils.isOnline(this)) {
                mViewpager.setCurrentItem(mListHomeFragments.indexOf(mFragmentFavorite));
            }
            else {
                mViewpager.setCurrentItem(0);
            }
        }
    }

    @Override
    public void onDestroyData() {
        XRadioSettingManager.setOnline(this, false);
        resetTimer();
        if (isHavingListStream()) {
            startMusicService(ACTION_STOP);
        }
        else {
            YPYStreamManager.getInstance().onDestroy();
        }
        super.onDestroyData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            menu.findItem(R.id.action_facebook).setVisible(!TextUtils.isEmpty(URL_FACEBOOK));
            menu.findItem(R.id.action_insta).setVisible(!TextUtils.isEmpty(URL_INSTAGRAM));
            menu.findItem(R.id.action_website).setVisible(!TextUtils.isEmpty(URL_WEBSITE));
            menu.findItem(R.id.action_twitter).setVisible(!TextUtils.isEmpty(URL_TWITTER));

            ConsentInformation consentInformation = ConsentInformation.getInstance(this);
            boolean b = consentInformation.isRequestLocationInEeaOrUnknown();
            menu.findItem(R.id.action_setting_ads).setVisible(b);

            initSetupForSearchView(menu, R.id.action_search, new IYPYSearchViewInterface() {
                @Override
                public void onStartSuggestion(String keyword) {

                }

                @Override
                public void onProcessSearchData(String keyword) {
                    if (!TextUtils.isEmpty(keyword)) {
                        searchView.setQuery(keyword, false);
                        goToSearch(keyword);
                    }
                }

                @Override
                public void onClickSearchView() {

                }

                @Override
                public void onCloseSearchView() {

                }
            });
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    private void goToSearch(String keyword) {
        if (!TextUtils.isEmpty(keyword)) {
            hiddenKeyBoardForSearchView();
            FragmentDetailList mFragmentSearch = (FragmentDetailList) getSupportFragmentManager().findFragmentByTag(TAG_FRAGMENT_DETAIL_SEARCH);
            if (mFragmentSearch != null) {
                mFragmentSearch.startSearch(keyword);
            }
            else {
                boolean isOnlineApp = mConfigureModel != null && mConfigureModel.isOnlineApp();
                backStack();
                setActionBarTitle(R.string.title_search);
                showHideLayoutContainer(true);
                Bundle mBundle = new Bundle();
                mBundle.putInt(KEY_TYPE_FRAGMENT, TYPE_SEARCH);
                mBundle.putBoolean(KEY_ALLOW_MORE, isOnlineApp);
                mBundle.putString(KEY_SEARCH, keyword);
                mBundle.putBoolean(KEY_ALLOW_READ_CACHE, false);
                mBundle.putBoolean(KEY_ALLOW_REFRESH, false);
                mBundle.putString(KEY_NAME_SCREEN, getString(R.string.title_search));

                goToFragment(TAG_FRAGMENT_DETAIL_SEARCH, R.id.container, FragmentDetailList.class.getName(), 0, mBundle);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sleep_mode:
                showDialogSleepMode();
                break;
            case R.id.action_rate_me:
                String urlApp = String.format(URL_FORMAT_LINK_APP, getPackageName());
                ShareActionUtils.goToUrl(this, urlApp);
                XRadioSettingManager.setRateApp(this, true);
                break;
            case R.id.action_share:
                String urlApp1 = String.format(URL_FORMAT_LINK_APP, getPackageName());
                String msg = String.format(getString(R.string.info_share_app), getString(R.string.app_name), urlApp1);
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/*");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, msg);
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.title_menu_share)));
                break;
            case R.id.action_contact_us:
                ShareActionUtils.shareViaEmail(this, YOUR_CONTACT_EMAIL, "", "");
                break;
            case R.id.action_facebook:
                goToUrl(getString(R.string.title_facebook), URL_FACEBOOK);
                break;
            case R.id.action_twitter:
                goToUrl(getString(R.string.title_twitter), URL_TWITTER);
                break;
            case R.id.action_website:
                goToUrl(getString(R.string.title_website), URL_WEBSITE);
                break;
            case R.id.action_insta:
                goToUrl(getString(R.string.title_instagram), URL_INSTAGRAM);
                break;
            case R.id.action_term_of_use:
                String host = mConfigureModel != null ? mConfigureModel.getUrlEndPoint() : null;
                if (!TextUtils.isEmpty(host)) {
                    goToUrl(getString(R.string.title_term_of_use), host + XRadioNetUtils.METHOD_TERM_OF_USE);
                }
                else{
                    goToUrl(getString(R.string.title_term_of_use), URL_TERM_OF_USE);
                }
                break;
            case R.id.action_privacy_policy:
                String host1 = mConfigureModel != null ? mConfigureModel.getUrlEndPoint() : null;
                if (!TextUtils.isEmpty(host1)) {
                    goToUrl(getString(R.string.title_privacy_policy), host1 + XRadioNetUtils.METHOD_PRIVACY_POLICY);
                }
                else{
                    goToUrl(getString(R.string.title_privacy_policy), URL_PRIVACY_POLICY);
                }
                break;
            case R.id.action_setting_ads:
                GDPRManager.getInstance().showDialogConsent(this,null);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                if (mFragmentDragDrop != null) {
                    mFragmentDragDrop.increaseVolume();
                }
                return true;
            }

        }
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                if (mFragmentDragDrop != null) {
                    mFragmentDragDrop.downVolume();
                }
                return true;
            }
        }
        else if (keyCode == KeyEvent.KEYCODE_MEDIA_NEXT) {
            if(ApplicationUtils.isOnline(this) && isHavingListStream()){
                startMusicService(ACTION_NEXT);
                return true;
            }
        }
        else if (keyCode == KeyEvent.KEYCODE_MEDIA_PREVIOUS) {
            if(ApplicationUtils.isOnline(this) && isHavingListStream()){
                startMusicService(ACTION_PREVIOUS);
                return true;
            }

        }
        else if (keyCode == KeyEvent.KEYCODE_MEDIA_PAUSE) {
            if(ApplicationUtils.isOnline(this) && isHavingListStream()){
                if(YPYStreamManager.getInstance().isPlaying()){
                    startMusicService(ACTION_TOGGLE_PLAYBACK);
                    return true;
                }
            }

        }
        else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY) {
            if(ApplicationUtils.isOnline(this) && isHavingListStream()){
                if(YPYStreamManager.getInstance().isPrepareDone() &&
                        !YPYStreamManager.getInstance().isPlaying()){
                    startMusicService(ACTION_TOGGLE_PLAYBACK);
                    return true;
                }
            }

        }
        else if (keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
            if(ApplicationUtils.isOnline(this) && isHavingListStream()){
                startMusicService(ACTION_TOGGLE_PLAYBACK);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean backToHome() {
        if (collapseListenMusic()) {
            return true;
        }
        boolean b = super.backToHome();
        if (b) {
            return true;
        }
        b = backStack();
        if (b) {
            showHideLayoutContainer(false);
            return true;
        }
        return false;
    }

    @Override
    public boolean isFragmentCheckBack() {
        try{
            if (mListHomeFragments != null && mListHomeFragments.size() > 0) {
                for (Fragment mFragment : mListHomeFragments) {
                    if (mFragment instanceof YPYFragment) {
                        boolean isBack = ((YPYFragment) mFragment).isCheckBack();
                        if (isBack) {
                            return true;
                        }
                    }
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return super.isFragmentCheckBack();
    }

    public void showHideLayoutContainer(boolean b) {
        mLayoutContainer.setVisibility(b ? View.VISIBLE : View.GONE);
        mTabLayout.setVisibility(b ? View.GONE : View.VISIBLE);
        mViewpager.setVisibility(b ? View.GONE : View.VISIBLE);

        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(b);
            getSupportActionBar().setHomeButtonEnabled(b);
            getSupportActionBar().setDisplayUseLogoEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            if (b) {
                mAppBarLayout.setExpanded(true);
                getSupportActionBar().setHomeAsUpIndicator(mBackDrawable);
            }
            else {
                setActionBarTitle(R.string.title_home_screen);
            }
        }

    }

    @Override
    public void notifyFavorite(int type, long id, boolean isFav) {
        super.notifyFavorite(type, id, isFav);
        if (mFragmentTopChart != null) {
            mFragmentTopChart.notifyFavorite(id, isFav);
        }
        runOnUiThread(() -> {
            if (mFragmentFavorite != null) {
                mFragmentFavorite.notifyData();
            }
            if (mFragmentDragDrop != null) {
                mFragmentDragDrop.notifyFavorite(id, isFav);
            }
        });

    }

    public void goToGenreModel(GenreModel model) {
        if (model != null) {
            boolean isOnlineApp = mConfigureModel != null && mConfigureModel.isOnlineApp();

            setActionBarTitle(model.getName());
            showHideLayoutContainer(true);
            Bundle mBundle = new Bundle();
            mBundle.putInt(KEY_TYPE_FRAGMENT, TYPE_DETAIL_GENRE);
            mBundle.putBoolean(KEY_ALLOW_MORE, true);
            mBundle.putBoolean(KEY_ALLOW_READ_CACHE, false);
            mBundle.putBoolean(KEY_ALLOW_REFRESH, true);
            mBundle.putString(KEY_NAME_SCREEN, model.getName());
            mBundle.putBoolean(KEY_ALLOW_REFRESH, isOnlineApp);

            mBundle.putLong(KEY_GENRE_ID, model.getId());

            String tag = getCurrentFragmentTag();
            if (TextUtils.isEmpty(tag)) {
                goToFragment(TAG_FRAGMENT_DETAIL_GENRE, R.id.container, FragmentDetailList.class.getName(), 0, mBundle);
            }
            else {
                goToFragment(TAG_FRAGMENT_DETAIL_GENRE, R.id.container, FragmentDetailList.class.getName(), tag, mBundle);
            }
        }
    }

    public void startPlayingList(RadioModel model, ArrayList<RadioModel> listRadioModels) {
        if (!ApplicationUtils.isOnline(this)) {
            if(isAllCheckNetWorkOff){
                showToast(R.string.info_connect_to_play);
                return;
            }
            if (YPYStreamManager.getInstance().isPrepareDone()) {
                startMusicService(ACTION_STOP);
            }
            showToast(R.string.info_connect_to_play);
            return;
        }
        RadioModel currentRadio = YPYStreamManager.getInstance().getCurrentRadio();
        if (currentRadio != null && currentRadio.equals(model)) {
            return;
        }
        showModeInterstitial(()->playRadio(model,listRadioModels));
    }

    private void playRadio(RadioModel model, ArrayList<RadioModel> listRadioModels){
        updateInfoOfPlayingTrack(model, true);
        String url = model != null ? model.getArtWork(mUrlHost) : null;
        if (mFragmentDragDrop != null) {
            mFragmentDragDrop.updateImage(url);
        }
        if (listRadioModels != null && listRadioModels.size() > 0) {
            ArrayList<RadioModel> mListPlaying = YPYStreamManager.getInstance().getListMusicRadio();
            if (mListPlaying == null || !mTotalMng.isListEqual(mListPlaying, listRadioModels)) {
                ArrayList<RadioModel> mListDatas = (ArrayList<RadioModel>) listRadioModels.clone();
                YPYStreamManager.getInstance().setListMusicRadio(mListDatas);

            }
            startPlayRadio(model);
        }
    }

    public void startPlayRadio(RadioModel trackModel) {
        try {
            mBtnSmallPlay.setImageResource(R.drawable.ic_play_arrow_white_36dp);
            boolean b = YPYStreamManager.getInstance().setCurrentData(trackModel);
            if (b) {
                startMusicService(ACTION_PLAY);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            mBtnSmallPlay.setImageResource(R.drawable.ic_play_arrow_white_36dp);
            startMusicService(ACTION_STOP);
        }

    }

    private void updateInfoOfPlayingTrack(boolean isNeedUpdateSocial) {
        RadioModel ringtoneModel = YPYStreamManager.getInstance().getCurrentRadio();
        updateInfoOfPlayingTrack(ringtoneModel, isNeedUpdateSocial);
    }

    private void updateInfoOfPlayingTrack(RadioModel ringtoneModel, boolean isNeedUpdateSocial) {
        try {
            if (ringtoneModel != null) {
                showLayoutListenMusic(true);
                mTvRadioName.setText(Html.fromHtml(ringtoneModel.getName()));
                String artist = ringtoneModel.getMetaData();
                if (TextUtils.isEmpty(artist)) {
                    artist = ringtoneModel.getTags();
                    if (TextUtils.isEmpty(artist)) {
                        artist = getString(R.string.title_unknown);
                    }
                }
                mTvSmallInfo.setText(artist);
                mTvSmallInfo.setSelected(true);

                String imgSong = ringtoneModel.getArtWork(mUrlHost);
                if (!TextUtils.isEmpty(imgSong)) {
                    GlideImageLoader.displayImage(this, mImgSmallSong, imgSong, R.drawable.ic_rect_img_default);
                }
                else {
                    mImgSmallSong.setImageResource(R.drawable.ic_rect_img_default);
                }
                if (mFragmentDragDrop != null) {
                    mFragmentDragDrop.updateInfo(isNeedUpdateSocial);
                }

            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void registerApplicationBroadcastReceiver() {
        if (mApplicationBroadcast != null) {
            return;
        }
        mApplicationBroadcast = new ApplicationBroadcast();
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(getPackageName() + ACTION_BROADCAST_PLAYER);
        registerReceiver(mApplicationBroadcast, mIntentFilter);
    }

    private class ApplicationBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent != null) {
                    String action = intent.getAction();
                    if (!TextUtils.isEmpty(action)) {
                        String packageName = getPackageName();
                        if (action.equals(packageName + ACTION_BROADCAST_PLAYER)) {
                            String actionPlay = intent.getStringExtra(KEY_ACTION);
                            if (!TextUtils.isEmpty(actionPlay)) {
                                if (actionPlay.equalsIgnoreCase(ACTION_UPDATE_COVER_ART)) {
                                    String value = intent.getStringExtra(KEY_VALUE);
                                    processUpdateImage(value);

                                }
                                else {
                                    long value = intent.getLongExtra(KEY_VALUE, -1);
                                    processBroadcast(actionPlay, value);
                                }

                            }
                        }

                    }
                }

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void processBroadcast(String actionPlay, long value) {
        if (actionPlay.equalsIgnoreCase(ACTION_LOADING)) {
            showLoading(true);
            updateInfoOfPlayingTrack(true);
            if (mFragmentDragDrop != null) {
                mFragmentDragDrop.showLoading(true);
                mFragmentDragDrop.updateInfo(false);
                RadioModel model = YPYStreamManager.getInstance().getCurrentRadio();
                mFragmentDragDrop.updateImage(model != null ? model.getArtWork(mUrlHost) : null);
            }
        }
        if (actionPlay.equalsIgnoreCase(ACTION_DIMINISH_LOADING)) {
            showLoading(false);
            if (mFragmentDragDrop != null) {
                mFragmentDragDrop.showLoading(false);
                mFragmentDragDrop.showLayoutControl();
            }
        }
        if (actionPlay.equalsIgnoreCase(ACTION_RESET_INFO)) {
            if (mFragmentDragDrop != null) {
                mFragmentDragDrop.updateInfo(false);
                mFragmentDragDrop.updateImage(null);
            }
        }
        if (actionPlay.equalsIgnoreCase(ACTION_COMPLETE)) {
            updateStatePlayer(false);
            mTvSmallInfo.setText(R.string.info_radio_ended_title);
            if (mFragmentDragDrop != null) {
                mFragmentDragDrop.updateInfoWhenComplete();
                mFragmentDragDrop.updateImage(null);
            }
        }
        if (actionPlay.equalsIgnoreCase(ACTION_CONNECTION_LOST)) {
            updateStatePlayer(false);
            mTvSmallInfo.setText(R.string.info_connection_lost);
            if (mFragmentDragDrop != null) {
                mFragmentDragDrop.updateInfoWhenComplete();
                mFragmentDragDrop.updateImage(null);
            }
        }
        else if (actionPlay.equalsIgnoreCase(ACTION_BUFFERING)) {
            showLoading(true);
            if (mFragmentDragDrop != null) {
                mFragmentDragDrop.showLoading(false);
                mFragmentDragDrop.updatePercent(value);
            }
        }
        else if (actionPlay.equalsIgnoreCase(ACTION_PAUSE)) {
            updateStatePlayer(false);
        }
        else if (actionPlay.equalsIgnoreCase(ACTION_PLAY)) {
            updateStatePlayer(true);
        }
        else if (actionPlay.equalsIgnoreCase(ACTION_STOP) || actionPlay.equalsIgnoreCase(ACTION_ERROR)) {
            updateStatePlayer(false);
            showLayoutListenMusic(false);
            if (mFragmentDragDrop != null) {
                mFragmentDragDrop.updateSleepMode(0);
                mFragmentDragDrop.updateStatusPlayer(false);
            }
            collapseListenMusic();
            if (actionPlay.equalsIgnoreCase(ACTION_ERROR)) {
                int resId = ApplicationUtils.isOnline(this) ? R.string.info_play_error : R.string.info_connect_to_play;
                showToast(resId);
            }
        }
        else if (actionPlay.equalsIgnoreCase(ACTION_UPDATE_INFO)) {
            updateInfoOfPlayingTrack(false);
        }
        else if (actionPlay.equalsIgnoreCase(ACTION_UPDATE_SLEEP_MODE)) {
            if (mFragmentDragDrop != null) {
                mFragmentDragDrop.updateSleepMode(value);
            }

        }

    }

    @Override
    public void onDoWhenNetworkOn() {
        super.onDoWhenNetworkOn();
        if(isHavingListStream()){
            if(isAllCheckNetWorkOff){
                isAllCheckNetWorkOff=false;
                startMusicService(ACTION_TOGGLE_PLAYBACK);
            }
        }

    }

    @Override
    public void onDoWhenNetworkOff() {
        super.onDoWhenNetworkOff();
        if(isHavingListStream()){
            isAllCheckNetWorkOff=true;
            startMusicService(ACTION_CONNECTION_LOST);
        }
    }

    public void processUpdateImage(String imgSong) {
        try {
            if (TextUtils.isEmpty(imgSong)) {
                RadioModel ringtoneModel = YPYStreamManager.getInstance().getCurrentRadio();
                imgSong = ringtoneModel.getArtWork(mUrlHost);
            }
            if (!TextUtils.isEmpty(imgSong)) {
                GlideImageLoader.displayImage(this, mImgSmallSong, imgSong, R.drawable.ic_rect_img_default);
            }
            else {
                mImgSmallSong.setImageResource(R.drawable.ic_rect_img_default);
            }
            if (mFragmentDragDrop != null) {
                mFragmentDragDrop.updateImage(imgSong);
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showLoading(boolean b) {
        mBtnSmallPlay.setVisibility(!b ? View.VISIBLE : View.INVISIBLE);
        mBtnSmallNext.setVisibility(!b ? View.VISIBLE : View.INVISIBLE);
        mBtnSmallPrev.setVisibility(!b ? View.VISIBLE : View.INVISIBLE);
        mProgressBar.setVisibility(b ? View.VISIBLE : View.GONE);
    }

    public void updateStatePlayer(boolean isPlaying) {
        int playId = isPlaying ? R.drawable.ic_pause_white_36dp : R.drawable.ic_play_arrow_white_36dp;
        mBtnSmallPlay.setImageResource(playId);
        if (mFragmentDragDrop != null) {
            mFragmentDragDrop.updateStatusPlayer(isPlaying);
        }

    }

    @Override
    protected void onDestroy() {
        if (mApplicationBroadcast != null) {
            unregisterReceiver(mApplicationBroadcast);
            mApplicationBroadcast = null;
        }
        super.onDestroy();
    }

    @OnClick({R.id.btn_small_next,R.id.btn_small_prev, R.id.btn_small_play})
    @Override
    public void onClick(View view) {
        if(isAllCheckNetWorkOff && !ApplicationUtils.isOnline(this)){
            showToast(R.string.info_connect_to_play);
            return;
        }
        switch (view.getId()) {
            case R.id.btn_small_next:
                startMusicService(ACTION_NEXT);
                break;
            case R.id.btn_small_prev:
                startMusicService(ACTION_PREVIOUS);
                break;
            case R.id.btn_small_play:
                startMusicService(ACTION_TOGGLE_PLAYBACK);
                break;
        }
    }

    public void showModeInterstitial(IYPYCallback mCallback){
        countInterstitial++;
        boolean b=SHOW_ADS && INTERSTITIAL_FREQUENCY>0;
        if(mAdvertisement!=null && b && countInterstitial%INTERSTITIAL_FREQUENCY==0){
            mAdvertisement.showLoopInterstitialAd(mCallback);
        }
        else{
            if(mCallback!=null){
                mCallback.onAction();
            }
        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(outState!=null && mViewpager!=null && mViewpager.getCurrentItem()>=0){
            outState.putInt(KEY_TOP_INDEX,mViewpager.getCurrentItem());
        }
    }

    @Override
    public void onUpdateUIWhenSupportRTL() {
        super.onUpdateUIWhenSupportRTL();
        try{
            mTvRadioName.setGravity(Gravity.END);
            mTvSmallInfo.setGravity(Gravity.END);
            mBtnSmallNext.setImageResource(R.drawable.ic_skip_previous_white_36dp);
            mBtnSmallPrev.setImageResource(R.drawable.ic_skip_next_white_36dp);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
}
