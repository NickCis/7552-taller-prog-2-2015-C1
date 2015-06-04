package whatsapp.client;

import model.ServerResultReceiver;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
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
import utils.UserAdapter;
import utils.UserEntity;

public class ActiveConversationsActivity extends Activity implements ServerResultReceiver.Listener {

	//private StableArrayAdapter adapter;
	private UserAdapter adapter;
	private ListView listview;
	private static boolean showing;
	private static ActiveConversationsActivity INSTANCE;
	private ArrayList<RowItem> rowItems;
	private List<ConversationEntity> conversations;
	private HashMap<String, Integer> map;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		INSTANCE = this;
		showing = true;
		loadConversations();
		setContentView(R.layout.active_conversations);
		populateView();
		bindListeners();
	}

	private void bindListeners(){
		listview.setOnItemLongClickListener(new OptionListener());
		listview.setOnItemClickListener(new ClickListener(this));
	}
	
	private void populateView() {
		
		listview = (ListView) findViewById(R.id.activeConversationsListview);
		//adapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_1, map);
		//listview.setAdapter(adapter);
		Drawable img = getResources().getDrawable(R.drawable.img1);
		rowItems = new ArrayList<RowItem>();
		if (conversations == null)
			return;
		for (int i = 0; i < conversations.size(); i++) {
			ConversationEntity c = conversations.get(i);
			UserEntity aux = c.getUser(0);

			RowItem item = new RowItem(aux.getNickname(), img, aux.getUsername(), "ultima conexion", aux.getUserId());
			rowItems.add(item);
		}
		adapter = new UserAdapter(this, rowItems);
		listview.setAdapter(adapter);
		registerForContextMenu(this.listview);

	}

	private void loadConversations(){
		DatabaseHelper dbH = new DatabaseHelper(this);
		dbH.open();
		UserEntity uEMe = null;
		if (dbH.fetchUser(DatabaseHelper.USERID_ME) == null)
			uEMe = dbH.createUser(1554587629, "Me", "Me", DatabaseHelper.NORMAL);
		map = new HashMap<String, Integer>();
		List<ConversationEntity> conversationsAux = dbH.fetchAllConversations();
		if (conversationsAux!= null){
			for (ConversationEntity cE : conversationsAux) {
				map.put(cE.getUser(1).getNickname(), cE.getConversationId());
			}
			
			conversations = (conversationsAux);
		}
		dbH.close();
		
	}

	public void onReceiveResult(int resultCode, Bundle resultData) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void informNuevo(String nuevo) {
		if (adapter.contains(nuevo)){
			int idx = adapter.getPosition(nuevo);
			//TODO:
			// CUANDO DEVUELVE ALGO DEVUELVE UN RELATIVE LAYOUT, VER COMO CAMBIAR ESO CUANDO ME LLEGUE UNA NOTIFIACION
			//TextView v = (TextView)listview.getChildAt(idx);
			//v.setTextAppearance(this, R.style.textview_notificated);

			
		}else{
			DatabaseHelper dbH = new DatabaseHelper(this);
			dbH.open();
			ConversationEntity ce = dbH.fetchConversation(nuevo);
			UserEntity aux = ce.getUser(1);
			//TODO: sacar
			Drawable img = getResources().getDrawable(R.drawable.img1);
			RowItem item = new RowItem(aux.getNickname(), img, aux.getUsername(), "ultima conexion", aux.getUserId());
			rowItems.add(item);
			adapter.notifyDataSetChanged();
			//adapter.addToMap(ce.getUser(1).getNickname(), ce.getUser(1).getUserId());
		}

	}

	private class ClickListener implements AdapterView.OnItemClickListener {

		private Context context;

		public ClickListener(Context context) {
			this.context = context;
		}

		public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
			//StableArrayAdapter sAA = (StableArrayAdapter) parent.getAdapter();
			RowItem item = adapter.getItem(position);
			final String conversationOf = item.getUserName();
			view.animate().setDuration(500).alpha(0).withEndAction(new Runnable() {
				@Override
				public void run() {
					view.setAlpha(1);
					Intent intent = new Intent(context, ConversationActivity.class);
					Bundle bundle = new Bundle();
					//intent.putExtra("conversationId", conversationID);
					intent.putExtra("id", conversationOf);
					//bundle.putInt("conversationId", conversationID);
					//intent.putExtra("whatsapp.client.ConversationActivity.data", bundle);
					startActivity(intent);
				}
			});
		}
	}

	private class OptionListener implements AdapterView.OnItemLongClickListener {

		public boolean onItemLongClick(final AdapterView<?> adapterView, final View arg1, final int idx, long arg3) {
			DialogFactory.createOkCancelDialog(ActiveConversationsActivity.this,
				"Eliminar conversación", "¿Está seguro que desea eliminar la conversación?",
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface arg0, int id) {
						//adapterView.get
						//UserEntity ue = (UserEntity) adapter.getItem(idx);
						RowItem item = adapter.getItem(idx);
						String itemSelected = item.getUserName();
						DatabaseHelper dbh = new DatabaseHelper(ActiveConversationsActivity.this);
						dbh.open();
						dbh.deleteConversation(dbh.fetchConversation(itemSelected));
						dbh.close();
						rowItems.remove(idx);
						//populateView();
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
		loadConversations();
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

	public static boolean isShowing() {
		return showing;
	}

	public static ActiveConversationsActivity getInstance() {
		return INSTANCE;
	}

/*
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
	*/
}
