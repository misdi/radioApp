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

package com.segitiga.radio.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.like.LikeButton;
import com.like.OnLikeListener;
import com.segitiga.radio.R;
import com.segitiga.radio.model.RadioModel;
import com.segitiga.radio.ypylibs.activity.YPYFragmentActivity;
import com.segitiga.radio.ypylibs.adapter.YPYRecyclerViewAdapter;
import com.segitiga.radio.ypylibs.imageloader.GlideImageLoader;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;


import static com.segitiga.radio.constants.IXRadioConstants.UI_CARD_GRID;
import static com.segitiga.radio.constants.IXRadioConstants.UI_CARD_LIST;
import static com.segitiga.radio.constants.IXRadioConstants.UI_FLAT_GRID;
import static com.segitiga.radio.constants.IXRadioConstants.UI_MAGIC_GRID;

/**
 * @author:YPY Global
 * @Email: bl911vn@gmail.com
 * @Website: http://segitiga.com
 * Created by YPY Global on 4/20/18.
 */
public class RadioAdapter extends YPYRecyclerViewAdapter<RadioModel> {

    private final int mTypeUI;
    private final int mSizeH;
    private final String mUrlHost;
    private final boolean isSupportRTL;
    private int mResId;
    private OnRadioListener onRadioListener;

    public RadioAdapter(Context mContext, ArrayList<RadioModel> listObjects, String mUrlHost, int sizeH, int typeUI) {
        super(mContext, listObjects);
        this.mSizeH=sizeH;
        this.mTypeUI=typeUI;
        this.isSupportRTL=((YPYFragmentActivity)mContext).isSupportRTL();
        this.mResId =R.layout.item_flat_list_radio;
        if(mTypeUI==UI_FLAT_GRID){
            this.mResId =R.layout.item_flat_grid_radio;
        }
        else if(mTypeUI== UI_CARD_GRID || mTypeUI==UI_MAGIC_GRID){
            this.mResId =R.layout.item_card_grid_radio;
        }
        else if(mTypeUI==UI_CARD_LIST){
            this.mResId =R.layout.item_card_list_radio;
        }
        this.mUrlHost=mUrlHost;

    }

    @Override
    public void onBindNormalViewHolder(RecyclerView.ViewHolder holder, int position) {
        RadioHolder mHolder = (RadioHolder) holder;
        final RadioModel radioModel = mListObjects.get(position);
        mHolder.mTvName.setText(radioModel.getName());

        String tag=radioModel.getTags();
        if(TextUtils.isEmpty(tag) && !TextUtils.isEmpty(radioModel.getBitRate())){
            tag=String.format(mContext.getString(R.string.format_bitrate),radioModel.getBitRate());
        }
        mHolder.mTvDes.setText(tag);
        mHolder.mBtnFavorite.setLiked(radioModel.isFavorite());

        if(!TextUtils.isEmpty(radioModel.getImage())){
            GlideImageLoader.displayImage(mContext,mHolder.mImgRadio,radioModel.getArtWork(mUrlHost), R.drawable.ic_rect_img_default);
        }
        else{
            mHolder.mImgRadio.setImageResource(R.drawable.ic_rect_img_default);
        }
        mHolder.mBtnFavorite.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                if(onRadioListener!=null){
                    onRadioListener.onFavorite(radioModel,true);
                }
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                if(onRadioListener!=null){
                    onRadioListener.onFavorite(radioModel,false);
                }
            }
        });

        if(mTypeUI==UI_FLAT_GRID || mTypeUI==UI_CARD_GRID){
            mHolder.mImgRadio.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onViewDetail(radioModel);
                }
            });
        }
        else{
            mHolder.mLayoutRoot.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onViewDetail(radioModel);
                }
            });
        }

    }
    public void setOnRadioListener(OnRadioListener onRadioListener) {
        this.onRadioListener = onRadioListener;
    }

    public interface OnRadioListener {
        public void onFavorite(RadioModel model,boolean isFavorite);
    }

    @Override
    public RecyclerView.ViewHolder onCreateNormalViewHolder(ViewGroup v, int viewType) {
        View mView = mInflater.inflate(mResId, v, false);
        return new RadioHolder(mView);
    }

    public class RadioHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_name)
        public TextView mTvName;

        @BindView(R.id.tv_des)
        public TextView mTvDes;

        @BindView(R.id.img_radio)
        public ImageView mImgRadio;

        @BindView(R.id.layout_root)
        public View mLayoutRoot;

        @BindView(R.id.btn_favourite)
        public LikeButton mBtnFavorite;


        RadioHolder(View convertView) {
            super(convertView);
            ButterKnife.bind(this,convertView);
            mTvName.setSelected(true);
            if(mSizeH>0 && (mTypeUI==UI_FLAT_GRID || mTypeUI==UI_CARD_GRID)){
                FrameLayout.LayoutParams mLayoutParams = (FrameLayout.LayoutParams) mImgRadio.getLayoutParams();
                mLayoutParams.height=mSizeH;
                mImgRadio.setLayoutParams(mLayoutParams);
            }
            if(isSupportRTL){
                mTvName.setGravity(Gravity.END);
                mTvDes.setGravity(Gravity.END);
            }

        }
    }
}
