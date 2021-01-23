package com.liqun.lib_audio.mediaplayer.db;

import android.database.sqlite.SQLiteDatabase;

import com.liqun.lib_audio.mediaplayer.app.AudioHelper;
import com.liqun.lib_audio.mediaplayer.model.AudioBean;
import com.liqun.lib_audio.mediaplayer.model.Favourite;

/**
 * 操作greenDao数据库帮助类
 */
public class GreenDaoHelper {

    private static final String DB_BAME = "music_db";
    // 数据库帮助类, 用来创建和升级数据库
    private static DaoMaster.DevOpenHelper mHelper;
    // 最终创建好的数据库
    private static SQLiteDatabase mDb;
    // 管理数据库
    private static DaoMaster mDaoMaster;
    // 管理各种实体Dao,不让业务层拿到session直接去操作数据库，统一由此类提供方法
    private static DaoSession mDaoSession;

    public static void initDatabase(){
        mHelper = new DaoMaster.DevOpenHelper(AudioHelper.getContext(), DB_BAME, null);
        mDb = mHelper.getWritableDatabase();
        mDaoMaster = new DaoMaster(mDb);
        mDaoSession = mDaoMaster.newSession();
    }

    /**
     * 添加收藏
     * @param audioBean
     */
    public static void addFavourite(AudioBean audioBean){
        FavouriteDao dao = mDaoSession.getFavouriteDao();
        Favourite favourite = new Favourite();
        favourite.setAudioId(audioBean.id);
        favourite.setAudioBean(audioBean);
        dao.insertOrReplace(favourite);
    }

    /**
     * 移除收藏
     * @param audioBean
     */
    public static void removeFavourite(AudioBean audioBean){
        FavouriteDao dao = mDaoSession.getFavouriteDao();
        Favourite favourite = selectFavourite(audioBean);
        dao.delete(favourite);
    }

    /**
     * 查询收藏
     * @param audioBean
     * @return
     */
    private static Favourite selectFavourite(AudioBean audioBean) {
        FavouriteDao dao = mDaoSession.getFavouriteDao();
        Favourite favourite = dao.queryBuilder().where(
                FavouriteDao.Properties.AudioId.eq(audioBean.id)).unique();
        return favourite;
    }
}
