package com.example.vinayak.firestorage.model;

import java.util.Comparator;

/**
 * Created by Vinayak on 11/16/2016.
 */
public class Message {
    String msgDate;
    String senderId;
    String receiverId;
    String msgText;

    public static Comparator<Message> DateOrder = new Comparator<Message>() {
        @Override
        public int compare(Message lhs, Message rhs) {
            if (lhs.getMsgDate()!=null && rhs.getMsgDate()!=null)
                return lhs.getMsgDate().compareTo(rhs.getMsgDate());
            return 0;
        }
    };

    public String getMsgDate() {
        return msgDate;
    }

    public void setMsgDate(String msgDate) {
        this.msgDate = msgDate;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMsgText() {
        return msgText;
    }

    public void setMsgText(String msgText) {
        this.msgText = msgText;
    }
}
