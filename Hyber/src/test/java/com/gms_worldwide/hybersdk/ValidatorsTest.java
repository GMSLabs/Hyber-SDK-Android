package com.gms_worldwide.hybersdk;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Andrew Kochura.
 */
public class ValidatorsTest {

    private Map<Long, Boolean> phones;
    private Map<String, Boolean> emails;

    @Before
    public void createDataForValidator() {

        phones = new HashMap<>();
        phones.put(0L, false);
        phones.put(35694568L, false);
        phones.put(100658945454L, false);
        phones.put(380999212256L, true);
        phones.put(100645454L, false);
        phones.put(38065745454L, false);
        phones.put(389655745454L, false);
        assertNotNull(phones);

        emails = new HashMap<>();
        emails.put(null, false);
        emails.put("", false);
        emails.put("lorymartinez@gmail.com", true);
        emails.put("lorymartinezgmail.com", false);
        assertNotNull(emails);

    }

    @Test
    public void testPhoneValidators() {

        System.out.print("---------/* Test Phone Validators */----------\n");
        Iterator<Map.Entry<Long, Boolean>> phone_it = phones.entrySet().iterator();
        System.out.print("______________________________________________\n");
        System.out.printf("%-16s%-8s%s\n", "value", "actual", "expected");
        System.out.print("----------------------------------------------\n");
        while (phone_it.hasNext()) {
            Map.Entry<Long, Boolean> pair = phone_it.next();
            boolean actual = HyberTools.isPhoneValid(pair.getKey());
            boolean expected = pair.getValue();
            System.out.printf("%-16s%-8s%s\n", "\'" + pair.getKey() + "\'", actual, expected);
            assertEquals(actual, expected);
            phone_it.remove();
        }
        System.out.print("______________________________________________\n\n");
    }

    @Test
    public void testEmailValidators() {
        System.out.print("---------/* Test Email Validators */----------\n");
        Iterator<Map.Entry<String, Boolean>> email_it = emails.entrySet().iterator();
        System.out.print("______________________________________________\n");
        System.out.printf("%-25s%-8s%s\n", "value", "actual", "expected");
        System.out.print("----------------------------------------------\n");
        while (email_it.hasNext()) {
            Map.Entry<String, Boolean> pair = email_it.next();
            boolean actual = HyberTools.isEmailValid(pair.getKey());
            boolean expected = pair.getValue();
            System.out.printf("%-25s%-8s%s\n", "\'" + pair.getKey() + "\'", actual, expected);
            assertEquals(actual, expected);
            email_it.remove();
        }
        System.out.print("______________________________________________\n\n");
    }

    @After
    public void clearValidatorData() {
        phones = null;
        emails = null;
    }

}
