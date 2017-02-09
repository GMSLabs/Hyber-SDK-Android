package com.hyber;

import android.util.Log;

import com.hyber.model.User;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.log.RealmLog;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.doCallRealMethod;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 19)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@SuppressStaticInitializationFor("io.realm.internal.Util")
@PrepareForTest({Realm.class, RealmLog.class})
public class ObjectRealmTest {
    // Robolectric, Using Power Mock https://github.com/robolectric/robolectric/wiki/Using-PowerMock
    private static final String TAG = ObjectRealmTest.class.getSimpleName();

    @Rule
    public PowerMockRule rule = new PowerMockRule();
    private Realm mockRealm;
    private RealmConfiguration mockRealmConfiguration;

    @Before
    public void setup() {
        Log.i(TAG, "Preparing for test");
        mockStatic(RealmLog.class);
        mockStatic(Realm.class);

        RealmConfiguration mockRealmConfiguration = mock(RealmConfiguration.class);
        Log.i(TAG, "Mock RealmConfiguration");

        Realm mockRealm = PowerMockito.mock(Realm.class);
        Log.i(TAG, "Mock Realm");

        when(Realm.getDefaultInstance()).thenReturn(mockRealm);

        this.mockRealmConfiguration = mockRealmConfiguration;
        this.mockRealm = mockRealm;
        Log.i(TAG, "Prepared for test");
    }

    @Test
    public void shouldBeAbleToGetDefaultInstance() {
        Log.i(TAG, "Start test of Realm.getDefaultInstance");

        assertThat(Realm.getDefaultInstance(), is(mockRealm));

        Log.i(TAG, "Finish test of Realm.getDefaultInstance");
    }

    @Test
    public void shouldBeAbleToMockRealmMethods() {
        Log.i(TAG, "Start test of mocked Realm methods");

        when(mockRealm.isAutoRefresh()).thenReturn(true);
        assertThat(mockRealm.isAutoRefresh(), is(true));

        when(mockRealm.isAutoRefresh()).thenReturn(false);
        assertThat(mockRealm.isAutoRefresh(), is(false));

        Log.i(TAG, "Finish test of mocked Realm methods");
    }

    @Test
    public void shouldBeAbleToCreateARealmObject() {
        Log.i(TAG, "Start test of create User Realm object");

        User user = new User();
        when(mockRealm.createObject(User.class)).thenReturn(user);

        User output = mockRealm.createObject(User.class);

        assertThat(output, is(user));

        Log.i(TAG, "Finish test of create User Realm object");
    }

    /**
     * This test verifies the behavior in the {@link Repository} class.
     */
    @Test
    public void shouldVerifyThatUserWasCreated() {
        Log.i(TAG, "Start test of save User Realm object");

        doCallRealMethod().when(mockRealm).executeTransaction(Mockito.any(Realm.Transaction.class));

        User user = mock(User.class);
        when(mockRealm.createObject(User.class)).thenReturn(user);

        Repository repo = new Repository(mockRealm, mockRealmConfiguration);
        repo.saveNewUser(new User("user_id", "user_phone", new Date(), "user_auth_token", "user_session_id", "user_fcm_token"));

        Log.i(TAG, "Finish test of save User Realm object");
    }

}
