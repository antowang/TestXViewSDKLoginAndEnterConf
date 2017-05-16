package com.cinlan.xview.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;

import com.cinlan.jni.ChatRequest;
import com.cinlan.jni.ChatRequestCallbackAdapter;
import com.cinlan.jni.ConfRequest;
import com.cinlan.jni.ConfRequestCallbackAdapter;
import com.cinlan.jni.ImRequest;
import com.cinlan.jni.ImRequestCallbackAdapter;
import com.cinlan.jni.VideoRequest;
import com.cinlan.jni.VideoRequestCallback;
import com.cinlan.jni.WBRequest;
import com.cinlan.jni.WBRequestCallbackAdapter;
import com.cinlan.xview.PublicInfo;
import com.cinlan.xview.bean.Conf;
import com.cinlan.xview.bean.ConfMessage;
import com.cinlan.xview.bean.ModifyConfDesc;
import com.cinlan.xview.bean.PersonalInfo;
import com.cinlan.xview.bean.RemoteSettingBean;
import com.cinlan.xview.bean.SyncCloseDevice;
import com.cinlan.xview.bean.SyncDevice;
import com.cinlan.xview.bean.User;
import com.cinlan.xview.bean.VideoDevice;
import com.cinlan.xview.bean.data.DocShare;
import com.cinlan.xview.bean.data.Label;
import com.cinlan.xview.bean.data.Page;
import com.cinlan.xview.msg.DataEntity;
import com.cinlan.xview.msg.EnterConfType;
import com.cinlan.xview.msg.EventConfMsg;
import com.cinlan.xview.msg.EventMsgType;
import com.cinlan.xview.msg.MediaEntity;
import com.cinlan.xview.msg.MemberEnter;
import com.cinlan.xview.msg.MemberExit;
import com.cinlan.xview.msg.MsgType;
import com.cinlan.xview.msg.PageListType;
import com.cinlan.xview.msg.PermissType;
import com.cinlan.xview.ui.EnterConf;
import com.cinlan.xview.utils.GlobalHolder;
import com.cinlan.xview.utils.XmlParserUtils;
import com.cinlan.xview.utils.XviewLog;
import com.cinlankeji.khb.iphone.R;

import org.greenrobot.eventbus.EventBus;

public class JNIService extends Service {

	private static final String XTAG = JNIService.class.getSimpleName();
	public static final int CONFLIST = 0;
	public static final int ENTER_CONF = 1;
	public static final String GET_CONFLIST = "com.cinlan.xview.conflist";
	public static final String GET_MESSAGELIST = "com.cinlan.xview.messagelist";
	public static final String XVIEW_JNI_CATA = "com.cinlan.xview.catagary";
	public static final int MEMBER_ENTER = 2;
	public static final int MEMBER_EXIT = 3;
	public static final int VIDEOLIST_COME = 4;
	public static final int PERMISS = 5;
	public static final int DOCCOME = 6;
	public static final int PAGELIST = 7;
	public static final int PAGE_CONTENT = 8;
	public static final int PAGE_CLOSE = 9;
	public static final int DATACOME = 10;
	public static final int DATAREMOVE = 11;
	public static final int KICK_CONF = 12;
	public static final int LOGIN_OUT = 13;
	public static final int DISCONNECTED = 14;
	public static final int MEDIA_MIXER = 15;
	public static final int MEDIA_DESTROY = 16;
	public static final int VIDEO_LIST = 17;
	public static final int CONF_MUTE = 18;
	public static final int MESSAGE_LIST = 19;
	public static final int VIDEOREMOTE_SETTING_COME = 20;
	public static final int SYNC_OPEN_VIDEO = 21;
	public static final int SYNC_CLOSE_VIDEO = 22;
	public static final int MODIFY_CONF_DESC = 23;
	private ConfRequestCB mConfCB = null;
	private VideoQuestCB mVideoCB = null;
	private WBRequestCB mWBRequestCB = null;
	private ImRequestJNICB mImRequestJNICB = null;
	private ChatRequestCB mChatRequestCB = null;
	private static int logoutFlag = -1;
	private LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(GlobalHolder.GlobalContext);

