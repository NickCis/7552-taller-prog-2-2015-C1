/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package whatsapp.client;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
        
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu items for use in the action bar
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.action_bar, menu);
            return super.onCreateOptionsMenu(menu);
        }
        
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle presses on the action bar items
            switch (item.getItemId()) {
                case R.id.action_search:
                    openSearch();
                    return true;
                case R.id.action_profile:
                    openProfile();
                    return true;
                case R.id.action_settings:
                    openSettings();
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
        
        public void openSearch()
        {
            
        }
        
        public void openProfile()
        {
            startActivity(new Intent(this, ProfileConfigurationActivity.class));
        }
        
        public void openSettings()
        {
            
        }
}
