package com.example.ascope_lite;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

public class Loading_Progress extends Dialog {
    public Loading_Progress(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_progress);
    }
}
