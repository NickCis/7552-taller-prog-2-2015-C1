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

public class Message extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "data";
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_TABLE = "message";
    public static final String KEY_CONVERSATIONID = "idconversation";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_USERID = "iduser";
    public static final String KEY_MEDIAID = "idmedia";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_STATUS = "status";
    public static final short NOT_SENT = 0;
    public static final short NOT_RECIEVED = NOT_SENT + 1;
    public static final short NOT_SEEN = NOT_RECIEVED + 1;
    public static final short SEEN = NOT_SEEN + 1;

    private SQLiteDatabase mDb;
    private static final String DATABASE_CREATE = "CREATE TABLE " + DATABASE_TABLE + " ("
            + KEY_CONVERSATIONID + " INTEGER NOT NULL, "
            + KEY_USERID + " INTEGER NOT NULL, "
            + KEY_MEDIAID + " INTEGER, "
            + KEY_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
            + KEY_CONTENT + " TEXT, "
            + KEY_STATUS + " SHORT INTEGER DEFAULT " + NOT_SENT + " NOT NULL, "
            + "FOREIGN KEY(" + KEY_CONVERSATIONID + ") REFERENCES " + ActiveConversation.DATABASE_TABLE + "(" + ActiveConversation.KEY_CONVERSATIONID + "), "
            + "FOREIGN KEY(" + KEY_USERID + ") REFERENCES " + User.DATABASE_TABLE + "(" + User.KEY_USERID + "), "
            + "FOREIGN KEY(" + KEY_MEDIAID + ") REFERENCES " + Media.DATABASE_TABLE + "(" + Media.KEY_MEDIAID + "));";

    private final Context context;

    public Message (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    
    public Message open() throws SQLException {
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
    
    public long createMessage(Integer conversationId, Integer idUser, Integer idMedia, Calendar date, String description, Short status) { 
          ContentValues values = new ContentValues(); 
          values.put(KEY_CONVERSATIONID, conversationId);
          values.put(KEY_USERID, idUser); 
          if (date != null)
          {
              values.put(KEY_TIMESTAMP, date.getTime().toString());
          }
          values.put(KEY_MEDIAID, idMedia);
          values.put(KEY_CONTENT, description);
          values.put(KEY_STATUS, status);
          
          return mDb.insert(DATABASE_TABLE, null, values); 
    }
    
    public boolean deleteMessage(Integer conversationId, Calendar date) { 
          return  mDb.delete(DATABASE_TABLE, KEY_CONVERSATIONID + "=? AND " + KEY_TIMESTAMP + "=?",new String[]{"" + conversationId, date.getTime().toString()}) > 0; 
    }
    
    public Cursor fetchAllMessages() { 
          return mDb.query(DATABASE_TABLE, new String[]{KEY_CONVERSATIONID, KEY_USERID, KEY_CONTENT, KEY_MEDIAID, KEY_STATUS, KEY_TIMESTAMP}, null, null, null, null, KEY_TIMESTAMP + " ASC"); 
    }
    
    public Cursor fetchMessages(Integer conversationId) throws SQLException { 
          Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String [] 
               {KEY_CONVERSATIONID, KEY_USERID, KEY_CONTENT, KEY_MEDIAID, KEY_STATUS, KEY_TIMESTAMP},
                KEY_CONVERSATIONID + "=?", 
                new String[]{"" + conversationId}, null, null, null, KEY_TIMESTAMP + " ASC"); 
          if (mCursor != null) 
          { 
                mCursor.moveToFirst(); 
          } 
          return mCursor;
    }
    
    public Cursor fetchMessages(String content) throws SQLException { 
          Cursor mCursor = mDb.query(true, DATABASE_TABLE, new String [] 
               {KEY_CONVERSATIONID, KEY_USERID, KEY_CONTENT, KEY_MEDIAID, KEY_STATUS, KEY_TIMESTAMP},
                KEY_CONTENT + "=?", 
                new String[]{content}, null, null, null, KEY_TIMESTAMP + " ASC"); 
          if (mCursor != null) 
          { 
                mCursor.moveToFirst(); 
          } 
          return mCursor;
    }
    
    public boolean updateActiveConversation(Integer conversationId, Integer idUser, Integer idMedia, Calendar date, String description, Short status) { 
          ContentValues values = new ContentValues(); 
          values.put(KEY_USERID, idUser); 
          values.put(KEY_MEDIAID, idMedia);
          values.put(KEY_CONTENT, description);
          values.put(KEY_STATUS, status);
          return  mDb.update(DATABASE_TABLE, values, KEY_CONVERSATIONID + "=?" + " AND " + KEY_TIMESTAMP + "=?", new String[]{"" + conversationId, "" + date.getTime().toString()}) > 0; 
    }
}