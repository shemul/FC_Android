package com.csform.android.uiapptemplate.data;

import android.view.View;

public interface OnRecyclerViewItemClickListener<Model> {
    public void onItemClick(View view, Model model);
}