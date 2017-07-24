package com.cinlan.xview.ui;

import java.util.List;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.cinlan.jni.ChatRequest;
import com.cinlan.xview.adapter.ConfMessageAdapter;
import com.cinlan.xview.bean.ConfMessage;
import com.cinlan.xview.msg.MsgType;
import com.cinlan.xview.service.JNIService;
import com.cinlan.xview.utils.ActivityHolder;
import com.cinlan.xview.utils.GlobalHolder;
import com.cinlankeji.khb.iphone.R;

public class ChatActivity extends Activity {

	private Button btnSend;
	private EditText etContent;
	private ListView lvChat;
	private ImageView ivBackChat;
	private ScrollView svChatLayout;
	private String msg = null;
	private ConfMessageReceiver receiverMessage;
	private Context context;
	private ConfMessageAdapter adapter;
	private long curGroupId;
	private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_chat_xviewsdk);
		ActivityHolder.getInstance().addActivity(this);
		context = this;
		adapter = new ConfMessageAdapter(this);

		initReceiver();
		curGroupId = GlobalHolder.getInstance().getmCurrentConf().getId();
		initView();
	}

	@Override
	protected void onResume() {
		super.onResume();

		lvChat.setSelection(GlobalHolder.getInstance().getConfMessages().size() - 1);
	}

	private void initView() {
		svChatLayout = (ScrollView) findViewById(R.id.svChatLayout_xviewsdk);

		btnSend = (Button) findViewById(R.id.btnSendMsg_xviewsdk);
		btnSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				msg = etContent.getText().toString();
				if (msg.isEmpty()) {
					Toast.makeText(context,
							getResources().getString(R.string.content_not_empty_xviewsdk),
							Toast.LENGTH_LONG).show();
				} else {
					sendMsg(msg);
				}
			}
		});
		etContent = (EditText) findViewById(R.id.etContent_xviewsdk);
		etContent.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@SuppressLint("NewApi")
			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() == 0) {
					btnSend.setBackground(ChatActivity.this.getResources()
							.getDrawable(R.drawable.selector_chatnosend_btn_xviewsdk));
				} else {
					btnSend.setBackground(ChatActivity.this.getResources()
							.getDrawable(R.drawable.selector_chatsend_btn_xviewsdk));
				}
			}
		});
		lvChat = (ListView) findViewById(R.id.lvChat_xviewsdk);
		lvChat.setAdapter(adapter);
		ivBackChat = (ImageView) findViewById(R.id.ivBackChat_xviewsdk);
		ivBackChat.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ChatActivity.this.finish();
			}
		});
	}

	private void initReceiver() {
		receiverMessage = new ConfMessageReceiver();
		IntentFilter filter = new IntentFilter(JNIService.GET_MESSAGELIST);
		filter.addCategory(JNIService.XVIEW_JNI_CATA);
		registerReceiver(receiverMessage, filter);
	}

	private void sendMsg(String msg) {
		String msgFormat = String.format("" + "<TChatData IsAutoReply="
				+ "\"False\"" + " MessageID=" + "\"" + "%s" + "\"" + ">"
				+ "\n\n\t" + "<FontList>" + "\n\n" + "\t\t"
				+ "<TChatFont Color=" + "\"13150603\"" + " Name="
				+ "\"Tahoma\"" + " Size=" + "\"10\"" + " Style=" + "\"\""
				+ "/>" + "\n\n\t" + "</FontList>" + "\n\n\t" + "<ItemList>"
				+ "\n\n" + "\t\t" + "<TTextChatItem NewLine=" + "\"True\""
				+ " FontIndex=" + "\"0\"" + " Text=" + "\"" + "%s" + "\""
				+ "/>" + "\n\n\t" + "</ItemList>" + "\n\n" + "</TChatData>",
				UUID.randomUUID().toString(), msg);

		byte[] data = msgFormat.getBytes();
		ChatRequest.getInstance()
				.sendChatText(curGroupId, 0, data, data.length);

		ConfMessage confMessage = new ConfMessage();
		confMessage.setnFromUserID(GlobalHolder.getInstance()
				.getLocalUserId());
		confMessage.setnGroupID(GlobalHolder.getInstance().getmCurrentConf()
				.getId());
		confMessage.setText(msg);
		long time = System.currentTimeMillis();
		confMessage.setnTime(time);
		confMessage.setnFromUserID(GlobalHolder.getInstance()
				.getLocalUserId());
		confMessage.setnGroupID(curGroupId);
		confMessage.setnLength(msg.length());
		GlobalHolder.getInstance().addConfMessage(confMessage);

		adapter.refreshDatas();
		adapter.notifyDataSetChanged();
		etContent.setText("");
		lvChat.setSelection(GlobalHolder.getInstance().getConfMessages().size() - 1);
	}

	class ConfMessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			int msgType = intent.getIntExtra("msgtype", -1);
			switch (msgType) {
			case MsgType.MESSAGE_LIST:
				List<ConfMessage> lists = GlobalHolder.getInstance()
						.getConfMessages();

				if (lists.size() > 0) {
					adapter.refreshDatas();
					adapter.notifyDataSetChanged();
				}
				break;

			default:
				break;
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (receiverMessage != null)
			unregisterReceiver(receiverMessage);
	}
}
