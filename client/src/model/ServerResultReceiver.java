/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * Clase encargada de bindear una activity a un servicio
 * @author rburdet
 */
public class ServerResultReceiver extends ResultReceiver{

    private Listener listener;
	
	public ServerResultReceiver(Handler handler) {
		super(handler);		
	}
	
	public void setListener(Listener listener) {
		this.listener = listener;
	}

	protected void onReceiveResult(int resultCode, Bundle resultData) {
		if (listener != null)
			listener.onReceiveResult(resultCode, resultData);
	}
	
	
	public static interface Listener {
		void onReceiveResult(int resultCode, Bundle resultData);
	}
}