	private Handler mConfHandler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case CONFLIST:
				@SuppressWarnings("unchecked")
				List<Conf> confs = (List<Conf>) msg.obj;
				GlobalHolder.getInstance().setConfs(confs);
				Intent intent = new Intent(GET_CONFLIST);
				intent.putExtra("msgtype", MsgType.CONFLIST);
				intent.addCategory(XVIEW_JNI_CATA);
				lbm.sendBroadcast(intent);
				XviewLog.i(XTAG, "CONFLIST  sendBroadcast success");
				break;
			case ENTER_CONF:
				EnterConfType type = (EnterConfType) msg.obj;
				Intent enterconfintent = new Intent(GET_CONFLIST);
				enterconfintent.putExtra("msgtype", MsgType.ENTERCONF_RESULT);
				enterconfintent.putExtra("msg", type);
				enterconfintent.addCategory(XVIEW_JNI_CATA);
				lbm.sendBroadcast(enterconfintent);
				XviewLog.i(XTAG, "ENTER_CONF  sendBroadcast success");
				break;




			case MEMBER_ENTER:
				Intent enterIntent = new Intent(GET_CONFLIST);
				enterIntent.putExtra("msgtype", MsgType.MEMBER_ENTER);
				enterIntent.addCategory(XVIEW_JNI_CATA);
				lbm.sendBroadcast(enterIntent);
				XviewLog.i(XTAG, "MEMBER_ENTER  sendBroadcast success");
				break;






			case MEMBER_EXIT:
				XviewLog.i(XTAG, " MEMBER_EXIT msg");
				MemberExit exit = (MemberExit) msg.obj;
				long getnUserID = exit.getnUserID();
				XviewLog.i(XTAG, " MEMBER_EXIT userid=" + getnUserID);
				User findUser = GlobalHolder.getInstance().findUser(getnUserID);
				XviewLog.i(XTAG, " MEMBER_EXIT findUser");
				GlobalHolder.getInstance().mUers.remove(new User(getnUserID));
				XviewLog.i(XTAG, " MEMBER_EXIT findUser");
				// GlobalHolder.getInstance().allUsers.remove(findUser);
				// GlobalHolder.getInstance().removeUserAvatar(findUser);
				GlobalHolder.getInstance().removeDevice(getnUserID);
				XviewLog.i(XTAG, " MEMBER_EXIT removeDevice");
				Intent exitIntent = new Intent(GET_CONFLIST);
				exitIntent.putExtra("msgtype", MsgType.MEMBER_EXIT);
				exitIntent.putExtra("userid", getnUserID);
				if (findUser != null)
					exitIntent.putExtra("name", findUser.getNickName());

				XviewLog.i(XTAG, " MEMBER_EXIT name=" + findUser.getNickName());
				exitIntent.putExtra("exitcode", exit.getExitcode());
				XviewLog.i(XTAG, " MEMBER_EXIT sendBroadcast exitcode");
				exitIntent.putExtra("email", exit.getEmail());
				XviewLog.i(XTAG, " MEMBER_EXIT sendBroadcast email");
				exitIntent.addCategory(XVIEW_JNI_CATA);
				XviewLog.i(XTAG, " MEMBER_EXIT sendBroadcast");
				lbm.sendBroadcast(exitIntent);
				XviewLog.i(XTAG, " MEMBER_EXIT sendBroadcast success");
				break;

			case VIDEOLIST_COME:
				String szXmlData = (String) msg.obj;
				// 通过解析得到所有已经进入视频会议了，并给大仓库GlobaHolder
				Map<Long, List<VideoDevice>> parserVideodevice = XmlParserUtils
						.parserVideodevice(new ByteArrayInputStream(szXmlData.getBytes()));

				if (parserVideodevice != null && parserVideodevice.size() != 0)
					GlobalHolder.getInstance().addDevice(parserVideodevice);


				EventBus.getDefault().post(new EventConfMsg(EventMsgType.ON_NEW_USER_ENTER));


				Intent enter2Intent = new Intent(GET_CONFLIST);
				enter2Intent.putExtra("msgtype", MsgType.VIDEO_LIST);
				enter2Intent.addCategory(XVIEW_JNI_CATA);
				lbm.sendBroadcast(enter2Intent);
				XviewLog.i(XTAG, "VIDEOLIST_COME  sendBroadcast success");
				break;
			case VIDEOREMOTE_SETTING_COME:
				RemoteSettingBean setting = (RemoteSettingBean) msg.obj;

