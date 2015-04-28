package whatsapp.client;

/**
 * POJO para encapsular un mensaje en una vista
 * @author rburdet
 */
public class OneComment {
	public boolean left;
	public String comment;

	public OneComment(boolean left, String comment) {
		super();
		this.left = left;
		this.comment = comment;
	}

}