package whatsapp.client;

import model.ServerResultReceiver;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import utils.DatabaseHelper;
import utils.UserEntity;

public class ProfileActivity extends Activity implements ServerResultReceiver.Listener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.profile);
            DatabaseHelper dbH = new DatabaseHelper(this);
            dbH.open();
            UserEntity uE = dbH.fetchUser(getIntent().getExtras().getBundle("whatsapp.client.ProfileActivity.data").getInt("userId"));
            dbH.close();
            TextView nickName = (TextView) findViewById(R.id.userProfileInformation_NickName);
            nickName.setText(uE.getNickname());
            TextView lastconn = (TextView) findViewById(R.id.userProfileInformation_LastConnection);
            lastconn.setText("24/05/2015");
            this.setTitle(uE.getNickname());
	}

    public void onReceiveResult(int resultCode, Bundle resultData) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
