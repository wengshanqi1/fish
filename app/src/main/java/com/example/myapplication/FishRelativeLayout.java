package com.example.myapplication;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class FishRelativeLayout extends RelativeLayout {
    private ImageView ivFish;
    private FishDrawable fishDrawable;




    public FishRelativeLayout(Context context) {
        this(context,null);
    }
    public FishRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    public FishRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        ivFish = new ImageView(context);
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ivFish.setLayoutParams(layoutParams);

        //创建  fish 并添加到ImageView
        fishDrawable = new FishDrawable();
        ivFish.setImageDrawable(fishDrawable);
        addView(ivFish);

    }
}
