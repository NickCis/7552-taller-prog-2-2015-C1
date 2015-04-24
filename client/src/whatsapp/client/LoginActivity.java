package whatsapp.client;

import model.LoginService;
import model.ServerResultReceiver;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity implements ServerResultReceiver.Listener{
    private static final String REGISTERED = "registered";
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (!isUserRegistered())
            setContentView(R.layout.login);
        else
        {
            Log.v("REGISTRO", "tengo que a la proxima activity porque ya estoy registrado");
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    public void register(View v){
        Bundle bundle = new Bundle();
        EditText userNameField = (EditText) findViewById(R.id.username);
        EditText passwordField = (EditText) findViewById(R.id.userpassword);
        bundle.putString("username", userNameField.getText().toString());
        bundle.putString("password", passwordField.getText().toString());
        startService(createCallingIntent(bundle));
    }

    private Intent createCallingIntent(Bundle bundle){
        Intent intent = new Intent(this,LoginService.class);
        ServerResultReceiver receiver = new ServerResultReceiver(new Handler());
        receiver.setListener(this);
        intent.putExtra("rec",receiver);
        intent.putExtra("info", bundle);
        return intent;
    }


    /**
     * Prueba si el usuario ya se registro alguna vez
     * @return true si el usuario no fue registrado, false si ya se registro alguna vez
     */
    private final boolean isUserRegistered(){
        SharedPreferences data = getSharedPreferences(REGISTERED, 0);
        return data.getBoolean(REGISTERED, false);
        //SharedPreferences.Editor editor = data.edit();
        //editor.

    }

    /**
     * Setea un flag de que el usuario ya fue registrado
     */
    private final void registerUser(){
        SharedPreferences data = getSharedPreferences(REGISTERED, 0);
        SharedPreferences.Editor editor = data.edit();
        editor.putBoolean(REGISTERED, true);
    }

    public void onReceiveResult(int resultCode, Bundle resultData) {
        registerUser();
        startActivity(new Intent(this, MainActivity.class));
    }
}
