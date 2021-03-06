package com.yashoid.mmv.fullsample.login;

import android.os.Handler;
import android.text.TextUtils;

import com.yashoid.mmv.Action;
import com.yashoid.mmv.Model;
import com.yashoid.mmv.ModelFeatures;
import com.yashoid.mmv.TypeProvider;
import com.yashoid.mmv.fullsample.Basics;
import com.yashoid.mmv.fullsample.Stateful;

import java.util.List;

public interface Login extends Stateful {

    String TYPE = "Login";

    String LOGIN = "login";

    String USERNAME = "username";
    String PASSWORD = "password";
    String CAN_LOGIN = "canLogin";
    String USER_ID = "userId";

    class LoginType implements TypeProvider {

        private Action usernameAction = new UsernameAction();
        private Action passwordAction = new PasswordAction();
        private Action loginAction = new LoginAction();

        @Override
        public boolean isOfType(ModelFeatures features) {
            return TYPE.equals(features.get(Basics.TYPE));
        }

        @Override
        public Action getAction(ModelFeatures features, String actionName, Object... params) {
            switch (actionName) {
                case USERNAME:
                    return usernameAction;
                case PASSWORD:
                    return passwordAction;
                case LOGIN:
                    return loginAction;
            }

            return null;
        }

        @Override
        public void getIdentifyingFeatures(ModelFeatures features, List<String> identifyingFeatures) {
            identifyingFeatures.add(Basics.TYPE);
        }

        class UsernameAction implements Action {

            @Override
            public Object perform(Model model, Object... params) {
                model.set(USERNAME, params[0]);
                model.set(CAN_LOGIN, canLogin(model));

                return null;
            }

        }

        class PasswordAction implements Action {

            @Override
            public Object perform(Model model, Object... params) {
                model.set(PASSWORD, params[0]);
                model.set(CAN_LOGIN, canLogin(model));

                return null;
            }

        }

        class LoginAction implements Action {

            @Override
            public Object perform(final Model model, Object... params) {
                final String username = model.get(USERNAME);
                final String password = model.get(PASSWORD);

                model.set(STATUS, STATUS_LOADING);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Math.random() > 0.7f) {
                            model.set(ERROR, "Random error!! Do it again.");
                            model.set(STATUS, STATUS_FAILED);
                        }
                        else {
                            model.set(USER_ID, 123);
                            model.set(STATUS, STATUS_SUCCESS);
                        }
                    }
                }, 2000);

                return null;
            }

        }

        private boolean canLogin(Model model) {
            return !TextUtils.isEmpty((String) model.get(USERNAME)) && !TextUtils.isEmpty((String) model.get(PASSWORD)) && !isLoading(model);
        }

        private boolean isLoading(Model model) {
            Integer status = model.get(STATUS);

            return status != null && status == STATUS_LOADING;
        }

    }

}
