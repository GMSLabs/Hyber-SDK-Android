package com.gms_worldwide.hybersdk;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

/**
 * Created by Andrew Kochura.
 */
public class AvailabilityRestApiTest {

    private int code200;
    private String clientKey;
    private String projectId;
    private long testUserPhone;
    private long testUniqAppDeviceId;

    @Before
    public void createDataForValidator() {
        clientKey = "zzz777zzzZZZzzz777zOz777zzzZZZzzz777zzz";
        projectId = "999999999";
        testUniqAppDeviceId = 545645645;
        HyberPlugins.initialize(projectId, clientKey);
        code200 = 200;
        testUserPhone = 380991234567L;
    }

    @Test
    public void testVMessagesRestApi() {
        System.out.print("--------/* Test V messages REST API */----------\n");
        System.out.print("______________________________________________\n");
        System.out.printf("%-25s%-8s%s\n", "value", "actual", "expected");
        System.out.print("----------------------------------------------\n");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        HyberPlugins.get().restClient()
                .getViberMessagesObservable(testUserPhone, testUniqAppDeviceId, calendar.getTimeInMillis())
                .subscribe(response -> {
                    System.out.printf("%-25s%-8s%s\n", "isSuccess", response.isSuccessful(), true);
                    assertEquals(true, response.isSuccessful());
                    System.out.printf("%-25s%-8s%s\n", "code", response.code(), code200);
                    assertEquals(code200, response.code());
                });

        System.out.print("______________________________________________\n\n");
    }

