package whatsapp.client;

import android.graphics.drawable.Drawable;

public class RowItem {

	private String nickName;
	private int id;
	private Drawable avatar;
	private String userName;
	private String contactType;

	public RowItem(String nickName, Drawable avatar, String userName,String contactType, int id) {

		this.nickName = nickName;
		this.avatar = avatar;
		this.userName = userName;
		this.contactType = contactType;
		this.id = id;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String member_name) {
		this.nickName = member_name;
	}

	public String getStatus() {
		return userName;
	}

	public void setStatus(String status) {
		this.userName = status;
	}

	public String getContactType() {
		return contactType;
	}

	public void setContactType(String contactType) {
		this.contactType = contactType;
	}

	public Drawable getAvatar() {
		return avatar;
	}
}