				if (setting != null) {
					Intent whIntent = new Intent(GET_CONFLIST);
					whIntent.putExtra("msgtype",
							MsgType.VIDEOREMOTE_SETTING_COME);
					whIntent.putExtra("dev", setting.getDev());
					whIntent.putExtra("nDisable", setting.getnDisable());
					whIntent.putExtra("nSizeIndex", setting.getnSizeIndex());
					whIntent.putExtra("fps", setting.getFps());
					whIntent.putExtra("bps", setting.getBps());
					whIntent.addCategory(XVIEW_JNI_CATA);
					lbm.sendBroadcast(whIntent);
					XviewLog.i(XTAG, "VIDEOREMOTE_SETTING_COME  sendBroadcast success");
				}
				break;
			case SYNC_OPEN_VIDEO:
				String str = (String) msg.obj;
				List<SyncDevice> devices = XmlParserUtils.parseSyncConfXml(str);
				for (int i = 0; i < devices.size(); i++) {
					Intent soIntent = new Intent(GET_CONFLIST);
					soIntent.putExtra("msgtype", MsgType.SYNC_OPEN_VIDEO);
					soIntent.putExtra("DstDeviceID", devices.get(i)
							.getDstDeviceID());
					soIntent.putExtra("DstUserID", devices.get(i)
							.getDstUserID());
					soIntent.addCategory(XVIEW_JNI_CATA);
					lbm.sendBroadcast(soIntent);
					XviewLog.i(XTAG, "SYNC_OPEN_VIDEO  sendBroadcast success");
				}
				break;
			case SYNC_CLOSE_VIDEO:
				SyncCloseDevice scd = (SyncCloseDevice) msg.obj;
				Intent scIntent = new Intent(GET_CONFLIST);
				scIntent.putExtra("msgtype", MsgType.SYNC_CLOSE_VIDEO);
				scIntent.putExtra("nDstUserID", scd.getnDstUserID());
				scIntent.putExtra("sDstMediaID", scd.getsDstMediaID());
				scIntent.putExtra("bClose", scd.isbClose());
				scIntent.addCategory(XVIEW_JNI_CATA);
				lbm.sendBroadcast(scIntent);
				XviewLog.i(XTAG, "SYNC_CLOSE_VIDEO  sendBroadcast success");
				break;
			case MODIFY_CONF_DESC:
				String xml = (String) msg.obj;
				ModifyConfDesc desc = XmlParserUtils
						.parseModifyConfDescXml(xml);
				Intent i = new Intent(GET_CONFLIST);
				Bundle bundle = new Bundle();
				bundle.putSerializable("modify_conf_desc", desc);
				i.putExtra("conf_desc", bundle);
				i.putExtra("msgtype", MsgType.MODIFY_CONF_DESC);
				i.addCategory(XVIEW_JNI_CATA);
				lbm.sendBroadcast(i);
				XviewLog.i(XTAG, "MODIFY_CONF_DESC  sendBroadcast success");
				break;
			case PERMISS:
				PermissType permiss = (PermissType) msg.obj;
				if (permiss != null && permiss.getType() == 3)
					GlobalHolder.getInstance().mSpeakUers.put(
							permiss.getUserid(), permiss.getStatus());
				Intent permissIntent = new Intent(GET_CONFLIST);
				permissIntent.putExtra("msgtype", MsgType.PERIMSSTYPE);
				permissIntent.addCategory(XVIEW_JNI_CATA);
				lbm.sendBroadcast(permissIntent);
				XviewLog.i(XTAG, "PERMISS  sendBroadcast success");
				break;
			case DOCCOME:
				DocShare mdoc = (DocShare) msg.obj;
				GlobalHolder.getInstance().getmDocShares().add(mdoc);
				Intent docIntent = new Intent(GET_CONFLIST);
				docIntent.putExtra("msgtype", MsgType.DOCHASCOME);
				docIntent.addCategory(XVIEW_JNI_CATA);
				lbm.sendBroadcast(docIntent);
				XviewLog.i(XTAG, "DOCCOME  sendBroadcast success");
				break;
			case PAGE_CLOSE:
				String wbid = (String) msg.obj;
				GlobalHolder.getInstance().removeDoc(wbid);
				Intent delDocIntent = new Intent(GET_CONFLIST);
				delDocIntent.putExtra("msgtype", MsgType.DOCCLOSE);
				delDocIntent.addCategory(XVIEW_JNI_CATA);
				lbm.sendBroadcast(delDocIntent);
				XviewLog.i(XTAG, "PAGE_CLOSE  sendBroadcast success");
				break;
			case PAGELIST:
				PageListType mpage = (PageListType) msg.obj;
				String szWBoardID = mpage.getSzWBoardID();
				int count = mpage.getCount();
				GlobalHolder.getInstance().updatePage(szWBoardID, count);
				Intent pagelistIntent = new Intent(GET_CONFLIST);
				pagelistIntent.putExtra("msgtype", MsgType.PAGECOUNTCOME);
				pagelistIntent.addCategory(XVIEW_JNI_CATA);
				lbm.sendBroadcast(pagelistIntent);
				XviewLog.i(XTAG, "PAGELIST  sendBroadcast success");
				break;
			case PAGE_CONTENT:
				Page mPage = (Page) msg.obj;
				GlobalHolder.getInstance().addPage(mPage.getWbid(), mPage);
				Intent pageIntent = new Intent(GET_CONFLIST);
				pageIntent.putExtra("msgtype", MsgType.PAGE_DISPLAY);
				pageIntent.addCategory(XVIEW_JNI_CATA);
				lbm.sendBroadcast(pageIntent);
				XviewLog.i(XTAG, "PAGE_CONTENT  sendBroadcast success");
				break;
			case DATACOME:
				DataEntity entity = (DataEntity) msg.obj;
				Label label = entity.getLabel();
				int getnPageID = entity.getnPageID();
				String szWBoardID2 = entity.getSzWBoardID();
				GlobalHolder.getInstance().addShareLable(label, getnPageID,
						szWBoardID2);
				Intent labelCome = new Intent(GET_CONFLIST);
				labelCome.putExtra("msgtype", MsgType.DATACOME);
				labelCome.putExtra("getnPageID", getnPageID);
				labelCome.putExtra("szWBoardID2", szWBoardID2);
				labelCome.addCategory(XVIEW_JNI_CATA);
				lbm.sendBroadcast(labelCome);
				XviewLog.i(XTAG, "DATACOME  sendBroadcast success");
				break;
			case DATAREMOVE:
				DataEntity remove_entity = (DataEntity) msg.obj;
				int remove_PageID = remove_entity.getnPageID();
				String remove_wbid = remove_entity.getSzWBoardID();
				String dataid = remove_entity.getSzDataID();
				GlobalHolder.getInstance().removeShareData(remove_wbid,
						remove_PageID, dataid);
				Intent labelremove = new Intent(GET_CONFLIST);
				labelremove.putExtra("msgtype", MsgType.DATACOME);
				labelremove.putExtra("getnPageID", remove_PageID);
				labelremove.putExtra("szWBoardID2", remove_wbid);
				labelremove.addCategory(XVIEW_JNI_CATA);
				lbm.sendBroadcast(labelremove);
				XviewLog.i(XTAG, "DATAREMOVE  sendBroadcast success");
				break;
			case KICK_CONF:
				Intent kickIntent = new Intent(GET_CONFLIST);
				kickIntent.putExtra("msgtype", MsgType.KICK_CONF);
				lbm.sendBroadcast(kickIntent);
				XviewLog.i(XTAG, "KICK_CONF  sendBroadcast success");
				break;
			case LOGIN_OUT:
				Intent ii = new Intent(
						"com.cinlan.xview.broadcast.FORCE_OFFLINE");
				ii.putExtra("logoutFlag", logoutFlag);
				lbm.sendBroadcast(ii);
				XviewLog.i(XTAG, "LOGIN_OUT  sendBroadcast success");
				break;
			case DISCONNECTED:
				Intent disconnectedIntent = new Intent(GET_CONFLIST);
				disconnectedIntent.putExtra("msgtype", MsgType.DISCONNECTED);
				lbm.sendBroadcast(disconnectedIntent);
				XviewLog.i(XTAG, "DISCONNECTED  sendBroadcast success");
				break;
			case MEDIA_MIXER:
				MediaEntity entyEntity = (MediaEntity) msg.obj;
				String mediaId = entyEntity.getMediaId();
				String name = entyEntity.getName();
				int monitorIndex = entyEntity.getMonitorIndex();
				int mixerType = entyEntity.getMixerType();
				long ownerID = entyEntity.getOwnerID();

