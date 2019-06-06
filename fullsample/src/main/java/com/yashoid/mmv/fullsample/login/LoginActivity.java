package com.yashoid.mmv.fullsample.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yashoid.mmv.Managers;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.fullsample.Basics;
import com.yashoid.mmv.fullsample.MainActivity;
import com.yashoid.mmv.fullsample.R;
import com.yashoid.mmv.Target;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, Target, Login {

    private Model mModel;

    private EditText mEditUsername;
    private EditText mEditPassword;
    private View mButtonLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEditUsername = findViewById(R.id.edit_username);
        mEditPassword = findViewById(R.id.edit_password);
        mButtonLogin = findViewById(R.id.button_login);

        mEditUsername.addTextChangedListener(new TextWatcher() {

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                mModel.perform(USERNAME, s.toString());
            }

        });

        mEditPassword.addTextChangedListener(new TextWatcher() {

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                mModel.perform(PASSWORD, s.toString());
            }

        });

        mButtonLogin.setOnClickListener(this);

        ModelFeatures features = new ModelFeatures.Builder().add(Basics.TYPE, TYPE).build();
        Managers.getInstance().registerTarget(this, features);
    }

    @Override
    public void onClick(View v) {
        mModel.perform(LOGIN);
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

    protected void onModelChanged() {
        String username = mModel.get(USERNAME);
        String password = mModel.get(PASSWORD);

        if (!mEditUsername.getText().toString().equals(username)) {
            mEditUsername.setText(username);
        }

        if (!mEditPassword.getText().toString().equals(password)) {
            mEditPassword.setText(password);
        }

        Boolean canLogin = mModel.get(CAN_LOGIN);

        if (canLogin == null) {
            canLogin = false;
        }

        mButtonLogin.setEnabled(canLogin);

        Integer status = mModel.get(STATUS);

        if (status != null) {
            switch (status) {
                case STATUS_SUCCESS:
                    Toast.makeText(this, "Login successful", Toast.LENGTH_LONG).show();

                    mModel.set(STATUS, STATUS_IDLE);

                    startActivity(MainActivity.getIntent(this));
                    finish();

                    break;
                case STATUS_LOADING:
                    mEditUsername.setEnabled(false);
                    mEditPassword.setEnabled(false);
                    mButtonLogin.setEnabled(false);
                    break;

                case STATUS_FAILED:
                    Toast.makeText(this, (String) mModel.get(ERROR), Toast.LENGTH_LONG).show();

                default:
                    mEditUsername.setEnabled(true);
                    mEditPassword.setEnabled(true);
                    mButtonLogin.setEnabled(canLogin);
                    break;
            }
        }
    }

}
