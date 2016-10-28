package com.hyber;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

public class Repository {

    private final RealmConfiguration realmConfig;
    private Realm realm;

    public Repository() {
        realmConfig = new RealmConfiguration.Builder()
                .schemaVersion(1)
                .name("Hyber.realm")
                .modules(new HyberSchemaModule())
                .deleteRealmIfMigrationNeeded() //TODO add true migration
                .build();
    }

    public void open() {
        realm = Realm.getInstance(realmConfig);
    }

    Realm getNewRealmInstance() {
        return Realm.getInstance(realmConfig);
    }

    @Nullable
    public User getCurrentUser() {
        return realm.where(User.class)
                .equalTo(User.INDEX_NUMBER, 0)
                .findFirst();
    }

    public RealmResults<Message> getMessages(@NonNull User user) {
        checkUser(user);
        return realm.where(Message.class)
                .equalTo(Message.USER_ID, user.getId())
                .findAllSorted(Message.DATE, Sort.ASCENDING);
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

    void updateUserSession(@NonNull final User user,
                           @NonNull @lombok.NonNull final String token,
                           @NonNull @lombok.NonNull final String refreshToken,
                           @NonNull @lombok.NonNull final Date expirationDate) {
        checkUser(user);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                user.getSession().setToken(token);
                user.getSession().setRefreshToken(refreshToken);
                user.getSession().setExpirationDate(expirationDate);
                user.getSession().setExpired(false);
                HyberLogger.i("session is updated");
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

    void clearUserData(@NonNull User user) {
        checkUser(user);
        getMessages(user).deleteAllFromRealm();
        user.getSession().deleteFromRealm();
        user.deleteFromRealm();
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
