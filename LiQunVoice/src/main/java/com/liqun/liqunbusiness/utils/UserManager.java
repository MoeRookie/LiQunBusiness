package com.liqun.liqunbusiness.utils;

import com.liqun.liqunbusiness.model.user.User;

/**
 * 单例管理登录用户信息
 */
public class UserManager {
    private static UserManager sUserManager = null;
    private User mUser = null;
    public static UserManager getInstance(){
        if (sUserManager == null) {
            synchronized (UserManager.class) {
                if (sUserManager == null) {
                    sUserManager = new UserManager();
                }
            }
        }
        return sUserManager;
    }
    /**
     * 保存用户信息到内存
     */
    public void saveUser(User user){
        mUser = user;
        saveLocal(user);
    }

    /**
     * 持久化用户信息
     * @param user
     */
    private void saveLocal(User user) {

    }

    /**
     * 获取用户信息
     * @return
     */
    public User getUser(){
        return mUser;
    }

    /**
     * 从本地获取
     * @return
     */
    public User getLocal(){
        return null;
    }

    /**
     * 判断是否登录过
     * @return
     */
    public boolean hasLogined(){
        return mUser != null;
    }

    /**
     * 移除用户信息
     */
    public void removeUser(){
        mUser = null;
        removeLocal();
    }

    /**
     * 从库中删除用户信息
     */
    private void removeLocal() {

    }
}
