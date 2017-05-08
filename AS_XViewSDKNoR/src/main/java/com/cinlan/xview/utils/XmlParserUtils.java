package com.cinlan.xview.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.graphics.Point;
import android.util.Xml;

import com.cinlan.xview.bean.Conf;
import com.cinlan.xview.bean.ConfMessage;
import com.cinlan.xview.bean.LocalDeviceSupportScreen;
import com.cinlan.xview.bean.ModifyConfDesc;
import com.cinlan.xview.bean.PersonalInfo;
import com.cinlan.xview.bean.Size;
import com.cinlan.xview.bean.SyncDevice;
import com.cinlan.xview.bean.User;
import com.cinlan.xview.bean.VideoDevice;
import com.cinlan.xview.bean.data.Beeline;
import com.cinlan.xview.bean.data.Brush;
import com.cinlan.xview.bean.data.Ellipse;
import com.cinlan.xview.bean.data.FreedomLine;
import com.cinlan.xview.bean.data.HeightLightLine;
import com.cinlan.xview.bean.data.Label;
import com.cinlan.xview.bean.data.Leaser;
import com.cinlan.xview.bean.data.Pen;
import com.cinlan.xview.bean.data.Rectangle;
import com.cinlan.xview.bean.data.RoundRect;

public class XmlParserUtils {

