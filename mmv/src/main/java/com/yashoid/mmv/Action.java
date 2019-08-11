package com.yashoid.mmv;

public interface Action {

    String ACTION_MODEL_CREATED = "_model_created";
    String ACTION_MODEL_LOADED_FROM_CACHE = "_model_loaded_from_cache";
    String ACTION_MODEL_NOT_EXISTED_IN_CACHE = "_model_not_existed_in_cache";
    String ACTION_MODEL_RECYCLED = "_model_recycled";

    Object perform(Model model, Object... params);

}
