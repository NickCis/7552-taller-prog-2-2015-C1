/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.Calendar;

/**
 *
 * @author umm194
 */
public class MessageEntity {
    private String content;
    private MediaEntity media;
    private Short status;
    private Calendar date;
    private ConversationEntity conversation;
    private UserEntity user;

    public MessageEntity(ConversationEntity conversation, UserEntity user, MediaEntity media, Calendar date, String content, Short status)
    {
        this.conversation = conversation;
        this.user = user;
        this.media = media;
        this.date = date;
        this.content = content;
        this.status = status;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the media
     */
    public MediaEntity getMedia() {
        return media;
    }

    /**
     * @param media the media to set
     */
    public void setMedia(MediaEntity media) {
        this.media = media;
    }

    /**
     * @return the status
     */
    public Short getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(Short status) {
        this.status = status;
    }

    /**
     * @return the date
     */
    public Calendar getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Calendar date) {
        this.date = date;
    }

    /**
     * @return the conversation
     */
    public ConversationEntity getConversation() {
        return conversation;
    }

    /**
     * @param conversation the conversation to set
     */
    public void setConversation(ConversationEntity conversation) {
        this.conversation = conversation;
    }

    /**
     * @return the user
     */
    public UserEntity getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(UserEntity user) {
        this.user = user;
    }
}
