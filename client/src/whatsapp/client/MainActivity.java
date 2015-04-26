package whatsapp.client;

import model.ServerResultReceiver;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import utils.ConversationEntity;
import utils.DatabaseHelper;
import utils.UserEntity;

public class MainActivity extends Activity implements ServerResultReceiver.Listener{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DatabaseHelper dbH = new DatabaseHelper(this);
        dbH.open();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(!prefs.getBoolean("firstTime", false)) {
            UserEntity uEMe = dbH.createUser(1554587629, "Me", DatabaseHelper.NORMAL);
            UserEntity uE = dbH.createUser(1511111111, "WhatsApp Info", DatabaseHelper.NORMAL);
            ConversationEntity cE = dbH.createConversation(uE);
            dbH.createMessage(cE, uE, null, null, "Bienvenido a Whatsapp", dbH.NOT_SEEN);
            dbH.createMessage(cE, uEMe, null, null, "Hola!!!", dbH.NOT_SEEN);
            dbH.createMessage(cE, uEMe, null, null, "Como estas?", dbH.NOT_SEEN);
            dbH.createMessage(cE, uE, null, null, "Bien, y vos?", dbH.NOT_SEEN);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }
        setContentView(R.layout.main);
        
        final ListView listview = (ListView) findViewById(R.id.mainListview);
        final ArrayList<String> list = new ArrayList<String>();
        
        for (ConversationEntity cE : dbH.fetchAllConversations())
        {
            list.add(cE.getUser(0).getName());
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
            List<ConversationEntity> list = dbH.fetchAllConversations();
            dbH.close();
            final int conversationID = list.get(position).getConversationId();
            view.animate().setDuration(500).alpha(0).withEndAction(new Runnable() {
                @Override
                public void run() {
                    view.setAlpha(1);
                    Intent intent = new Intent(context, ConversationActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("conversationId", conversationID);
                    intent.putExtra("whatsapp.client.MainActivity.data", bundle);
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
