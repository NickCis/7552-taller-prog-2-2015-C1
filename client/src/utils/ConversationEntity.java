/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author umm194
 */
public class ConversationEntity {
    private Integer conversationId;
    private List<UserEntity> users;
    private Calendar last_message_time;

    public ConversationEntity(Integer conversationId)
    {
        this(conversationId, Calendar.getInstance());
    }
    
    public ConversationEntity(Integer conversationId, Calendar lastMessageTime, List<UserEntity> users)
    {
        this(conversationId, lastMessageTime);
        this.users = users;
    }
    
    public ConversationEntity(Integer conversationId, Calendar lastMessageTime)
    {
        this.conversationId = conversationId;
        this.last_message_time = lastMessageTime;
        this.users = new ArrayList<UserEntity>();
    }
    
    public ConversationEntity(Integer conversationId, String lastMessageTime)
    {
        this.conversationId = conversationId;
        Calendar cal = Calendar.getInstance();
        cal.setTime(Date.valueOf(lastMessageTime));
        this.last_message_time = cal;
        this.users = new ArrayList<UserEntity>();
    }
    
    /**
     * @return the conversationId
     */
    public Integer getConversationId() {
        return conversationId;
    }

    /**
     * @param conversationId the conversationId to set
     */
    public void setConversationId(Integer conversationId) {
        this.conversationId = conversationId;
    }

    /**
     * @return the users
     */
    public List<UserEntity> getUsers() {
        return users;
    }

    /**
     * @param users the users to set
     */
    public void setUsers(List<UserEntity> users) {
        this.users = users;
    }

    /**
     * @return the last_message_time
     */
    public Calendar getLast_message_time() {
        return last_message_time;
    }

    /**
     * @param last_message_time the last_message_time to set
     */
    public void setLast_message_time(Calendar last_message_time) {
        this.last_message_time = last_message_time;
    }
    
    public void addUser(UserEntity user)
    {
        this.users.add(user);
    }
    
    public UserEntity getUser(int index)
    {
        return this.users.get(index);
    }
    
    public void deleteUser(int index)
    {
        this.users.remove(index);
    }
    
    public void deleteUser(UserEntity user)
    {
        this.users.remove(user);
    }
}
