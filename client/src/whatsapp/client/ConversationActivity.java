/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package whatsapp.client;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import de.svenjacobs.loremipsum.LoremIpsum;
import java.util.List;
import java.util.Random;
import model.ServerResultReceiver;
import utils.ConversationEntity;
import utils.DatabaseHelper;
import utils.MessageEntity;
import utils.UserEntity;

/**
 *
 * @author umm194
 */
public class ConversationActivity extends Activity implements ServerResultReceiver.Listener{
    private DiscussArrayAdapter adapter;
    private ListView lv;
    private LoremIpsum ipsum;
    private EditText editText1;
    private static Random random;
    private DatabaseHelper dbH;
    private ConversationEntity cE;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discuss);
        this.dbH = new DatabaseHelper(this);
        random = new Random();
        //TODO: esto tendria que venir del server o de almacenamiento interno ni bien entro
        ipsum = new LoremIpsum();
        lv = (ListView) findViewById(R.id.listView1);
        adapter = new DiscussArrayAdapter(getApplicationContext(), R.layout.listitem_discuss);
        lv.setAdapter(adapter);
        editText1 = (EditText) findViewById(R.id.editText1);
        editText1.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View arg0, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    dbH.open();
                    dbH.createMessage(cE, dbH.fetchUser(DatabaseHelper.USERID_ME), null, null, editText1.getText().toString(), DatabaseHelper.NOT_RECIEVED);
                    adapter.add(new OneComment(true, editText1.getText().toString()));
                    editText1.setText("");
                    return true;
                }
                return false;
            }
        });
        
        DatabaseHelper dbH = new DatabaseHelper(this);
        dbH.open();
        this.cE = dbH.fetchConversation(new ConversationEntity(getIntent().getExtras().getBundle("whatsapp.client.MainActivity.data").getInt("conversationId")));
        addItems(dbH.fetchMessages(this.cE));
        dbH.close();
    }

    //TODO: MOCK, tendria q salir del servidor tmb
    private void addItems(List<MessageEntity> listaMensajes)
    {
        DatabaseHelper dbH = new DatabaseHelper(this);
        dbH.open();
        UserEntity uEMe = dbH.fetchUser(DatabaseHelper.USERID_ME);

        for (MessageEntity mE : listaMensajes)
        {
            adapter.add(new OneComment(mE.getUser().equals(uEMe), mE.getContent()));
        }
    }

    public void onReceiveResult(int resultCode, Bundle resultData) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}