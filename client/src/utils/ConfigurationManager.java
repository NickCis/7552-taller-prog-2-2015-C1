/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 *
 * @author rburdet
 */
public class ConfigurationManager {

	private static ConfigurationManager INSTANCE;	
	public static final String USER_CFG = "user_cfg";
	public static final String SAVED_IP = "IP";
	public static final String SAVED_PORT = "PORT";
	
	
	public static ConfigurationManager getInstance(){
		return INSTANCE == null ? new ConfigurationManager() : INSTANCE;
	}
	
	private ConfigurationManager(){}
	
	public String getString(Context ctx,String value){
		SharedPreferences data = ctx.getSharedPreferences(USER_CFG, 0);
		String str="";
		if (data != null) {
			str = data.getString(value, str);
		}
		return str;
	}
}
