package com.gms_worldwide.hybersdk;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.DeleteBuilder;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.UpdateBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Database helper class used to manage the creation and upgrading of your database. This class also usually provides
 * the DAOs used by the other classes.
 */

/**
 * Created by Andrew Kochura.
 */
class HyberDatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = "com.gms_worldwide.hybersdk.HyberDatabaseHelper";

    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "HyberSDKStorage.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 1;

    // the DAO object we use to access the HyberMessageDBModel table
    private Dao<HyberMessageDBModel, Integer> messageDao = null;
    private RuntimeExceptionDao<HyberMessageDBModel, Integer> messageRuntimeDao = null;
    // the DAO object we use to access the HyberMessageUpdateDBModel table
    private Dao<HyberMessageUpdateDBModel, Integer> messageUpdateDao = null;
    private RuntimeExceptionDao<HyberMessageUpdateDBModel, Integer> messageUpdateRuntimeDao = null;
    // the DAO object we use to access the CurrentUserDBModel table
    private Dao<HyberCurrentUserDBModel, Integer> currentUserDao = null;
    private RuntimeExceptionDao<HyberCurrentUserDBModel, Integer> currentUserRuntimeDao = null;

    // the DAO object we use to access the HyberAlphaNameDBModel table
    private Dao<HyberAlphaNameDBModel, Integer> alphasDao = null;
    private RuntimeExceptionDao<HyberAlphaNameDBModel, Integer> alphasRuntimeDao = null;

    /**
     * Instantiates a Hyber database helper.
     *
     * @param context the context
     */
    public HyberDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, HyberMessageDBModel.class);
            TableUtils.createTable(connectionSource, HyberMessageUpdateDBModel.class);
            TableUtils.createTable(connectionSource, HyberCurrentUserDBModel.class);
            TableUtils.createTable(connectionSource, HyberAlphaNameDBModel.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Save incoming message.
     *
     * @param from    the from
     * @param message the message
     * @param time    the time
     * @param type    the type
     * @param owner   the owner
     */
    public int saveIncomingMessage(String from, String message, long time, int type, String owner) throws SQLException {
        HyberMessageDBModel messageDBModel =
                new HyberMessageDBModel(from, message, time, type, owner);
        getMessageDao().create(messageDBModel);
        //int id = getMessageDao().queryBuilder().where().eq("time", time).queryForFirst().getId();
        return messageDBModel.getId();//id > 0 ? id : -1;
    }

    /**
     * Update messages of day list.
     *
     * @param messageModels the message models
     * @return the list
     */
    public List<HyberMessageModel> updateMessagesOfDay(
            List<HyberMessageModel> messageModels) {
        List<HyberMessageModel> addingMessageModels = new ArrayList<>();
        try {
            for (HyberMessageModel messageModel : messageModels) {
                HyberMessageDBModel messageDBModel =
                        getMessageDao().queryBuilder()
                                .where().eq("time", messageModel.getTime())
                                .and().eq("type", messageModel.getType())
                                .and().eq("owner", messageModel.getOwner())
                                .queryForFirst();
                if (messageDBModel == null) {
                    addingMessageModels.add(messageModel);
                    HyberMessageDBModel model = new HyberMessageDBModel(messageModel.getFrom(),
                            messageModel.getMessage(), messageModel.getTime(),
                            messageModel.getType(), messageModel.getOwner());
                    getMessageDao().create(model);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return addingMessageModels;
    }

    /**
     * Add message update info.
     *
     * @param time        the time
     * @param type        the type
     * @param is_fool_day the is fool day
     */
    public void addMessageUpdateInfo(long time, int type, boolean is_fool_day) {
        try {
            HyberMessageUpdateDBModel messageUpdateDBModel =
                    getMessageUpdateDao().queryBuilder().where().eq("time", time).queryForFirst();
            if (messageUpdateDBModel == null) {
                messageUpdateDBModel =
                        new HyberMessageUpdateDBModel(time, type, is_fool_day);
                getMessageUpdateDao().create(messageUpdateDBModel);
            } else {
                messageUpdateDBModel.setTime(time);
                messageUpdateDBModel.setType(type);
                messageUpdateDBModel.setIs_fool_day(is_fool_day);
                getMessageUpdateDao().update(messageUpdateDBModel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Is message update for fool day boolean.
     *
     * @param time the time
     * @param type the type
     * @return the boolean
     */
    public boolean isMessageUpdateForFoolDay(long time, int type) {
        HyberMessageUpdateDBModel messageUpdateDBModel = null;
        try {
            messageUpdateDBModel = getMessageUpdateDao().queryBuilder()
                    .where().eq("time", time)
                    .and().eq("type", type)
                    .queryForFirst();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return messageUpdateDBModel != null && messageUpdateDBModel.is_fool_day();
    }

    /**
     * Update alphas.
     *
     * @param alphaNames the alpha names
     */
    public void updateAlphas(List<String> alphaNames) {
        try {
            TableUtils.clearTable(connectionSource, HyberAlphaNameDBModel.class);
            List<HyberAlphaNameDBModel> dbModels = new ArrayList<>();
            for (String name : alphaNames) {
                dbModels.add(new HyberAlphaNameDBModel(name));
            }
            if (dbModels.size() > 0) {
                getAlphasDao().create(dbModels);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Is alpha name valid boolean.
     *
     * @param name the name
     * @return the boolean
     */
    public boolean isAlphaNameValid(String name) {
        List<HyberAlphaNameDBModel> dbModels =
                null;
        try {
            dbModels = getAlphasDao().queryBuilder().where().eq("name", name).query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dbModels != null && dbModels.size() > 0;
    }

    /**
     * Clean user data.
     */
    public void cleanUserData() {
        try {
            TableUtils.clearTable(connectionSource, HyberMessageDBModel.class);
            TableUtils.clearTable(connectionSource, HyberMessageUpdateDBModel.class);
            TableUtils.clearTable(connectionSource, HyberCurrentUserDBModel.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, HyberMessageDBModel.class, true);
            TableUtils.dropTable(connectionSource, HyberMessageUpdateDBModel.class, true);
            TableUtils.dropTable(connectionSource, HyberCurrentUserDBModel.class, true);
            TableUtils.dropTable(connectionSource, HyberAlphaNameDBModel.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets messages.
     *
     * @param year  the year
     * @param month the month
     * @param day   the day
     * @param types the types
     * @return the messages
     */
    public List<HyberMessageModel> getMessages(int year, int month, int day, List<Integer> types) {
        List<HyberMessageModel> hyberMessageModels = new ArrayList<>();
        try {
            QueryBuilder<HyberMessageDBModel, Integer> queryBuilder = getMessageDao().queryBuilder();
            queryBuilder.orderBy("time", false);
            queryBuilder.where().between("time",
                    HyberTools.getStartOfDayUtcTime(year, month, day),
                    HyberTools.getEndOfDayUtcTime(year, month, day))
                    .and().in("type", types)
                    .and().eq("deleted", false);

            List<HyberMessageDBModel> messageDBModels = queryBuilder.query();
            for (HyberMessageDBModel dbModel : messageDBModels) {
                hyberMessageModels.add(new HyberMessageModel(dbModel));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hyberMessageModels;
    }

    /**
     * Gets messages.
     *
     * @param dateFrom the time un UTC format
     * @param dateTo   the time un UTC format
     * @param types    the types
     * @return the messages
     */
    public List<HyberMessageModel> getMessages(long dateFrom, long dateTo, List<Integer> types) {
        List<HyberMessageModel> hyberMessageModels = new ArrayList<HyberMessageModel>();
        try {
            QueryBuilder<HyberMessageDBModel, Integer> queryBuilder = getMessageDao().queryBuilder();
            queryBuilder.orderBy("time", false);
            queryBuilder.where()
                    .eq("deleted", false)
                    .and().between("time", dateFrom, dateTo)
                    .and().in("type", types);

            List<HyberMessageDBModel> messageDBModels = queryBuilder.query();
            if (messageDBModels.size() > 0)
                for (HyberMessageDBModel dbModel : messageDBModels) {
                    hyberMessageModels.add(new HyberMessageModel(dbModel));
                }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hyberMessageModels;
    }

    /**
     * Gets all messages.
     *
     * @param types the types
     * @return the messages
     */
    public List<HyberMessageModel> getAllMessages(List<Integer> types) {
        List<HyberMessageModel> hyberMessageModels = new ArrayList<>();
        try {
            QueryBuilder<HyberMessageDBModel, Integer> queryBuilder = getMessageDao().queryBuilder();
            queryBuilder.orderBy("time", false);
            queryBuilder.where().in("type", types)
                    .and().eq("deleted", false);

            List<HyberMessageDBModel> messageDBModels = queryBuilder.query();
            for (HyberMessageDBModel dbModel : messageDBModels) {
                hyberMessageModels.add(new HyberMessageModel(dbModel));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hyberMessageModels;
    }

    public List<HyberMessageModel> getUnReadMessages() {
        List<HyberMessageModel> hyberMessageModels = new ArrayList<>();
        try {
            QueryBuilder<HyberMessageDBModel, Integer> queryBuilder = getMessageDao().queryBuilder();
            queryBuilder.orderBy("time", false);
            queryBuilder.where().eq("isRead", false);

            List<HyberMessageDBModel> messageDBModels = queryBuilder.query();
            for (HyberMessageDBModel dbModel : messageDBModels) {
                hyberMessageModels.add(new HyberMessageModel(dbModel));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hyberMessageModels;
    }

    /**
     * Delete message.
     *
     * @param id the id
     */
    public void deleteMessage(int id) {
        try {
            HyberMessageDBModel messageDBModel = getMessageDao().queryForId(id);
            if (messageDBModel != null) {
                messageDBModel.setDeleted(true);
            }
            getMessageDao().update(messageDBModel);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unDeleteMessage(int id) {
        try {
            HyberMessageDBModel messageDBModel = getMessageDao().queryForId(id);
            if (messageDBModel != null) {
                messageDBModel.setDeleted(false);
            }
            getMessageDao().update(messageDBModel);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set message is read.
     *
     * @param id the id
     */
    public void setReadMessage(int id) {
        try {
            HyberMessageDBModel messageDBModel = getMessageDao().queryForId(id);
            if (messageDBModel != null) {
                messageDBModel.setRead(true);
            }
            getMessageDao().update(messageDBModel);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set all messages is read.
     */
    public void setReadForAllMessages() {
        try {
            UpdateBuilder<HyberMessageDBModel, Integer> updateBuilder =
                    getMessageDao().updateBuilder();
            updateBuilder.updateColumnValue("isRead", true);
            updateBuilder.where().eq("isRead", false);
            updateBuilder.update();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set message is read.
     *
     * @param id the id
     */
    public void setUnReadMessage(int id) {
        try {
            HyberMessageDBModel messageDBModel = getMessageDao().queryForId(id);
            if (messageDBModel != null) {
                messageDBModel.setRead(false);
            }
            getMessageDao().update(messageDBModel);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clear deleted messages.
     */
    public void clearDeletedMessages() {
        try {
            DeleteBuilder<HyberMessageDBModel, Integer> deleteBuilder = getMessageDao().deleteBuilder();
            deleteBuilder.where().eq("deleted", true);
            deleteBuilder.delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets current user phone.
     *
     * @return the current user phone
     */
    public long getCurrentUserPhone() {
        return getCurrentUser().getPhone();
    }

    /**
     * Gets current user unique id.
     *
     * @return the current user unique id
     */
    public long getCurrentUserUniqueId() {
        return getCurrentUser().getUniqAppDeviceId();
    }

    /**
     * Gets current user.
     *
     * @return the current user
     */
    public HyberCurrentUserDBModel getCurrentUser() {
        try {
            List<HyberCurrentUserDBModel> userDBModels = getCurrentUserDao().queryForAll();
            if (userDBModels.size() == 1) {
                return userDBModels.get(0);
            } else {
                TableUtils.clearTable(connectionSource, HyberCurrentUserDBModel.class);
                getCurrentUserDao().create(new HyberCurrentUserDBModel());
                return getCurrentUserDao().queryForAll().get(0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Update current user Hyber current user db model.
     *
     * @param userDBModel the user db model
     * @return the Hyber current user db model
     */
    public HyberCurrentUserDBModel updateCurrentUser(HyberCurrentUserDBModel userDBModel) {
        try {
            getCurrentUserDao().update(userDBModel);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return getCurrentUser();
    }

    /**
     * Returns the Database Access Object (DAO) for our MessageDBModel class. It will create it or just give the cached
     * value.
     *
     * @return the message dao
     * @throws SQLException the sql exception
     */
    public Dao<HyberMessageDBModel, Integer> getMessageDao() throws SQLException {
        if (messageDao == null) {
            messageDao = getDao(HyberMessageDBModel.class);
        }
        return messageDao;
    }

    /**
     * Returns the Database Access Object (DAO) for our MessageDBModel class. It will create it or just give the cached
     * value.
     *
     * @return the message update dao
     * @throws SQLException the sql exception
     */
    public Dao<HyberMessageUpdateDBModel, Integer> getMessageUpdateDao() throws SQLException {
        if (messageUpdateDao == null) {
            messageUpdateDao = getDao(HyberMessageUpdateDBModel.class);
        }
        return messageUpdateDao;
    }

    /**
     * Returns the Database Access Object (DAO) for our HyberCurrentUserDBModel class. It will create it or just give the cached
     * value.
     *
     * @return the current user dao
     * @throws SQLException the sql exception
     */
    public Dao<HyberCurrentUserDBModel, Integer> getCurrentUserDao() throws SQLException {
        if (currentUserDao == null) {
            currentUserDao = getDao(HyberCurrentUserDBModel.class);
        }
        return currentUserDao;
    }

    /**
     * Returns the Database Access Object (DAO) for our HyberAlphaNameDBModel class. It will create it or just give the cached
     * value.
     *
     * @return the alphas dao
     * @throws SQLException the sql exception
     */
    public Dao<HyberAlphaNameDBModel, Integer> getAlphasDao() throws SQLException {
        if (alphasDao == null) {
            alphasDao = getDao(HyberAlphaNameDBModel.class);
        }
        return alphasDao;
    }

    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our MessageDBModel class. It will
     * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
     */
    private RuntimeExceptionDao<HyberMessageDBModel, Integer> getMessageDataDao() {
        if (messageRuntimeDao == null) {
            messageRuntimeDao = getRuntimeExceptionDao(HyberMessageDBModel.class);
        }
        return messageRuntimeDao;
    }

    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our MessageDBModel class. It will
     * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
     */
    private RuntimeExceptionDao<HyberMessageUpdateDBModel, Integer> getMessageUpdateDataDao() {
        if (messageUpdateRuntimeDao == null) {
            messageUpdateRuntimeDao = getRuntimeExceptionDao(HyberMessageUpdateDBModel.class);
        }
        return messageUpdateRuntimeDao;
    }

    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our CurrentUserDBModel class. It will
     * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
     *
     * @return the current user data dao
     */
    public RuntimeExceptionDao<HyberCurrentUserDBModel, Integer> getCurrentUserDataDao() {
        if (currentUserRuntimeDao == null) {
            currentUserRuntimeDao = getRuntimeExceptionDao(HyberCurrentUserDBModel.class);
        }
        return currentUserRuntimeDao;
    }

    /**
     * Returns the RuntimeExceptionDao (Database Access Object) version of a Dao for our CurrentUserDBModel class. It will
     * create it or just give the cached value. RuntimeExceptionDao only through RuntimeExceptions.
     *
     * @return the alphas data dao
     */
    public RuntimeExceptionDao<HyberAlphaNameDBModel, Integer> getAlphasDataDao() {
        if (alphasRuntimeDao == null) {
            alphasRuntimeDao = getRuntimeExceptionDao(HyberAlphaNameDBModel.class);
        }
        return alphasRuntimeDao;
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        messageDao = null;
        messageRuntimeDao = null;
        messageUpdateDao = null;
        messageUpdateRuntimeDao = null;
        currentUserDao = null;
        currentUserRuntimeDao = null;
        alphasDao = null;
        alphasRuntimeDao = null;
    }
}