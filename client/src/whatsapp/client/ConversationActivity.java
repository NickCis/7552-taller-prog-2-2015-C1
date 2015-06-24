/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package whatsapp.client;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
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
		lv = (ListView) findViewById(R.id.listView1);
		adapter = new DiscussArrayAdapter(getApplicationContext(), R.layout.listitem_discuss);
		loadMessages();
		populateView();
	}

	private void populateView(){
		editText1 = (EditText) findViewById(R.id.editText1);
		editText1.setOnKeyListener(new View.OnKeyListener() {
			public boolean onKey(View arg0, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
					String msg;
					adapter.add(new OneComment(true, msg = editText1.getText().toString()));
					send(msg);
					editText1.setText("");
					return true;
				}
				return false;
			}
		});
		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int idx, long arg3) {
				OneComment item = adapter.getItem(idx);
				if (!item.sent){
					send(item.getComment());
					DialogFactory.createProgressDialog(ConversationActivity.this, "Reenviando");
				}
				
				return true;
			}
		});

	}

	private void loadMessages(){
		lv.setAdapter(adapter);
		this.dbH = DatabaseHelper.getInstance(this);
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
		dbH.open();
		for (MessageEntity mE : listaMensajes) {
			OneComment c;
			adapter.add(c =new OneComment(mE.getUser().equals(dbH.getUserMe()), mE.getContent()));
			mE.setStatus(DatabaseHelper.SEEN);
		}
		adapter.notifyDataSetChanged();
                dbH.close();
	}

	/**
	 * Para actualizar cuando hubo un visto
	 */
	private void refreshItems(){
		dbH.open();
		String from = (String) getIntent().getExtras().get("id");
		if (from == null) {
			int id = (Integer)getIntent().getExtras().get("conversationId");
			this.cE = dbH.fetchConversation(new ConversationEntity(id));
		} else {
			this.cE = dbH.fetchConversation(from);
		}
		List<MessageEntity> listaMensajes=dbH.fetchMessages(this.cE);
		for (MessageEntity mE : listaMensajes) {
			OneComment c = adapter.getItem(mE.getContent());
			//if (mE.getStatus() == DatabaseHelper.SEEN)
			//	adapter.setSeen(c);
			//if (mE.getStatus() == DatabaseHelper.SENT)
			//	adapter.setSent(c);
			
		}
	}
		
		private void addItems(List<MessageEntity> listaMensajes) {
			dbH = DatabaseHelper.getInstance(this);
			dbH.open();
			
			for (MessageEntity mE : listaMensajes) {
				if (mE.getStatus() == DatabaseHelper.NOT_SEEN) {
					adapter.add(new OneComment(mE.getUser().equals(dbH.getUserMe()), mE.getContent()));
					mE.setStatus(DatabaseHelper.SEEN);
				}
			}
			dbH.close();
		}
		
		void send(String msg) {
			Bundle bundle = new Bundle();
			HashMap<String, String> params = new HashMap<String, String>();
			ServerResultReceiver receiver = new ServerResultReceiver(new Handler());
			receiver.setListener(this);
			String access_token = ConfigurationManager.getInstance().getString(this, ConfigurationManager.ACCESS_TOKEN);
			params.put("access_token", access_token);
			params.put("message", msg);
			bundle.putSerializable("params", params);
			String ip = ConfigurationManager.getInstance().getString(this, ConfigurationManager.SAVED_IP);
			String port = ConfigurationManager.getInstance().getString(this, ConfigurationManager.SAVED_PORT);
			//TODO: Manejar bien esto
			String to = this.cE.getUser(1).getUsername();
			String URI = ip + ":" + port + "/" + "user/" + to + "/messages";
			bundle.putString("URI", URI);
			bundle.putSerializable("params", params);
			Intent intent = new Intent(this, POSTService.class);
			//TODO: Se le tiene q asociar un listener para q vuelva ?
			intent.putExtra("info", bundle);
			intent.putExtra("rec", receiver);
			startService(intent);
		}
		
		public void onReceiveResult(int resultCode, Bundle resultData) {
			//TODO: La respuesta del servidor tiene que estar aca para que sea concordante con el resto
			DialogFactory.disposeDialog();
			
			if (resultCode == 0){
				HashMap h = (HashMap) resultData.getSerializable("params");
				String message = (String) h.get("message");
				//OneComment comment = adapter.getItem(message);
				//if (!comment.sent){
				//	comment.setSentStatus(true);
				//	adapter.setSent(comment);
				//	adapter.notifyDataSetChanged();
				//}
				dbH.open();
				dbH.createMessage(cE, dbH.getUserMe(), null, null, message, DatabaseHelper.SEEN);
				dbH.close();
			}else if (resultCode == 1){
				HashMap h = (HashMap) resultData.getSerializable("params");
				String message = (String) h.get("message");
				OneComment comment = adapter.getItem(message);
				comment.setSentStatus(false);
				adapter.setNotSent(comment);
				adapter.notifyDataSetChanged();
				
			}
			
			
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
		
		/**
		 * Agrega mensajes llegados del servidor
		 * @param conversation , conversacion que estoy teniendo con ese usuario
		 */
		public void addMsgs(ConversationEntity conversation) {
			this.cE = conversation;
			dbH = DatabaseHelper.getInstance(this);
			dbH.open();
			addItems(dbH.fetchMessages(this.cE));
			dbH.close();
		}
		
		public static boolean isShowing() {
			return showing;
		}
		
		public static ConversationActivity getInstance() {
			return INSTANCE;
		}
		
		public void refresh(){
			loadMessages();
			populateView();
			//refreshItems();
		}
}
