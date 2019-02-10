package com.example.milica.nbp_florer;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by Milica on 08-Feb-19.
 */

public class ParentCommentView extends FrameLayout {

    public ParentCommentView(Context context) {
        super(context);
        Init();
    }

    public ParentCommentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ParentCommentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);Init();
    }

    public ParentCommentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);Init();
    }
    public void Init() {
        inflate(getContext(), R.layout.custom_comment_parent, this);
    }
}
