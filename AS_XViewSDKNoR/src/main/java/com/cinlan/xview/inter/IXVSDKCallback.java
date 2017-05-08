package com.cinlan.xview.inter;

import java.util.List;

/**
 * SDK接口
 * 
 */
public interface IXVSDKCallback {

	/**
	 * 1-登录超时<br>
	 * 2-账号或密码错误<br>
	 * 3-连接错误<br>
	 * 4-服务器拒绝<br>
	 * 5-登录成功<br>
	 * 6-服务器地址或端口为空<br>
	 * 7-网络无法连接<br>
	 * 8-DNS解析出错<br>
	 * 9-加载so库错误<br>
	 * 10-格式错误<br>
	 * 11-未知错误<br>
	 * 
	 * @param flag
	 */
	void onLoginResultListener(int flag);

	/**
	 *  1-主动注销成功
	 *
	 * @param flag
	 */
	void onLogoutResultListener(int flag);

	/**
	 * 1-无此会议id<br>
	 * 2-进会成功<br>
	 * 3-进会失败<br>
	 * 4-会议列表为空<br>
	 * 
	 * @param flag
	 */
	void onEnterConfListener(int flag);

	/**
	 * 1-被踢出会议<br>
	 * 2-被挤出会议<br>
	 * 3-退出会议<br>
	 * 
	 * @param flag
	 */
	void onConfMsgListener(int flag);

	/**
	 * 有成员进入会议
	 * 
	 * @param users
	 *            用户id集合
	 */
	void onMemberEnterListener(long userId, String nickName, String userData);

	/**
	 * 有成员退出会议
	 * 
	 * @param userId
	 *            用户id
	 * @param nickName
	 *            昵称
	 */
	void onMemberExitListener(long userId, String nickName, String userData);

	/**
	 * 请求参会者列表
	 * 
	 * @param users
	 *            用户id集合
	 */
	void onGetUserListListener(List<String> users);

	/**
	 * 创建会议接口回调<br>
	 * 
	 * 成功返回会议id,失败返回0.
	 * 
	 * @param result
	 *            返回结果
	 */
	void onCreateConfCallback(String result);

	/**
	 * 销毁会议接口回调<br>
	 * 
	 * 成功返回1,失败返回0.
	 * 
	 * @param result
	 *            返回结果
	 */
	void onDestroyConfCallback(int result);
}
