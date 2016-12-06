package com.equipe4.thechessgame.DialogUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import java.util.Map;

/**
 * Created by Alexis on 2016-11-27.
 */

public class ImageChooserDialog extends AlertDialog{

    private Map<Bitmap,Object> choices;
    private OnImageClickListener listener;
    private final int SIZE_IMAGE_PROMOTION_DIALOG = 200;


    public ImageChooserDialog(Context context,
                              Map<Bitmap, Object> choices,
                              OnImageClickListener listener) {
        super(context);
        this.choices = choices;
        this.listener = listener;
        init();
    }
    protected ImageChooserDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
    protected ImageChooserDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected void init(){
        final RadioGroup r = new RadioGroup(getContext());
        setRadioGroup(r,this);
        setView(r);
    }
    private void setRadioGroup(RadioGroup radioGroup, final AlertDialog dialog) {
        radioGroup.setLayoutParams(new LinearLayout.LayoutParams
                (
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                ));
        radioGroup.setOrientation(LinearLayout.HORIZONTAL);
        radioGroup.setHorizontalGravity(Gravity.CENTER);
        radioGroup.setPadding( SIZE_IMAGE_PROMOTION_DIALOG / 5,
                SIZE_IMAGE_PROMOTION_DIALOG / 5,
                SIZE_IMAGE_PROMOTION_DIALOG / 5,
                SIZE_IMAGE_PROMOTION_DIALOG / 5);
        for (Map.Entry<Bitmap, Object> entry : choices.entrySet()) {
            final ImageButton button = new ImageButton(dialog.getContext());
            button.setLayoutParams(
                    new LinearLayout.LayoutParams(SIZE_IMAGE_PROMOTION_DIALOG, SIZE_IMAGE_PROMOTION_DIALOG));
            button.setBackground(new BitmapDrawable(dialog.getContext().getResources(),entry.getKey()));
            button.setScaleType(ImageView.ScaleType.FIT_CENTER);
            button.setTag(entry.getValue()); //to identify the button
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onImageClickListener(v.getTag());
                    dialog.dismiss();
                }
            });
            radioGroup.addView(button);
        }
    }
    public interface OnImageClickListener{
        void onImageClickListener(Object tag);
    }
}
