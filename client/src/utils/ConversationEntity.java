/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
	private String id;

	public ConversationEntity(Integer conversationId) {
		this(conversationId, Calendar.getInstance());
	}

	public ConversationEntity(Integer conversationId, Calendar lastMessageTime, List<UserEntity> users) {
		this(conversationId, lastMessageTime);
		this.users = users;
	}

	public ConversationEntity(String id) {
		this.id = id;
		this.users = new ArrayList<UserEntity>();
		this.last_message_time = Calendar.getInstance();
	}

	public ConversationEntity(Integer conversationId, Calendar lastMessageTime) {
		this.conversationId = conversationId;
		this.last_message_time = lastMessageTime;
		this.users = new ArrayList<UserEntity>();
	}

	public ConversationEntity(Integer conversationId, String lastMessageTime) {
		this.conversationId = conversationId;
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(lastMessageTime));
		} catch (ParseException pE) {
			System.out.println(pE.getCause().getMessage());
		}
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

	public void addUser(UserEntity user) {
		this.users.add(user);
	}

	public UserEntity getUser(int index) {
		return this.users.get(index);
	}

	public void deleteUser(int index) {
		this.users.remove(index);
	}

	public void deleteUser(UserEntity user) {
		this.users.remove(user);
	}
}
