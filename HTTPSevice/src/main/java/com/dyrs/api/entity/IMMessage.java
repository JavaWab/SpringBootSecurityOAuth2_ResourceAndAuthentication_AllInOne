package com.dyrs.api.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "t_message")
public class IMMessage {
    @Id
    @Column(name = "msg_id", length = 50)
    @JsonProperty("msg_id")
    private String msgID;

    @Column(nullable = false, length = 10)
    private String type;

    @Column(name = "from_jid", length = 50)
    @JsonProperty("from_jid")
    private String fromJID;

    @Column(name = "from_nick", length = 50)
    @JsonProperty("from_nick")
    private String fromNick;

    @Column(name = "from_uid", length = 50)
    @JsonProperty("from_uid")
    private String fromUID;

    @Column(name = "from_icon", length = 200)
    @JsonProperty("from_icon")
    private String fromIcon;

    @Column(name = "to_jid", length = 50)
    @JsonProperty("to_jid")
    private String toJID;

    @Column(name = "to_uid", length = 50)
    @JsonProperty("to_uid")
    private String toUID;

    @Column(name = "chat_id", length = 200)
    @JsonProperty("chat_id")
    private String chatID;

    @Column(name = "location", length = 200)
    @JsonProperty("location")
    private String location;

    @Column(name = "to_icon", length = 200)
    @JsonProperty("to_icon")
    private String toIcon;

    @Column(nullable = false, length = 500)
    @JsonProperty
    private String message;
    @JsonProperty
    @Column(nullable = false)
    private Date time;

    public String getMsgID() {
        return msgID;
    }

    public void setMsgID(String msgID) {
        this.msgID = msgID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFromJID() {
        return fromJID;
    }

    public void setFromJID(String fromJID) {
        this.fromJID = fromJID;
    }

    public String getFromNick() {
        return fromNick;
    }

    public void setFromNick(String fromNick) {
        this.fromNick = fromNick;
    }

    public String getFromUID() {
        return fromUID;
    }

    public void setFromUID(String fromUID) {
        this.fromUID = fromUID;
    }

    public String getFromIcon() {
        return fromIcon;
    }

    public void setFromIcon(String fromIcon) {
        this.fromIcon = fromIcon;
    }

    public String getToJID() {
        return toJID;
    }

    public void setToJID(String toJID) {
        this.toJID = toJID;
    }

    public String getToUID() {
        return toUID;
    }

    public void setToUID(String toUID) {
        this.toUID = toUID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getToIcon() {
        return toIcon;
    }

    public void setToIcon(String toIcon) {
        this.toIcon = toIcon;
    }
}
