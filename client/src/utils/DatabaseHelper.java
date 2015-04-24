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

public class DatabaseHelper extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "data";
    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_USER_TABLE = "user";
    public static final String DATABASE_CONVERSATION_TABLE = "conversation";
    public static final String DATABASE_MEDIA_TABLE = "media";
    public static final String DATABASE_MESSAGE_TABLE = "message";
    public static final String KEY_CONVERSATIONID = "idconversation";
    public static final String KEY_MEDIAID = "idmedia";
    public static final String KEY_USERID = "iduser";
    public static final String KEY_DATE = "last_message_time";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_NAME = "name";
    public static final String KEY_STATUS = "status";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_TYPE = "type";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_TIMESTAMP = "timestamp";
    
    public static final short NOT_SENT = 0;
    public static final short NOT_RECIEVED = NOT_SENT + 1;
    public static final short NOT_SEEN = NOT_RECIEVED + 1;
    public static final short SEEN = NOT_SEEN + 1;
    
    public static final short NORMAL = 0;
    public static final short BLOKED = NORMAL + 1;

    private SQLiteDatabase mDb;
    private static final String DATABASE_USER_CREATE = "CREATE TABLE " + DATABASE_USER_TABLE + " ("
            + KEY_USERID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_PHONE + " INTEGER NOT NULL, "
            + KEY_NAME + " TEXT NOT NULL, "
            + KEY_STATUS + " SHORT INTEGER NOT NULL);";
    
    private static final String DATABASE_CONVERSATION_CREATE = "CREATE TABLE " + DATABASE_CONVERSATION_TABLE + " ("
            + KEY_CONVERSATIONID + " INTEGER NOT NULL, "
            + KEY_USERID + " INTEGER NOT NULL CONSTRAINT fk_conversation_iduser REFERENCES " + DATABASE_USER_TABLE + "(" + KEY_USERID + "), "
            + KEY_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
            + "PRIMARY KEY(" + KEY_CONVERSATIONID + ", " + KEY_USERID + "));";
    
    private static final String DATABASE_MEDIA_CREATE = "CREATE TABLE " + DATABASE_MEDIA_TABLE + " ("
            + KEY_MEDIAID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_LOCATION + " TEXT NOT NULL, "
            + KEY_TYPE + " SHORT INTEGER NOT NULL);";
    
    private static final String DATABASE_MESSAGE_CREATE = "CREATE TABLE " + DATABASE_MESSAGE_TABLE + " ("
            + KEY_CONVERSATIONID + " INTEGER NOT NULL CONSTRAINT fk_message_idconversation REFERENCES " + DATABASE_CONVERSATION_TABLE + "(" + KEY_CONVERSATIONID + "), "
            + KEY_USERID + " INTEGER NOT NULL CONSTRAINT fk_message_iduser REFERENCES " + DATABASE_USER_TABLE + "(" + KEY_USERID + "), "
            + KEY_MEDIAID + " INTEGER CONSTRAINT fk_message_idmedia REFERENCES " + DATABASE_MEDIA_TABLE + "(" + KEY_MEDIAID + "), "
            + KEY_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
            + KEY_CONTENT + " TEXT, "
            + KEY_STATUS + " SHORT INTEGER DEFAULT " + NOT_SENT + " NOT NULL, "
            + "PRIMARY KEY(" + KEY_CONVERSATIONID + ", " + KEY_USERID + ", " + KEY_TIMESTAMP + "));";

    private final Context context;

    public DatabaseHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    
    public void open() throws SQLException {
        this.mDb = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_USER_CREATE);
        db.execSQL(DATABASE_CONVERSATION_CREATE);
        db.execSQL(DATABASE_MEDIA_CREATE);
        db.execSQL(DATABASE_MESSAGE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_CONVERSATION_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_MEDIA_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_MESSAGE_TABLE);
        
        onCreate(db);
    }
    
    public UserEntity createUser(Integer phone, String name, Short status) {
        ContentValues values = new ContentValues(); 
        values.put(KEY_NAME, name);
        values.put(KEY_PHONE, phone);
        values.put(KEY_STATUS, status);
        long userId = mDb.insert(DATABASE_USER_TABLE, null, values); 
        return new UserEntity((int)userId, name, phone, status);
    }
    
    public boolean deleteUser(Integer userId) {
        boolean result = mDb.delete(DATABASE_USER_TABLE, KEY_USERID + "=?",new String[]{"" + userId}) > 0; 
        return result;
    }
    
    public boolean deleteUser(UserEntity user) {
        boolean result = mDb.delete(DATABASE_USER_TABLE, KEY_USERID + "=?",new String[]{"" + user.getUserId()}) > 0;
        return result;
    }
    
    public List<UserEntity> fetchAllUsers() {
        Cursor cursor = mDb.query(DATABASE_USER_TABLE, new String[]{KEY_USERID, KEY_NAME, KEY_PHONE, KEY_STATUS}, null, null, null, null, KEY_USERID + " ASC");
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
        Cursor cursor = mDb.query(true, DATABASE_USER_TABLE, new String [] 
             {KEY_USERID, KEY_NAME, KEY_PHONE, KEY_STATUS}, KEY_USERID + 
             "=?", new String[]{"" + userId}, null, null, null, null); 
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
        boolean result = mDb.update(DATABASE_USER_TABLE, values, KEY_USERID + "=?", new String[]{"" + user.getUserId()}) > 0; 
        return result;
    }
    
    public ConversationEntity createConversation(UserEntity user)
    {
        return this.createConversation(user, null);
    }
    
    public ConversationEntity createConversation(UserEntity user, Calendar date) {
        ConversationEntity result = null;
        if (user != null)
        {
            result = new ConversationEntity(this.fetchLastConversationId(),date);
            result.addUser(user);
            ContentValues initialValues = new ContentValues(); 
            initialValues.put(KEY_USERID, user.getUserId());
            if (date != null)
            {
                initialValues.put(KEY_DATE, date.getTime().toString());
            }
            initialValues.put(KEY_CONVERSATIONID, result.getConversationId());
            mDb.insert(DATABASE_CONVERSATION_TABLE, null, initialValues);
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
        mDb.insert(DATABASE_CONVERSATION_TABLE, null, initialValues);
        return conversation;
    }
    
    public boolean deleteConversation(ConversationEntity conversation) {
        boolean result = mDb.delete(DATABASE_CONVERSATION_TABLE, KEY_CONVERSATIONID + "=" + conversation.getConversationId(), null) > 0; 
        return result;
    }
    
    public List<ConversationEntity> fetchAllConversations() {
        Cursor cursor = mDb.query(DATABASE_CONVERSATION_TABLE, new String[]{KEY_CONVERSATIONID, KEY_USERID, KEY_DATE}, null, null, null, null, KEY_DATE + " DESC");
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
                cE.addUser(this.fetchUser(cursor.getInt(cursor.getColumnIndex(this.KEY_USERID))));
            } while (cursor.moveToNext());
        }
        return list;
    }
    
    public ConversationEntity fetchConversation(ConversationEntity conversation) throws SQLException {
        Cursor cursor = mDb.query(true, DATABASE_CONVERSATION_TABLE, new String [] 
             {KEY_CONVERSATIONID, KEY_USERID, KEY_DATE}, KEY_CONVERSATIONID + 
             "=?", new String[]{"" + conversation.getConversationId()}, null, null, null, KEY_DATE + " DESC"); 
        ConversationEntity cE = null;
        if (cursor != null) 
        {
            cE = new ConversationEntity(conversation.getConversationId(), cursor.getString(cursor.getColumnIndex(this.KEY_DATE)));
            cursor.moveToFirst();
            do
            {
                DatabaseHelper user = new DatabaseHelper(this.context);
                cE.addUser(user.fetchUser(cursor.getInt(cursor.getColumnIndex(this.KEY_USERID))));
            }while (cursor.moveToNext());
        } 
        return cE;
    }
    
    private Integer fetchLastConversationId() throws SQLException { 
        Cursor cursor = mDb.query(true, DATABASE_CONVERSATION_TABLE, new String [] {KEY_CONVERSATIONID},
                null,
                null,
                null,
                null,
                KEY_DATE,
                "1"); 
        if (cursor != null && cursor.getCount() > 0) 
        {
            cursor.moveToFirst();
            return cursor.getInt(0);
        }
        return 1;
    }
    
    public boolean updateConversation(ConversationEntity conversation) {
        for(UserEntity user : conversation.getUsers())
        {
            ContentValues args = new ContentValues(); 
            args.put(KEY_USERID, user.getUserId()); 
            args.put(KEY_DATE, conversation.getLast_message_time().getTime().toString());
            boolean result = mDb.update(DATABASE_CONVERSATION_TABLE, args, KEY_CONVERSATIONID + "=" + conversation.getConversationId(), null) > 0;
            if (!result) 
            {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }
    
    public MediaEntity createMedia(String location, Short type) {
        ContentValues values = new ContentValues(); 
        values.put(KEY_LOCATION, location);
        values.put(KEY_TYPE, type);
        long mediaId = mDb.insert(DATABASE_MEDIA_TABLE, null, values);
        return new MediaEntity((int) mediaId, location, type);
    }
    
    public boolean deleteMedia(Integer mediaId) { 
        boolean result = mDb.delete(DATABASE_MEDIA_TABLE, KEY_MEDIAID + "=?",new String[]{"" + mediaId}) > 0;
        return result;
    }
    
    public List<MediaEntity> fetchAllMedia() { 
        Cursor cursor = mDb.query(DATABASE_MEDIA_TABLE, new String[]{KEY_MEDIAID, KEY_LOCATION, KEY_TYPE}, null, null, null, null, KEY_MEDIAID + " ASC"); 
        List<MediaEntity> list = new ArrayList<MediaEntity>();
        if (cursor != null)
        {
            cursor.moveToFirst();
            do
            {
                MediaEntity mE = new MediaEntity(cursor.getInt(cursor.getColumnIndex(this.KEY_MEDIAID)),
                        cursor.getString(cursor.getColumnIndex(this.KEY_LOCATION)),
                        cursor.getShort(cursor.getColumnIndex(this.KEY_TYPE)));
                list.add(mE);
            } while (!cursor.moveToNext());
        }
        return list;
    }
    
    public MediaEntity fetchMedia(Integer mediaId) throws SQLException {
        Cursor cursor = mDb.query(true, DATABASE_MEDIA_TABLE, new String [] 
            {KEY_MEDIAID, KEY_LOCATION, KEY_TYPE}, KEY_MEDIAID + 
            "=?", new String[]{"" + mediaId}, null, null, null, null); 
        MediaEntity mE = null;
        if (cursor != null) 
        { 
            cursor.moveToFirst(); 
            mE = new MediaEntity(cursor.getInt(cursor.getColumnIndex(this.KEY_MEDIAID)),
                    cursor.getString(cursor.getColumnIndex(this.KEY_LOCATION)),
                    cursor.getShort(cursor.getColumnIndex(this.KEY_TYPE)));
        } 
        return mE;
    }
    
    public boolean updateMedia(Integer mediaId, String location, Short type) { 
        ContentValues values = new ContentValues(); 
        values.put(KEY_LOCATION, location); 
        values.put(KEY_TYPE, type);
        boolean result = mDb.update(DATABASE_MEDIA_TABLE, values, KEY_MEDIAID + "=?", new String[]{"" + mediaId}) > 0;
        return result;
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
          this.mDb.insert(DATABASE_MESSAGE_TABLE, null, values);
          return new MessageEntity(conversation, user, media, date, content, status);
    }
    
    public boolean deleteMessage(ConversationEntity conversation, UserEntity user, Calendar date) { 
        boolean result = this.mDb.delete(DATABASE_MESSAGE_TABLE, KEY_CONVERSATIONID + " =? AND " + KEY_USERID + " =? AND " + KEY_TIMESTAMP + " =? ", new String[]{"" + conversation.getConversationId(), "" + user.getUserId(), date.getTime().toString()}) > 0; 
        return result;
    }
    
    public boolean deleteMessages(ConversationEntity conversation)
    {
        boolean result = this.mDb.delete(DATABASE_MESSAGE_TABLE, KEY_CONVERSATIONID + " =? ", new String[]{"" + conversation.getConversationId()}) > 0;
        return result;
    }
    
    public List<MessageEntity> fetchAllMessages() {
        List<MessageEntity> list = new ArrayList<MessageEntity>();
        for (ConversationEntity cE : this.fetchAllConversations())
        {
            list.addAll(fetchMessages(cE));
        }
        return list;
    }
    
    public List<MessageEntity> fetchMessages(ConversationEntity conversation) throws SQLException {
        Cursor cursor = this.mDb.query(true, DATABASE_MESSAGE_TABLE, new String [] 
             {KEY_CONVERSATIONID, KEY_USERID, KEY_CONTENT, KEY_MEDIAID, KEY_STATUS, KEY_TIMESTAMP},
              KEY_CONVERSATIONID + "=?", 
              new String[]{"" + conversation.getConversationId()}, null, null, null, KEY_TIMESTAMP + " ASC");
        List<MessageEntity> list = new ArrayList<MessageEntity>();
        if (cursor != null) 
        { 
            cursor.moveToFirst(); 
            do
            {
                ConversationEntity cE = this.fetchConversation(conversation);
                DatabaseHelper user = new DatabaseHelper(this.context);
                UserEntity uE = user.fetchUser(cursor.getInt(cursor.getColumnIndex(KEY_USERID)));
                MediaEntity mE = this.fetchMedia(cursor.getInt(cursor.getColumnIndex(KEY_MEDIAID)));
                Calendar date = Calendar.getInstance();
                date.setTime(new Date(cursor.getString(cursor.getColumnIndex(KEY_TIMESTAMP))));
                list.add(new MessageEntity(cE, uE, mE, date, cursor.getString(cursor.getColumnIndex(KEY_CONTENT)), cursor.getShort(cursor.getColumnIndex(KEY_STATUS))));
            } while (!cursor.moveToNext());
        } 
        return list;
    }
    
    public List<MessageEntity> fetchMessages(String content) throws SQLException {
        Cursor cursor = this.mDb.query(true, DATABASE_MESSAGE_TABLE, new String [] 
             {KEY_CONVERSATIONID, KEY_USERID, KEY_CONTENT, KEY_MEDIAID, KEY_STATUS, KEY_TIMESTAMP},
              KEY_CONTENT + " like '%?%'", 
              new String[]{content}, null, null, null, KEY_TIMESTAMP + " ASC"); 
        List<MessageEntity> list = new ArrayList<MessageEntity>();
        if (cursor != null) 
        { 
            cursor.moveToFirst();
            do
            {
                ConversationEntity cE = this.fetchConversation(new ConversationEntity(cursor.getInt(cursor.getColumnIndex(KEY_CONVERSATIONID))));
                DatabaseHelper user = new DatabaseHelper(this.context);
                UserEntity uE = user.fetchUser(cursor.getInt(cursor.getColumnIndex(KEY_USERID)));
                MediaEntity mE = this.fetchMedia(cursor.getInt(cursor.getColumnIndex(KEY_MEDIAID)));
                Calendar date = Calendar.getInstance();
                date.setTime(new Date(cursor.getString(cursor.getColumnIndex(KEY_TIMESTAMP))));
                list.add(new MessageEntity(cE, uE, mE, date, cursor.getString(cursor.getColumnIndex(KEY_CONTENT)), cursor.getShort(cursor.getColumnIndex(KEY_STATUS))));
            } while (cursor.moveToNext());
        } 
        return list;
    }
    
    public boolean updateMessage(ConversationEntity conversation, UserEntity user, MediaEntity media, Calendar date, String description, Short status) { 
          ContentValues values = new ContentValues(); 
          if (media != null)
          {
              values.put(KEY_MEDIAID, media.getMediaId());
          }
          values.put(KEY_CONTENT, description);
          values.put(KEY_STATUS, status);
          boolean result = this.mDb.update(DATABASE_MESSAGE_TABLE, values, KEY_CONVERSATIONID + " =? AND " + KEY_USERID + " =? AND " + KEY_TIMESTAMP + " =? ", new String[]{"" + conversation.getConversationId(), "" + user.getUserId(),"" + date.getTime().toString()}) > 0; 
          return result;
    }
}