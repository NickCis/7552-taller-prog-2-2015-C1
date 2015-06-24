package utils;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.AbstractList;
import java.util.ArrayList;
import whatsapp.client.R;
import whatsapp.client.RowItem;

public class UserAdapter extends ArrayAdapter {

	Context context;
	List<RowItem> usuarios;

	public UserAdapter(Context context, int resource, Object[] objects) {
		super(context, resource, objects);
		usuarios.add((RowItem) objects[1]);
	}

	public UserAdapter(Context ctx, Object[] objects){
		super(ctx,-1,objects);
		this.context = ctx;
		usuarios = new ArrayList<RowItem>();
		for (int i =0 ; i<objects.length ; i++)
			usuarios.add((RowItem) objects[i]);
	}


	public UserAdapter(Context ctx, ArrayList<RowItem> array ){
		super(ctx,-1,array.toArray());
		this.context = ctx;
		usuarios = array;
	}

/*
	public UserAdapter(Context context, List<RowItem> rowItems, int a ) {
		this.context = context;
		this.usuarios = rowItems;
	}
		*/

	public void add(RowItem item){
		this.usuarios.add(item);
	}

	@Override
	public int getCount() {
		return usuarios.size();
	}


	public RowItem getItem(int position){
		return usuarios.get(position);
	}

	@Override
	public long getItemId(int position) {
		return usuarios.indexOf(getItem(position));
	}

	public void clear() {
		for (int i = 0 ; usuarios.size()!=0 ; )
			this.usuarios.remove(i);
	}

	/* private view holder class */
	private class ViewHolder {

		ImageView profile_pic;
		TextView member_name;
		TextView derecha;
		TextView abajo;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;

		LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

		//if (convertView == null) {

			convertView = mInflater.inflate(R.layout.list_item, null);
			holder = new ViewHolder();

			holder.member_name = (TextView) convertView
					.findViewById(R.id.name);
			holder.profile_pic = (ImageView) convertView
					.findViewById(R.id.pic);
			holder.derecha = (TextView) convertView.findViewById(R.id.contact);
			holder.abajo = (TextView) convertView
					.findViewById(R.id.sta);

			//UserEntity row_pos = usuarios.get(position);
			RowItem row_pos = usuarios.get(position);

			//holder.profile_pic.setImageBitmap(row_pos.getAvatar());
			holder.profile_pic.setImageDrawable(row_pos.getAvatar());
			holder.member_name.setText(row_pos.getPrincipal());
			
			
			holder.derecha.setText(row_pos.getDerecha());
			holder.abajo.setText(row_pos.getAbajo());

			convertView.setTag(holder);
		//} else {
		if (convertView != null) 
			holder = (ViewHolder) convertView.getTag();
		//}

		return convertView;
	}


	/**
	 * Metodo para remover usuarios en la lista de usuario por nombre
	 * @param name 
	 */
	/*
	public void remove(String name){
		for (RowItem usuario : usuarios) {
			if (usuario.getUserName().equalsIgnoreCase(name)){
				this.usuarios.remove(usuario);
				return;
			}
		}
	}
	
	public void remove(int index){
		this.usuarios.remove(index);
	}
	
	public void remove(RowItem rowItem){
		this.usuarios.remove(rowItem);
	}
	*/

	public boolean contains(String item){
		for (RowItem usuario : usuarios) {
			if (usuario.contains(item))
				return true;
		}
		return false;
	}

	public int getPosition(String item){
		for (int i = 0 ; i< usuarios.size() ; i++) {
			if (usuarios.get(i).contains(item))
				return i;
		}
		return -1;
	}
}