				GlobalHolder.getInstance().addMediaDevice(mediaId, name, monitorIndex, mixerType, ownerID);



				Intent mediaIntent = new Intent(GET_CONFLIST);
				mediaIntent.putExtra("msgtype", MsgType.MEDIA_MIXER);

				lbm.sendBroadcast(mediaIntent);
				XviewLog.i(XTAG, "MEDIA_MIXER  sendBroadcast success");
				break;

			case MEDIA_DESTROY:
				String mediaID = (String) msg.obj;
				GlobalHolder.getInstance().removeMediaDevice(mediaID);
				Intent removeMediaIntent = new Intent(GET_CONFLIST);
				removeMediaIntent.putExtra("msgtype", MsgType.MEDIA_REMOVE);
				removeMediaIntent.putExtra("removeMedia", mediaID);
				lbm.sendBroadcast(removeMediaIntent);
				XviewLog.i(XTAG, "MEDIA_DESTROY  sendBroadcast success");
				break;

			case CONF_MUTE:
				Intent Muteintent = new Intent(GET_CONFLIST);
				Muteintent.putExtra("msgtype", MsgType.CONF_MUTE);
				lbm.sendBroadcast(Muteintent);
				XviewLog.i(XTAG, "CONF_MUTE  sendBroadcast success");
			case MESSAGE_LIST:
				List<ConfMessage> msgs = (List<ConfMessage>) msg.obj;
				GlobalHolder.getInstance().addConfMessages(msgs);
				Intent in = new Intent(GET_MESSAGELIST);
				in.putExtra("msgtype", MsgType.MESSAGE_LIST);
				in.addCategory(XVIEW_JNI_CATA);
				lbm.sendBroadcast(in);
				XviewLog.i(XTAG, "MESSAGE_LIST  sendBroadcast success");
				break;
			}

		};
	};
	static JNIService js = null;

	public static JNIService getInstance() {
		if (js == null) {
			js = new JNIService();
		}
		return js;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		XviewLog.i(XTAG, "onCreate");

		mConfCB = new ConfRequestCB(mConfHandler);
		mVideoCB = new VideoQuestCB(mConfHandler);
		mWBRequestCB = new WBRequestCB(mConfHandler);
		mImRequestJNICB = new ImRequestJNICB(mConfHandler);
		mChatRequestCB = new ChatRequestCB(mConfHandler);

		ImRequest.getInstance().addCallback(mImRequestJNICB);
		ConfRequest.getInstance().addCallback(mConfCB);
		VideoRequest.getInstance().addCallback(mVideoCB);
		WBRequest.getInstance().addCallback(mWBRequestCB);
		ChatRequest.getInstance().addCallback(mChatRequestCB);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	class ImRequestJNICB extends ImRequestCallbackAdapter {
		private Handler mHandler;

		public ImRequestJNICB(Handler mConfHandler) {
			this.mHandler = mConfHandler;
		}

		@Override
		public void OnLogoutCallback(int status) { // self:2 other:1
			super.OnLogoutCallback(status);
			XviewLog.i(XTAG, " logout self:2 other:1 status:" + status);

			if (status == 2) {
				logoutFlag = 2;
			} else if (status == 1) {
				logoutFlag = 1;
			}
			Message.obtain(mHandler, LOGIN_OUT, null).sendToTarget();
		}

		@Override
		public void OnConnectResponse(int nResult) {
			super.OnConnectResponse(nResult);
		}

		@Override
		public void OnServerFaild(String sModuleName) {
			super.OnServerFaild(sModuleName);
		}

		@Override
		public void OnSignalDisconnected() {
			super.OnSignalDisconnected();
			Message.obtain(mHandler, DISCONNECTED, null).sendToTarget();
		}

		@Override
		public void OnGetPersonalInfo(long nUserID, String InfoXml) {
			super.OnGetPersonalInfo(nUserID, InfoXml);
			if (!InfoXml.isEmpty()) {
				InputStream is = new ByteArrayInputStream(InfoXml.getBytes());
				PersonalInfo info = XmlParserUtils.parsePersonalInfo(is);
				GlobalHolder.getInstance().addPersonalInfo(nUserID, info);
			}
		}

		@Override
		public void OnLoginCallback(long nUserID, int nStatus, int nResult) {
			super.OnLoginCallback(nUserID, nStatus, nResult);

			RequestLogInResponse.Result res = RequestLogInResponse.Result
					.fromInt(nResult);
		}

		@Override
		public void OnConnectResponseCallback(int nResult) {
			super.OnConnectResponseCallback(nResult);
			RequestLogInResponse.Result res = RequestLogInResponse.Result
					.fromInt(nResult);
		}
	}

	class ChatRequestCB extends ChatRequestCallbackAdapter {
		private Handler mHandler;

		public ChatRequestCB(Handler mChatHandler) {
			this.mHandler = mChatHandler;
		}

		@Override
		public void OnRecvChatPictureCallback(long nGroupID, int nBusinessType,
				long nFromUserID, long nTime, byte[] pPicData, int nLength) {
			super.OnRecvChatPictureCallback(nGroupID, nBusinessType,
					nFromUserID, nTime, pPicData, nLength);
		}

		@Override
		public void OnRecvChatTextCallback(long nGroupID, int nBusinessType,
				long nFromUserID, long nTime, byte[] byteXmlText) {
			try {
				super.OnRecvChatTextCallback(nGroupID, nBusinessType,
						nFromUserID, nTime, byteXmlText);
				String szXmlText = new String(byteXmlText);
				if (!szXmlText.isEmpty()) {
					InputStream is = new ByteArrayInputStream(
							szXmlText.getBytes());

					List<ConfMessage> lists = XmlParserUtils.parseMessageXml(
							is, nGroupID, nBusinessType, nFromUserID, nTime,
							byteXmlText.length);
					ConfMessage msg = lists.get(0);
					msg.setnGroupID(nGroupID);
					msg.setnBusinessType(nBusinessType);
					msg.setnFromUserID(nFromUserID);
					long time = nTime;
					String strTime = String.valueOf(time);
					if (strTime.length() == 10) {
						strTime = strTime + "000";
					}
					time = Long.parseLong(strTime);
					msg.setnTime(time);
					msg.setnLength(byteXmlText.length);
					lists.remove(0);
					lists.add(msg);
					Message.obtain(mHandler, MESSAGE_LIST, lists)
							.sendToTarget();

				}
			} catch (Exception e) {
				return;
			}
		}

		@Override
		public void OnSendChatPictureCallback(long nGroupID, long nToUserID,
				byte[] pPicData, int nLength) {
			super.OnSendChatPictureCallback(nGroupID, nToUserID, pPicData,
					nLength);
		}

		@Override
		public void OnSendChatTextCallback(long mGroupID, long nToUserID,
				String szText) {
			super.OnSendChatTextCallback(mGroupID, nToUserID, szText);
		}

	}

	class ConfRequestCB extends ConfRequestCallbackAdapter {
		private Handler mHandler;

		public ConfRequestCB(Handler mConfHandler) {
			this.mHandler = mConfHandler;
		}

		@Override
		public void OnGetConfList(String szConfListXml) {
			super.OnGetConfList(szConfListXml);

			List<Conf> conflist = XmlParserUtils
					.parserConfList_server(new ByteArrayInputStream(
							szConfListXml.getBytes()));
			Message.obtain(mHandler, CONFLIST, conflist).sendToTarget();
		}

		@Override
		public void OnEnterConfCallback(long nConfID, long nTime,
				String szConfData, int nJoinResult) {
			super.OnEnterConfCallback(nConfID, nTime, szConfData, nJoinResult);
			EnterConfType type = new EnterConfType();
			type.setnConfID(nConfID);
			type.setnJoinResult(nJoinResult);
			type.setNtime(nTime);
			type.setSzConfData(szConfData);
			Message.obtain(mHandler, ENTER_CONF, type).sendToTarget();
		}

		@Override
		public void OnConfMemberEnterCallback(long nConfID, long nTime,
				String szUserInfos) {
			super.OnConfMemberEnterCallback(nConfID, nTime, szUserInfos);

			User enterConfMem = XmlParserUtils
					.parserEnterConfMem(new ByteArrayInputStream(szUserInfos
							.getBytes()));

			if (enterConfMem != null) {
				GlobalHolder.EnterMemberUserId = enterConfMem.getmUserId();
				GlobalHolder.EnterMemberUserNickName = enterConfMem.getNickName();
				GlobalHolder.EnterMemberUserData = enterConfMem.getmEmail();
			}


			//添加进入的成员
			GlobalHolder.getInstance().addAttended(enterConfMem);


			MemberEnter enter = new MemberEnter();
			enter.setnConfID(nConfID);
			enter.setnTime(nTime);
			enter.setSzUserInfos(szUserInfos);
			Message.obtain(mHandler, MEMBER_ENTER, enter).sendToTarget();

			SharedPreferences pre = getSharedPreferences("UserIdName", getApplication().MODE_PRIVATE);
			SharedPreferences.Editor editor = pre.edit();
			editor.putString("" + enterConfMem.getmUserId(),
					"" + enterConfMem.getNickName());
			editor.commit();
		}

		@Override
		public void OnConfMemberExitCallback(long nConfID, long nTime,
											 long nUserID, int exitcode, String email) {
			super.OnConfMemberExitCallback(nConfID, nTime, nUserID, exitcode, email);
			MemberExit exit = new MemberExit();
			exit.setnConfID(nConfID);
			exit.setnTime(nTime);
			exit.setnUserID(nUserID);
			exit.setExitcode(exitcode);
			exit.setEmail(email);
			Message.obtain(mHandler, MEMBER_EXIT, exit).sendToTarget();
		}

		@Override
		public void OnGrantPermissionCallback(long userid, int type, int status) {
			super.OnGrantPermissionCallback(userid, type, status);

			PermissType mPermissType = new PermissType();
			mPermissType.setStatus(status);
			mPermissType.setType(type);
			mPermissType.setUserid(userid);
			Message.obtain(mHandler, PERMISS, mPermissType).sendToTarget();
		}

		@Override
		public void OnKickConfCallback(int nReason) {
			super.OnKickConfCallback(nReason);

			Message.obtain(mHandler, KICK_CONF, null).sendToTarget();
		}

		@Override
		public void OnCreateVideoMixer(String mediaId, String name,
				int monitorIndex, int mixerType, long ownerID) {
			super.OnCreateVideoMixer(mediaId, name, monitorIndex, mixerType,
					ownerID);
			MediaEntity entity = new MediaEntity();

			// String mediaID = mediaId.split(":")[0];
			// Integer mediaid = new Integer(mediaID);

			entity.setMediaId(mediaId);
			entity.setMixerType(mixerType);
			entity.setMonitorIndex(monitorIndex);
			entity.setName(name);
			entity.setOwnerID(ownerID);
			Message.obtain(mHandler, MEDIA_MIXER, entity).sendToTarget();
		}

		@Override
		public void OnDestroyVideoMixer(String mediaId) {
			super.OnDestroyVideoMixer(mediaId);
			Message.obtain(mHandler, MEDIA_DESTROY, mediaId).sendToTarget();
		}

		public void OnConfMute() {
			super.OnConfMute();
			Message.obtain(mHandler, CONF_MUTE, "").sendToTarget();
		};

		/**
		 * <video DstDeviceID='4611686018427388088:HP Truevision
		 * HD____2074422303' DstUserID='4611686018427388088' Full='0'
		 * IsSyncing='1' PageId='0' Pos='0'/><br>
		 */
		@Override
		public void OnConfSyncOpenVideoCallback(String sDstMediaID) {
			super.OnConfSyncOpenVideoCallback(sDstMediaID);
			// 通过userid判断
			Message.obtain(mHandler, SYNC_OPEN_VIDEO, sDstMediaID)
					.sendToTarget();
		}

		@Override
		public void OnConfSyncCloseVideoCallback(long nDstUserID,
				String sDstMediaID, boolean bClose) {
			super.OnConfSyncCloseVideoCallback(nDstUserID, sDstMediaID, bClose);
			// 通过userid判断
			SyncCloseDevice scd = new SyncCloseDevice(nDstUserID, sDstMediaID,
					bClose);
			Message.obtain(mHandler, SYNC_CLOSE_VIDEO, scd).sendToTarget();
		}

		@Override
		public void OnConfNotify(String confXml, String creatorXml) {
			super.OnConfNotify(confXml, creatorXml);
		}

		/**
		 * 修改会议描述
		 */
		@Override
		public void OnModifyConfDesc(long nConfID, String szConfDescXml) {
			super.OnModifyConfDesc(nConfID, szConfDescXml);
			Message.obtain(mHandler, MODIFY_CONF_DESC, szConfDescXml)
					.sendToTarget();
		}
	}

	class WBRequestCB extends WBRequestCallbackAdapter {

		private Handler mHandler;

		public WBRequestCB(Handler mConfHandler) {
			this.mHandler = mConfHandler;
		}

		@Override
		public void OnWBoardChatInviteCallback(long nGroupID,
				int nBusinessType, long nFromUserID, String szWBoardID,
				int nWhiteIndex, String szFileName, int type) {
			DocShare doc = new DocShare();
			if ("".equals(szFileName)) {
				doc.setFilename(getResources().getString(R.string.wb_xviewsdk)
						+ nWhiteIndex);
				Page page = new Page();
				page.setIndex(1);
				page.setWbid(szWBoardID);
				page.setPaths("");
				doc.getPages().add(page);
				doc.setWb(true);
			} else {
				// 锟斤拷签锟斤拷
				if (szFileName.length() > 5) {
					int index = szFileName.lastIndexOf("\\");
					szFileName = szFileName.substring(index + 1);
				}
				doc.setFilename(szFileName);
			}
			doc.setType(type);
			doc.setBussinesstype(nBusinessType);
			doc.setFromUserID(nFromUserID);
			doc.setGroupId(nGroupID);
			doc.setWBoardID(szWBoardID);
			Message.obtain(mHandler, DOCCOME, doc).sendToTarget();
		}

		@Override
		public void OnWBoardPageListCallback(String szWBoardID,
				String szPageData, int nPageID) {
			int count = XmlParserUtils.parserDocNums(new ByteArrayInputStream(
					szPageData.getBytes()));
			PageListType mPageListType = new PageListType();
			mPageListType.setCount(count);
			mPageListType.setSzPageData(szPageData);
			mPageListType.setSzWBoardID(szWBoardID);
			Message.obtain(mHandler, PAGELIST, mPageListType).sendToTarget();
		}

		@Override
		public void OnWBoardDocDisplayCallback(String szWBoardID, int nPageID,
				String szFileName, int result) {
			Page page = new Page();
			page.setIndex(nPageID);
			page.setPaths(szFileName);
			page.setWbid(szWBoardID);
			Message.obtain(mHandler, PAGE_CONTENT, page).sendToTarget();
		}

		@Override
		public void OnWBoardClosedCallback(long nGroupID, int nBusinessType,
				long nUserID, String szWBoardID) {
			Message.obtain(mHandler, PAGE_CLOSE, szWBoardID).sendToTarget();
		}

		@Override
		public void OnWBoardAddPageCallback(String szWBoardID, int nPageID) {
			PageListType mPageListType = new PageListType();
			mPageListType.setCount(nPageID);
			mPageListType.setSzWBoardID(szWBoardID);
			Message.obtain(mHandler, PAGELIST, mPageListType).sendToTarget();
		}

		@Override
		public void OnRecvAddWBoardDataCallback(String szWBoardID, int nPageID,
				String szDataID, String szData) {

			InputStream in = new ByteArrayInputStream(szData.getBytes());
			Label label = XmlParserUtils.parserLables(in);
			DataEntity entity = new DataEntity();
			entity.setLabel(label);
			entity.setnPageID(nPageID);
			entity.setSzWBoardID(szWBoardID);
			entity.setSzDataID(szDataID);
			Message.obtain(mHandler, DATACOME, entity).sendToTarget();
		}

		@Override
		public void OnRecvAppendWBoardDataCallback(String szWBoardID,
				int nPageID, String szDataID, String szData) {
			InputStream in = new ByteArrayInputStream(szData.getBytes());
			Label label = XmlParserUtils.parserLables(in);
			DataEntity entity = new DataEntity();
			entity.setLabel(label);
			entity.setnPageID(nPageID);
			entity.setSzWBoardID(szWBoardID);
			entity.setSzDataID(szDataID);
			Message.obtain(mHandler, DATACOME, entity).sendToTarget();
		}

		/*
		 * OnWBoardDataRemoved 98771db40-70e3-4d92-a828-fc8fd79ec2eb 1
		 * {44B4E3AA-B0CA-4C56-98D1-46E07CEC3B44}
		 * 
		 * @see
		 * com.cinlan.jni.WBRequestCallbackAdapter#OnWBoardDataRemovedCallback
		 * (java.lang.String, int, java.lang.String)
		 */
		@Override
		public void OnWBoardDataRemovedCallback(String szWBoardID, int nPageID,
				String szDataID) {
			super.OnWBoardDataRemovedCallback(szWBoardID, nPageID, szDataID);
			DataEntity entity = new DataEntity();
			entity.setnPageID(nPageID);
			entity.setSzDataID(szDataID);
			entity.setSzWBoardID(szWBoardID);
			Message.obtain(mHandler, DATAREMOVE, entity).sendToTarget();
		}

	}


	/**
	 * 当有新的用户进入之后,改用户的视频设备信息返回
	 *
	 *
	 */
	class VideoQuestCB implements VideoRequestCallback {

		private Handler mHandler;

		public VideoQuestCB(Handler mConfHandler) {
			this.mHandler = mConfHandler;
		}

		@Override
		public void OnRemoteUserVideoDevice(String szXmlData) {
			Message.obtain(mHandler, VIDEOLIST_COME, szXmlData).sendToTarget();
			System.out.println("szXmlData:" + szXmlData);
			XviewLog.e("szXmlData:", szXmlData);
		}

		@Override
		public void OnRemoteUpdateVideoSetting(String dev, int nDisable,
				int nSizeIndex, int fps, int bps) {
			XviewLog.i("ImRequest UI", "OnRemoteUpdateVideoSetting " + dev
					+ ", " + nDisable + ", " + nSizeIndex + ", " + fps + ", "
					+ bps);
			RemoteSettingBean bean = new RemoteSettingBean(dev, nDisable,
					nSizeIndex, fps, bps);
			Message.obtain(mHandler, VIDEOREMOTE_SETTING_COME, bean)
					.sendToTarget();
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}
}
