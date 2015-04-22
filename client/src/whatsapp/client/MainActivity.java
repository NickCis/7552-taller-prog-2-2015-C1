package whatsapp.client;

import model.ServerResultReceiver;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import utils.Conversation;
import utils.ConversationEntity;
import utils.Message;
import utils.User;
import utils.UserEntity;

public class MainActivity extends Activity implements ServerResultReceiver.Listener{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(!prefs.getBoolean("firstTime", false)) {
            User user = new User(this);
            UserEntity uE = user.createUser(1511111111, "WhatsApp Info", User.NORMAL);
            Conversation conversation = new Conversation(this);
            ConversationEntity cE = conversation.createConversation(uE);
            Message message = new Message(this);
            message.createMessage(cE, uE, null, cE.getLast_message_time(), "Bienvenido a Whatsapp", Message.NOT_SEEN);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }
        setContentView(R.layout.main);
        
        final ListView listview = (ListView) findViewById(R.id.mainListview);
        final ArrayList<String> list = new ArrayList<String>();
        
        Conversation conversation = new Conversation(this);
        for (ConversationEntity cE : conversation.fetchAllConversations())
        {
            list.add(cE.getUser(0).getName());
        }
        
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
            Conversation conversation = new Conversation(this.context);
            List<ConversationEntity> list = conversation.fetchAllConversations();
            final int conversationID = list.get(position).getConversationId();
            view.animate().setDuration(2000).alpha(0).withEndAction(new Runnable() {
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
