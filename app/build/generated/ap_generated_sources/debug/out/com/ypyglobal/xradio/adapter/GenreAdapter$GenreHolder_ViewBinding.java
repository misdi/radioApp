// Generated code from Butter Knife. Do not modify!
package com.ypyglobal.xradio.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.cardview.widget.CardView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.ypyglobal.xradio.R;
import com.ypyglobal.xradio.ypylibs.view.MaterialIconView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class GenreAdapter$GenreHolder_ViewBinding implements Unbinder {
  private GenreAdapter.GenreHolder target;

  @UiThread
  public GenreAdapter$GenreHolder_ViewBinding(GenreAdapter.GenreHolder target, View source) {
    this.target = target;

    target.mTvName = Utils.findRequiredViewAsType(source, R.id.tv_name, "field 'mTvName'", TextView.class);
    target.mImgBg = Utils.findRequiredViewAsType(source, R.id.img_genre, "field 'mImgBg'", ImageView.class);
    target.mImgChevron = Utils.findOptionalViewAsType(source, R.id.img_chevron, "field 'mImgChevron'", MaterialIconView.class);
    target.mLayoutRoot = Utils.findRequiredViewAsType(source, R.id.layout_root, "field 'mLayoutRoot'", RelativeLayout.class);
    target.mCardView = Utils.findOptionalViewAsType(source, R.id.card_view, "field 'mCardView'", CardView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    GenreAdapter.GenreHolder target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mTvName = null;
    target.mImgBg = null;
    target.mImgChevron = null;
    target.mLayoutRoot = null;
    target.mCardView = null;
  }
}
