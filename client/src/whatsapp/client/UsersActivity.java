package whatsapp.client;

import model.ServerResultReceiver;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.GETService;
import model.POSTService;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import services.AppController;
import utils.ConfigurationManager;
import utils.ConversationEntity;
import utils.DatabaseHelper;
import utils.UserAdapter;
import utils.UserEntity;

public class UsersActivity extends Activity implements ServerResultReceiver.Listener {

	private UserAdapter adapter;
	private ArrayList<RowItem> rowItems;
	List<UserEntity> users;
	ListView viewUsuarios;
	Button agregar;
	UsersActivity uA;
	UserEntity ueAux;
	RowItem rowItemAux;
	private static UsersActivity INSTANCE;
	private static boolean showing;

	@Override
	public void onCreate(Bundle savedInstanceState) {
            this.uA = this;
	    showing = true;
            super.onCreate(savedInstanceState);
	    INSTANCE = this;
            setContentView(R.layout.users);
            viewUsuarios = (ListView) findViewById(R.id.usersListview);
	    agregar = (Button) findViewById(R.id.buttonAgregar);
            loadUsers();
            populateView();
            addActionListeners();
            /*
             LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>();
             for (UserEntity uE : users) {
             map.put(uE.getNickname(), uE.getUserId());
             }
             */
	}

	private void addActionListeners() {
		viewUsuarios.setOnItemClickListener(new ClickListener(this));
		bindDialog();
	}

