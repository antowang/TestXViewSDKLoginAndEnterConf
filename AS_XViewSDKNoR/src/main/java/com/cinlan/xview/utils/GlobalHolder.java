package com.cinlan.xview.utils;

import android.content.Context;
import android.view.WindowManager;

import com.cinlan.xview.bean.ClientDev;
import com.cinlan.xview.bean.Conf;
import com.cinlan.xview.bean.ConfMessage;
import com.cinlan.xview.bean.PersonalInfo;
import com.cinlan.xview.bean.User;
import com.cinlan.xview.bean.UserDevice;
import com.cinlan.xview.bean.VideoDevice;
import com.cinlan.xview.bean.data.DocShare;
import com.cinlan.xview.bean.data.Label;
import com.cinlan.xview.bean.data.Page;
import com.cinlan.xview.msg.ClientEnterMsg;
import com.cinlan.xview.msg.EventMsgType;
import com.cinlan.xview.msg.MediaEntity;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class GlobalHolder {

    public static Context GlobalContext;
    private static GlobalHolder holder = null;
    private Conf mCurrentConf = null;
    private User mLocalUser = null;
    private List<Conf> mConfs = new ArrayList<Conf>();
    private List<ConfMessage> mMsgs = new ArrayList<ConfMessage>();
    public Set<User> mUers = new HashSet<User>();
    public static List<String> listXml = new ArrayList<String>();
    public static long CurrentConfId = 0;
    public static long EnterMemberUserId = 0;
    public static String EnterMemberUserNickName = "";
    public static String EnterMemberUserData = "";
    /**
     * 包含已经打开的UserDevice的集合
     */
    public static List<UserDevice> mOpenUerDevList = new ArrayList<UserDevice>();


    public static List<ClientDev> mClientDevList = new ArrayList<>();

    public static List<User> mUserList = new ArrayList<>();


    /**
     * 已经打开的设备集合列表
     */
    public static List<Long> openDevIdList = new ArrayList<>();


    public List<MediaEntity> mOpenMedia = new ArrayList<MediaEntity>();
    public List<MediaEntity> list = new ArrayList<MediaEntity>();
    public List<UserDevice> mOpenedSyncVideos = new ArrayList<UserDevice>();
    public List<MediaEntity> mOpenedSyncMedias = new ArrayList<MediaEntity>();
    public Map<Long, PersonalInfo> infos = new HashMap<Long, PersonalInfo>();
    public Set<UserDevice> userdevices = new HashSet<UserDevice>();
    /**
     * userid和对应的设备集合
     */
    public static Map<Long, List<VideoDevice>> videodevices = new HashMap<Long, List<VideoDevice>>();
    public static Map<Long, List<VideoDevice>> closeVideoDevices = new HashMap<Long, List<VideoDevice>>();


    public static List<VideoDevice> mCaptureList = new ArrayList<>();


    public static List<User> allUsers = new ArrayList<User>();
    public Map<Long, Integer> mSpeakUers = new HashMap<Long, Integer>();
    public List<DocShare> mDocShares = new ArrayList<DocShare>();
    public Map<String, Integer> pages = new HashMap<String, Integer>();
    private Map<Long, String> users = new HashMap<Long, String>();
    public int height;
    public int width;
    public static UserDevice currendUserDevice;

    public List<UserDevice> getUserDevice() {
        List<UserDevice> newDevice = new ArrayList<>(userdevices);
        Collections.sort(newDevice);
        return newDevice;
    }

    public synchronized List<DocShare> getmDocShares() {
        return mDocShares;
    }

    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        return height;
    }

    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int width = wm.getDefaultDisplay().getWidth();
        return width;
    }

    public synchronized void setmDocShares(List<DocShare> mDocShares) {
        this.mDocShares = mDocShares;
    }

    /**
     * find doc
     *
     * @param wbid
     * @return
     */
    public synchronized DocShare findDocShare(String wbid) {
        if (wbid == null)
            return null;
        DocShare temp = null;
        for (DocShare doc : mDocShares) {
            if (wbid.equals(doc.getWBoardID())) {
                temp = doc;
                break;
            }
        }
        return temp;
    }

    /**
     * update page numbers
     *
     * @param szwbid
     * @param count
     */
    public synchronized void updatePage(String szwbid, int count) {
        DocShare findDocShare = findDocShare(szwbid);
        if (findDocShare != null)
            findDocShare.setPagenums(count);
    }

    /**
     * add page to doc
     *
     * @param szwbid
     * @param page
     */
    public synchronized void addPage(String szwbid, Page page) {
        DocShare findDocShare = findDocShare(szwbid);
        if (findDocShare != null) {
            findDocShare.getPages().add(page);
        }
    }

    /**
     * del a doc by wbid
     *
     * @param wbid
     */
    public synchronized void removeDoc(String wbid) {
        DocShare findDocShare = findDocShare(wbid);
        if (findDocShare != null) {
            mDocShares.remove(findDocShare);
        }
    }

    public static synchronized boolean removeUserAvatar(User user) {
        if (userAvatar.containsKey(user)) {
            userAvatar.remove(user);
            return true;
        }
        return false;
    }

    public static synchronized int getAvatarAccordingUser(long userid) {
        for (int i = 0; i < allUsers.size(); i++) {
            if (allUsers.get(i).getmUserId() == userid) {
                for (int j = 0; j < userAvatar.size(); j++) {
                    if (userAvatar.containsKey(allUsers.get(i))) {
                        int avatarId = userAvatar.get(allUsers.get(i));
                        return avatarId;
                    }
                }
            }
        }
        return -1;
    }

    public synchronized List<User> getmUers() {
        ArrayList<User> users = new ArrayList<User>();
        users.addAll(mUers);
        return users;
    }

    public synchronized void saveIdNameUser(long userId, String userName) {
        Map<Long, String> users = new HashMap<Long, String>();
        users.put(userId, userName);
        this.users.putAll(users);
    }

    public synchronized Map<Long, String> getIdNameUser() {
        if (!this.users.isEmpty()) {
            return this.users;
        }
        return null;
    }

    public synchronized void addPersonalInfo(long userid, PersonalInfo info) {
        if (!infos.containsKey(userid))
            infos.put(userid, info);

    }

    public synchronized void updatePersonalInfo(long userid, PersonalInfo info) {
        if (infos.containsKey(userid)) {
            infos.remove(userid);
        }
        infos.put(userid, info);
    }

    public synchronized void removePersonalInfo(long userid) {
        if (infos.size() > 0 && infos.containsKey(userid)) {
            infos.remove(userid);
        }
    }

    public synchronized void clearPersonalInfo() {
        infos.clear();
    }

    public String getNameFromPersonalInfo(long userid) {
        if (!infos.containsKey(userid))
            return null;
        PersonalInfo info = infos.get(userid);
        if (!info.getEmail().isEmpty())
            return info.getEmail();
        return null;
    }

    public synchronized ArrayList<UserDevice> getOpenUers() {
        ArrayList<UserDevice> users = new ArrayList<UserDevice>();
        users.addAll(mOpenUerDevList);
        return users;
    }

    public Conf getmCurrentConf() {
        return mCurrentConf;
    }

    public void setmCurrentConf(Conf mCurrentConf) {
        this.mCurrentConf = mCurrentConf;
    }

    public List<Conf> getConfs() {
        return mConfs;
    }

    public void setConfs(List<Conf> mConfss) {
        this.mConfs = mConfss;
    }

    public List<ConfMessage> getConfMessages() {
        return mMsgs;
    }

    public void addConfMessages(List<ConfMessage> mMsgs) {
        this.mMsgs.addAll(mMsgs);
    }

    public void addConfMessage(ConfMessage msg) {
        this.mMsgs.add(msg);
    }

    public static synchronized GlobalHolder getInstance() {
        if (holder == null) {
            holder = new GlobalHolder();
        }
        return holder;
    }

    public synchronized User getCurrentUser() {
        return mLocalUser;
    }

    public synchronized long getLocalUserId() {
        if (mLocalUser == null) {
            return 0;
        } else {
            return mLocalUser.getmUserId();
        }
    }

    /**
     * 设置本地User
     *
     * @param mCurrentUser
     */
    public synchronized void setLocalUser(User mCurrentUser) {
        this.mLocalUser = mCurrentUser;
    }

    public int getUserCount() {
        return mUers.size();
    }


    @Deprecated
    public synchronized void addSelf() {
        if (mUers.contains(mLocalUser))
            return;
        mUers.add(mLocalUser);
        saveIdNameUser(mLocalUser.getmUserId(), mLocalUser.getNickName());
        currendUserDevice = new UserDevice();
        VideoDevice device = new VideoDevice();
        currendUserDevice.setUser(mLocalUser);
        currendUserDevice.setDevice(device);
        device.setId(mLocalUser.getmUserId() + ":Camera");
        userdevices.add(currendUserDevice);
        allUsers.add(mLocalUser);
        ArrayList<VideoDevice> videoDevices = new ArrayList<>();
        videoDevices.add(device);
        videodevices.put(mLocalUser.getmUserId(), videoDevices);
    }

    public synchronized void removeDevice(Long userid) {
        videodevices.remove(userid);
        for (int i = 0; i < userdevices.size(); i++) {
            UserDevice device = getUserDevice().get(i);
            if (device.getUser() != null
                    && device.getUser().getmUserId() == userid) {
                userdevices.remove(device);
                i--;
            }
        }
    }

    public synchronized void removeOpendDevice(long userid, String deviceid) {
        if ("".equals(deviceid) || null == deviceid)
            return;
        Iterator<UserDevice> sListIterator = mOpenUerDevList.iterator();
        while (sListIterator.hasNext()) {
            UserDevice device = sListIterator.next();
            if (device.getDevice() != null && device.getUser() != null
                    && device.getUser().getmUserId() == userid
                    && deviceid.equals(device.getDevice().getId())) {
                sListIterator.remove();
            }
        }
    }


    /**
     * 添加user
     * author:sivin
     *
     * @param user
     */
    public synchronized void addUser(User user) {
        if (user == null) return;
        if (findUser2(user.getmUserId()) != null) return;
        mUserList.add(user);
    }


    /**
     * author :sivin
     * 移除用户
     *
     * @param userId
     */
    public void removeUser(Long userId) {
        for (int i = 0; i < mUserList.size(); i++) {
            if (mUserList.get(i).getmUserId() == userId) {
                User u = mUserList.remove(i);
                u = null;
            }
        }
    }

    public void deleteClient(Long userId){
        for(int i = 0 ; i < mClientDevList.size() ; i ++){
            if(mClientDevList.get(i).getUser().getmUserId() == userId){
                mClientDevList.remove(i);
            }
        }
        removeUser(userId);
    }


    /**
     * author:sivin
     */
    public void initSelf() {
        if (findUser2(mLocalUser.getmUserId()) != null) return;
        mUserList.add(mLocalUser);
        ClientDev dev = new ClientDev();
        dev.setUser(mLocalUser);
        List<VideoDevice> vdList = new ArrayList<>();
        VideoDevice vd = new VideoDevice();
        vd.setId(mLocalUser.getmUserId() + ":Camera");
        vdList.add(vd);
        dev.setVideoDevLists(vdList);
        mClientDevList.add(0, dev);
    }


    /**
     * author:sivin
     *
     * @param userId userId
     * @return user
     */
    public User findUser2(long userId) {
        for (User u : mUserList) {
            if (u.getmUserId() == userId) {
                return u;
            }
        }
        return null;
    }


    /**
     * author: sivin
     *
     * @param userId userId
     * @return ClientDev
     */
    public ClientDev findClientDev(Long userId) {

        for (ClientDev dev : mClientDevList) {
            if (dev.getUser().getmUserId() == userId) {
                return dev;
            }
        }
        return null;
    }


    /**
     * author :sivin
     *
     * @param map map
     */
    public synchronized void addVideoDev(Map<Long, List<VideoDevice>> map) {
        Set<Long> userIdList = map.keySet();
        for (Long userId : userIdList) {
            ClientDev clientDev = findClientDev(userId);
            if (clientDev != null)
                continue;

            if (clientDev == null) {
                User user = findUser2(userId);
                if (user == null) {
                    XviewLog.e("xview", "该设备没有找到User" + userId);
                    continue;
                }
                clientDev = new ClientDev();
                clientDev.setUser(user);
                mClientDevList.add(clientDev);
            }

            List<VideoDevice> vdListFromSever = map.get(userId);

            if (vdListFromSever == null || vdListFromSever.size() < 0) {
                XviewLog.e("xview", "userId = " + userId + " 设备集 == null || 设备集.size < 0");
                continue;
            }

            List<VideoDevice> localVdList = clientDev.getVideoDevLists();
            localVdList.clear();
            localVdList.addAll(vdListFromSever);


            /**
             * 发送消息到{@link com.cinlan.xview.ui.p2p.view.CommunicateFragment#onNewUserEnter}
             */
            EventBus.getDefault().post(new ClientEnterMsg(EventMsgType.ON_NEW_USER_ENTER, userId, clientDev));
        }
    }


    /**
     * author : sivin
     * 获取可用的Client数量
     *
     * @return
     */
    public static int getCanOpenClientNum() {
        int num = 0;
        for (ClientDev dev : mClientDevList) {
            if (dev.isCanOpen()) {
                num++;
            }
        }
        return num;
    }


    /**
     * author :sivin
     */
    public void release() {
        mUserList.clear();
        mClientDevList.clear();
    }


    public synchronized void addAttended(User user) {
        if (user == null)
            return;
        if (findUser(user.getmUserId()) == null) {
            mUers.add(user);
            saveIdNameUser(user.getmUserId(), user.getNickName());
        }
        UserDevice u = new UserDevice();
        u.setUser(user);
        addUserDevice(u);
    }

    private void addUserDevice(UserDevice u) {
        if (u == null)
            return;
        synchronized (userdevices) {
            boolean flag = false;
            for (UserDevice ud : userdevices) {
                if (ud == u) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                userdevices.add(u);
            }
        }
    }

    /**
     * Add UserDevice.
     *
     * @param maps
     */
    public synchronized void addDevice(Map<Long, List<VideoDevice>> maps) {

        /**
         * Set UserDevice is Disable.
         */
        if (maps != null && maps.size() > 0) {
            for (Entry<Long, List<VideoDevice>> temp : maps.entrySet()) {
                Long key = temp.getKey();
                List<VideoDevice> value = temp.getValue();
                if (value == null || value.size() == 0)
                    continue;
                UserDevice u = findUserDevice(key, value.get(0).getId());
                if (u != null) {
                    u.getDevice().setDisable(value.get(0).getDisable());
                    continue;
                }
            }
        }

        /**
         * Add or remove VideoDevice.
         */
        for (Map.Entry<Long, List<VideoDevice>> temp : maps.entrySet()) {
            Long userid = temp.getKey();
            List<VideoDevice> videosFromServer = temp.getValue();
            /**
             * Get UserDevice.
             */
            UserDevice u = findUserDevice(userid);

            if (u != null && videosFromServer != null
                    && videosFromServer.size() > 0) {
                if (videosFromServer.size() != 0) {
                    /**
                     * The original logic is to determine whether the
                     * videosFromServer.size () is 1.If it is 1, it is directly
                     * u.setDevice (videosFromServer.get (0)). Logic has bug,
                     * changed to no matter whether or not to perform the
                     * following logic for 1.
                     */
                    u.setDevice(videosFromServer.get(0));
                    /**
                     * Gets the List of VideoDevice corresponding to userid.
                     */
                    List<VideoDevice> localVideoDeviceList = videodevices
                            .get(userid);
                    /**
                     * If the local storage device list is not empty.
                     */
                    if (localVideoDeviceList != null) {
                        /**
                         * If the number of devices that are passed by the
                         * server is larger than the number of devices that are
                         * stored locally, the device is added.
                         */
                        if (videosFromServer.size() > localVideoDeviceList
                                .size()) {
                            UserDevice userDevice = new UserDevice();
                            VideoDevice vd = videosFromServer
                                    .get(videosFromServer.size() - 1);
                            userDevice.setDevice(vd);
                            userDevice.setUser(u.getUser());
                            addUserDevice(userDevice);
                        } else if (videosFromServer.size() < localVideoDeviceList
                                .size()) {
                            /**
                             * If the number of devices that are sent by the
                             * server is smaller than the number of devices that
                             * are stored locally, the device is removed.
                             */
                            removeVideo(
                                    userid,
                                    localVideoDeviceList
                                            .get(localVideoDeviceList.size() - 1));
                        }
                    } else {
                        /**
                         * If the list of local storage devices is empty, add
                         * the device list from the server to the server.
                         */
                        for (int i = 1; i < videosFromServer.size(); i++) {
                            VideoDevice device = videosFromServer.get(i);
                            UserDevice userdevice = new UserDevice();
                            userdevice.setDevice(device);
                            userdevice.setUser(u.getUser());
                            addUserDevice(userdevice);
                        }
                    }
                }
            }
            /**
             * Add VideoDevice.
             */
            videodevices.put(userid, temp.getValue());


        }

    }

    private void removeVideo(Long userid, VideoDevice videoDevice) {

        for (int i = 0; i < userdevices.size(); i++) {
            UserDevice device = getUserDevice().get(i);
            if (device.getUser() != null
                    && device.getUser().getmUserId() == userid
                    || device.equals(videoDevice)) {
                userdevices.remove(device);
//                ConfActivity.mContext.closeDevice(device);
                return;
            }
        }
    }

    private synchronized UserDevice findUserDevice(Long userid, String deviceid) {
        UserDevice device = null;
        for (int i = 0; i < userdevices.size(); i++) {
            UserDevice userDevice = getUserDevice().get(i);
            User user = userDevice.getUser();
            VideoDevice videoDevice = userDevice.getDevice();
            if (user != null && videoDevice != null) {
                if (user.getmUserId() == userid
                        && deviceid.equals(videoDevice.getId())) {
                    device = userDevice;
                    break;
                }
            }
        }
        return device;
    }

    public synchronized void addShareLable(Label label, int pageID,
                                           String boardID) {
        if (label == null)
            return;
        DocShare docshares = null;
        for (DocShare doc : mDocShares) {
            if (doc.getWBoardID().equals(boardID)) {
                docshares = doc;
            }
        }
        if (docshares == null) {
            return;
        }
        Map<Integer, List<Label>> data = docshares.getData();
        if (data.get(pageID) == null) {
            List<Label> labels = new ArrayList<Label>();
            data.put(pageID, labels);
        }
        String labelid = label.getPageid();
        boolean islabel = false;
        Label label_need = null;
        for (Label lab : data.get(pageID)) {
            if (labelid.equals(lab.getPageid())) {
                islabel = true;
                label_need = lab;
            }
        }
        if (islabel) {
            label_need.getPoints().addAll(label.getPoints());
        } else {
            data.get(pageID).add(label);
        }
    }

    public synchronized void removeShareData(String remove_wbid,
                                             int remove_PageID, String dataid) {
        if (dataid == null || "".equals(dataid))
            return;
        DocShare findDocShare = findDocShare(remove_wbid);
        if (findDocShare != null) {
            Map<Integer, List<Label>> data = findDocShare.getData();
            List<Label> list = data.get(remove_PageID);
            Label temp = null;
            for (int i = 0; list != null && i < list.size(); i++) {
                Label label = list.get(i);
                if (dataid.equals(label.getPageid())) {
                    temp = label;
                    break;
                }
            }
            list.remove(temp);
        }
    }

    public synchronized User findUser(long userid) {
        User temp = null;
        ArrayList<User> allusers = new ArrayList<User>(mUers);
        for (User u : allusers) {
            if (u.getmUserId() == userid) {
                temp = u;
                break;
            }
        }
        return temp;
    }

    /**
     * @return 杩斿洖鐨勬槸鎵�湁鐨勭敤鎴风殑瑙嗛璁惧
     */
    public synchronized List<VideoDevice> getDevices() {
        List<VideoDevice> listValue = new ArrayList<VideoDevice>();
        Iterator<Long> iterator = videodevices.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            List<VideoDevice> list = videodevices.get(key);
            if (list != null)
                listValue.addAll(list);
        }
        return listValue;
    }

    public void setMetrics(int widthPixels, int heightPixels) {
        this.width = widthPixels;
        this.height = heightPixels;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public synchronized UserDevice findUserDevice(long userid) {
        UserDevice device = null;
        for (int i = userdevices.size() - 1; i >= 0; i--) {
            UserDevice userDevice = getUserDevice().get(i);
            if (userDevice.getUser().getmUserId() == userid) {
                device = userDevice;
                break;
            }
        }
        return device;
    }

    public static synchronized List<UserDevice> findHasOpenUserDevice(
            long userid) {
        List<UserDevice> users = new ArrayList<UserDevice>();
        for (int i = 0; i < mOpenUerDevList.size(); i++) {
            UserDevice userDevice = mOpenUerDevList.get(i);
            if (userDevice.getUser().getmUserId() == userid) {
                users.add(userDevice);
            }
        }
        return users;
    }

    MediaEntity entity;
    private static Map<User, Integer> userAvatar;


    /**
     * 添加一个MediaDevice,如果没有就添加,有就不添加
     *
     * @param mediaId
     * @param name
     * @param monitorIndex
     * @param mixerType
     * @param ownerID
     */
    public synchronized void addMediaDevice(String mediaId, String name,
                                            int monitorIndex, int mixerType, long ownerID) {
        entity = new MediaEntity();

        if (list.size() != 0) {

            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getMediaId().equals(mediaId)) {
                    return;
                }
            }
            entity.setMediaId(mediaId);
            entity.setName(name);
            entity.setMonitorIndex(monitorIndex);
            entity.setMixerType(mixerType);
            entity.setOwnerID(ownerID);
            list.add(entity);
        } else {
            entity.setMediaId(mediaId);
            entity.setName(name);
            entity.setMonitorIndex(monitorIndex);
            entity.setMixerType(mixerType);
            entity.setOwnerID(ownerID);
            list.add(entity);
        }

    }

    public synchronized List<MediaEntity> getMediaEntityList() {
        return list;
    }

    public synchronized void removeMediaDevice(String mediaID) {
        if (mediaID != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getMediaId().equals(mediaID)) {
                    list.remove(i);
                }
            }
        }
    }
}
