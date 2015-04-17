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

public class ActiveConversation extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "data";
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_TABLE = "active_conversation";
    public static final String KEY_USER = "iduser";
    public static final String KEY_DATE = "last_message_time";
    public static final String KEY_CONVERSATIONID = "idconversation";

    private SQLiteDatabase mDb;
    
    private static final String DATABASE_CREATE = "CREATE TABLE " + DATABASE_TABLE + " ("
            + KEY_CONVERSATIONID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "FOREIGN KEY(" + KEY_USER + ") REFERENCES " + User.DATABASE_TABLE + "(" + KEY_USER + ") NOT NULL, "
            + KEY_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP);";

    private final Context context;

    public ActiveConversation (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    
    public ActiveConversation open() throws SQLException {
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
    
    public long createActiveConversation(Integer idUser, Calendar date) { 
          ContentValues initialValues = new ContentValues(); 
          initialValues.put(KEY_USER, idUser); 
          initialValues.put(KEY_DATE, date.getTime().toString()); 
          
          return mDb.insert(DATABASE_TABLE, null, initialValues); 
    }
    
    public boolean deleteActiveConversation(Integer conversationId) { 
          return  mDb.delete(DATABASE_TABLE, KEY_CONVERSATIONID + "=" + conversationId, null) > 0; 
    }
    
    public Cursor fetchAllActiveConversations() { 
          return mDb.query(DATABASE_TABLE, new String[]{KEY_CONVERSATIONID, KEY_USER, KEY_DATE}, null, null, null, KEY_DATE + " DESC", null); 
    }
    
    public Cursor fetchActiveConversation(Integer conversationId) throws SQLException { 
          Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String [] 
               {KEY_CONVERSATIONID, KEY_USER, KEY_DATE}, KEY_CONVERSATIONID + 
               "=?", new String[]{"" + conversationId}, null, null, null, null); 
          if (mCursor != null) 
          { 
                mCursor.moveToFirst(); 
          } 
          return mCursor;
    }
    
    public boolean updateActiveConversation(Integer conversationId, Integer idUser, Calendar date) { 
          ContentValues args = new ContentValues(); 
          args.put(KEY_USER, idUser); 
          args.put(KEY_DATE, date.getTime().toString());
          
          return  mDb.update(DATABASE_TABLE, args, KEY_CONVERSATIONID + "=" + conversationId, null) > 0; 
    }
}