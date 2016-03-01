package com.malalaoshi.android.entity;

/**
 * Charge entity
 * Created by tianwei on 2/28/16.
 */
public class ChargeEntity {
    private String id;
    private String object;
    private Long created;
    private boolean livemode;
    private boolean paid;
    private boolean refunded;
    private String app;
    private String channel;
    private String order_no;
    private String client_ip;
    private int amount;
    private int amount_settle;
    private String currency;
    private String subject;
    private String body;
    private Object extra;
    private Long time_paid;
    private Long time_expire;
    private Long time_settle;
    private String transaction_no;
    private String refunds;
    private String amount_refunded;
    private String failure_code;
    private String failure_msg;
    private String credential;
    private String description;

    public static class Refunds {
        private String object;
        private String url;
        private String has_more;
    }
}
