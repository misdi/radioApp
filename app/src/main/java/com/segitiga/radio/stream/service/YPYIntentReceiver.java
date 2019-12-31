/*
 * Copyright (c) 2018. YPY Global - All Rights Reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at.
 *
 *         http://ypyglobal.com/sourcecode/policy
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.segitiga.radio.stream.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.segitiga.radio.stream.constant.IYPYStreamConstants;
import com.segitiga.radio.stream.manager.YPYStreamManager;
import com.segitiga.radio.ypylibs.utils.IOUtils;


public class YPYIntentReceiver extends BroadcastReceiver implements IYPYStreamConstants {

	public static final String TAG = YPYIntentReceiver.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent intent) {
		try{
			if (intent == null || TextUtils.isEmpty(intent.getAction())) {
				return;
			}
			String action = intent.getAction();
			String packageName = context.getPackageName();
			if (action.equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
				if(YPYStreamManager.getInstance().isPlaying()){
					startService(context, ACTION_TOGGLE_PLAYBACK);
				}
			}
			else if (action.equals(packageName + ACTION_NEXT)) {
				if(!YPYStreamManager.getInstance().isLoading()){
					startService(context, ACTION_NEXT);
				}
			}
			else if (action.equals(packageName + ACTION_TOGGLE_PLAYBACK)) {
				if(YPYStreamManager.getInstance().isPrepareDone()){
					startService(context, ACTION_TOGGLE_PLAYBACK);
				}
			}
			else if (action.equals(packageName + ACTION_STOP)) {
				startService(context, ACTION_STOP);
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}

	}

	private void startService(Context context, String action){
		try{
			Intent mIntent1= new Intent(context,YPYStreamService.class);
			mIntent1.setAction(context.getPackageName() +action);
			if(IOUtils.hasAndroid80()){
				context.startForegroundService(mIntent1);
			}
			else{
				context.startService(mIntent1);
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}

	}

}
