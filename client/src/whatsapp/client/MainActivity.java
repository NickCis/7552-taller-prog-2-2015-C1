/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package whatsapp.client;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import model.NotificationService;

/**
 *
 * @author umm194
 */
public class MainActivity extends TabActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		startService(new Intent(this,NotificationService.class));

		TabHost tabHost = getTabHost();
		TabSpec activeConversationSpec = tabHost.newTabSpec("ActiveConversations");
		// setting Title and Icon for the Tab
		activeConversationSpec.setIndicator("", getResources().getDrawable(R.drawable.active_conversations_tab));
		Intent activeConversationsIntent = new Intent(this, ActiveConversationsActivity.class);
		activeConversationSpec.setContent(activeConversationsIntent);

		TabSpec usersSpec = tabHost.newTabSpec("Users");
		// setting Title and Icon for the Tab
		usersSpec.setIndicator("", getResources().getDrawable(R.drawable.users_tab));
		Intent usersIntent = new Intent(this, UsersActivity.class);
		usersSpec.setContent(usersIntent);

		tabHost.addTab(activeConversationSpec);
		tabHost.addTab(usersSpec);
	}
}
