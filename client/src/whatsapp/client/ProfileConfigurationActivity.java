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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import utils.DatabaseHelper;

/**
 *
 * @author umm194
 */
public class ProfileConfigurationActivity extends Activity
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
                    dbH.close();
                }
                break; 
            } 
        } 
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
        dbH.updateUser(dbH.getUserMe()); // Actualiza foto, estado y nickname
        dbH.close();
        this.finish();
    }
}
