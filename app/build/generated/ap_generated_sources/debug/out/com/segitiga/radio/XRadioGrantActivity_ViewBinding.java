// Generated code from Butter Knife. Do not modify!
package com.segitiga.radio;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class XRadioGrantActivity_ViewBinding implements Unbinder {
  private XRadioGrantActivity target;

  private View view7f0801a5;

  private View view7f0801aa;

  private View view7f08005e;

  @UiThread
  public XRadioGrantActivity_ViewBinding(XRadioGrantActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public XRadioGrantActivity_ViewBinding(final XRadioGrantActivity target, View source) {
    this.target = target;

    View view;
    target.mTvInfo = Utils.findRequiredViewAsType(source, R.id.tv_info, "field 'mTvInfo'", TextView.class);
    view = Utils.findRequiredView(source, R.id.tv_policy, "method 'onClick'");
    view7f0801a5 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.tv_tos, "method 'onClick'");
    view7f0801aa = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.btn_allow, "method 'onClick'");
    view7f08005e = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onClick(p0);
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    XRadioGrantActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.mTvInfo = null;

    view7f0801a5.setOnClickListener(null);
    view7f0801a5 = null;
    view7f0801aa.setOnClickListener(null);
    view7f0801aa = null;
    view7f08005e.setOnClickListener(null);
    view7f08005e = null;
  }
}
