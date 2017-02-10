package com.hyber;

import android.util.Log;

import com.hyber.model.User;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import io.realm.Realm;
import io.realm.log.RealmLog;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, manifest=Config.NONE)
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@SuppressStaticInitializationFor("io.realm.internal.Util")
@PrepareForTest({Realm.class, RealmLog.class})
public class ObjectRealmTest {

    private static final String TAG = ObjectRealmTest.class.getSimpleName();

    @Rule
    public PowerMockRule rule = new PowerMockRule();
    Realm mockRealm;

    @Before
    public void setup() {
        Log.i(TAG, "Preparing for test");
        mockStatic(RealmLog.class);
        mockStatic(Realm.class);

        Realm mockRealm = PowerMockito.mock(Realm.class);
        Log.i(TAG, "Mock Realm");

        when(Realm.getDefaultInstance()).thenReturn(mockRealm);

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

}
