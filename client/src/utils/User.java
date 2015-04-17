/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.Calendar;

/**
 *
 * @author umm194
 */

public class User extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "data";
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_TABLE = "user";
    public static final String KEY_USERID = "iduser";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_NAME = "name";
    public static final String KEY_STATUS = "status";
    
    public static final short NORMAL = 0;
    public static final short BLOKED = NORMAL + 1;

    private SQLiteDatabase mDb;
    private static final String DATABASE_CREATE = "CREATE TABLE " + DATABASE_TABLE + " ("
            + KEY_USERID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_PHONE + " INTEGER NOT NULL, "
            + KEY_NAME + " TEXT NOT NULL, "
            + KEY_STATUS + " SHORT INTEGER NOT NULL);";

    private final Context context;

    public User (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    
    public User open() throws SQLException {
        this.mDb = this.getWritableDatabase();
        return this;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public long createUser(Integer phone, String name, Short status) {
          ContentValues values = new ContentValues(); 
          values.put(KEY_NAME, name);
          values.put(KEY_PHONE, phone);
          values.put(KEY_STATUS, status);
          
          return mDb.insert(DATABASE_TABLE, null, values); 
    }
    
    public boolean deleteUser(Integer userId) { 
          return  mDb.delete(DATABASE_TABLE, KEY_USERID + "=?",new String[]{"" + userId}) > 0; 
    }
    
    public Cursor fetchAllUsers() { 
          return mDb.query(DATABASE_TABLE, new String[]{KEY_USERID, KEY_NAME, KEY_PHONE, KEY_STATUS}, null, null, null, KEY_USERID + " ASC", null); 
    }
    
    public Cursor fetchUser(Integer userId) throws SQLException { 
          Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String [] 
               {KEY_USERID, KEY_NAME, KEY_PHONE, KEY_STATUS}, KEY_USERID + 
               "=?", new String[]{"" + userId}, null, null, null, null); 
          if (mCursor != null) 
          { 
                mCursor.moveToFirst(); 
          } 
          return mCursor;
    }
    
    public boolean updateUser(Integer userId, Integer phone, String name, Short status) { 
          ContentValues values = new ContentValues(); 
          values.put(KEY_PHONE, phone); 
          values.put(KEY_NAME, name);
          values.put(KEY_STATUS, status);
          
          return  mDb.update(DATABASE_TABLE, values, KEY_USERID + "=?", new String[]{"" + userId}) > 0; 
    }
}