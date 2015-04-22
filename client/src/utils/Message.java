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
import java.util.Date;
import java.util.List;

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
            + KEY_CONVERSATIONID + " INTEGER NOT NULL CONSTRAINT fk_message_idconversation REFERENCES " + Conversation.DATABASE_TABLE + "(" + Conversation.KEY_CONVERSATIONID + "), "
            + KEY_USERID + " INTEGER NOT NULL CONSTRAINT fk_message_iduser REFERENCES " + User.DATABASE_TABLE + "(" + User.KEY_USERID + "), "
            + KEY_MEDIAID + " INTEGER CONSTRAINT fk_message_idmedia REFERENCES " + Media.DATABASE_TABLE + "(" + Media.KEY_MEDIAID + "), "
            + KEY_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
            + KEY_CONTENT + " TEXT, "
            + KEY_STATUS + " SHORT INTEGER DEFAULT " + NOT_SENT + " NOT NULL, "
            + "PRIMARY KEY(" + KEY_CONVERSATIONID + ", " + KEY_USERID + ", " + KEY_TIMESTAMP + "));";

    private final Context context;

    public Message (Context context) {
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
    
    public MessageEntity createMessage(ConversationEntity conversation, UserEntity user, MediaEntity media, Calendar date, String content, Short status) { 
          ContentValues values = new ContentValues(); 
          values.put(KEY_CONVERSATIONID, conversation.getConversationId());
          values.put(KEY_USERID, user.getUserId()); 
          if (date != null)
          {
              values.put(KEY_TIMESTAMP, date.getTime().toString());
          }
          if (media != null)
          {
              values.put(KEY_MEDIAID, media.getMediaId());
          }
          values.put(KEY_CONTENT, content);
          values.put(KEY_STATUS, status);
          this.open();
          this.mDb.insert(DATABASE_TABLE, null, values);
          this.close();
          return new MessageEntity(conversation, user, media, date, content, status);
    }
    
    public boolean deleteMessage(ConversationEntity conversation, UserEntity user, Calendar date) { 
        this.open();
        boolean result = this.mDb.delete(DATABASE_TABLE, KEY_CONVERSATIONID + " =? AND " + KEY_USERID + " =? AND " + KEY_TIMESTAMP + " =? ", new String[]{"" + conversation.getConversationId(), "" + user.getUserId(), date.getTime().toString()}) > 0; 
        this.close();
        return result;
    }
    
    public boolean deleteMessages(ConversationEntity conversation)
    {
        this.open();
        boolean result = this.mDb.delete(DATABASE_TABLE, KEY_CONVERSATIONID + " =? ", new String[]{"" + conversation.getConversationId()}) > 0;
        this.close();
        return result;
    }
    
    public List<MessageEntity> fetchAllMessages() {
        List<MessageEntity> list = new ArrayList<MessageEntity>();
        Conversation conversation = new Conversation(this.context);
        for (ConversationEntity cE : conversation.fetchAllConversations())
        {
            list.addAll(fetchMessages(cE));
        }
        return list;
    }
    
    public List<MessageEntity> fetchMessages(ConversationEntity conversation) throws SQLException {
        this.open();
        Cursor cursor = this.mDb.query(true, DATABASE_TABLE, new String [] 
             {KEY_CONVERSATIONID, KEY_USERID, KEY_CONTENT, KEY_MEDIAID, KEY_STATUS, KEY_TIMESTAMP},
              KEY_CONVERSATIONID + "=?", 
              new String[]{"" + conversation.getConversationId()}, null, null, null, KEY_TIMESTAMP + " ASC");
        this.close();
        List<MessageEntity> list = new ArrayList<MessageEntity>();
        if (cursor != null) 
        { 
            cursor.moveToFirst(); 
            do
            {
                Conversation conv = new Conversation(this.context);
                ConversationEntity cE = conv.fetchConversation(conversation);
                User user = new User(this.context);
                UserEntity uE = user.fetchUser(cursor.getInt(cursor.getColumnIndex(KEY_USERID)));
                Media media = new Media(this.context);
                MediaEntity mE = media.fetchMedia(cursor.getInt(cursor.getColumnIndex(KEY_MEDIAID)));
                Calendar date = Calendar.getInstance();
                date.setTime(new Date(cursor.getString(cursor.getColumnIndex(KEY_TIMESTAMP))));
                list.add(new MessageEntity(cE, uE, mE, date, cursor.getString(cursor.getColumnIndex(KEY_CONTENT)), cursor.getShort(cursor.getColumnIndex(KEY_STATUS))));
            } while (!cursor.moveToNext());
        } 
        return list;
    }
    
    public List<MessageEntity> fetchMessages(String content) throws SQLException {
        this.open();
        Cursor cursor = this.mDb.query(true, DATABASE_TABLE, new String [] 
             {KEY_CONVERSATIONID, KEY_USERID, KEY_CONTENT, KEY_MEDIAID, KEY_STATUS, KEY_TIMESTAMP},
              KEY_CONTENT + " like '%?%'", 
              new String[]{content}, null, null, null, KEY_TIMESTAMP + " ASC"); 
        this.close();
        List<MessageEntity> list = new ArrayList<MessageEntity>();
        if (cursor != null) 
        { 
            cursor.moveToFirst();
            do
            {
                Conversation conversation = new Conversation(this.context);
                ConversationEntity cE = conversation.fetchConversation(new ConversationEntity(cursor.getInt(cursor.getColumnIndex(KEY_CONVERSATIONID))));
                User user = new User(this.context);
                UserEntity uE = user.fetchUser(cursor.getInt(cursor.getColumnIndex(KEY_USERID)));
                Media media = new Media(this.context);
                MediaEntity mE = media.fetchMedia(cursor.getInt(cursor.getColumnIndex(KEY_MEDIAID)));
                Calendar date = Calendar.getInstance();
                date.setTime(new Date(cursor.getString(cursor.getColumnIndex(KEY_TIMESTAMP))));
                list.add(new MessageEntity(cE, uE, mE, date, cursor.getString(cursor.getColumnIndex(KEY_CONTENT)), cursor.getShort(cursor.getColumnIndex(KEY_STATUS))));
            } while (cursor.moveToNext());
        } 
        return list;
    }
    
    public boolean updateActiveConversation(ConversationEntity conversation, UserEntity user, MediaEntity media, Calendar date, String description, Short status) { 
          ContentValues values = new ContentValues(); 
          if (media != null)
          {
              values.put(KEY_MEDIAID, media.getMediaId());
          }
          values.put(KEY_CONTENT, description);
          values.put(KEY_STATUS, status);
          this.open();
          boolean result = this.mDb.update(DATABASE_TABLE, values, KEY_CONVERSATIONID + " =? AND " + KEY_USERID + " =? AND " + KEY_TIMESTAMP + " =? ", new String[]{"" + conversation.getConversationId(), "" + user.getUserId(),"" + date.getTime().toString()}) > 0; 
          this.close();
          return result;
    }
}