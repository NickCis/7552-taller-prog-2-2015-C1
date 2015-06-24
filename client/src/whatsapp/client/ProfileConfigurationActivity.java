/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/
package whatsapp.client;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import model.MultipartRequest;
import model.POSTService;
import model.ServerResultReceiver;
import services.AppController;
import utils.ConfigurationManager;
import utils.DatabaseHelper;

/**
 *
 * @author umm194
 */
public class ProfileConfigurationActivity extends Activity implements ServerResultReceiver.Listener
{
	
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.profile_configuration);
		this.context = this;
		ImageView imageView = (ImageView) findViewById(R.id.userProfileAvatar);
		DatabaseHelper dbH = DatabaseHelper.getInstance(this);
		dbH.open();
		if (dbH.getUserMe().getAvatar() != null)
		{
			imageView.setImageBitmap(dbH.getUserMe().getAvatar());
		}
		String[] items = new String[DatabaseHelper.STATUS_COUNT];
		items[DatabaseHelper.STATUS_ONLINE] = "Online";
		items[DatabaseHelper.STATUS_OFFLINE] = "Offline";
		items[DatabaseHelper.STATUS_DO_NOT_DISTURB] = "Do not disturb";
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
		Spinner spinner = (Spinner) findViewById(R.id.statusSelection);
		spinner.setAdapter(adapter);
		if (dbH.getUserMe().getStatus() != null)
		{
			spinner.setSelection(dbH.getUserMe().getStatus());
		}
		
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
			{
				DatabaseHelper dbH = DatabaseHelper.getInstance(context);
				dbH.open();
				dbH.getUserMe().setStatus(Short.valueOf(Integer.toString(pos)));
				dbH.close();
			}
			
			public void onNothingSelected(AdapterView<?> parent)
			{
				
			}
			
		});
		
		EditText nickname = (EditText) findViewById(R.id.nicknameConfiguration);
		nickname.setText(dbH.getUserMe().getNickname());
		EditText statusMessage = (EditText) findViewById(R.id.statusMessageConfiguration);
		statusMessage.setText(dbH.getUserMe().getStatusMessage());
		dbH.close();
	}
	
	public void addImage(View v)
	{
		startActivityForResult(new Intent(this, GalleryActivity.class), 1);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode)
		{
			case (1) :
			{
				if (resultCode == Activity.RESULT_OK)
				{
					ImageView imageView = (ImageView) findViewById(R.id.userProfileAvatar);
					DatabaseHelper dbH = DatabaseHelper.getInstance(this);
					dbH.open();
					dbH.getUserMe().setAvatar(decodeFile(data.getStringExtra("filepath"), 200,200));
					imageView.setImageBitmap(dbH.getUserMe().getAvatar());
					sendAvatarServer(data.getStringExtra("filepath"));
					dbH.close();
				}
				break;
			}
		}
	}

	private void sendAvatarServer(final String filepath){
		DatabaseHelper dbh = DatabaseHelper.getInstance(this);
		dbh.open();
		String username = dbh.getUserMe().getUsername();
		String ip = ConfigurationManager.getInstance().getString(this, ConfigurationManager.SAVED_IP);
		String port = ConfigurationManager.getInstance().getString(this, ConfigurationManager.SAVED_PORT);
		String access_token = ConfigurationManager.getInstance().getString(this, ConfigurationManager.ACCESS_TOKEN);
		String URI = ip + ":" + port + "/user/" + username +"/avatar?access_token="+access_token;

		dbh.close();
		MultipartRequest req = new MultipartRequest(URI, new Response.ErrorListener() {

			public void onErrorResponse(VolleyError ve) {
				Log.d("fallo", "fallo al subir imagen");
			}
		}, new Response.Listener<String>() {
			public void onResponse(String t) {
				Log.d("aca esta bien", "no fallo");
			}
		}, new File(filepath), "avatar");
		AppController.getInstance().addToRequestQueue(req);
		
		//String URI = ip + ":" + port + "/user/" + username +"/avatar?access_token="+access_token;
		//send("user/"+username+"/avatar",null,2,1);
		/*
		ImageRequest imreq = new ImageRequest(URI, new Response.Listener<Bitmap>() {
			public void onResponse(Bitmap t) {
				ueAux.setAvatar(t);
				DatabaseHelper dbh = DatabaseHelper.getInstance(UsersActivity.this);
				dbh.open();
				UserEntity userToUpdate = dbh.fetchUser(ueAux.getUsername());
				if (userToUpdate!=null){
					userToUpdate.setAvatar(t);
					dbh.updateUser(userToUpdate);
				}
				dbh.close();
				Drawable d = new BitmapDrawable(getResources(), ueAux.getAvatar());
				rowItemAux.setAvatar(d);
				adapter.notifyDataSetChanged();
			}
		}, 0,0, null, null);
		AppController.getInstance().addToRequestQueue(imreq);
		*/
	}
	
	/*
	* Resizing image size
	*/
	public static Bitmap decodeFile(String filePath, int WIDTH, int HIGHT) {
		try {
			
			File f = new File(filePath);
			
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);
			
			final int REQUIRED_WIDTH = WIDTH;
			final int REQUIRED_HIGHT = HIGHT;
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
				&& o.outHeight / scale / 2 >= REQUIRED_HIGHT)
				scale *= 2;
			
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(View v)
	{
		DatabaseHelper dbH = DatabaseHelper.getInstance(context);
		dbH.open();
		EditText nickname = (EditText) findViewById(R.id.nicknameConfiguration);
		dbH.getUserMe().setNickname(nickname.getText().toString());
		EditText statusMessage = (EditText) findViewById(R.id.statusMessageConfiguration);
		dbH.getUserMe().setStatusMessage(statusMessage.getText().toString());
		dbH.updateUser(dbH.getUserMe()); // Actualiza foto, estado y nickname
		dbH.close();
		send(dbH.getUserMe().getUsername());
		this.finish();
	}
	
	private void send(String username){
		Bundle bundle = new Bundle();
		HashMap<String, String> params = new HashMap<String, String>();
		ServerResultReceiver receiver = new ServerResultReceiver(new Handler());
		receiver.setListener(this);
		String access_token = ConfigurationManager.getInstance().getString(this, ConfigurationManager.ACCESS_TOKEN);
		params.put("access_token", access_token);
		DatabaseHelper dbh = DatabaseHelper.getInstance(context);
		dbh.open();
		params.put("nickname", dbh.getUserMe().getNickname());
		//TODO: tiene q estar en el servidor
		params.put("status", dbh.getUserMe().getStatusMessage());
		params.put("online",dbh.getUserMe().getStatus() == DatabaseHelper.STATUS_ONLINE ? "true" : "false");
		bundle.putSerializable("params", params);
		String ip = ConfigurationManager.getInstance().getString(this, ConfigurationManager.SAVED_IP);
		String port = ConfigurationManager.getInstance().getString(this, ConfigurationManager.SAVED_PORT);
		String URI = ip + ":" + port + "/" + "user/" + username + "/profile";
		bundle.putString("URI", URI);
		bundle.putSerializable("params", params);
		Intent intent = new Intent(this, POSTService.class);
		//TODO: Se le tiene q asociar un listener para q vuelva ?
		intent.putExtra("info", bundle);
		intent.putExtra("rec", receiver);
		startService(intent);
	}
	
	
	public void onReceiveResult(int resultCode, Bundle resultData) {
		if (resultCode==1)
			DialogFactory.createAlertDialog(context, "Error cambiando perfil", "No se pudo enviar la informacion al servidor, pruebe nuevamente mas tarde", MainActivity.class);
	}
}

