package whatsapp.client;

/**
 * POJO para encapsular un mensaje en una vista
 * @author rburdet
 */
public class OneComment {
	public boolean left;
	public boolean sent;
	private String comment;

	public OneComment(boolean left, String comment) {
		super();
		this.left = left;
		this.comment = comment;
	}

	public void setSentStatus(boolean status){
		this.sent = status;
	}

	public String getComment(){
		return comment;
	}

}