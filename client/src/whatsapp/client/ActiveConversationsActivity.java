package whatsapp.client;

import model.ServerResultReceiver;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabWidget;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import utils.ConversationEntity;
import utils.DatabaseHelper;
import utils.UserEntity;

public class ActiveConversationsActivity extends Activity implements ServerResultReceiver.Listener {

	StableArrayAdapter adapter;
	ListView listview;
	private static boolean showing;
	private static ActiveConversationsActivity INSTANCE;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		INSTANCE = this;
		showing = true;
		DatabaseHelper dbH = new DatabaseHelper(this);
		dbH.open();
		/*
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
			UserEntity uEMe = dbH.createUser(1554587629, "Me", "Me", DatabaseHelper.NORMAL);
			UserEntity uE = dbH.createUser(1511111111, "WhatsApp Info", "WhatsApp Info", DatabaseHelper.NORMAL);
			dbH.createUser(1522222222, "Rodrigo", "Rodri", DatabaseHelper.NORMAL);
			dbH.createUser(1533333333, "Nico", "Nico", DatabaseHelper.NORMAL);
			ConversationEntity cE = dbH.createConversation(uE, Calendar.getInstance());
			dbH.createMessage(cE, uE, null, null, "Bienvenido a Whatsapp", dbH.NOT_SEEN);
			dbH.createMessage(cE, uEMe, null, null, "Hola!!!", dbH.NOT_SEEN);
			dbH.createMessage(cE, uEMe, null, null, "Como estas?", dbH.NOT_SEEN);
			dbH.createMessage(cE, uE, null, null, "Bien, y vos?", dbH.NOT_SEEN);
		*/
		setContentView(R.layout.active_conversations);

		listview = (ListView) findViewById(R.id.activeConversationsListview);

		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (ConversationEntity cE : dbH.fetchAllConversations()) {
			map.put(cE.getUser(0).getNickname(), cE.getConversationId());
		}
		dbH.close();
		adapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_1, map);
		listview.setAdapter(adapter);

		listview.setOnItemLongClickListener(new OptionListener());
		listview.setOnItemClickListener(new ClickListener(this));
	}

	public void onReceiveResult(int resultCode, Bundle resultData) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void informNuevo(String nuevo) {
		if (adapter.contains(nuevo)){
			int idx = adapter.getPosition(nuevo);
			TextView v = (TextView)listview.getChildAt(idx);
			v.setTextAppearance(this, R.style.textview_notificated);

			
		}else{
			DatabaseHelper dbH = new DatabaseHelper(this);
			dbH.open();
			ConversationEntity ce = dbH.fetchConversation(nuevo);
			adapter.add(ce.getUser(0).getNickname());
			adapter.addToMap(ce.getUser(0).getNickname(), ce.getUser(0).getUserId());

		}

	}

	private class ClickListener implements AdapterView.OnItemClickListener {

		private Context context;

		public ClickListener(Context context) {
			this.context = context;
		}

		public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
			StableArrayAdapter sAA = (StableArrayAdapter) parent.getAdapter();
			final int conversationID = (int) sAA.getItemId(position);
			view.animate().setDuration(500).alpha(0).withEndAction(new Runnable() {
				@Override
				public void run() {
					view.setAlpha(1);
					Intent intent = new Intent(context, ConversationActivity.class);
					Bundle bundle = new Bundle();
					intent.putExtra("conversationId", conversationID);
					//bundle.putInt("conversationId", conversationID);
					//intent.putExtra("whatsapp.client.ConversationActivity.data", bundle);
					startActivity(intent);
				}
			});
		}
	}

	private class OptionListener implements AdapterView.OnItemLongClickListener {

		public boolean onItemLongClick(AdapterView<?> adapterView, final View arg1, final int idx, long arg3) {
			DialogFactory.createOkCancelDialog(ActiveConversationsActivity.this,
				"Eliminar conversación", "¿Está seguro que desea eliminar la conversación?",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface arg0, int id) {
						//adapterView.get
						String itemSelected = adapter.getItem(idx);
						adapter.remove(itemSelected);
						adapter.notifyDataSetChanged();
					}
				});
			return true;
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
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause(); //To change body of generated methods, choose Tools | Templates.
		showing = false;
	}

	public static boolean isShowing() {
		return showing;
	}

	public static ActiveConversationsActivity getInstance() {
		return INSTANCE;
	}

	private class StableArrayAdapter extends ArrayAdapter<String> {

		HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

		public StableArrayAdapter(Context context, int textViewResourceId, HashMap<String, Integer> objects) {
			super(context, textViewResourceId, new ArrayList<String>(objects.keySet()));
			this.mIdMap = objects;
		}

		@Override
		public long getItemId(int position) {
			String item = getItem(position);
			return mIdMap.get(item);
		}

		public void addToMap(String key, Integer value){
			this.mIdMap.put(key, value);
		}

		@Override
		public void remove(String object) {
			/* 
			 * Esto es lo mas cercano que podemos hacer todavia pero no me cierra, porque deberia remover por ID y no por nickname
			 * Con nicknames repetidos podria fallar el hashing
			 */

			Integer conversationId = this.mIdMap.get(object);
			if (conversationId != null) {
				DatabaseHelper dbH = new DatabaseHelper(ActiveConversationsActivity.this);
				dbH.open();
				dbH.deleteConversation(dbH.fetchConversation(object));
				dbH.close();
				this.mIdMap.remove(object);
				super.remove(object);
			}
		}

		public boolean contains(String item){
			return adapter.getPosition(item) != -1;
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}
	}
}
