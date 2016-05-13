package com.gms_worldwide.hybersdk;

/**
 * Created by Andrew Kochura.
 */
public class HyberMessageModel {

    private int id;
    private String from;
    private String message;
    private long time;
    private int type;
    private String owner;
    private boolean isRead;

    /**
     * Instantiates a Hyber message model.
     *
     * @param messageDBModel the message db model
     */
    public HyberMessageModel(HyberMessageDBModel messageDBModel) {
        this.id = messageDBModel.getId();
        this.from = messageDBModel.getFrom();
        this.message = messageDBModel.getMessage();
        this.time = messageDBModel.getTime();
        this.type = messageDBModel.getType();
        this.owner = messageDBModel.getOwner();
        this.isRead = messageDBModel.isRead();
    }

    /**
     * Instantiates a Hyber message model.
     *
     * @param from    the from
     * @param message the message
     * @param time    the time
     * @param type    the type
     * @param owner   the owner
     */
    public HyberMessageModel(int id, String from, String message, long time, int type, String owner, boolean isRead) {
        this.id = id;
        this.from = from;
        this.message = message;
        this.time = time;
        this.type = type;
        this.owner = owner;
        this.isRead = isRead;
    }

    /**
     * Sets owner.
     *
     * @param isRead set false if message is unread else set true
     */
    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets from.
     *
     * @return the from
     */
    public String getFrom() {
        return from;
    }

    /**
     * Sets from.
     *
     * @param from the from
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * Gets message.
     *
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets message.
     *
     * @param message the message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets time.
     *
     * @return the time
     */
    public long getTime() {
        return time;
    }

    /**
     * Sets time.
     *
     * @param time the time
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Gets owner.
     *
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets owner.
     *
     * @param owner the owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Gets owner.
     *
     * @return the owner
     */
    public boolean isRead() {
        return isRead;
    }

    /**
     * Gets type desc.
     *
     * @return the type desc
     */
    public String getTypeDesc() {
        switch (type) {
            case HyberConstants.PUSH_TYPE:
                return "PUSH";
            case HyberConstants.VIBER_TYPE:
                return "VIBER";
            case HyberConstants.SMS_TYPE:
                return "SMS";
            default:
                return "unexpected";
        }
    }

}
