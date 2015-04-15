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
import java.util.HashMap;
import java.util.List;

public class MainActivity extends Activity implements ServerResultReceiver.Listener{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        final ListView listview = (ListView) findViewById(R.id.mainListview);
        
        String[] values = new String[] { "Rodri", "Nico", "TuVieja",
            "Maty", "Se", "Los", "Coje", "A",
            "Todos", "P.D.", "Rodri", "La", "Tiene", "Chiquitita"};

        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < values.length; ++i) {
            list.add(values[i]);
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
            final String item = (String) parent.getItemAtPosition(position);
            view.animate().setDuration(2000).alpha(0).withEndAction(new Runnable() {
                @Override
                public void run() {
                    view.setAlpha(1);
                    Intent intent = new Intent(context, ConversationActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("name", item);
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
