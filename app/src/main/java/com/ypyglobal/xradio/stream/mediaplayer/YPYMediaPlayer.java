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

package com.ypyglobal.xradio.stream.mediaplayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.icy.IcyHeaders;
import com.google.android.exoplayer2.metadata.icy.IcyInfo;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.DefaultHlsExtractorFactory;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.ypyglobal.xradio.ypylibs.executor.YPYExecutorSupplier;
import com.ypyglobal.xradio.ypylibs.utils.YPYLog;


/**
 * @author:YPY Global
 * @Email: baodt@hanet.com
 * @Website: http://hanet.com/
 * @Project: cyberfm
 * Created by YPY Global on 5/21/17.
 */

public class YPYMediaPlayer {
    public static final String TAG = YPYMediaPlayer.class.getSimpleName();

    private Context mContext;

    private String mUserAgent;
    private OnStreamListener onStreamListener;

    private boolean isPrepaired;
    private SimpleExoPlayer mAudioPlayer;

    @SuppressLint("HandlerLeak")
    public YPYMediaPlayer(Context mContext) {
        this.mContext = mContext;
    }

    public YPYMediaPlayer(Context mContext, String mUserAgent) {
        this(mContext);
        this.mUserAgent = mUserAgent;
    }

    public void release() {
        isPrepaired = false;
        if (mAudioPlayer != null) {
            mAudioPlayer.addMetadataOutput(null);
            mAudioPlayer.release();
            mAudioPlayer = null;
        }

    }

    public void setVolume(float volume) {
        try {
            if (mAudioPlayer != null) {
                mAudioPlayer.setVolume(volume);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOnStreamListener(OnStreamListener onStreamListener) {
        this.onStreamListener = onStreamListener;
    }


    public void setDataSource(String url) {
        if (!TextUtils.isEmpty(url)) {
            String mUrlStream = url;
//            DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            AdaptiveTrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory();
            DefaultTrackSelector trackSelector = new DefaultTrackSelector(trackSelectionFactory);

            mAudioPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector);
            mAudioPlayer.addListener(new Player.EventListener() {
                @Override
                public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

                }

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                }

                @Override
                public void onLoadingChanged(boolean isLoading) {

                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    if (playbackState == Player.STATE_ENDED || playbackState == Player.STATE_IDLE) {
                        if (onStreamListener != null) {
                            onStreamListener.onComplete();
                        }
                    }
                    else if (playbackState == Player.STATE_READY) {
                        if (onStreamListener != null && !isPrepaired) {
                            isPrepaired = true;
                            onStreamListener.onPrepare();
                        }
                    }
                }

                @Override
                public void onRepeatModeChanged(int repeatMode) {

                }

                @Override
                public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {
                    if (onStreamListener != null) {
                        onStreamListener.onError();
                    }
                }

                @Override
                public void onPositionDiscontinuity(int reason) {
                }

                @Override
                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                }

                @Override
                public void onSeekProcessed() {

                }
            });
            DataSource.Factory dataSourceFactory;
            MediaSource mediaSource;

            if (mUrlStream.endsWith("_Other")) {
                mUrlStream = mUrlStream.replace("_Other", "");
            }

            dataSourceFactory = new DefaultDataSourceFactory(mContext, getUserAgent(mContext));
            YPYLog.e("DCM","======>start stream url stream="+ mUrlStream);
            if (mUrlStream.endsWith(".m3u8") || mUrlStream.endsWith(".M3U8")) {
                mediaSource = new HlsMediaSource.Factory(dataSourceFactory)
                        .setAllowChunklessPreparation(false)
                        .setExtractorFactory(
                        new DefaultHlsExtractorFactory(
                                DefaultTsPayloadReaderFactory.FLAG_IGNORE_H264_STREAM, false))
                        .createMediaSource(Uri.parse(mUrlStream));
            }
            else {
                mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory,new DefaultExtractorsFactory())
                        .createMediaSource(Uri.parse(mUrlStream));
            }
            mAudioPlayer.prepare(mediaSource);
            mAudioPlayer.addMetadataOutput(metadata -> {
                if(metadata!=null && metadata.length()>0){
                    try{
                        int size = metadata.length();
                        for(int i=0;i<size;i++){
                            Metadata.Entry mEntry = metadata.get(i);
                            if(mEntry instanceof IcyInfo){
                                processMetadata(( ((IcyInfo) mEntry).title));
                                break;
                            }
                            else if(mEntry instanceof IcyHeaders){
                                processMetadata(((IcyHeaders) mEntry).name);
                                break;
                            }

                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            start();
            return;

        }
        if (onStreamListener != null) {
            onStreamListener.onError();
        }
    }

    private void processMetadata(String title){
        try{
            StreamInfo mStreamInfo = new StreamInfo();
            if(!TextUtils.isEmpty(title)){
                String[] metadata = title.split(" - ");
                if(metadata.length>0){
                    if(metadata.length==3){
                        mStreamInfo.artist = metadata[1];
                        mStreamInfo.title = metadata[2];
                    }
                    else if (metadata.length==2){
                        mStreamInfo.artist = metadata[0];
                        mStreamInfo.title = metadata[1];
                    }
                    else{
                        mStreamInfo.title = metadata[0];
                    }
                }
            }
            YPYExecutorSupplier.getInstance().forMainThreadTasks().execute(() -> {
                if (onStreamListener != null) {
                    onStreamListener.onUpdateMetaData(mStreamInfo);
                }
            });
        }
        catch (Exception e){
            e.printStackTrace();
        }


    }


    public void start() {
        try {
            if (mAudioPlayer != null) {
                mAudioPlayer.setPlayWhenReady(true);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void pause() {
        try {
            if (mAudioPlayer != null) {
                mAudioPlayer.setPlayWhenReady(false);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean isPlaying() {
        try {
            return mAudioPlayer != null && mAudioPlayer.getPlayWhenReady();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    private String getUserAgent(Context mContext) {
        return Util.getUserAgent(mContext, getClass().getSimpleName());
    }


    public interface OnStreamListener {
        void onPrepare();
        void onError();
        void onComplete();
        void onBuffering(long percent);
        void onUpdateMetaData(StreamInfo info);
    }

    public class StreamInfo {
        public String title;
        public String artist;
        public String imgUrl;
    }


}
