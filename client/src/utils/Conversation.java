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

public class Conversation extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "data";
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_TABLE = "conversation";
    public static final String KEY_USERID = "iduser";
    public static final String KEY_DATE = "last_message_time";
    public static final String KEY_CONVERSATIONID = "idconversation";

    private SQLiteDatabase mDb;
    
    private static final String DATABASE_CREATE = "CREATE TABLE " + DATABASE_TABLE + " ("
            + KEY_CONVERSATIONID + " INTEGER NOT NULL, "
            + KEY_USERID + " INTEGER NOT NULL CONSTRAINT fk_conversation_iduser REFERENCES " + User.DATABASE_TABLE + "(" + User.KEY_USERID + "), "
            + KEY_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
            + "PRIMARY KEY(" + KEY_CONVERSATIONID + ", " + KEY_USERID + "));";

    private final Context context;

    public Conversation (Context context) {
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
    
    public ConversationEntity createConversation(UserEntity user)
    {
        return this.createConversation(user, Calendar.getInstance());
    }
    
    public ConversationEntity createConversation(UserEntity user, Calendar date) {
        ConversationEntity result = null;
        if (user != null)
        {
            result = new ConversationEntity(this.fetchLastConversationId(),date);
            result.addUser(user);
            ContentValues initialValues = new ContentValues(); 
            initialValues.put(KEY_USERID, user.getUserId()); 
            initialValues.put(KEY_DATE, date.getTime().toString());
            initialValues.put(KEY_CONVERSATIONID, result.getConversationId());
            this.open();
            mDb.insert(DATABASE_TABLE, null, initialValues);
            this.close();
        }
        
        return result;
    }
    
    public ConversationEntity createConversation(ConversationEntity conversation, UserEntity user, Calendar date) { 
        ConversationEntity result = null;
        conversation.addUser(user);
        conversation.setLast_message_time(date);
        ContentValues initialValues = new ContentValues(); 
        initialValues.put(KEY_CONVERSATIONID, conversation.getConversationId());
        initialValues.put(KEY_USERID, user.getUserId()); 
        initialValues.put(KEY_DATE, date.getTime().toString());
        this.open();
        mDb.insert(DATABASE_TABLE, null, initialValues);
        this.close();
        return conversation;
    }
    
    public boolean deleteConversation(ConversationEntity conversation) {
        this.open();
        boolean result = mDb.delete(DATABASE_TABLE, KEY_CONVERSATIONID + "=" + conversation.getConversationId(), null) > 0; 
        this.close();
        return result;
    }
    
    public List<ConversationEntity> fetchAllConversations() {
        this.open();
        Cursor cursor = mDb.query(DATABASE_TABLE, new String[]{KEY_CONVERSATIONID, KEY_USERID, KEY_DATE}, null, null, null, null, KEY_DATE + " DESC");
        this.close();
        List<ConversationEntity> list = new ArrayList<ConversationEntity>();
        if (cursor != null)
        {
            cursor.moveToFirst();
            Integer conversationIndex = cursor.getInt(cursor.getColumnIndex(this.KEY_CONVERSATIONID));
            ConversationEntity cE = new ConversationEntity(conversationIndex,
                    cursor.getString(cursor.getColumnIndex(this.KEY_DATE)));
            do
            {
                int conversationId = cursor.getInt(cursor.getColumnIndex(this.KEY_CONVERSATIONID));
                if (conversationIndex != conversationId)
                {
                    conversationIndex = conversationId;
                    list.add(cE);
                    cE = new ConversationEntity(conversationId,
                    cursor.getString(cursor.getColumnIndex(this.KEY_DATE)));
                }
                User user = new User(this.context);
                cE.addUser(user.fetchUser(cursor.getInt(cursor.getColumnIndex(this.KEY_USERID))));
            } while (cursor.moveToNext());
        }
        return list;
    }
    
    public ConversationEntity fetchConversation(ConversationEntity conversation) throws SQLException {
        this.open();
        Cursor cursor = mDb.query(true, DATABASE_TABLE, new String [] 
             {KEY_CONVERSATIONID, KEY_USERID, KEY_DATE}, KEY_CONVERSATIONID + 
             "=?", new String[]{"" + conversation.getConversationId()}, null, null, null, KEY_DATE + " DESC"); 
        this.close();
        ConversationEntity cE = null;
        if (cursor != null) 
        {
            cE = new ConversationEntity(conversation.getConversationId(), cursor.getString(cursor.getColumnIndex(this.KEY_DATE)));
            cursor.moveToFirst();
            do
            {
                User user = new User(this.context);
                cE.addUser(user.fetchUser(cursor.getInt(cursor.getColumnIndex(this.KEY_USERID))));
            }while (cursor.moveToNext());
        } 
        return cE;
    }
    
    private Integer fetchLastConversationId() throws SQLException { 
        this.open();
        Cursor cursor = mDb.query(true, DATABASE_TABLE, new String [] 
             {KEY_CONVERSATIONID}, null, null, null, null, null, KEY_DATE + " DESC LIMIT 1"); 
        this.close();
        if (cursor != null) 
        { 
            cursor.moveToFirst(); 
        } 
        return cursor.getInt(0);
    }
    
    public boolean updateConversation(ConversationEntity conversation) {
        for(UserEntity user : conversation.getUsers())
        {
            ContentValues args = new ContentValues(); 
            args.put(KEY_USERID, user.getUserId()); 
            args.put(KEY_DATE, conversation.getLast_message_time().getTime().toString());
            this.open();
            boolean result = mDb.update(DATABASE_TABLE, args, KEY_CONVERSATIONID + "=" + conversation.getConversationId(), null) > 0;
            this.close();
            if (!result) 
            {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }
}