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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
    
    private void open() throws SQLException {
        this.mDb = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public UserEntity createUser(Integer phone, String name, Short status) {
        ContentValues values = new ContentValues(); 
        values.put(KEY_NAME, name);
        values.put(KEY_PHONE, phone);
        values.put(KEY_STATUS, status);
        this.open();
        long userId = mDb.insert(DATABASE_TABLE, null, values); 
        this.close();
        return new UserEntity((int)userId, name, phone, status);
    }
    
    public boolean deleteUser(Integer userId) {
        this.open();
        boolean result = mDb.delete(DATABASE_TABLE, KEY_USERID + "=?",new String[]{"" + userId}) > 0; 
        this.close();
        return result;
    }
    
    public boolean deleteUser(UserEntity user) {
        this.open();
        boolean result = mDb.delete(DATABASE_TABLE, KEY_USERID + "=?",new String[]{"" + user.getUserId()}) > 0;
        this.close();
        return result;
    }
    
    public List<UserEntity> fetchAllUsers() {
        this.open();
        Cursor cursor = mDb.query(DATABASE_TABLE, new String[]{KEY_USERID, KEY_NAME, KEY_PHONE, KEY_STATUS}, null, null, null, null, KEY_USERID + " ASC");
        this.close();
        List<UserEntity> list = null;
        if (cursor != null)
        {
            list = new ArrayList<UserEntity>();
            cursor.moveToFirst();
            do
            {
                UserEntity uE = new UserEntity(cursor.getInt(cursor.getColumnIndex(this.KEY_USERID)),
                        cursor.getString(cursor.getColumnIndex(this.KEY_NAME)),
                        cursor.getInt(cursor.getColumnIndex(this.KEY_PHONE)),
                        cursor.getShort(cursor.getColumnIndex(this.KEY_STATUS)));
                list.add(uE);
            } while (cursor.moveToNext());
        }
        return list;
    }
    
    public UserEntity fetchUser(Integer userId) throws SQLException {
        this.open();
        Cursor cursor = mDb.query(true, DATABASE_TABLE, new String [] 
             {KEY_USERID, KEY_NAME, KEY_PHONE, KEY_STATUS}, KEY_USERID + 
             "=?", new String[]{"" + userId}, null, null, null, null); 
        this.close();
        UserEntity uE = null;
        if (cursor != null) 
        { 
            cursor.moveToFirst();
            uE = new UserEntity(userId,
                    cursor.getString(cursor.getColumnIndex(this.KEY_NAME)),
                    cursor.getInt(cursor.getColumnIndex(this.KEY_PHONE)),
                    cursor.getShort(cursor.getColumnIndex(this.KEY_STATUS)));
            
        } 
        return uE;
    }
    
    public boolean updateUser(UserEntity user) { 
        ContentValues values = new ContentValues(); 
        values.put(KEY_PHONE, user.getPhone()); 
        values.put(KEY_NAME, user.getName());
        values.put(KEY_STATUS, user.getStatus());
        this.open();
        boolean result = mDb.update(DATABASE_TABLE, values, KEY_USERID + "=?", new String[]{"" + user.getUserId()}) > 0; 
        this.close();
        return result;
    }
}