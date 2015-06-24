package whatsapp.client;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class DiscussArrayAdapter extends ArrayAdapter<OneComment> {

	private TextView campoEscritura;
	private List<OneComment> comentarios = new ArrayList<OneComment>();
	private LinearLayout wrapper;

	@Override
	public void add(OneComment object) {
		comentarios.add(object);
		super.add(object);
	}

	public DiscussArrayAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}

	public int getCount() {
		return this.comentarios.size();
	}

	public OneComment getItem(int index) {
		return this.comentarios.get(index);
	}

	public OneComment getItem(String item){
		for (OneComment comment : comentarios)
			if (comment.getComment().equals(item))
				return comment;
		return null;
	}

	public void setNotSent(OneComment comment){
		campoEscritura.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.red_exclamation_icon, 0, 0, 0);
	}

	public void setSeen(OneComment comment){
		campoEscritura.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.settings_gray, 0, 0, 0);
	}

	public void setSent(OneComment comment){
		campoEscritura.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.simpletick, 0, 0, 0);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.listitem_discuss, parent, false);
		}

		wrapper = (LinearLayout) row.findViewById(R.id.wrapper);

		OneComment coment = getItem(position);

		campoEscritura = (TextView) row.findViewById(R.id.comment);

		campoEscritura.setText(coment.getComment());

		campoEscritura.setBackgroundResource(coment.left ? R.drawable.bubble_yellow : R.drawable.bubble_green);
		wrapper.setGravity(coment.left ? Gravity.LEFT : Gravity.RIGHT);



		return row;
	}

	public Bitmap decodeToBitmap(byte[] decodedByte) {
		return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
	}

}