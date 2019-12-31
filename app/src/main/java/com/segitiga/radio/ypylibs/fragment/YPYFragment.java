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

package com.segitiga.radio.ypylibs.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.segitiga.radio.ypylibs.activity.YPYFragmentActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * @author:YPY Global
 * @Email: bl911vn@gmail.com
 * @Website: www.segitiga.com
 */
public abstract class YPYFragment extends Fragment implements IYPYFragmentConstants {

	private View mRootView;
	private boolean isExtractData;

	private String mNameFragment;
	private int mIdFragment;
	protected String mNameScreen;

	private boolean isAllowFindViewContinuous;
	private boolean isCreated;
	private boolean isFirstInTab;


	private boolean isLoadingData;
	private Unbinder mBinder;

	protected Bundle mSavedInstanceState;

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mRootView = onInflateLayout(inflater,container,savedInstanceState);
		if(mRootView!=null){
			mBinder= ButterKnife.bind(this,mRootView);
		}
		return mRootView;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (!isExtractData) {
			isExtractData = true;
			if(savedInstanceState==null){
				onExtractData(getArguments());
			}
			else{
				this.mSavedInstanceState=savedInstanceState;
				onExtractData(savedInstanceState);
			}
			findView();
		}
		else{
			if(isAllowFindViewContinuous){
				findView();
			}
		}
		isCreated=true;
	}

	@Override
	public void onStart() {
		super.onStart();
		if(isAllowFindViewContinuous && isCreated){
			findView();
		}

	}

	public abstract View onInflateLayout(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);
	public abstract void findView();

	public void onExtractData(Bundle savedInstance){
		if (savedInstance != null) {
			mNameFragment = savedInstance.getString(KEY_NAME_FRAGMENT);
			mIdFragment = savedInstance.getInt(KEY_ID_FRAGMENT);
			mNameScreen = savedInstance.getString(KEY_NAME_SCREEN);
		}
	}


	public void backToHome(YPYFragmentActivity mContext) {
		try{
			FragmentTransaction mFragmentTransaction;
			FragmentManager mFragmentManager = mContext.getSupportFragmentManager();
			mFragmentTransaction = mFragmentManager.beginTransaction();
			mFragmentTransaction.remove(this);

			Fragment mFragmentHome = getFragmentHome(mContext);
			if(mFragmentHome!=null){
				String screenName=((YPYFragment) mFragmentHome).getScreenName();
				if(!TextUtils.isEmpty(screenName)){
					mContext.setActionBarTitle(screenName);
				}
				mFragmentTransaction.show(mFragmentHome);
			}
			mFragmentTransaction.commit();
		}
		catch (Exception e){
			e.printStackTrace();
		}

	}

	public String getScreenName() {
		return mNameScreen;
	}

	public void setAllowFindViewContinuous(boolean isAllowFindViewContinuous) {
		this.isAllowFindViewContinuous = isAllowFindViewContinuous;
	}

	private Fragment getFragmentHome(FragmentActivity mContext){
		Fragment mFragmentHome=null;
		if(mIdFragment>0){
			mFragmentHome = mContext.getSupportFragmentManager().findFragmentById(mIdFragment);
		}
		else{
			if(!TextUtils.isEmpty(mNameFragment)){
				mFragmentHome = mContext.getSupportFragmentManager().findFragmentByTag(mNameFragment);
			}
		}
		return mFragmentHome;
	}

	public void notifyData(){

	}

	public void notifyData(int pos){

	}

	public void startLoadData(){

	}

	public void onNetworkChange(boolean isNetworkOn){

	}

	protected boolean isLoadingData() {
		return isLoadingData;
	}

	protected void setLoadingData(boolean loadingData) {
		isLoadingData = loadingData;
	}
	protected boolean isFirstInTab() {
		return isFirstInTab;
	}

	public void setFirstInTab(boolean firstInTab) {
		isFirstInTab = firstInTab;
	}


	public boolean isCheckBack(){
		return false;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mBinder!=null){
			mBinder.unbind();
		}
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle outState) {
		super.onSaveInstanceState(outState);
		try {
			outState.putInt(KEY_ID_FRAGMENT, mIdFragment);
			outState.putString(KEY_NAME_FRAGMENT, mNameFragment);
			outState.putString(KEY_NAME_SCREEN, mNameScreen);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}
}
