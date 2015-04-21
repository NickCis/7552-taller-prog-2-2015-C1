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

/**
 *
 * @author umm194
 */

public class Media extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "data";
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_TABLE = "media";
    public static final String KEY_MEDIAID = "idmedia";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_TYPE = "type";

    private SQLiteDatabase mDb;
    private static final String DATABASE_CREATE = "CREATE TABLE " + DATABASE_TABLE + " ("
            + KEY_MEDIAID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_LOCATION + " TEXT NOT NULL, "
            + KEY_TYPE + " SHORT INTEGER NOT NULL);";

    private final Context context;

    public Media (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    
    public Media open() throws SQLException {
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
    
    public long createMedia(String location, Short type) {
          ContentValues values = new ContentValues(); 
          values.put(KEY_LOCATION, location);
          values.put(KEY_TYPE, type);
          
          return mDb.insert(DATABASE_TABLE, null, values); 
    }
    
    public boolean deleteMedia(Integer mediaId) { 
          return  mDb.delete(DATABASE_TABLE, KEY_MEDIAID + "=?",new String[]{"" + mediaId}) > 0; 
    }
    
    public Cursor fetchAllMedia() { 
          return mDb.query(DATABASE_TABLE, new String[]{KEY_MEDIAID, KEY_LOCATION, KEY_TYPE}, null, null, null, null, KEY_MEDIAID + " ASC"); 
    }
    
    public Cursor fetchMedia(Integer mediaId) throws SQLException {
          Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String [] 
               {KEY_MEDIAID, KEY_LOCATION, KEY_TYPE}, KEY_MEDIAID + 
               "=?", new String[]{"" + mediaId}, null, null, null, null); 
          if (mCursor != null) 
          { 
                mCursor.moveToFirst(); 
          } 
          return mCursor;
    }
    
    public boolean updateMedia(Integer mediaId, String location, Short type) { 
          ContentValues values = new ContentValues(); 
          values.put(KEY_LOCATION, location); 
          values.put(KEY_TYPE, type);
          
          return  mDb.update(DATABASE_TABLE, values, KEY_MEDIAID + "=?", new String[]{"" + mediaId}) > 0; 
    }
}