package com.yashoid.mmv.fullsample;

public interface Stateful {

    String STATUS = "status";
    String ERROR = "error";

    int STATUS_IDLE = 0;
    int STATUS_LOADING = 1;
    int STATUS_FAILED = 2;
    int STATUS_SUCCESS = 3;

}
