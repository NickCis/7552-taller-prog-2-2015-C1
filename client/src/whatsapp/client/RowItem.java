package whatsapp.client;

import android.graphics.drawable.Drawable;

public class RowItem {

	private String nickName;
	private int id;
	private Drawable avatar;
	private String abajo;
	private String derecha;

	public RowItem(){
	
	}

	public RowItem(String username){
		this.abajo = username;
	}
	

	/** Crea una linea de un listview con toda la informacion necesaria
	 * 
	 * @param pricipal nick
	 * @param avatar imagen
	 * @param abajo nombre de usuario
	 * @param derecha Nombre de usuario 
	 * @param id identificador
	 */
	public RowItem(String pricipal, Drawable avatar, String abajo,String derecha, int id) {

		this.nickName = pricipal;
		this.avatar = avatar;
		this.abajo = abajo;
		this.derecha = derecha;
		this.id = id;
	}

	public String getPrincipal() {
		return nickName;
	}

	public void setPrincipal(String member_name) {
		this.nickName = member_name;
	}

	public String getAbajo() {
		return abajo;
	}

	public void setDerecha(String status) {
		this.derecha = status;;
	}

	public String getDerecha() {
		return derecha;
	}

	public void setAbajo(String contactType) {
		this.abajo = contactType;
	}

	public Drawable getAvatar() {
		return avatar;
	}

	public void setAvatar(Drawable d){
		avatar=d;
	}

	public boolean contains(String item){
		return item.equals(abajo);
	}
}
