/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package whatsapp.client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import model.ServerResultReceiver;

/**
 *
 * @author rburdet
 */
public class RegisterActivity extends Activity implements ServerResultReceiver.Listener{

	 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//TODO: Si el usuario ya se registro tendria que recordar ese usuario
		//if (!isUserRegistered())
		setContentView(R.layout.register);

	}

	
	public void onReceiveResult(int resultCode, Bundle resultData) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
}
