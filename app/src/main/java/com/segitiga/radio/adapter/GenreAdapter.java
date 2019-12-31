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
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.segitiga.radio.R;
import com.segitiga.radio.model.GenreModel;
import com.segitiga.radio.ypylibs.activity.YPYFragmentActivity;
import com.segitiga.radio.ypylibs.adapter.YPYRecyclerViewAdapter;
import com.segitiga.radio.ypylibs.imageloader.GlideImageLoader;
import com.segitiga.radio.ypylibs.view.MaterialIconView;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;


import static com.segitiga.radio.constants.IXRadioConstants.RATE_MAGIC_HEIGHT;
import static com.segitiga.radio.constants.IXRadioConstants.UI_CARD_GRID;
import static com.segitiga.radio.constants.IXRadioConstants.UI_CARD_LIST;
import static com.segitiga.radio.constants.IXRadioConstants.UI_FLAT_GRID;
import static com.segitiga.radio.constants.IXRadioConstants.UI_FLAT_LIST;
import static com.segitiga.radio.constants.IXRadioConstants.UI_MAGIC_GRID;

/**
 * @author:YPY Global
 * @Email: bl911vn@gmail.com
 * @Website: http://segitiga.com
 * Created by YPY Global on 4/20/18.
 */
public class GenreAdapter extends YPYRecyclerViewAdapter<GenreModel> {

    private final int mTypeUI;
    private final int mSizeH;
    private final String mUrlHost;
    private final boolean isSupportRTL;
    private int mResId;

    public GenreAdapter(Context mContext, ArrayList<GenreModel> listObjects, String mUrlHost, int sizeH, int typeUI) {
        super(mContext, listObjects);
        this.mSizeH = sizeH;
        this.mTypeUI = typeUI;
        this.isSupportRTL=((YPYFragmentActivity)mContext).isSupportRTL();
        this.mResId = R.layout.item_flat_list_genre;
        if (mTypeUI == UI_FLAT_GRID) {
            this.mResId = R.layout.item_flat_grid_genre;
        }
        else if (mTypeUI == UI_CARD_GRID || mTypeUI == UI_MAGIC_GRID) {
            this.mResId = R.layout.item_card_grid_genre;
        }
        else if (mTypeUI == UI_CARD_LIST) {
            this.mResId = R.layout.item_card_list_genre;
        }
        this.mUrlHost = mUrlHost;

    }

    @Override
    public void onBindNormalViewHolder(RecyclerView.ViewHolder holder, int position) {
        GenreHolder mHolder = (GenreHolder) holder;
        final GenreModel genreModel = mListObjects.get(position);
        mHolder.mTvName.setText(genreModel.getName());

        if (!TextUtils.isEmpty(genreModel.getImage())) {
            GlideImageLoader.displayImage(mContext, mHolder.mImgBg, genreModel.getArtWork(mUrlHost), R.drawable.ic_rect_img_default);
        }
        else {
            mHolder.mImgBg.setImageResource(R.drawable.ic_rect_img_default);
        }
        if (mTypeUI == UI_MAGIC_GRID) {
            CardView.LayoutParams mLayoutParams = (CardView.LayoutParams) mHolder.mLayoutRoot.getLayoutParams();
            int index2 = position % 4;
            if (index2 == 0 || index2 == 2) {
                mLayoutParams.height = (int) (RATE_MAGIC_HEIGHT * mSizeH);
            }
            else {
                mLayoutParams.height = mSizeH;
            }
            mHolder.mLayoutRoot.setLayoutParams(mLayoutParams);
        }
        if (mHolder.mCardView != null) {
            mHolder.mCardView.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onViewDetail(genreModel);
                }
            });
        }
        else {
            mHolder.mLayoutRoot.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onViewDetail(genreModel);
                }
            });
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateNormalViewHolder(ViewGroup v, int viewType) {
        View mView = mInflater.inflate(mResId, v, false);
        return new GenreHolder(mView);
    }

    public class GenreHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_name)
        public TextView mTvName;

        @BindView(R.id.img_genre)
        public ImageView mImgBg;

        @BindView(R.id.img_chevron)
        @Nullable
        public MaterialIconView mImgChevron;

        @BindView(R.id.layout_root)
        public RelativeLayout mLayoutRoot;

        @BindView(R.id.card_view)
        @Nullable
        public CardView mCardView;

        GenreHolder(View convertView) {
            super(convertView);
            ButterKnife.bind(this, convertView);
            if (mSizeH > 0) {
                if (mTypeUI == UI_FLAT_GRID) {
                    FrameLayout.LayoutParams mLayoutParams = (FrameLayout.LayoutParams) mLayoutRoot.getLayoutParams();
                    mLayoutParams.height = mSizeH;
                    mLayoutRoot.setLayoutParams(mLayoutParams);
                }
                else if (mTypeUI == UI_CARD_GRID) {
                    CardView.LayoutParams mLayoutParams = (CardView.LayoutParams) mLayoutRoot.getLayoutParams();
                    mLayoutParams.height = mSizeH;
                    mLayoutRoot.setLayoutParams(mLayoutParams);
                }

            }
            if(isSupportRTL && (mTypeUI == UI_FLAT_LIST || mTypeUI==UI_CARD_LIST)){
                mTvName.setGravity(Gravity.RIGHT);
                if(mImgChevron!=null){
                    mImgChevron.setText(Html.fromHtml(mContext.getString(R.string.icon_chevron_left)));
                }
            }

        }
    }
}
