package com.cinlan.xview.adapter;

import java.util.List;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cinlan.xview.bean.data.DocShare;
import com.cinlan.xview.utils.GlobalHolder;
import com.cinlankeji.khb.iphone.R;

public class ConfDataAdapter extends BaseAdapter {

	private Activity mContext;
	private List<DocShare> mDocShares;

	public ConfDataAdapter(Activity context, List<DocShare> mDocShares) {
		this.mContext = context;
		this.mDocShares = mDocShares;
	}

	@Override
	public int getCount() {
		return mDocShares.size();
	}

	@Override
	public Object getItem(int position) {
		DocShare docShare = mDocShares.get(position);
		return docShare;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewDataHolder holder = null;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.item_conf_data_xviewsdk, null);
			holder = new ViewDataHolder();
			holder.item_tv_docname = (TextView) convertView
					.findViewById(R.id.item_tv_docname_xviewsdk);
			holder.item_tv_docnumbers = (TextView) convertView
					.findViewById(R.id.item_tv_docnumbers_xviewsdk);
			convertView.setTag(holder);
		} else {
			holder = (ViewDataHolder) convertView.getTag();
		}

		DocShare doc = mDocShares.get(position);
		holder.item_tv_docname.setText(doc.getFilename());
		int count = doc.getPagenums();
		if (count == 0) {
			Integer integer = GlobalHolder.getInstance().pages.get(doc
					.getWBoardID());
			if (integer != null)
				count = integer.intValue();
		}
		holder.item_tv_docnumbers.setText(count
				+ mContext.getResources().getString(R.string.pagenumbers_xviewsdk));
		return convertView;
	}

	private static class ViewDataHolder {
		TextView item_tv_docname;
		TextView item_tv_docnumbers;
	}

}
