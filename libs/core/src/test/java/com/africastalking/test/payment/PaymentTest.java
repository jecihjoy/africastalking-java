package com.africastalking.test.payment;

import com.africastalking.*;
import com.africastalking.payment.recipient.Bank;
import com.africastalking.payment.response.B2BResponse;
import com.africastalking.payment.response.B2CResponse;
import com.africastalking.payment.response.BankTransferResponse;
import com.africastalking.payment.response.CheckoutResponse;
import com.africastalking.payment.response.CheckoutValidateResponse;
import com.africastalking.payment.recipient.Business;
import com.africastalking.payment.recipient.Consumer;
import com.africastalking.payment.PaymentCard;
import com.africastalking.payment.BankAccount;
import com.africastalking.Status;
import com.africastalking.test.Fixtures;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class PaymentTest {

    @Before
    public void setup() {
        AfricasTalking.initialize(Fixtures.USERNAME, Fixtures.API_KEY);
        AfricasTalking.setLogger(new Logger() {
            @Override
            public void log(String message, Object... args) {
                System.err.println(message);
            }
        });
    }

    @Test
    public void testMobileCheckout() throws IOException {
        PaymentService service = AfricasTalking.getService(PaymentService.class);
        CheckoutResponse resp = service.mobileCheckout("TestProduct", "0711082302", "KES 877", new HashMap());
        Assert.assertEquals(Status.PENDING_CONFIRMATION, resp.status);
    }

    @Test
    public void testCardCheckout() throws IOException {
        PaymentService service = AfricasTalking.getService(PaymentService.class);
        PaymentCard card = new PaymentCard(9223372036854775807L, 232, "Oct", 23, "NG","1222");
        CheckoutResponse resp = service.cardCheckout("Ikoyi Store", "NGN 877", card, "Test card checkout", new HashMap());
        Assert.assertEquals(Status.INVALID_REQUEST, resp.status);
    }

    @Test
    public void testCardCheckoutValidation() throws IOException {
        PaymentService service = AfricasTalking.getService(PaymentService.class);
        CheckoutValidateResponse resp = service.validateCardCheckout("sometxId", "someToken");
        Assert.assertEquals(Status.INVALID_REQUEST, resp.status);
    }

    @Test
    public void testBankCheckout() throws IOException {
        PaymentService service = AfricasTalking.getService(PaymentService.class);
        BankAccount account = new BankAccount("salama", "084802842", "NG", 90, "Zenith Bank");
        CheckoutResponse resp = service.bankCheckout("Ikoyi Store", "NGN 877", account,"Some narration", new HashMap());
        Assert.assertEquals(Status.INVALID_REQUEST, resp.status);
    }

    @Test
    public void testBankCheckoutValidation() throws IOException {
        PaymentService service = AfricasTalking.getService(PaymentService.class);
        CheckoutValidateResponse resp = service.validateBankCheckout("sometxId", "someToken");
        Assert.assertEquals(Status.INVALID_REQUEST, resp.status);
    }

    @Test
    public void testBankTransfer() throws IOException {
        PaymentService service = AfricasTalking.getService(PaymentService.class);
        List<Bank> recipients = Arrays.asList(new Bank(new BankAccount("Bob", "2323", "NG", 434,  "Zenith"), "NGN 5673", "Some narration", null));
        BankTransferResponse resp = service.bankTransfer("Ikoyi Store", recipients);
        Assert.assertEquals(Status.INVALID_REQUEST, resp.entries.get(0).status);
    }

    @Test
    public void testPayConsumer() throws IOException {
        PaymentService service = AfricasTalking.getService(PaymentService.class);
        Consumer recip = new Consumer("Salama", "0711082302", "KES 432", null);
        List<Consumer> consumers = Arrays.asList(recip);
        B2CResponse resp = service.payConsumers("TestProduct", consumers);
        Assert.assertEquals("KES 432.0000", resp.totalValue);
    }

    @Test
    public void testPayBusiness() throws IOException {
        PaymentService service = AfricasTalking.getService(PaymentService.class);
        Business recip = new Business("SBDev", "AccDest", Business.TRANSFER_TYPE_B2B, Business.PROVIDER_ATHENA, "KES 2512");
        B2BResponse resp = service.payBusiness("TestProduct", recip);
        Assert.assertEquals("Queued", resp.status);
    }


}
