package com.yashoid.mmv.sample;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yashoid.mmv.Managers;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.target.ActivityTarget;

import java.util.Map;

public class MainActivity extends ActivityTarget implements View.OnClickListener {

    private Managers mManagers;

    private TextView mTextHello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextHello = findViewById(R.id.text_hello);
        mTextHello.setOnClickListener(this);

        ModelFeatures features = new ModelFeatures.Builder()
                .build();

        mManagers = Managers.getInstance();
        mManagers.registerTarget(this, features);
    }

    @Override
    public void onClick(View v) {
        Model model = getModel();

        model.perform("doTheThing");
    }

    @Override
    protected void onModelChanged(String... featureNames) {
        super.onModelChanged(featureNames);

        Model model = getModel();

        StringBuilder sb = new StringBuilder();

        sb.append("Object Start\n");

        for (Map.Entry<String, Object> entry: model.getAllFeatures().entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append('\n');
        }

        sb.append("Object End");

        mTextHello.setText(sb.toString());
    }

}
