package com.yashoid.mmv;

public interface Action {

    String ACTION_MODEL_CREATED = "_model_created";
    String ACTION_MODEL_RECYCLED = "_model_recycled";

    Object perform(Model model, Object... params);

}
