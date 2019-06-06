package com.yashoid.mmv;

public interface Action {

    Object perform(Model model, Object... params);

}
