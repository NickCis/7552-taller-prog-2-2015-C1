/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package whatsapp.client;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import model.ServerResultReceiver;

/**
 *
 * @author umm194
 */
public class ConversationActivity extends Activity implements ServerResultReceiver.Listener{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.conversation);
        
        final ListView listview = (ListView) findViewById(R.id.conversationListview);
        
        String[] values = new String[] { "Mensaje1", "Mensaje2", "etc", "etc"
        , "etc", "etc", "etc", "etc", "etc", "etc", "etc", "etc", "etc", "etc", "etc"};

        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < values.length; ++i) {
            list.add(values[i]);
        }

        final StableArrayAdapter adapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);
        
        listview.setOnItemClickListener(new ClickListener(this)); // Para seleccionar y copiar mensajes
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