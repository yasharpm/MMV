package com.yashoid.mmv.sample;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.yashoid.mmv.Managers;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.Target;

import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Target {

    private Model mModel;

    private TextView mTextHello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextHello = findViewById(R.id.text_hello);
        mTextHello.setOnClickListener(this);

        ModelFeatures features = new ModelFeatures.Builder()
                .build();

        Managers.getInstance().registerTarget(this, features);
    }

    @Override
    public void onClick(View v) {
        mModel.perform("doTheThing");
    }

    @Override
    public void setModel(Model model) {
        mModel = model;

        onModelChanged();
    }

    @Override
    public void onFeaturesChanged(String... featureNames) {
        onModelChanged();
    }

    private void onModelChanged() {
        StringBuilder sb = new StringBuilder();

        sb.append("Object Start\n");

        for (Map.Entry<String, Object> entry: mModel.getAllFeatures().entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append('\n');
        }

        sb.append("Object End");

        mTextHello.setText(sb.toString());
    }

}
