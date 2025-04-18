// Generated by view binder compiler. Do not edit!
package com.okamikosoft.pdf_signature_sdk.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import com.okamikosoft.pdf_signature_sdk.R;
import java.lang.NullPointerException;
import java.lang.Override;

public final class FragmentDemoBinding implements ViewBinding {
  @NonNull
  private final FrameLayout rootView;

  @NonNull
  public final FrameLayout containerId;

  private FragmentDemoBinding(@NonNull FrameLayout rootView, @NonNull FrameLayout containerId) {
    this.rootView = rootView;
    this.containerId = containerId;
  }

  @Override
  @NonNull
  public FrameLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentDemoBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentDemoBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_demo, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentDemoBinding bind(@NonNull View rootView) {
    if (rootView == null) {
      throw new NullPointerException("rootView");
    }

    FrameLayout containerId = (FrameLayout) rootView;

    return new FragmentDemoBinding((FrameLayout) rootView, containerId);
  }
}
