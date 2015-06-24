package whatsapp.client;

import model.ServerResultReceiver;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import utils.DatabaseHelper;
import utils.UserEntity;

public class ProfileActivity extends Activity implements ServerResultReceiver.Listener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.profile);
            DatabaseHelper dbH = DatabaseHelper.getInstance(this);
            dbH.open();
            UserEntity uE = dbH.fetchUser(getIntent().getExtras().getBundle("whatsapp.client.ProfileActivity.data").getInt("userId"));
            dbH.close();
            TextView nickName = (TextView) findViewById(R.id.userProfileInformation_NickName);
            nickName.setText(uE.getNickname());
            if (uE.getCheckin() != null)
            {
                TextView lastconn = (TextView) findViewById(R.id.userProfileInformation_LastConnection);
                lastconn.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS").format(uE.getCheckin().getTime()));
            }
            
            if (uE.getAvatar() != null)
            {
                ImageView imageView = (ImageView) findViewById(R.id.userProfileAvatar);
                imageView.setImageBitmap(uE.getAvatar());
            }
            TextView statusMessage = (TextView) findViewById(R.id.userProfileInformation_StatusMessage);
            statusMessage.setText(uE.getStatusMessage());
            TextView status = (TextView) findViewById(R.id.userProfileInformation_Status);
            String estado = "";
            if (uE.getStatus() == DatabaseHelper.STATUS_ONLINE)
            {
                status.setText(DatabaseHelper.STATUS_TEXT_ONLINE);
            }
            else if (uE.getStatus() == DatabaseHelper.STATUS_OFFLINE)
            {
                status.setText(DatabaseHelper.STATUS_TEXT_OFFLINE);
            }
            else
            {
                status.setText(DatabaseHelper.STATUS_TEXT_DO_NOT_DISTURB);
            }
            this.setTitle(uE.getNickname());
	}

    public void onReceiveResult(int resultCode, Bundle resultData) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
