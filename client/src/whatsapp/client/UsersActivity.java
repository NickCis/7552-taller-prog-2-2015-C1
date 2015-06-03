package whatsapp.client;

import model.ServerResultReceiver;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.POSTService;
import org.json.JSONException;
import org.json.JSONObject;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
            this.uA = this;
            super.onCreate(savedInstanceState);
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
		//TODO: Integrar con lo de mati
		//viewUsuarios.setOnItemLongClickListener(new OptionListener());
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
		Drawable img = getResources().getDrawable(R.drawable.img1);
		rowItems = new ArrayList<RowItem>();
		if (users == null)
			return;
		for (int i = 0; i < users.size(); i++) {
			UserEntity aux = users.get(i);
			RowItem item = new RowItem(aux.getNickname(), img, aux.getUsername(), "ultima conexion", aux.getUserId());
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
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		DatabaseHelper dbH = new DatabaseHelper(this);
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
						Bundle bundle = new Bundle();
						bundle.putInt("conversationId", conversationID);
						intent.putExtra("whatsapp.client.ConversationActivity.data", bundle);
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
		}
		return true;
	}

	private void loadUsers() {
		DatabaseHelper dbH = new DatabaseHelper(this);
		dbH.open();

		users = dbH.fetchAllUsers();
		//users.remove(dbH.fetchUser(dbH.USERID_ME));
		dbH.close();

	}

	public void onReceiveResult(int resultCode, Bundle resultData) {
		if (resultCode == 0) {
			try {
				JSONObject data = new JSONObject(resultData.getString("data"));
					Bundle bundle = new Bundle();
					HashMap<String, String> params = new HashMap<String, String>();
			
				/*if (!data.contains("access_token")) {
				 Bundle bundle = new Bundle();
				 bundle.putString("username", user);
				 bundle.putString("password", pass);
				 URI = getIP() + ":" + getPort() + "/" + "auth";
				 bundle.putString("URI", URI);
				 //TODO: esto es auth
				 InfoDialog.createProgressDialog(this, "Registrando... por favor espere");
				 startService(createCallingIntent(bundle));
				 } else{
				 //Tengo access token, vengo de auth -> tengo q ir a conversaciones y guardar token
				 LoginActivity.storeAcessToken(this,data);
				
				 }*/
			} catch (JSONException ex) {
				Logger.getLogger(RegisterActivity.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
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
			list.remove(dbH.fetchUser(dbH.USERID_ME));
			List<UserEntity> users = new ArrayList<UserEntity>();
			users.add(list.get(position));
			users.add(dbH.fetchUser(dbH.USERID_ME));
			//TODO : ESTO ANDA BIEN ? 
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
					Bundle bundle = new Bundle();
					bundle.putInt("conversationId", conversationID);
					intent.putExtra("whatsapp.client.ConversationActivity.data", bundle);
					startActivity(intent);
				}
			});
		}
	}
	private class OptionListener implements AdapterView.OnItemLongClickListener{

		public boolean onItemLongClick(final AdapterView<?> adapterView, final View arg1,final int idx, long arg3) {
			DialogFactory.createOkCancelDialog(UsersActivity.this, 
					"Eliminar conversación", "¿Está seguro que desea eliminar la conversación?",
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

	private class AgregarListener implements OnClickListener{
		private String contact;
		private EditText edit;
		private AlertDialog dialog;

		AgregarListener(){
			this.edit = new EditText(UsersActivity.this);
		}

		public void onClick(DialogInterface arg0, int arg1) {
			this.contact = edit.getText().toString();
			dialog.dismiss();
			Logger.getLogger(UsersActivity.class.getName()).log(Level.INFO, "ENTRE AL SEND");
			System.out.println("entreeeeefsdfasdfsadf");
			send();

		}

		private void send(){
			Bundle bundle = new Bundle();
			HashMap<String, String> params = new HashMap<String, String>();
			String access_token = ConfigurationManager.getInstance().getString(UsersActivity.this, ConfigurationManager.ACCESS_TOKEN);
			params.put("users[]", contact);
			params.put("access_token",access_token);
			bundle.putSerializable("params", params);
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
	

}
