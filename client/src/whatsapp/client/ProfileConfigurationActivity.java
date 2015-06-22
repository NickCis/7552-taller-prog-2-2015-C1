/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package whatsapp.client;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_configuration);
        ImageView imageView = (ImageView) findViewById(R.id.userProfileAvatar);
        DatabaseHelper dbH = DatabaseHelper.getInstance(this);
        dbH.open();
        if (dbH.getUserMe().getAvatar() != null)
        {
            imageView.setImageBitmap(dbH.getUserMe().getAvatar());
        }
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
                    dbH.updateUser(dbH.getUserMe());
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
}
