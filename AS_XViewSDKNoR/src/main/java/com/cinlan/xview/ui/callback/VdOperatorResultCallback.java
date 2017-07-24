package com.cinlan.xview.ui.callback;

/**
 *
 * Created by sivin on 2017/6/7.
 */

public interface VdOperatorResultCallback {

    int OPERATOR_OK = 0x00200;

    int NO_PLACE = 0x00203;



    void operatorCallback(int result);


}
