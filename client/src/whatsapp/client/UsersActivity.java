package whatsapp.client;

import model.ServerResultReceiver;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import utils.ConversationEntity;
import utils.DatabaseHelper;
import utils.UserAdapter;
import utils.UserEntity;

public class UsersActivity extends Activity implements ServerResultReceiver.Listener {

	ListView mylistview;
	private ArrayList<RowItem> rowItems;
	List<UserEntity> users;
	ListView viewUsuarios;
        UsersActivity uA;

	@Override
	public void onCreate(Bundle savedInstanceState) {
            this.uA = this;
            super.onCreate(savedInstanceState);
            setContentView(R.layout.users);
            viewUsuarios = (ListView) findViewById(R.id.usersListview);
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
	}

	private void populateView() 
        {
            //VOY A TENER QUE USAR UN BITMAP, 
            //TODO: Cambiar esto cuando este implementado el enviador de imagenes del servidor
            Drawable img = getResources().getDrawable(R.drawable.img1);
            rowItems = new ArrayList<RowItem>();
            for (int i = 0; i < users.size(); i++) {
                UserEntity aux = users.get(i);
                RowItem item = new RowItem(aux.getNickname(), img, aux.getUsername(), "ultima conexion", aux.getUserId());
                rowItems.add(item);
            }
            //final StableArrayAdapter adapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_1, map);
            final UserAdapter adapter = new UserAdapter(this, rowItems);
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
        users.remove(dbH.fetchUser(dbH.USERID_ME));
        dbH.close();

    }

    public void onReceiveResult(int resultCode, Bundle resultData) {
        throw new UnsupportedOperationException("Not supported yet.");
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

    private class StableArrayAdapter extends ArrayAdapter<String> 
    {
        LinkedHashMap<String, Integer> mIdMap = new LinkedHashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId, LinkedHashMap<String, Integer> objects) {
                super(context, textViewResourceId, new ArrayList<String>(objects.keySet()));
                this.mIdMap = objects;
        }

        @Override
        public long getItemId(int position) {
                String item = getItem(position);
                return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
                return true;
        }
    }
}
