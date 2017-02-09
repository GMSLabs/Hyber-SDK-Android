package com.hyber;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.hyber.log.HyberLogger;
import com.hyber.model.Device;
import com.hyber.model.HyberModule;
import com.hyber.model.Message;
import com.hyber.model.User;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

public class Repository {

    private final RealmConfiguration realmConfig;
    private Realm realm;

    public Repository() {
        realmConfig = new RealmConfiguration.Builder()
                .schemaVersion(0)
                .name("hyber.realm")
                .modules(new HyberModule())
                .deleteRealmIfMigrationNeeded()
                .build();
    }

    /**
     * Constructor specially for tests
     * @param mockRealm = mocked Realm
     * @param mockRealmConfig = mocked RealmConfiguration
     */
    public Repository(Realm mockRealm, RealmConfiguration mockRealmConfig) {
        realm = mockRealm;
        realmConfig = mockRealmConfig;
    }

    public void open() {
        realm = Realm.getInstance(realmConfig);
    }

    Realm getNewRealmInstance() {
        return Realm.getInstance(realmConfig);
    }

    public void executeTransaction(Realm.Transaction transaction) {
        realm.executeTransaction(transaction);
    }

    public void executeTransactionAsync(Realm.Transaction transaction) {
        realm.executeTransactionAsync(transaction);
    }

    @Nullable
    public User getCurrentUser() {
        return realm.where(User.class).findFirst();
    }

    @Nullable
    public RealmResults<Message> getMessages(@NonNull User user) {
        checkUser(user);
        return realm.where(Message.class)
                .equalTo(Message.USER_ID, user.getId())
                .findAllSorted(Message.DATE, Sort.ASCENDING);
    }

    @Nullable
    public RealmResults<Device> getDevices(@NonNull User user) {
        checkUser(user);
        return realm.where(Device.class).equalTo(Device.USER_ID, user.getId()).findAll();
    }

    @Nullable
    Message getMessageById(@NonNull User user,
                           @NonNull @lombok.NonNull String messageId) {
        checkUser(user);
        return realm.where(Message.class)
                .equalTo(Message.USER_ID, user.getId())
                .equalTo(Message.ID, messageId)
                .findFirst();
    }

    RealmResults<Message> getAllUnreportedMessages() {
        return realm.where(Message.class)
                .equalTo(Message.IS_REPORTED, false)
                .findAllSorted(Message.DATE, Sort.DESCENDING);
    }

    void saveNewUser(@NonNull @lombok.NonNull final User user) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(user);
                HyberLogger.i("user with id %s is saved", user.getId());
            }
        });
    }

    void updateFcmToken(@NonNull @lombok.NonNull final User user, @NonNull @lombok.NonNull final String fcmToken) {
        checkUser(user);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                user.setFcmToken(fcmToken);
                user.setIsFcmTokenSent(false);
                HyberLogger.i("Fcm token is updated for user with id %s", user.getId());
            }
        });
    }

    void saveMessageOrUpdate(@NonNull @lombok.NonNull final Message message) {
        checkUser(message.getUser());
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(message);
                HyberLogger.i("message with id %s is saved", message.getId());
            }
        });
    }

    void saveMessagesOrUpdate(@NonNull @lombok.NonNull final User user,
                              @NonNull @lombok.NonNull final Iterable<Message> messages) {
        checkUser(user);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealmOrUpdate(messages);
                for (Message msg : messages) {
                    HyberLogger.i("message with id %s is saved", msg.getId());
                }
            }
        });
    }

    void clearUserData(@NonNull final User user) {
        checkUser(user);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                getMessages(user).deleteAllFromRealm();
                getDevices(user).deleteAllFromRealm();
                user.deleteFromRealm();
            }
        });
    }

    void clearAllData() {
        if (!realm.isClosed())
            realm.close();
        Realm.deleteRealm(realmConfig);
    }

    public void close() {
        realm.close();
    }

    private void checkUser(@Nullable User user) {
        if (user == null || realm.where(User.class).equalTo(User.ID, user.getId()).findFirst() == null) {
            throw new IllegalStateException(
                    "Hyber user must be exist and valid to calling this Hyber repository method");
        }
    }

}