	public static Label parserLables(InputStream in) {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(in, "UTF-8");

			Label label = null;
			Point point = null;
			List<Point> points = new ArrayList<Point>();
			Pen pen = null;
			Brush brush = null;
			for (int type = parser.getEventType(); type != XmlPullParser.END_DOCUMENT; type = parser
					.next()) {
				if (type == XmlPullParser.START_TAG) {
					if ("TFreedomLineMeta".equals(parser.getName())) {
						label = new FreedomLine();
						label.setPageid(parser.getAttributeValue(null, "ID"));
					} else if ("TRoundRectMeta".equals(parser.getName())) {
						label = new RoundRect();
						label.setPageid(parser.getAttributeValue(null, "ID"));
					} else if ("TBeelineMeta".equals(parser.getName())) {
						label = new Beeline();
						label.setPageid(parser.getAttributeValue(null, "ID"));
					} else if ("TRectangleMeta".equals(parser.getName())) {
						label = new Rectangle();
						label.setPageid(parser.getAttributeValue(null, "ID"));
					} else if ("TEllipseMeta".equals(parser.getName())) {
						label = new Ellipse();
						label.setPageid(parser.getAttributeValue(null, "ID"));
					} else if ("THighlightLineMeta".equals(parser.getName())) {
						label = new HeightLightLine();
						label.setPageid(parser.getAttributeValue(null, "ID"));
					} else if ("TEraseLineMeta".equals(parser.getName())) { // 橡皮擦功能
						label = new Leaser();
						label.setPageid(parser.getAttributeValue(null, "ID"));
					} else if ("Points".equals(parser.getName())) {
						String pointling = parser.nextText();
						String[] strings = pointling.split(" ");

						boolean flag = true; // 标志是x还是y
						for (String p : strings) {
							if (flag) {
								point = new Point();
								points.add(point);
								point.x = Integer.parseInt(p);
							} else {
								point.y = Integer.parseInt(p);
							}
							flag = !flag;
						}

						label.setPoints(points);
					} else if ("Pen".equals(parser.getName())) {
						pen = new Pen();
						pen.setAlign(parser.getAttributeValue(null, "Align"));
						pen.setColor(Integer.parseInt(parser.getAttributeValue(
								null, "Color")));
						pen.setDashStyle(parser.getAttributeValue(null,
								"DashStyle"));
						pen.setEndCap(parser.getAttributeValue(null, "EndCap"));
						pen.setLineJoin(parser.getAttributeValue(null,
								"LineJoin"));
						pen.setStartCap(parser.getAttributeValue(null,
								"StartCap"));
						pen.setWidth(Integer.parseInt(parser.getAttributeValue(
								null, "Width")));
						label.setPen(pen);
					} else if ("Brush".equals(parser.getName())) {
						brush = new Brush();
						brush.setBreushType(parser.getAttributeValue(0));
						brush.setHatchBackColor(Integer.parseInt(parser
								.getAttributeValue(1)));
						brush.setHatchForeColor(Integer.parseInt(parser
								.getAttributeValue(2)));
						brush.setHatchStyle(parser.getAttributeValue(3));
						brush.setSolidColor(Integer.parseInt(parser
								.getAttributeValue(4)));
						label.setBrush(brush);
					}
				}

			}
			return label;
		} catch (Exception e) {
			return null;
		}
	}

	/*
	 * 
	 * InfoXml="<user avatarlocation="http://192.168.0.152:80/avatar/AVATAR_1016.
	 * png
	 * 
	 * " avatarname="6d0816a1-623d-4f6b-9373-20c1f155377f.png" bsystemavatar="
	 * 0" id=" 1016" needauth="1" nickname="閮織鏄? privacy="2" sign="ccc"
	 * usercode=" 10000156161"/>
	 */
	// 加入会议成功的回调返回的xml
	public static List<Conf> parserConfList(InputStream in) {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(in, "UTF-8"); //

			List<Conf> confs = new ArrayList<Conf>();
			Conf conf = null;

			for (int type = parser.getEventType(); type != XmlPullParser.END_DOCUMENT; type = parser
					.next()) { // 寰幆瑙ｆ瀽
				if (type == XmlPullParser.START_TAG) {
					if ("conf".equals(parser.getName())) { //
						conf = new Conf(); //
						String id = parser.getAttributeValue(null, "id"); //
						String subject = parser.getAttributeValue(null,
								"subject"); //
						boolean haskey = (parser.getAttributeValue(null,
								"haskey").equals("1") ? true : false);
						conf.setHaskey(haskey);
						if (haskey) {
							String key = parser.getAttributeValue(null, "key");
							conf.setKey(key);
						}

						conf.setId(Long.parseLong(id));
						conf.setSubject(subject);
						confs.add(conf);
					} else if ("chairuser".equals(parser.getName())) {

					}
				}

			}

			return confs;
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	// 解析会议（四个属性）<xml><conf createuserid='0' haskey='1' id='514140365552'
	// starttime='1414036555' subject='129_01'/>
	public static List<Conf> parserConfList_server(InputStream in) {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(in, "UTF-8"); //
			List<Conf> confs = new ArrayList<Conf>();
			Conf conf = null;
			for (int type = parser.getEventType(); type != XmlPullParser.END_DOCUMENT; type = parser
					.next()) {
				if (type == XmlPullParser.START_TAG) {
					if ("conf".equals(parser.getName())) {
						conf = new Conf();
						// 最新返回的顺序
						long starttime = Long.parseLong(parser
								.getAttributeValue(null, "starttime"));
						String id = parser.getAttributeValue(null, "id");
						String createuserid = parser.getAttributeValue(null,
								"createuserid");
						String haskey = parser
								.getAttributeValue(null, "haskey");
						conf.setStarttime(starttime);
						conf.setId(Long.parseLong(id));
						conf.setCreateuserid(Long.parseLong(createuserid));
						conf.setHaskey(Integer.parseInt(haskey) == 1 ? true
								: false);
						conf.setSubject(parser.getAttributeValue(null,
								"subject"));
						confs.add(conf);
					}
				}
			}
			Collections.reverse(confs);
			return confs;
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/*
	 * 
	 * <conf canaudio='1' candataop='1' canvideo='1' chairnickname=''
	 * chairuserid='112814' createuserid='0' createusernickname='' endtime='0'
	 * haskey='0' id='4606935000' key='' layout='1' lockchat='0' lockconf='0'
	 * lockfiletrans='0' mode='2' pollingvideo='0' starttime='1367025609'
	 * subject='长城H2发布' syncdesktop='0' syncdocument='0' syncvideo='0'/>
	 * 
	 * 
	 * 最新版OnEnterConf 4611686532635247916 1111 1420791322<conf canaudio='1'
	 * candataop='1' canvideo='1' confnotify='' createuserid='0'
	 * createusernickname='' endtime='1893430860' haskey='1'
	 * id='4611686532635247916' isquickmode='0'layout='33620737' lockchat='0'
	 * lockconf='0' lockfiletrans='0' lockpollingvideo='0'lockrecord='0'
	 * mode='1' pageid='0' pollingvideo='0' quickvalue='7' rollcall='0'
	 * rollingmessage='' starttime='1420786000' subject='129_08级联'
	 * syncdesktop='0'syncdocument='0' syncvideo='0' userlistmode='1'
	 * videoh323='1' videomixer='1' videomonitor='1' videosip='1'
	 * videowall='1'/> 0
	 */

	// 解析进入会议后的 会议信息
	public static Conf parserOnEnterConf(InputStream in) {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(in, "UTF-8");
			Conf conf = null;
			for (int type = parser.getEventType(); type != XmlPullParser.END_DOCUMENT; type = parser
					.next()) {
				if (type == XmlPullParser.START_TAG) {

					if ("conf".equals(parser.getName())) {
						conf = new Conf();
						conf.setCanaudio(parser.getAttributeValue(null,
								"canaudio").equals("0") ? true : false);
						conf.setCandataop(parser.getAttributeValue(null,
								"candataop").equals("0") ? true : false);
						conf.setCanvideo(parser.getAttributeValue(null,
								"canvideo").equals("0") ? true : false);
						conf.setChairnickname(parser.getAttributeValue(null,
								"chairnickname"));
						// conf.setChairuserid(Long.parseLong(parser.getAttributeValue(null,
						// "chairuserid")));
						conf.setCreateuserid(Long.parseLong(parser
								.getAttributeValue(null, "createuserid")));
						conf.setCreateusernickname(parser.getAttributeValue(
								null, "createusernickname"));
						conf.setEndtime(Long.parseLong(parser
								.getAttributeValue(null, "endtime")));
						conf.setHaskey(parser.getAttributeValue(null, "haskey")
								.equals("0") ? true : false);
						conf.setId(Long.parseLong(parser.getAttributeValue(
								null, "id")));
						conf.setKey(parser.getAttributeValue(null, "key"));
						conf.setLayout(parser.getAttributeValue(null, "layout")
								.equals("0") ? true : false);
						conf.setLockchat(parser.getAttributeValue(null,
								"lockchat").equals("0") ? true : false);
						conf.setLockconf(parser.getAttributeValue(null,
								"lockconf").equals("0") ? true : false);
						conf.setLockfiletrans(parser.getAttributeValue(null,
								"lockfiletrans").equals("0") ? true : false);
						conf.setMode(Integer.parseInt(parser.getAttributeValue(
								null, "mode")));
						conf.setPollingvideo(parser.getAttributeValue(null,
								"pollingvideo").equals("0") ? true : false);
						conf.setStarttime(Long.parseLong(parser
								.getAttributeValue(null, "starttime")));
						conf.setSubject(parser.getAttributeValue(null,
								"subject"));
						conf.setSyncdesktop(parser.getAttributeValue(null,
								"syncdesktop").equals("0") ? true : false);
						conf.setSyncdocument(parser.getAttributeValue(null,
								"syncdocument").equals("0") ? true : false);
						conf.setSyncvideo(parser.getAttributeValue(null,
								"syncvideo").equals("0") ? true : false);
					}
				}

			}

			return conf;
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	// 解析用戶個人信息,用于显示个人详细信息页面使用

	/*
	 * <personalinfo address="" birthday="" email="10000156238" homepage=""
	 * job="" memo="" mobile="" postcode="" realname="" sex="1" telephone="" />
	 */

	/**
	 * 01-08 15:30:30.754: E/ImRequest UI(27892): OnGetPersonalInfo
	 * 4611686018427388050--> <personalinfo address='' birthday='' email=''
	 * homepage='' job='' memo='' mobile='' postcode='' realname='' sex='0'
	 * telephone=''/>
	 */
	public static User parserCurrentUser(InputStream in) {
		try {
			XmlPullParser parser = Xml.newPullParser(); // 获取解析器
			parser.setInput(in, "UTF-8"); // 设置输入流
			User person = null;
			for (int type = parser.getEventType(); type != XmlPullParser.END_DOCUMENT; type = parser
					.next()) { // 循环解析
				if (type == XmlPullParser.START_TAG) { // 判断如果遇到开始标签事件
					if ("personalinfo".equals(parser.getName())) { // 标签名为conf
						person = new User();
						person.setAddress(parser.getAttributeValue(null,
								"address"));
						// person.setBirthday(parser.getAttributeValue(null,
						// "birthday"));
						person.setEmail(parser.getAttributeValue(null, "email"));
						// person.setHomepage(parser.getAttributeValue(null,
						// "homepage"));
						// person.setJob(parser.getAttributeValue(null, "job"));
						// person.setMemo(parser.getAttributeValue(null,
						// "memo"));
						// person.setMobile(parser.getAttributeValue(null,
						// "mobile"));
						// person.setPostcode(parser.getAttributeValue(null,
						// "postcode"));
						// person.setRealname(parser.getAttributeValue(null,
						// "realname"));
						// person.setSex(parser.getAttributeValue(null,
						// "sex").equals("0")?true:false);
						person.setTelephone(parser.getAttributeValue(null,
								"telephone"));
					}
				}

			}

			return person;
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	};;

	/**
	 * 01-09 17:05:22.797: E/ImRequest UI(14427): OnConfMemberExit
	 * 4611686532635247916 1420794233 4611686018427388005 01-09 17:05:29.997:
	 * E/ImRequest UI(14427): OnConfMemberEnter 4611686532635247916 1420794240
	 * <user avatarlocation='' avatarname='' bsystemavatar='1' email='f1291'
	 * id='4611686018427388005' needauth='0' nickname='f1291' privacy='0'
	 * serverid='4194304' sign='0' uetype='1'/>
	 * 
	 * @param in
	 * @return
	 */
	public static User parserEnterConfMem(InputStream in) {
		try {
			XmlPullParser parser = Xml.newPullParser(); // 获取解析器
			parser.setInput(in, "UTF-8"); // 设置输入流

			User user = null;

			for (int type = parser.getEventType(); type != XmlPullParser.END_DOCUMENT; type = parser
					.next()) { // 循环解析
				if (type == XmlPullParser.START_TAG) { // 判断如果遇到开始标签事件
					if ("user".equals(parser.getName())) { // 标签名为conf
						user = new User();
						user.setNickName(parser.getAttributeValue(null,
								"nickname"));
						user.setmUserId(Long.parseLong(parser
								.getAttributeValue(null, "id")));
						user.setEmail(parser.getAttributeValue(null,
								"email"));
					}
				}
			}
			return user;
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	// 解析远端设备列表
	public static Map<Long, List<VideoDevice>> parserVideodevice(InputStream in) {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(in, "UTF-8");
			Map<Long, List<VideoDevice>> mapdevices = new HashMap<Long, List<VideoDevice>>();
			List<VideoDevice> devices = null;
			VideoDevice device = null;
			long userid = 0;
			Size size = null;
			for (int type = parser.getEventType(); type != XmlPullParser.END_DOCUMENT; type = parser
					.next()) {
				if (type == XmlPullParser.START_TAG) {
					if ("user".equals(parser.getName())) {
						userid = Long.parseLong(parser.getAttributeValue(null,
								"id"));
					} else if ("videolist".equals(parser.getName())) {
						devices = new ArrayList<VideoDevice>();
					} else if ("video".equals(parser.getName())) {
						device = new VideoDevice();
						device.setDisable(Integer.parseInt(parser
								.getAttributeValue(null, "disabled")));
						device.setBps(parser.getAttributeValue(null, "bps"));
						device.setDesc(parser.getAttributeValue(null, "desc"));
						device.setFbs(parser.getAttributeValue(null, "fps"));
						String id = parser.getAttributeValue(null, "id");
						device.setId(id);
						device.setSelectedindex(Integer.parseInt("0"));
						device.setVideotype(parser.getAttributeValue(null,
								"videotype"));
						devices.add(device);
					} else if ("size".equals(parser.getName())) {
						size = new Size();
						size.setH(Integer.parseInt(parser.getAttributeValue(
								null, "h")));
						size.setW(Integer.parseInt(parser.getAttributeValue(
								null, "w")));
						device.getSizelist().add(size);
					}
				} else if (type == XmlPullParser.END_TAG) {
					if ("user".equals(parser.getName())) {
						mapdevices.put(userid, devices);
					}
				}
			}

			return mapdevices;
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	// 解析文档共享中共返回多少页
	public static int parserDocNums(InputStream in) {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(in, "UTF-8");

			int count = 0;
			for (int type = parser.getEventType(); type != XmlPullParser.END_DOCUMENT; type = parser
					.next()) {
				if (type == XmlPullParser.START_TAG) {
					if ("page".equals(parser.getName())) {
						count++;
					}
				}
			}
			return count;
		} catch (Exception e) {
			return 0;
		}
	}

	public static PersonalInfo parsePersonalInfo(InputStream is) {
		PersonalInfo info = null;
		XmlPullParserFactory factory;
		try {
			factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(is, "UTF-8");
			int type = parser.getEventType();
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_DOCUMENT:
					info = new PersonalInfo();
					break;
				case XmlPullParser.START_TAG:
					if ("personalinfo".equals(parser.getName())) {
						String address = parser.getAttributeValue(null,
								"address");
						String birthday = parser.getAttributeValue(null,
								"birthday");
						String email = parser.getAttributeValue(null, "email");
						String homepage = parser.getAttributeValue(null,
								"homepage");
						String job = parser.getAttributeValue(null, "job");
						String memo = parser.getAttributeValue(null, "memo");
						String mobile = parser
								.getAttributeValue(null, "mobile");
						String postcode = parser.getAttributeValue(null,
								"postcode");
						String realname = parser.getAttributeValue(null,
								"realname");
						String sex = parser.getAttributeValue(null, "sex");
						String telephone = parser.getAttributeValue(null,
								"telephone");
						info.setAddress(address);
						info.setBirthday(birthday);
						info.setEmail(email);
						info.setHomepage(homepage);
						info.setJob(job);
						info.setMemo(memo);
						info.setMobile(mobile);
						info.setPostcode(postcode);
						info.setRealname(realname);
						info.setSex(sex);
						info.setTelephone(telephone);
					}
					break;
				case XmlPullParser.END_TAG:

					break;
				default:
					break;
				}
				type = parser.next();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return info;
	}


	public static List<LocalDeviceSupportScreen> parseDeviceListXml(
			InputStream is, int status) {
		LocalDeviceSupportScreen screen = null;
		List<LocalDeviceSupportScreen> lists = null;
		XmlPullParserFactory factory;
		String name = status == 2 ? "Camera Facing back"
				: "Camera Facing front";

		try {
			factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(is, "UTF-8");
			int type = parser.getEventType();
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_DOCUMENT:
					screen = new LocalDeviceSupportScreen();
					lists = new ArrayList<LocalDeviceSupportScreen>();
					break;
				case XmlPullParser.START_TAG:
					String devName = "";
					String fps = "";
					if ("device".equals(parser.getName())) {
						devName = parser.getAttributeValue(null, "devName");
						fps = parser.getAttributeValue(null, "fps");

						if (name.equals(devName)) {
							if ("size".equals(parser.getName())) {
								String width = parser.getAttributeValue(null,
										"width");
								String height = parser.getAttributeValue(null,
										"height");

								screen.setDevName(name);
								screen.setFps(fps);
								screen.setWidth(width);
								screen.setHeight(height);
							}
							String s = parser.getText();
							String d = parser.nextText();
							int c = parser.next();
							int f = parser.nextTag();
						}
					}

					break;
				case XmlPullParser.END_TAG:
					if ("device".equals(parser.getName()) && screen != null) {
						lists.add(screen);
					}
					break;
				default:
					break;
				}
				type = parser.next();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		return lists;

	}

	public static List<ConfMessage> parseMessageXml(InputStream is,
			long nGroupID, int nBusinessType, long nFromUserID, long nTime,
			int nLength) {
		ConfMessage msg = null;
		List<ConfMessage> lists = null;
		XmlPullParserFactory factory;
		try {
			factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(is, "UTF-8");
			int type = parser.getEventType();
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_DOCUMENT:
					lists = new ArrayList<ConfMessage>();
					msg = new ConfMessage();
					break;
				case XmlPullParser.START_TAG:
					if ("TChatData".equals(parser.getName())) {
						String IsAutoReply = parser.getAttributeValue(null,
								"IsAutoReply");
						String MessageID = parser.getAttributeValue(null,
								"MessageID");

						msg.setIsAutoReply(IsAutoReply);
						msg.setMessageID(MessageID);
					}
					if ("TChatFont".equals(parser.getName())) {
						String FontColor = parser.getAttributeValue(null,
								"Color");
						String FontName = parser
								.getAttributeValue(null, "Name");
						String FontSize = parser
								.getAttributeValue(null, "Size");
						String FontStyle = parser.getAttributeValue(null,
								"Style");
						msg.setFontColor(FontColor);
						msg.setFontName(FontName);
						msg.setFontSize(FontSize);
						msg.setFontStyle(FontStyle);
					}
					if ("TTextChatItem".equals(parser.getName())) {
						String NewLine = parser.getAttributeValue(null,
								"NewLine");
						String FontIndex = parser.getAttributeValue(null,
								"FontIndex");
						String Text = parser.getAttributeValue(null, "Text");

						msg.setNewLine(NewLine);
						msg.setFontIndex(FontIndex);
						msg.setText(Text);
					}
					break;
				case XmlPullParser.END_TAG:
					if ("TChatData".equals(parser.getName()) && msg != null) {
						msg.setnGroupID(nGroupID);
						msg.setnBusinessType(nBusinessType);
						msg.setnFromUserID(nFromUserID);
						msg.setnTime(nTime);
						msg.setnLength(nLength);

						lists.add(msg);
						msg = null;
					}
					break;
				}
				type = parser.next();
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return lists;
	}

	public static List<SyncDevice> parseSyncConfXml(String content) {
		List<SyncDevice> devices = new ArrayList<SyncDevice>();
		SyncDevice device = null;
		XmlPullParserFactory factory;
		InputStream is = new ByteArrayInputStream(content.getBytes());
		try {
			factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(is, "UTF-8");
			int type = parser.getEventType();
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if ("video".equals(parser.getName())) {
						device = new SyncDevice();
						String DstDeviceID = parser.getAttributeValue(null,
								"DstDeviceID");
						String DstUserID = parser.getAttributeValue(null,
								"DstUserID");
						String IsSyncing = parser.getAttributeValue(null,
								"IsSyncing");
						device.setDstDeviceID(DstDeviceID);
						device.setDstUserID(DstUserID);
						device.setIsSyncing(Integer.parseInt(IsSyncing));
					}
					break;
				case XmlPullParser.END_TAG:
					if (device != null)
						devices.add(device);
					break;
				default:

					break;
				}
				type = parser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return devices;
	}

	public static ModifyConfDesc parseModifyConfDescXml(String content) {
		ModifyConfDesc desc = null;
		XmlPullParserFactory factory;
		InputStream is = new ByteArrayInputStream(content.getBytes());
		try {
			factory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(is, "UTF-8");
			int type = parser.getEventType();
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_DOCUMENT:
					desc = new ModifyConfDesc();
					break;
				case XmlPullParser.START_TAG:
					if ("conf".equals(parser.getName())) {
						String canaudio = parser.getAttributeValue(null,
								"canaudio");
						desc.setCanaudio(canaudio);
						String candataop = parser.getAttributeValue(null,
								"candataop");
						desc.setCandataop(candataop);
						String canvideo = parser.getAttributeValue(null,
								"canvideo");
						desc.setCanvideo(canvideo);
						String createuserid = parser.getAttributeValue(null,
								"createuserid");
						desc.setCreateuserid(createuserid);
						String createusernickname = parser.getAttributeValue(
								null, "createusernickname");
						desc.setCreateusernickname(createusernickname);
						String datauitype = parser.getAttributeValue(null,
								"datauitype");
						desc.setDatauitype(datauitype);
						String endtime = parser.getAttributeValue(null,
								"endtime");
						desc.setEndtime(endtime);
						String freeapply = parser.getAttributeValue(null,
								"freeapply");
						desc.setFreeapply(freeapply);
						String haskey = parser
								.getAttributeValue(null, "haskey");
						desc.setHaskey(haskey);
						String id = parser.getAttributeValue(null, "id");
						desc.setId(id);
						String isquickmode = parser.getAttributeValue(null,
								"isquickmode");
						desc.setIsquickmode(isquickmode);
						String layout = parser
								.getAttributeValue(null, "layout");
						desc.setLayout(layout);
						String lockchat = parser.getAttributeValue(null,
								"lockchat");
						desc.setLockchat(lockchat);
						String lockconf = parser.getAttributeValue(null,
								"lockconf");
						desc.setLockconf(lockconf);
						String lockfiletrans = parser.getAttributeValue(null,
								"lockfiletrans");
						desc.setLockfiletrans(lockfiletrans);
						String lockpollingvideo = parser.getAttributeValue(
								null, "lockpollingvideo");
						desc.setLockpollingvideo(lockpollingvideo);
						String lockrecord = parser.getAttributeValue(null,
								"lockrecord");
						desc.setLockrecord(lockrecord);
						String mode = parser.getAttributeValue(null, "mode");
						desc.setMode(mode);
						String pageid = parser
								.getAttributeValue(null, "pageid");
						desc.setPageid(pageid);
						String pollingvideo = parser.getAttributeValue(null,
								"pollingvideo");
						desc.setPollingvideo(pollingvideo);
						String quickvalue = parser.getAttributeValue(null,
								"quickvalue");
						desc.setQuickvalue(quickvalue);
						String rollcall = parser.getAttributeValue(null,
								"rollcall");
						desc.setRollcall(rollcall);
						String rollingmessage = parser.getAttributeValue(null,
								"rollingmessage");
						desc.setRollingmessage(rollingmessage);
						String starttime = parser.getAttributeValue(null,
								"starttime");
						desc.setStarttime(starttime);
						String subject = parser.getAttributeValue(null,
								"subject");
						desc.setSubject(subject);
						String syncdesktop = parser.getAttributeValue(null,
								"syncdesktop");
						desc.setSyncdesktop(syncdesktop);
						String syncdocument = parser.getAttributeValue(null,
								"syncdocument");
						desc.setSyncdocument(syncdocument);
						String syncvideo = parser.getAttributeValue(null,
								"syncvideo");
						desc.setSyncvideo(syncvideo);
						String userlayout = parser.getAttributeValue(null,
								"userlayout");
						desc.setUserlayout(userlayout);
						String userlistmode = parser.getAttributeValue(null,
								"userlistmode");
						desc.setUserlistmode(userlistmode);
						String videoh323 = parser.getAttributeValue(null,
								"videoh323");
						desc.setVideoh323(videoh323);
						String videomixer = parser.getAttributeValue(null,
								"videomixer");
						desc.setVideomixer(videomixer);
						String videomonitor = parser.getAttributeValue(null,
								"videomonitor");
						desc.setVideomonitor(videomonitor);
						String videosip = parser.getAttributeValue(null,
								"videosip");
						desc.setVideosip(videosip);
						String videotosipdevid = parser.getAttributeValue(null,
								"videotosipdevid");
						desc.setVideotosipdevid(videotosipdevid);
						String videotosipid = parser.getAttributeValue(null,
								"videotosipid");
						desc.setVideotosipid(videotosipid);
						String videowall = parser.getAttributeValue(null,
								"videowall");
						desc.setVideowall(videowall);
					}
					break;
				case XmlPullParser.END_TAG:
					if (desc != null)
						return desc;
					break;
				default:

					break;
				}
				type = parser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
}
