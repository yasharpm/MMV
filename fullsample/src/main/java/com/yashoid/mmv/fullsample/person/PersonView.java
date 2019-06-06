package com.yashoid.mmv.fullsample.person;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.yashoid.mmv.Model;
import com.yashoid.mmv.fullsample.R;
import com.yashoid.mmv.Target;

public class PersonView extends FrameLayout implements Target, Person {

    private Model mModel;

    private ImageView mImagePhoto;
    private TextView mTextName;
    private TextView mTextPoints;

    public PersonView(Context context) {
        super(context);
        initialize(context, null, 0);
    }

    public PersonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public PersonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setBackground(new RippleDrawable(ColorStateList.valueOf(Color.LTGRAY), null, null));
        }

        LayoutInflater.from(context).inflate(R.layout.view_person, this, true);

        mImagePhoto = findViewById(R.id.image_photo);
        mTextName = findViewById(R.id.text_name);
        mTextPoints = findViewById(R.id.text_points);
    }

    @Override
    public void setModel(Model model) {
        mModel = model;

        updateModel();
    }

    @Override
    public void onFeaturesChanged(String... featureNames) {
        updateModel();
    }

    private void updateModel() {
        mImagePhoto.setImageBitmap((Bitmap) mModel.get(PHOTO));
        mTextName.setText((String) mModel.get(NAME));
        mTextPoints.setText("" + mModel.get(POINTS));
    }

}
