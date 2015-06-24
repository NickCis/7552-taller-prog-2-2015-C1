/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package whatsapp.client;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;
import java.util.HashMap;
import model.NotificationService;
import model.POSTService;
import model.ServerResultReceiver;
import utils.ConfigurationManager;
import utils.DatabaseHelper;

/**
 *
 * @author umm194
 */
public class MainActivity extends TabActivity implements ServerResultReceiver.Listener{
	double longitude;
	double latitude;
	
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
			case R.id.action_checkin:
				checkin();
			case R.id.action_broadcast:
				doBroadCast();
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

	private void doBroadCast(){
		final ListenerAction listenerOK = new ListenerAction(1);
		AlertDialog dialog = DialogFactory.createInputDialog(this, "Mensaje de difusion:",listenerOK,listenerOK.getEdit());
		listenerOK.setDialog(dialog);
	}
	
	public void checkin(){
		final ListenerAction listenerOK = new ListenerAction(0);
		AlertDialog dialog = DialogFactory.createInputDialog(this, "CheckIn en:",listenerOK,listenerOK.getEdit());
		listenerOK.setDialog(dialog);
	}

	private void sendBroadcast(String msg){
		String ip = ConfigurationManager.getInstance().getString(this, ConfigurationManager.SAVED_IP);
		String port = ConfigurationManager.getInstance().getString(this, ConfigurationManager.SAVED_PORT);
		String access_token = ConfigurationManager.getInstance().getString(this, ConfigurationManager.ACCESS_TOKEN);
		Bundle bundle = new Bundle();
		HashMap<String,String> params = new HashMap<String,String>();
		params.put("access_token", access_token);
		params.put("message", msg);
		bundle.putSerializable("params", params);
		final String URI = ip + ":" + port + "/broadcast";
		bundle.putString("URI", URI);
		Log.i("Broadcast", "Haciendo broadcast");
		startService(createCallingIntent(bundle));

	}

	private void sendAll(String place){
		String username = ConfigurationManager.getInstance().getString(this, ConfigurationManager.USERNAME);
		
		String ip = ConfigurationManager.getInstance().getString(this, ConfigurationManager.SAVED_IP);
		String port = ConfigurationManager.getInstance().getString(this, ConfigurationManager.SAVED_PORT);
		String access_token = ConfigurationManager.getInstance().getString(this, ConfigurationManager.ACCESS_TOKEN);
		Bundle bundle = new Bundle();
		HashMap<String,String> params = new HashMap<String,String>();
		params.put("latitude", Double.toString(latitude));
		params.put("longitude", Double.toString(longitude));
		params.put("name",place);
		params.put("access_token", access_token);
		/* hacer esto b ien cuando este implementado el checkin
		DatabaseHelper dbh = DatabaseHelper.getInstance(this);
		dbh.open();
		dbh.getUserMe().setCheckin(null);
		*/
		bundle.putSerializable("params", params);
		final String URI = ip + ":" + port + "/user/" + username + "/checkin";
		bundle.putString("URI", URI);
		Log.i("Checkin", "Haciendo checkin");
		startService(createCallingIntent(bundle));

	}
	
	public void send(final String place) {
		LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
		
		LocationListener locationListener = new LocationListener(){
			public void onLocationChanged(Location location) {
				longitude = location.getLongitude();
				latitude = location.getLatitude();
				sendAll(place);
			}
			
			public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
				throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
			}
			
			public void onProviderEnabled(String arg0) {
				throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
			}
			
			public void onProviderDisabled(String provider) {
				if(provider.equals("gps")){
					Toast.makeText(getApplicationContext(), "GPS is off", Toast.LENGTH_LONG).show();
					startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
				}
		            Log.i("lm_disabled",provider);
			}
		};

		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, locationListener);
		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0, locationListener);
	}
		
	
	private Intent createCallingIntent(Bundle bundle) {
		Intent intent = new Intent(this, POSTService.class);
		ServerResultReceiver receiver = new ServerResultReceiver(new Handler());
		receiver.setListener(this);
		intent.putExtra("rec", receiver);
		intent.putExtra("info", bundle);
		return intent;
	}
	
	public void openSettings()
	{
		
	}

	private class ListenerAction implements OnClickListener{
		private EditText edit;
		private AlertDialog dialog;
		private String str;
		private int type;
		
		ListenerAction(int type){
			this.edit = new EditText(MainActivity.this);
			this.type = type;
		}
		
		public void onClick(DialogInterface arg0, int arg1) {
			str = edit.getText().toString();
			dialog.dismiss();
			if (type == 0){
				send(str);
				DialogFactory.createProgressDialog(MainActivity.this, "Haciendo checkin");
			}
			if (type == 1){
				sendBroadcast(str);
				DialogFactory.createProgressDialog(MainActivity.this, "Enviando mensajes");
			}
		}
		
		private void setInput(EditText edit) {
			this.edit = edit;
		}
		
		private void setDialog(AlertDialog dialog) {
			this.dialog = dialog;
		}
		
		private EditText getEdit() {
			if (this.edit.getParent() != null)
				return this.edit = new EditText(MainActivity.this);
			else
				return this.edit;
		}
	}
	
	public void onReceiveResult(int resultCode, Bundle resultData) {
		DialogFactory.disposeDialog();
		if (resultCode != 0){
			DialogFactory.createAlertDialog(this, "No se pudo realizar", "Intente de nuevo mas tarde",MainActivity.class);
			Log.e("Error", "Hubo un error en la conexion con el servidor");
		}else if (resultCode == 0){
			Log.i("Exito", "Se tuvo exito en la conexion con el servidor");
		}
	}
}
