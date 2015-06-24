/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import android.graphics.Bitmap;
import java.util.Calendar;

/**
 *
 * @author umm194
 */
public class UserEntity {
    private Integer userId;
    private Integer phone;
    private String username;
    private String nickname;
    private Short status;
    private Bitmap avatar;
    private Calendar lastTime;
    private String statusMessage;
    private Checkin checkin;

    public UserEntity(Integer userId, String username, String nickname, Integer phone, Short status, Bitmap avatar, Calendar lastTime, String statusMessage, Checkin checkin)
    {
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
        this.phone = phone;
        this.status = status;
        this.avatar = avatar;
        this.lastTime = lastTime;
        this.statusMessage = statusMessage;
        this.checkin = checkin;
    }

    public UserEntity(String username){
    	this.username = username;
    }
    
    public void setAvatar(Bitmap img){
            this.avatar = img;
    }

    public Bitmap getAvatar() {
            return avatar;
    }
		
    /**
     * @return the userId
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * @return the phone
     */
    public Integer getPhone() {
        return phone;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(Integer phone) {
        this.phone = phone;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the name to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * @return the nickname
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @param nickname the nickname to set
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
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
     * @return the checkin
     */
    public Calendar getLastTime() {
        return lastTime;
    }

    /**
     * @param lastTime the checkin to set
     */
    public void setLastTime(Calendar lastTime) {
        this.lastTime = lastTime;
    }
    
    /**
     * @return the statusMessage
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * @param statusMessage the statusMessage to set
     */
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
    
    /**
     * @return the checkin
     */
    public Checkin getCheckin() {
        return checkin;
    }

    /**
     * @param checkin the checkin to set
     */
    public void setCheckin(Checkin checkin) {
        this.checkin = checkin;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if(obj == null)
            return false;
        if(!(obj instanceof UserEntity))
            return false;

        UserEntity other = (UserEntity) obj;
        if(!this.userId.equals(other.userId)) return false;
        if(!this.username.equals(other.username)) return false;
        if(!this.nickname.equals(other.nickname)) return false;
        if(!this.phone.equals(other.phone)) return false;
        if(!this.status.equals(other.status)) return false;
	if(this.avatar != null && other.avatar != null)
            if(!this.avatar.equals(other.avatar)) return false;
        if(this.checkin != null && other.checkin != null)
            if(!this.checkin.equals(other.checkin)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.userId != null ? this.userId.hashCode() : 0);
        return hash;
    }
}
