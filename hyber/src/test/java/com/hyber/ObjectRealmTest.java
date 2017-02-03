package com.hyber;

import android.util.Log;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
//import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.rule.PowerMockRule;
//import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import io.realm.Realm;
//import io.realm.examples.unittesting.model.Dog;
//import io.realm.examples.unittesting.repository.DogRepository;
//import io.realm.examples.unittesting.repository.DogRepositoryImpl;
import io.realm.log.RealmLog;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.verify;
//import static org.mockito.internal.verification.VerificationModeFactory.times;
//import static org.powermock.api.mockito.PowerMockito.doCallRealMethod;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 19, manifest = "src/main/AndroidManifest.xml")
@PowerMockIgnore({"org.mockito.*", "org.robolectric.*", "android.*"})
@SuppressStaticInitializationFor("io.realm.internal.Util")
@PrepareForTest({Realm.class, RealmLog.class})
public class ObjectRealmTest {
    // Robolectric, Using Power Mock https://github.com/robolectric/robolectric/wiki/Using-PowerMock
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

}
