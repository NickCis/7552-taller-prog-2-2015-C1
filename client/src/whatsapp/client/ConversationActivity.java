/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package whatsapp.client;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import model.POSTService;
import model.ServerResultReceiver;
import utils.ConfigurationManager;
import utils.ConversationEntity;
import utils.DatabaseHelper;
import utils.MessageEntity;
import utils.UserEntity;

/**
 * Activity de una conversacion entre clientes
 *
 * @author rburdet
 */
public class ConversationActivity extends Activity implements ServerResultReceiver.Listener {

	private static ConversationActivity INSTANCE;
	private DiscussArrayAdapter adapter;
	private ListView lv;
	private EditText editText1;
	private DatabaseHelper dbH;
	private ConversationEntity cE;
	private static boolean showing;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		INSTANCE = this;
		showing = true;
		setContentView(R.layout.activity_discuss);
		this.dbH = new DatabaseHelper(this);
		//TODO: esto tendria que venir del server o de almacenamiento interno ni bien entro
		lv = (ListView) findViewById(R.id.listView1);
		adapter = new DiscussArrayAdapter(getApplicationContext(), R.layout.listitem_discuss);
		lv.setAdapter(adapter);
		editText1 = (EditText) findViewById(R.id.editText1);
		editText1.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View arg0, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					dbH.open();
					dbH.createMessage(cE, dbH.fetchUser(DatabaseHelper.USERID_ME), null, null, editText1.getText().toString(), DatabaseHelper.NOT_RECIEVED);
					dbH.close();
					String msg;
					adapter.add(new OneComment(true, msg = editText1.getText().toString()));
					send(msg);
					editText1.setText("");
					return true;
				}
				return false;
			}
		});

		DatabaseHelper dbH = new DatabaseHelper(this);
		dbH.open();
		String from = (String) getIntent().getExtras().get("id");
		if (from == null) {
			int id = (Integer)getIntent().getExtras().get("conversationId");
			this.cE = dbH.fetchConversation(new ConversationEntity(id));
		} else {
			this.cE = dbH.fetchConversation(from);
		}
		addAllItems(dbH.fetchMessages(this.cE));
		dbH.close();
	}

	private void addAllItems(List<MessageEntity> listaMensajes) {
		DatabaseHelper dbH = new DatabaseHelper(this);
		dbH.open();
		UserEntity uEMe = dbH.fetchUser(DatabaseHelper.USERID_ME);

		for (MessageEntity mE : listaMensajes) {
			adapter.add(new OneComment(mE.getUser().equals(uEMe), mE.getContent()));
			mE.setStatus(DatabaseHelper.SEEN);
		}
	}

	private void addItems(List<MessageEntity> listaMensajes) {
		DatabaseHelper dbH = new DatabaseHelper(this);
		dbH.open();
		UserEntity uEMe = dbH.fetchUser(DatabaseHelper.USERID_ME);

		for (MessageEntity mE : listaMensajes) {
			if (mE.getStatus() == DatabaseHelper.NOT_SEEN) {
				adapter.add(new OneComment(mE.getUser().equals(uEMe), mE.getContent()));
				mE.setStatus(DatabaseHelper.SEEN);
			}
		}
	}

	void send(String msg) {
		Bundle bundle = new Bundle();
		HashMap<String, String> params = new HashMap<String, String>();
		String access_token = ConfigurationManager.getInstance().getString(this, ConfigurationManager.ACCESS_TOKEN);
		params.put("access_token", access_token);
		params.put("message", msg);
		bundle.putSerializable("params", params);
		String ip = ConfigurationManager.getInstance().getString(this, ConfigurationManager.SAVED_IP);
		String port = ConfigurationManager.getInstance().getString(this, ConfigurationManager.SAVED_PORT);
		//TODO: Manejar bien esto
		String to = this.cE.getUser(0).getUsername();
		String URI = ip + ":" + port + "/" + "user/" + to + "/messages";
		bundle.putString("URI", URI);
		bundle.putSerializable("params", params);
		Intent intent = new Intent(this, POSTService.class);
		//TODO: Se le tiene q asociar un listener para q vuelva ? 
		intent.putExtra("info", bundle);
		startService(intent);
	}

	public void onReceiveResult(int resultCode, Bundle resultData) {
		//TODO: La respuesta del servidor tiene que estar aca para que sea concordante con el resto 
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	protected void onResume() {
		super.onResume();
		showing = true;
	}

	@Override
	protected void onDestroy() {
		showing = false;
		Log.d("ConversationActivity", "DESTRUYENDO ACTIVITY****************************");
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause(); //To change body of generated methods, choose Tools | Templates.
		showing = false;
	}

	/**
	 * Agrego mensajes a la vista de mensajes 
	 * @param conversation 
	 */
	public void addMsgs(String msg){
		adapter.add(new OneComment(false,msg ));
	}

	public void addMsgs(ConversationEntity conversation) {
		this.cE = conversation;
		dbH = new DatabaseHelper(this);
		dbH.open();
	    //dbH.fetchConversation(cE)
		//this.cE = dbH.fetchConversation(new ConversationEntity(getIntent().getExtras().getBundle("whatsapp.client.ConversationActivity.data").getInt("conversationId")));
		addItems(dbH.fetchMessages(this.cE));

            //SharedPreferences data = getSharedPreferences("pepe", 0);
		//Set<String> set = data.getStringSet("messages", new LinkedHashSet<String>());
		//if (set.size() != 0) {
		//        for (String set1 : set) {
		//                adapter.add(new OneComment(false, set1));
		//        }
		//}
		//set.clear();
	}

	public static boolean isShowing() {
		return showing;
	}

	public static ConversationActivity getInstance() {
		return INSTANCE;
	}
}