    /*@Test
    public void testRegistration() {
        System.out.print("----*//* Test User Registration REST API *//*-----\n");
        System.out.print("______________________________________________\n");
        System.out.printf("%-25s%-8s%s\n", "value", "actual", "expected");
        System.out.print("----------------------------------------------\n");

        hyberApiClient.appRegistrationObservable(testUserPhone, testUserEmail)
                .subscribe(response -> {
                    System.out.printf("%-25s%-8s%s\n", "code", response.code(), code200);
                    assertEquals(code200, response.code());
                    System.out.printf("%-25s%-8s%s\n", "status", response.body().getStatus(), false);
                    assertEquals(false, response.body().getStatus());
                    System.out.printf("%-25s%-8s%s\n", "uniqAppDeviceId", response.body().getUniqAppDeviceId(), 0);
                    assertEquals(0, response.body().getUniqAppDeviceId());
                });

        System.out.print("______________________________________________\n\n");
    }

    @Test
    public void testLoginValidation() {
        System.out.print("--*//* Test User Login Validation REST API *//*---\n");
        System.out.print("______________________________________________\n");
        System.out.printf("%-25s%-8s%s\n", "value", "actual", "expected");
        System.out.print("----------------------------------------------\n");

        hyberApiClient.validationLoginAppObservable(testUniqAppDeviceId, testValidationCode, testUserPhone, testUserEmail)
                .subscribe(response -> {
                    System.out.printf("%-25s%-8s%s\n", "code", response.code(), code200);
                    assertEquals(code200, response.code());
                    System.out.printf("%-25s%-8s%s\n", "status", response.body().getStatus(), false);
                    assertEquals(false, response.body().getStatus());
                    System.out.printf("%-25s%-8s%s\n", "uniqAppDeviceId", response.body().getUniqAppDeviceId(), 0);
                    assertEquals(0, response.body().getUniqAppDeviceId());
                });

        System.out.print("______________________________________________\n\n");
    }

    @Test
    public void testValidationPhone() {
        System.out.print("--*//* Test User Validation Phone REST API *//*---\n");
        System.out.print("______________________________________________\n");
        System.out.printf("%-25s%-8s%s\n", "value", "actual", "expected");
        System.out.print("----------------------------------------------\n");

        hyberApiClient.validationPhoneObservable(testUniqAppDeviceId, testValidationCode, testUserPhone)
                .subscribe(response -> {
                    System.out.printf("%-25s%-8s%s\n", "code", response.code(), code200);
                    assertEquals(code200, response.code());
                    System.out.printf("%-25s%-8s%s\n", "status", response.body().getStatus(), false);
                    assertEquals(false, response.body().getStatus());
                    System.out.printf("%-25s%-8s%s\n", "uniqAppDeviceId", response.body().getUniqAppDeviceId(), 0);
                    assertEquals(0, response.body().getUniqAppDeviceId());
                });

        System.out.print("______________________________________________\n\n");
    }

    @Test
    public void testValidationEmail() {
        System.out.print("--*//* Test User Validation Email REST API *//*---\n");
        System.out.print("______________________________________________\n");
        System.out.printf("%-25s%-8s%s\n", "value", "actual", "expected");
        System.out.print("----------------------------------------------\n");

        hyberApiClient.validationEmailObservable(testUniqAppDeviceId, testValidationCode, testUserEmail)
                .subscribe(response -> {
                    System.out.printf("%-25s%-8s%s\n", "code", response.code(), code200);
                    assertEquals(code200, response.code());
                    System.out.printf("%-25s%-8s%s\n", "status", response.body().getStatus(), false);
                    assertEquals(false, response.body().getStatus());
                    System.out.printf("%-25s%-8s%s\n", "uniqAppDeviceId", response.body().getUniqAppDeviceId(), 0);
                    assertEquals(0, response.body().getUniqAppDeviceId());
                });

        System.out.print("______________________________________________\n\n");
    }

    @Test
    public void testGetCurrencies() {
        System.out.print("------*//* Test Get Currencies REST API *//*------\n");
        System.out.print("______________________________________________\n");
        System.out.printf("%-25s%-8s%s\n", "value", "actual", "expected");
        System.out.print("----------------------------------------------\n");

        hyberApiClient.getCurrencyObservable()
                .subscribe(response -> {
                    System.out.printf("%-25s%-8s%s\n", "code", response.code(), code200);
                    assertEquals(code200, response.code());
                    System.out.printf("%-25s%-8s%s\n", "content", response.body().getType_currency(), null);
                    assertEquals(null, response.body().getType_currency());
                });

        System.out.print("______________________________________________\n\n");
    }

    @Test
    public void testGetMarkers() {
        System.out.print("-------*//* Test Get Markers REST API *//*--------\n");
        System.out.print("______________________________________________\n");
        System.out.printf("%-25s%-8s%s\n", "value", "actual", "expected");
        System.out.print("----------------------------------------------\n");

        for (int v : testMarkersVersion) {
            hyberApiClient.getMarkersObservable(v)
                    .subscribe(response -> {
                        System.out.printf("--> requested version --> %s\n", v);
                        System.out.printf("%-25s%-8s%s\n", "code", response.code(), code200);
                        assertEquals(code200, response.code());
                        System.out.printf("%-25s%-8s%s\n", "version", response.body().getVersion(), 0);
                        assertEquals(0, response.body().getVersion());
                        System.out.printf("%-25s%-8s%s\n", "content", response.body().getMap_markers(), null);
                        assertEquals(null, response.body().getMap_markers());
                    });
        }

        System.out.print("______________________________________________\n\n");
    }

    @Test
    public void testUpdateUserEmail() {
        System.out.print("----*//* Test Update User Email REST API *//*-----\n");
        System.out.print("______________________________________________\n");
        System.out.printf("%-25s%-8s%s\n", "value", "actual", "expected");
        System.out.print("----------------------------------------------\n");

        hyberApiClient.updateEmailObservable(testUniqAppDeviceId, testUserEmail)
                .subscribe(response -> {
                    System.out.printf("%-25s%-8s%s\n", "code", response.code(), code200);
                    assertEquals(code200, response.code());
                    System.out.printf("%-25s%-8s%s\n", "status", response.body().getStatus(), false);
                    assertEquals(false, response.body().getStatus());
                    System.out.printf("%-25s%-8s%s\n", "uniqAppDeviceId", response.body().getUniqAppDeviceId(), 0);
                    assertEquals(0, response.body().getUniqAppDeviceId());
                });

        System.out.print("______________________________________________\n\n");
    }

    @Test
    public void testUpdateUserPhone() {
        System.out.print("----*//* Test Update User Phone REST API *//*-----\n");
        System.out.print("______________________________________________\n");
        System.out.printf("%-25s%-8s%s\n", "value", "actual", "expected");
        System.out.print("----------------------------------------------\n");

        hyberApiClient.updatePhoneObservable(testUniqAppDeviceId, testUserPhone)
                .subscribe(response -> {
                    System.out.printf("%-25s%-8s%s\n", "code", response.code(), code200);
                    assertEquals(code200, response.code());
                    System.out.printf("%-25s%-8s%s\n", "status", response.body().getStatus(), false);
                    assertEquals(false, response.body().getStatus());
                    System.out.printf("%-25s%-8s%s\n", "uniqAppDeviceId", response.body().getUniqAppDeviceId(), 0);
                    assertEquals(0, response.body().getUniqAppDeviceId());
                });

        System.out.print("______________________________________________\n\n");
    }*/

    @After
    public void clearValidatorData() {
        code200 = 0;
        testUserPhone = 0;
    }

}
