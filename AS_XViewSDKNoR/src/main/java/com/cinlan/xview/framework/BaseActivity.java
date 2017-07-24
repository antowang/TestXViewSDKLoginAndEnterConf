package com.cinlan.xview.framework;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.cinlan.xview.PublicInfo;
import com.cinlan.xview.utils.SPUtil;
import com.cinlan.xview.widget.XViewAlertDialog;
import com.cinlankeji.khb.iphone.R;

/**
 *
 * Created by sivin on 2017/5/20.
 */

public class BaseActivity extends Activity {

    protected Context mContext;
    private XViewAlertDialog mAlertDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
    }

    protected void showAlertDialog() {
        if (mAlertDialog == null) {
            mAlertDialog = new XViewAlertDialog(this)
                    .builder()
                    .setTitle(getResources().getString(R.string.hint_xviewsdk))
                    .setMsg(getResources().getString(R.string.isExit_xviewsdk))
                    .setPositiveButton(
                            getResources().getString(R.string.sure_xviewsdk),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    boolean isEnterConf = SPUtil.getConfigBoolean(mContext, "islockconf", false);
                                    if (PublicInfo.isAnonymousLogin || isEnterConf) {
                                        PublicInfo.logout(mContext);
                                    }
                                }
                            })
                    .setNegativeButton(
                            getResources().getString(R.string.cancel_xviewsdk),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
        }

        mAlertDialog.show();
    }

    protected void dismissDialog() {
        if (mAlertDialog != null)
            mAlertDialog.dismissDialog();
    }


}
