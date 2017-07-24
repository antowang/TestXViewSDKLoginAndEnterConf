package com.test.xview.testxviewsdk;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cinlan.xview.agent.ConfType;
import com.cinlan.xview.inter.IXVCallback;
import com.cinlan.xview.agent.XViewAgent;

import java.util.List;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    /**
     * 设置服务器
     */
    private EditText etServerIP;
    private EditText etServerPort;
    private Button btnSetServer;

    /**
     * 进入会议
     */
    private EditText etConfId, etNickName, etConfPwd, etUserId;
    private Button btnCommit;

    /**
     * 创建会议
     */
    private EditText etSubject, etOrgId, etChairPwd, etNormalPwd, etStartTime,
            etEndTime, etMaxMember;
    private Button btnCreateConf;

    /**
     * 销毁会议
     */
    private EditText etDestroyConfId;
    private Button btnDestroyConf;

    private Context mContext;
    private XViewAgent mXViewAgent;
    private IXVCallback callbackListener;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String confId = (String) msg.obj;
            etConfId.setText(confId);
            etDestroyConfId.setText(confId);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        callbackListener = new IXVCallback() {

            @Override
            public void onLogoutResultListener(int flag) {
                Log.i(TAG, "注销结果, " + flag);
                if (flag == 1) {
                    btnDestroyConf.performClick();
                }
            }

            @Override
            public void onMemberExitListener(long userId, String nickName, String userData) {
                Log.i(TAG, "成员退会, " + userId + ", " + nickName + ", " + userData);
            }

            @Override
            public void onLoginResultListener(int flag) {
                String result = "";
                switch (flag) {
                    case 1:
                        result = "登录超时";
                        break;
                    case 2:
                        result = "账号或密码错误";
                        break;
                    case 3:
                        result = "连接错误";
                        break;
                    case 4:
                        result = "服务器拒绝";
                        break;
                    case 5:
                        result = "登录成功";
                        break;
                    case 6:
                        result = "服务器地址或端口为空";
                        break;
                    case 7:
                        result = "网络无法连接";
                        break;
                    case 8:
                        result = "DNS解析错误";
                        break;
                    case 9:
                        result = "加载底层库错误";
                        break;
                    case 10:
                        result = "格式不正确";
                        break;
                    case 11:
                        result = "未知错误";
                        break;
                    default:
                        break;
                }
                Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
                Log.i(TAG, "登录结果, " + flag);
            }

            @Override
            public void onGetUserListListener(List<String> users) {
                Log.i(TAG, "获取参会者列表, " + users.size());
            }

            @Override
            public void onEnterConfListener(int flag) {
                String result = "";
                switch (flag) {
                    case 1:
                        result = "无此会议id";
                        break;
                    case 2:
                        result = "入会成功";
                        break;
                    case 3:
                        result = "入会失败";
                        break;
                    case 4:
                        result = "会议列表为空";
                        break;
                    default:
                        break;
                }
                Toast.makeText(mContext, result, Toast.LENGTH_SHORT).show();
                Log.i(TAG, "入会结果, " + flag);
            }

            @Override
            public void onConfMsgListener(int flag) {
                Log.i(TAG, "会议内消息, " + flag);
            }

            @Override
            public void onCreateConfCallback(String result) {
                if (result.length() == 12) {
                    Toast.makeText(mContext,
                            "建会成功，会议号为:" + result + "\n您可以选择进入会议或删除会议",
                            Toast.LENGTH_LONG).show();
                    Message msg = Message.obtain();
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                } else {
                    Toast.makeText(mContext, "建会失败，错误代码:" + result,
                            Toast.LENGTH_LONG).show();
                }
                Log.i(TAG, "创建会议:" + result);
            }

            @Override
            public void onMemberEnterListener(long userId, String nickName, String userData) {
                Log.i(TAG, "有成员进入:" + userId + ", " + nickName + ", " + userData);
            }

            @Override
            public void onDestroyConfCallback(int result) {
                if (result == 1) {
                    Toast.makeText(mContext, "删除会议成功", Toast.LENGTH_LONG).show();

                    Message msg = Message.obtain();
                    msg.obj = "";
                    mHandler.sendMessage(msg);
                } else if (result == 0) {
                    Toast.makeText(mContext, "删除会议失败", Toast.LENGTH_LONG).show();
                }
                Log.i(TAG, "销毁会议:" + result);
            }
        };

        mXViewAgent = XViewAgent.getInstance(this);
        mXViewAgent.registerXViewCallback(callbackListener);
        initView();
    }

    private String sSubject;
    private long nOrgID;
    private String sChairPasswd;
    private String sParticipantPasswd;
    private long nStartTime;
    private long nEndTime;
    private int nMaxParticipant;

    private void initView() {
        etConfId = (EditText) findViewById(R.id.etConfId);
        etConfId.setText("");
        etNickName = (EditText) findViewById(R.id.etNickName);
        etNickName.setText("游客");
        etConfPwd = (EditText) findViewById(R.id.etConfPwd);
        etConfPwd.setText("wangandong");
        etUserId = (EditText) findViewById(R.id.etUserId);
        etUserId.setText("用户1");
        btnCommit = (Button) findViewById(R.id.btnCommit);
        btnCommit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String confIds = etConfId.getText().toString().trim();

                String nickName = etNickName.getText().toString().trim();
                long confId = 0;
                if (!confIds.isEmpty()) {
                    confId = Long.parseLong(confIds);
                }


                if (nickName.isEmpty()) {
                    nickName = "游客";
                }


                final String confPwd = etConfPwd.getText().toString().trim();
                final String userId = etUserId.getText().toString().trim();
//                514966593527L
//                final long finalConfId =  514967204137L;
                if (confId == 0)
                    confId = 514994167795L;
                final long finalConfId = confId;
                final String finalNickName = nickName;

                // mXViewAgent.loginXView(finalConfId, confPwd, finalNickName, userId);

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        mXViewAgent.loginXView(mContext, finalConfId, confPwd, finalNickName, userId, ConfType.MULTI);

                    }
                }).start();

            }
        });

        etSubject = (EditText) findViewById(R.id.etSubject);
        etOrgId = (EditText) findViewById(R.id.etOrgId);
        etChairPwd = (EditText) findViewById(R.id.etChairPwd);
        etNormalPwd = (EditText) findViewById(R.id.etNormalPwd);
        etStartTime = (EditText) findViewById(R.id.etStartTime);
        etEndTime = (EditText) findViewById(R.id.etEndTime);
        etMaxMember = (EditText) findViewById(R.id.etMaxMember);

        etSubject.setText("测试会议1"); // 会议主题
        etOrgId.setText("318"); // 组织id
        etChairPwd.setText("dongdong"); // 主席密码
        etNormalPwd.setText("wangandong"); // 普通密码
        etStartTime.setText("0"); // 开始时间
        etEndTime.setText("0"); // 结束时间,0代表永久会议
        etMaxMember.setText("10000"); // 最大会议人数


        btnCreateConf = (Button) findViewById(R.id.btnCreateConf);
        btnCreateConf.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sSubject = etSubject.getText().toString().trim();
                nOrgID = Long.parseLong(etOrgId.getText().toString().trim());
                sChairPasswd = etChairPwd.getText().toString().trim();
                sParticipantPasswd = etNormalPwd.getText().toString().trim();
                nStartTime = Long.parseLong(etStartTime.getText().toString().trim());
                nEndTime = Long.parseLong(etEndTime.getText().toString().trim());
                nMaxParticipant = Integer.parseInt(etMaxMember.getText().toString().trim());

                mXViewAgent.createConf(mContext, sSubject, nOrgID, sChairPasswd,
                        sParticipantPasswd, nStartTime, nEndTime,
                        nMaxParticipant);
            }
        });


        etDestroyConfId = (EditText) findViewById(R.id.etDestroyConfId);
        btnDestroyConf = (Button) findViewById(R.id.btnDestroyConf);
        btnDestroyConf.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String s = etDestroyConfId.getText().toString().trim();
                if (s.isEmpty())
                    return;
                long confId = Long.parseLong(s);
                mXViewAgent.destroyConf(mContext, confId);
            }
        });

        etServerIP = (EditText) findViewById(R.id.etServerIP2);
        etServerPort = (EditText) findViewById(R.id.etServerPort2);


        String ip = etServerIP.getText().toString().trim();
        String port = etServerPort.getText().toString().trim();
        if (ip.equals(""))
            ip = "192.168.4.99";
        if (port.equals("")) {
            port = "18181";
        }


        btnSetServer = (Button) findViewById(R.id.btnSetServer2);
        final String finalIp = ip;
        final String finalPort = port;
        btnSetServer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                mXViewAgent.setServer(mContext, finalIp, finalPort);
                Toast.makeText(mContext, "设置服务器地址:" + finalIp + "\n端口:" + finalPort,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

}