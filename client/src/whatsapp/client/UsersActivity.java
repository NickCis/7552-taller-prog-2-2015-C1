package whatsapp.client;

import model.ServerResultReceiver;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import utils.ConversationEntity;
import utils.DatabaseHelper;
import utils.UserEntity;

public class UsersActivity extends Activity implements ServerResultReceiver.Listener{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DatabaseHelper dbH = new DatabaseHelper(this);
        dbH.open();
        setContentView(R.layout.users);
        
        final ListView listview = (ListView) findViewById(R.id.usersListview);
        
        final ArrayList<String> list = new ArrayList<String>();
        List<UserEntity> users = dbH.fetchAllUsers();
        users.remove(dbH.fetchUser(dbH.USERID_ME));
        for (UserEntity uE : users)
        {
            list.add(uE.getName());
        }
        dbH.close();
        final StableArrayAdapter adapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new ClickListener(this));
    }

    public void onReceiveResult(int resultCode, Bundle resultData) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    private class ClickListener implements AdapterView.OnItemClickListener
    {
        private Context context;
        
        public ClickListener (Context context)
        {
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
            if (cE == null)
            {
                cE = dbH.createConversation(users, Calendar.getInstance());
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

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
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
