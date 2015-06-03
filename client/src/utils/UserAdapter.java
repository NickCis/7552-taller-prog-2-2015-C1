package utils;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import whatsapp.client.R;
import whatsapp.client.RowItem;

public class UserAdapter extends BaseAdapter {

	Context context;
	List<RowItem> usuarios;

	public UserAdapter(Context context, List<RowItem> rowItems) {
		this.context = context;
		this.usuarios = rowItems;
	}

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

	/* private view holder class */
	private class ViewHolder {

		ImageView profile_pic;
		TextView member_name;
		TextView status;
		TextView contactType;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;

		LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

		if (convertView == null) {

			convertView = mInflater.inflate(R.layout.list_item, null);
			holder = new ViewHolder();

			holder.member_name = (TextView) convertView
					.findViewById(R.id.name);
			holder.profile_pic = (ImageView) convertView
					.findViewById(R.id.pic);
			holder.status = (TextView) convertView.findViewById(R.id.sta);
			holder.contactType = (TextView) convertView
					.findViewById(R.id.contact);

			//UserEntity row_pos = usuarios.get(position);
			RowItem row_pos = usuarios.get(position);

			//holder.profile_pic.setImageBitmap(row_pos.getAvatar());
			holder.profile_pic.setImageDrawable(row_pos.getAvatar());
			holder.member_name.setText(row_pos.getNickName());
			
			
			holder.status.setText(row_pos.getStatus());
			holder.contactType.setText(row_pos.getStatus());

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		return convertView;
	}

	/**
	 * Metodo para remover usuarios en la lista de usuario por nombre
	 * @param name 
	 */
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
