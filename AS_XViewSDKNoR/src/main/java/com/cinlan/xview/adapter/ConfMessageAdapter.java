package com.cinlan.xview.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cinlan.xview.bean.ConfMessage;
import com.cinlan.xview.utils.GlobalHolder;
import com.cinlan.xview.widget.CircleImageView;
import com.cinlankeji.khb.iphone.R;

public class ConfMessageAdapter extends BaseAdapter {

	private Context context;
	private List<ConfMessage> lists = GlobalHolder.getInstance()
			.getConfMessages();

	public void refreshDatas() {
		lists = GlobalHolder.getInstance().getConfMessages();
	}

	public ConfMessageAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return lists.size();
	}

	@Override
	public Object getItem(int position) {
		return lists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.chat_item_itemin_xviewsdk, parent, false);
			viewHolder.tvLeftContent = (TextView) convertView
					.findViewById(R.id.text_in_xviewsdk);
			viewHolder.tvRightContent = (TextView) convertView
					.findViewById(R.id.text_out_xviewsdk);
			viewHolder.tvChatTime = (TextView) convertView
					.findViewById(R.id.tvChatTime_xviewsdk);
			viewHolder.tvLocalUserName = (TextView) convertView
					.findViewById(R.id.tvLocalUserName_xviewsdk);
			viewHolder.tvUserName = (TextView) convertView
					.findViewById(R.id.tvUserName_xviewsdk);
			viewHolder.llLeftChatLayout = (LinearLayout) convertView
					.findViewById(R.id.llLeftChatLayout_xviewsdk);
			viewHolder.llRightChatLayout = (LinearLayout) convertView
					.findViewById(R.id.llRightChatLayout_xviewsdk);
			viewHolder.rlChatTime = (RelativeLayout) convertView
					.findViewById(R.id.rlChatTime_xviewsdk);
			viewHolder.ivIconIn = (CircleImageView) convertView
					.findViewById(R.id.icon_in_xviewsdk);
			viewHolder.ivIconOut = (CircleImageView) convertView
					.findViewById(R.id.icon_out_xviewsdk);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		ConfMessage tempMsg = null;
		if (position > 0) {
			tempMsg = lists.get(position - 1);
		}
		ConfMessage msg = lists.get(position);
		if (tempMsg != null) {
			if (msg.getnTime() - tempMsg.getnTime() > (60 * 1000)) {

				// viewHolder.rlChatTime.setVisibility(View.VISIBLE);
				// long time = msg.getnTime();
				// Date nowTime = new Date(time);
				// SimpleDateFormat sdFormatter = new SimpleDateFormat(
				// "yyyy-MM-dd hh:mm:ss");
				// SimpleDateFormat sdFormatter2 = new
				// SimpleDateFormat("hh:mm:ss");
				// String retStrFormatNowDate = sdFormatter.format(nowTime);
				// String retStrFormatNowDate2 = sdFormatter2.format(nowTime);
				// String displayTime = Utils.formatDisplayTime(""
				// + retStrFormatNowDate, "yyyy-MM-dd hh:mm:ss");
				// if ("刚刚".equals(displayTime)) {
				// displayTime = retStrFormatNowDate2;
				// }
				// viewHolder.tvChatTime.setText(displayTime);

				viewHolder.rlChatTime.setVisibility(View.VISIBLE);
				long time = msg.getnTime();
				Date nowTime = new Date(time);
				SimpleDateFormat sdFormatter = new SimpleDateFormat(
						"MM-dd HH:mm");
				String retStrFormatNowDate = sdFormatter.format(nowTime);
				viewHolder.tvChatTime.setText(retStrFormatNowDate);
			} else {
				viewHolder.rlChatTime.setVisibility(View.GONE);
			}
		}

		if (msg.getnFromUserID() == GlobalHolder.getInstance()
				.getLocalUserId()) {
			viewHolder.tvLocalUserName.setText(getNameAccordingId(msg
					.getnFromUserID()));
			viewHolder.llRightChatLayout.setVisibility(View.VISIBLE);
			viewHolder.llLeftChatLayout.setVisibility(View.GONE);
			if (!"null".equals(msg.getText()))
				viewHolder.tvRightContent.setText(msg.getText());
		} else {
			viewHolder.tvUserName.setText(getNameAccordingId(msg
					.getnFromUserID()));
			viewHolder.llRightChatLayout.setVisibility(View.GONE);
			viewHolder.llLeftChatLayout.setVisibility(View.VISIBLE);
			if (!"null".equals(msg.getText())) {
				String ss = "";
				try {
					ss = msg.getText();
				} catch (Exception e) {
					ss = "";
				}
				viewHolder.tvLeftContent.setText(ss);
			}
		}

		return convertView;
	}

	public class ViewHolder {
		public LinearLayout llLeftChatLayout;
		public LinearLayout llRightChatLayout;
		public RelativeLayout rlChatTime;
		public TextView tvRightContent;
		public TextView tvLeftContent;
		public TextView tvChatTime;
		public TextView tvLocalUserName;
		public TextView tvUserName;
		public CircleImageView ivIconIn;
		public CircleImageView ivIconOut;
	}

	private String getNameAccordingId(long userId) {
		Map<Long, String> users = GlobalHolder.getInstance().getIdNameUser();
		if (users != null && users.size() > 0) {
			for (Entry<Long, String> temp : users.entrySet()) {
				if (temp.getKey() == userId) {
					return temp.getValue();
				}
			}
		}
		// return "null";
		SharedPreferences pre = context.getSharedPreferences("UserIdName",
				context.MODE_PRIVATE);
		String name = pre.getString("" + userId, "null");
		return name;
	}
}