	private void bindDialog(){
		final AgregarListener listenerOK = new AgregarListener();
		agregar.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				AlertDialog dialog = DialogFactory.createInputDialog(UsersActivity.this, "Agregar a ", listenerOK,listenerOK.getEdit());
				listenerOK.setDialog(dialog);
			}
		});
	}

	private void populateView() {
		viewUsuarios= (ListView) findViewById(R.id.usersListview);
		//Drawable img = getResources().getDrawable(R.drawable.img1);
		rowItems = new ArrayList<RowItem>();
		if (users == null)
			return;
		for (int i = 0; i < users.size(); i++) {
			UserEntity aux = users.get(i);
			Drawable img = new BitmapDrawable(getResources(), aux.getAvatar());
			String status = aux.getStatus() == DatabaseHelper.CONNECTED  ? "online" : "offline";
			RowItem item = new RowItem(aux.getNickname(), img, aux.getUsername(), status, aux.getUserId());
			rowItems.add(item);
		}
		adapter = new UserAdapter(this, rowItems);
		viewUsuarios.setAdapter(adapter);
		registerForContextMenu(this.viewUsuarios);

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.layout.user_options , menu);
	}


	@Override
	public boolean onContextItemSelected(MenuItem item) 
	{
		final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		final DatabaseHelper dbH = new DatabaseHelper(this);
		dbH.open();
		List<UserEntity> list = dbH.fetchAllUsers();
		list.remove(dbH.fetchUser(dbH.USERID_ME));
		UserEntity uE = list.get(info.position);
		switch(item.getItemId()){
			case R.id.conversation:
				List<UserEntity> users = new ArrayList<UserEntity>();
				users.add(uE);
				users.add(dbH.fetchUser(dbH.USERID_ME));
				ConversationEntity cE = dbH.fetchConversation(new ConversationEntity(users));
				if (cE == null) {
					cE = dbH.createConversation(users, Calendar.getInstance());
					// Mandar a server (?)
				}
				final int conversationID = cE.getConversationId();
				final View vista = this.viewUsuarios.getChildAt(info.position);
				dbH.close();
				vista.animate().setDuration(500).alpha(0).withEndAction(new Runnable() {
					@Override
					public void run() {
						vista.setAlpha(1);
						Intent intent = new Intent(uA, ConversationActivity.class);
						//Bundle bundle = new Bundle();
						//bundle.putInt("conversationId", conversationID);
						intent.putExtra("conversationId", conversationID);
						startActivity(intent);
					}
				});
				break;
			case R.id.profile:
				dbH.close();
				Intent intent = new Intent(this, ProfileActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("userId", uE.getUserId());
				intent.putExtra("whatsapp.client.ProfileActivity.data", bundle);
				startActivity(intent);
				break;

			case R.id.delete:
				DialogFactory.createOkCancelDialog(UsersActivity.this,
					"Eliminar conversación", "¿Está seguro que desea eliminar la conversación?",
					new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface arg0, int id) {
							//String itemSelected = ((RowItem) adapter.getItem(idx)).getUserName();
							RowItem rowItem = (RowItem) adapter.getItem(info.position);
							if (dbH.fetchConversation(rowItem.getUserName()) != null)
								dbH.deleteConversation(dbH.fetchConversation(rowItem.getUserName()));
							dbH.deleteUser(dbH.fetchUser(rowItem.getUserName()));
							dbH.close();
							rowItems.remove(rowItem);
							adapter.notifyDataSetChanged();
							//adapter.remove(itemSelected);
							//adapter.remove(rowItem);
						}
					});
		}
		return true;
	}

	private void loadUsers() {
            DatabaseHelper dbH = new DatabaseHelper(this);
            dbH.open();
            users = dbH.fetchAllUsers();
            users.remove(dbH.fetchUser(DatabaseHelper.USERID_ME));
            dbH.close();

	}

	private void mostrarErroneo(String usuarioNoEncontroado){
		System.out.println(usuarioNoEncontroado);
		DialogFactory.createAlertDialog(this, "Usuario no encontrado", "El nombre de usuario: " +usuarioNoEncontroado + " no esta registrado, verifique e intente nuevamente");
		//TODO : mostrar en un dialogo
		//ERROR , [usuario] inexistente

	}

	private void agregar(){
		DatabaseHelper dbh = new DatabaseHelper(this);
		//TODO: Sacar esto
		dbh.open();
		UserEntity fetchUser = dbh.fetchUser(ueAux.getUsername());
		if (fetchUser == null){
			UserEntity ue = dbh.createUser(0, ueAux.getUsername(), ueAux.getNickname(), ueAux.getStatus());
			users.add(ue);
		}
	}

	public void onReceiveResult(int resultCode, Bundle resultData) {
		if (resultCode == 0) {
			int type = resultData.getInt("type");
			JSONObject data;
			String username = null;
			try {
				data = new JSONObject(resultData.getString("data"));
				switch(type){
					case 0:
						data.get("error");
						JSONArray error = (JSONArray) data.get("error");
						if (error.length()!=0){
							mostrarErroneo((String)error.get(0));
							return;
						}
						JSONArray success = (JSONArray) data.get("success");
						
						ueAux = new UserEntity(username = (String)success.get(0));
						rowItemAux = new RowItem(username);
						rowItems.add(rowItemAux);
						fetchInfo(username);
						break;
					case 1:
						updateProfile(data);
						updateRow(rowItemAux);
						agregar();

				}
			} catch (JSONException ex) {
				Logger.getLogger(RegisterActivity.class.getName()).log(Level.SEVERE, null, ex);
			}
		}else if (resultCode == 1){
			DialogFactory.createAlertDialog(UsersActivity.this, "Error de conexion", "Hubo un problema en la conexion, pruebe de nuevo mas tarde");
		}
	}

	public void updateRow(RowItem row){
		updateRow(row,null);
	}

	public void updateRow(RowItem row, UserEntity ue){
		if (ue == null)
			ue = ueAux;
		String statusShow = ue.getStatus() == DatabaseHelper.CONNECTED  ? "online" : "offline";
		row.setNickName(ue.getNickname());
		row.setStatus(statusShow);
		if (ue.getAvatar()!=null){
			Drawable d = new BitmapDrawable(getResources(), ueAux.getAvatar());
			rowItemAux.setAvatar(d);
		}
		adapter.notifyDataSetChanged();
		
	}

	public void updateProfile(JSONObject data){
		try{
			String username = data.getString("username");
			String nickname = data.getString("nickname");
			boolean connected = data.getBoolean("online");
			long lastActivity = data.getLong("last_activity");
			JSONObject status = data.getJSONObject("status");
			long lastStatus = status.getLong("time");
			String statusText = status.getString("text");
			ueAux.setNickname(nickname);
			ueAux.setStatus(connected ? DatabaseHelper.CONNECTED : DatabaseHelper.DISCONNECTED);
		}catch(JSONException ex){}
	}

	public void updateView(String username){
		int idx = getIdxUser(username);
		DatabaseHelper dbh = new DatabaseHelper(this);
		dbh.open();
		UserEntity ue = dbh.fetchUser(username);
		if (idx != -1){
			RowItem row = rowItems.get(idx);
			updateRow(row,ue);
		}
		dbh.close();
	}

	private class ClickListener implements AdapterView.OnItemClickListener {

		private Context context;

		public ClickListener(Context context) {
			this.context = context;
		}

		public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
			DatabaseHelper dbH = new DatabaseHelper(this.context);
			dbH.open();
			List<UserEntity> list = dbH.fetchAllUsers();
			List<UserEntity> users = new ArrayList<UserEntity>();
			//el primer usuario es Me
			users.add(list.get(position+1));
			users.add(dbH.fetchUser(dbH.USERID_ME));
			ConversationEntity cE = dbH.fetchConversation(new ConversationEntity(users));
			if (cE == null) {
				cE = dbH.createConversation(users, Calendar.getInstance());
				// Mandar a server (?)
			}
			final int conversationID = cE.getConversationId();
			dbH.close();
			view.animate().setDuration(500).alpha(0).withEndAction(new Runnable() {
				@Override
				public void run() {
					view.setAlpha(1);
					Intent intent = new Intent(context, ConversationActivity.class);
					//Bundle bundle = new Bundle();
					//bundle.putInt("conversationId", conversationID);
					intent.putExtra("conversationId", conversationID);
					startActivity(intent);
				}
			});
		}
	}
	private class OptionListener implements AdapterView.OnItemLongClickListener{

		public boolean onItemLongClick(final AdapterView<?> adapterView, final View arg1,final int idx, long arg3) {
			DialogFactory.createOkCancelDialog(UsersActivity.this, 
					"Eliminar Usuario", "¿Está seguro que desea eliminar el usuario seleccionado?",
					new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface arg0, int id) {
					//String itemSelected = ((RowItem) adapter.getItem(idx)).getUserName();
					RowItem rowItem = (RowItem) adapter.getItem(idx);
					rowItems.remove(rowItem);
					//adapter.remove(itemSelected);
					//adapter.remove(rowItem);
				}
			});
			return true;
		}
	
	}

	private void fetchInfo(String username){
		fetchAvatar(username);
		fetchProfile(username);
	}

	private void fetchProfile(String username){
		send("user/"+username+"/profile",null,1,1);
	}

	//TODO: sacar de aca
	private void fetchAvatar(final String username){
		String ip = ConfigurationManager.getInstance().getString(UsersActivity.this, ConfigurationManager.SAVED_IP);
		String port = ConfigurationManager.getInstance().getString(UsersActivity.this, ConfigurationManager.SAVED_PORT);
		String access_token = ConfigurationManager.getInstance().getString(UsersActivity.this, ConfigurationManager.ACCESS_TOKEN);
		String URI = ip + ":" + port + "/user/" + username +"/avatar?access_token="+access_token;
		//send("user/"+username+"/avatar",null,2,1);
		ImageRequest imreq = new ImageRequest(URI, new Response.Listener<Bitmap>() {
			public void onResponse(Bitmap t) {
				ueAux.setAvatar(t);
				Drawable d = new BitmapDrawable(getResources(), ueAux.getAvatar());
				rowItemAux.setAvatar(d);
				adapter.notifyDataSetChanged();
			}
		}, 0,0, null, null);
		AppController.getInstance().addToRequestQueue(imreq);
	}

	public int getIdxUser(String username){
		for (int i = 0 ; i < rowItems.size() ; i++){
			if (rowItems.get(i).getUserName().equals(username))
				return i;
		}
		return -1;
	}


	//TODO: arreglar esto , el tipo de request no deberia ser un parametro ,tendria q usar polimorfismo
	private void send(String endpoint,HashMap<String,String>params,int type,int request){
		Class<?> clase = null;
		switch(request){
			case 0:
				clase = POSTService.class;
				break;
			case 1:
				clase = GETService.class;
				break;
		}
		
		Bundle b = createCommonBundle(params,type);
		ServerResultReceiver receiver = new ServerResultReceiver(new Handler());
		Intent intent = new Intent(UsersActivity.this, clase);
		receiver.setListener(UsersActivity.this);
		
		String ip = ConfigurationManager.getInstance().getString(UsersActivity.this, ConfigurationManager.SAVED_IP);
		String port = ConfigurationManager.getInstance().getString(UsersActivity.this, ConfigurationManager.SAVED_PORT);
		String URI = ip + ":" + port + "/" + endpoint;
		b.putString("URI", URI);
		
		intent.putExtra("rec", receiver);
		intent.putExtra("info", b);
		startService(intent);
	}
	
	private Bundle createCommonBundle(HashMap<String,String> params,int type){
		Bundle bundle = new Bundle();
		String access_token = ConfigurationManager.getInstance().getString(UsersActivity.this, ConfigurationManager.ACCESS_TOKEN);
		if (params == null)
			params = new HashMap<String, String>();
		//params.put("users[]", contact);
		params.put("access_token",access_token);
		bundle.putSerializable("params", params);
		bundle.putInt("type", type);
		return bundle;
	}
	private class AgregarListener implements OnClickListener{
		private EditText edit;
		private AlertDialog dialog;
		private String contact;
		
		AgregarListener(){
			this.edit = new EditText(UsersActivity.this);
		}
		
		public void onClick(DialogInterface arg0, int arg1) {
			contact = edit.getText().toString();
			dialog.dismiss();
			HashMap<String,String> params = new HashMap<String, String>();
			params.put("users[]", contact);
			send("contacts",params,0,0);
		}
		
		
		
		/*
		private void send(){
			Bundle bundle = new Bundle();
			HashMap<String, String> params = new HashMap<String, String>();
			String access_token = ConfigurationManager.getInstance().getString(UsersActivity.this, ConfigurationManager.ACCESS_TOKEN);
			params.put("users[]", contact);
			params.put("access_token",access_token);
			bundle.putSerializable("params", params);
			bundle.putInt("type", 0);
			String ip = ConfigurationManager.getInstance().getString(UsersActivity.this, ConfigurationManager.SAVED_IP);
			String port = ConfigurationManager.getInstance().getString(UsersActivity.this, ConfigurationManager.SAVED_PORT);
			//String URI = ip + ":" + port + "/contacts?access_token="+access_token;
			String URI = ip + ":" + port + "/contacts";
			bundle.putString("URI", URI);
			bundle.putSerializable("params", params);
			Intent intent = new Intent(UsersActivity.this, POSTService.class);
			ServerResultReceiver receiver = new ServerResultReceiver(new Handler());
			receiver.setListener(UsersActivity.this);
			//TODO: Se le tiene q asociar un listener para q vuelva ?
			intent.putExtra("rec", receiver);
			intent.putExtra("info", bundle);
			startService(intent);
		}
		*/

		private void setInput(EditText edit) {
			this.edit = edit;
		}

		private void setDialog(AlertDialog dialog) {
			this.dialog = dialog;
		}

		private EditText getEdit() {
			if (this.edit.getParent() != null)
				return this.edit = new EditText(UsersActivity.this);
			else
				return this.edit;
		}
	}

	public static UsersActivity getInstance() {
		return INSTANCE;
	}

	@Override
	protected void onResume() {
		super.onResume();
		showing = true;
		loadUsers();
		populateView();
	}


	@Override
	protected void onDestroy() {
		showing = false;
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause(); //To change body of generated methods, choose Tools | Templates.
		showing = false;
	}

	public boolean isShowing(){
		return showing;
	}
}
