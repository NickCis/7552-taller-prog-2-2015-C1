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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
    public static final String DATABASE_LOGIN_TABLE = "login";
    public static final String KEY_CONVERSATIONID = "idconversation";
    public static final String KEY_MEDIAID = "idmedia";
    public static final String KEY_USERID = "iduser";
    public static final String KEY_LOGINID = "idlogin";
    public static final String KEY_DATE = "last_message_time";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_NICKNAME = "nickname";
    public static final String KEY_STATUS = "status";
    public static final String KEY_LAST_TIME = "last_time";
    public static final String KEY_CHECKIN_LAT = "latitud";
    public static final String KEY_CHECKIN_LON = "longitud";
    public static final String KEY_STATUS_MESSAGE = "status_message";
    public static final String KEY_LOCATION = "location";
    public static final String KEY_TYPE = "type";
    public static final String KEY_CONTENT = "content";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_AVATAR = "avatar";
    
    public static final short NOT_SENT = 0;
    public static final short SENT = NOT_SENT + 1;
    public static final short NOT_RECIEVED = NOT_SENT + 1;
    public static final short NOT_SEEN = NOT_RECIEVED + 1;
    public static final short SEEN = NOT_SEEN + 1;
    
    public static final short NORMAL = 0;
    public static final short BLOKED = NORMAL + 1;

    public static final short STATUS_ONLINE = 0 ;
    public static final short STATUS_OFFLINE = STATUS_ONLINE + 1 ;
    public static final short STATUS_COUNT = STATUS_OFFLINE + 1;
    
    public static final String STATUS_TEXT_ONLINE = "Online";
    public static final String STATUS_TEXT_OFFLINE = "Offline";
    
    private UserEntity userMe;
    private LoginEntity login;

    private SQLiteDatabase mDb;
    
    private static final String DATABASE_LOGIN_CREATE = "CREATE TABLE " + DATABASE_LOGIN_TABLE + " ("
            + KEY_LOGINID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_USERNAME + " TEXT NOT NULL);";
    
    private static final String DATABASE_USER_CREATE = "CREATE TABLE " + DATABASE_USER_TABLE + " ("
            + KEY_USERID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_PHONE + " INTEGER NOT NULL, "
            + KEY_USERNAME + " TEXT NOT NULL, "
            + KEY_NICKNAME + " TEXT NOT NULL, "
            + KEY_STATUS + " SHORT INTEGER NOT NULL, "
            + KEY_AVATAR + " BLOB, "
            + KEY_LAST_TIME + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
            + KEY_STATUS_MESSAGE + " TEXT, "
            + KEY_CHECKIN_LAT + " REAL, "
            + KEY_CHECKIN_LON + " REAL, "
            + KEY_LOGINID + " INTEGER NOT NULL CONSTRAINT fk_user_idlogin REFERENCES " + DATABASE_LOGIN_TABLE + "(" + KEY_LOGINID + "));";
    
    private static final String DATABASE_CONVERSATION_CREATE = "CREATE TABLE " + DATABASE_CONVERSATION_TABLE + " ("
            + KEY_CONVERSATIONID + " INTEGER NOT NULL, "
            + KEY_USERID + " INTEGER NOT NULL CONSTRAINT fk_conversation_iduser REFERENCES " + DATABASE_USER_TABLE + "(" + KEY_USERID + "), "
            + KEY_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
            + KEY_LOGINID + " INTEGER NOT NULL CONSTRAINT fk_conversation_idlogin REFERENCES " + DATABASE_LOGIN_TABLE + "(" + KEY_LOGINID + "), "
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

    private Context context;
    private static DatabaseHelper instance = null;

    public static DatabaseHelper getInstance(Context context)
    {
        if (instance == null)
        {
            instance = new DatabaseHelper(context);
        }
        else
        {
            DatabaseHelper dbH = new DatabaseHelper(context);
            dbH.userMe = instance.userMe;
            dbH.login = instance.login;
            instance = dbH;
        }
        return instance;
    }
    
    private DatabaseHelper (Context context) 
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    
    public void open() throws SQLException 
    {
        this.mDb = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) 
    {
        db.execSQL(DATABASE_LOGIN_CREATE);
        db.execSQL(DATABASE_USER_CREATE);
        db.execSQL(DATABASE_CONVERSATION_CREATE);
        db.execSQL(DATABASE_MEDIA_CREATE);
        db.execSQL(DATABASE_MESSAGE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
    {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_LOGIN_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_CONVERSATION_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_MEDIA_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_MESSAGE_TABLE);
        
        onCreate(db);
    }
    
    public UserEntity login(String username)
    {
        this.login = fetchLogin(username);
        if (this.login == null)
        {
            ContentValues values = new ContentValues();
            values.put(KEY_USERNAME, username);
            long loginId = mDb.insert(DATABASE_LOGIN_TABLE, null, values);
            this.login = fetchLogin((int)loginId);
            this.setUserMe(createUser(11111, username, username + " Nick", this.STATUS_ONLINE, null, null, "Hola! Estoy en MensajerO!", null));
        }
        else
        {
            this.setUserMe(fetchUser(username));
        }
        return this.getUserMe();
    }

    public LoginEntity fetchLogin(String username)
    {
        LoginEntity lE = null;
        Cursor cursor = mDb.query(DATABASE_LOGIN_TABLE, new String[]{KEY_LOGINID, KEY_USERNAME}, KEY_USERNAME + "=?", new String[]{"" + username}, null, null, null);
        if (cursor != null && cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            lE = new LoginEntity(cursor.getInt(cursor.getColumnIndex(KEY_LOGINID)), cursor.getString(cursor.getColumnIndex(KEY_USERNAME)));
        }
        return lE;
    }
    
    public LoginEntity fetchLogin(Integer loginId)
    {
        LoginEntity lE = null;
        Cursor cursor = mDb.query(DATABASE_LOGIN_TABLE, new String[]{KEY_LOGINID, KEY_USERNAME}, KEY_LOGINID + "=?", new String[]{"" + loginId}, null, null, null);
        if (cursor != null && cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            lE = new LoginEntity(cursor.getInt(cursor.getColumnIndex(KEY_LOGINID)), cursor.getString(cursor.getColumnIndex(KEY_USERNAME)));
        }
        return lE;
    }
    
    public LoginEntity fetchLogin(LoginEntity login)
    {
        LoginEntity lE = null;
        if (login != null)
        {
            if (login.getLoginId() != null)
            {
                lE = fetchLogin(login.getLoginId());
            }
            else if (login.getUsername() != null)
            {
                lE = fetchLogin(login.getUsername());
            }
        }
        return lE;
    }
    
    public boolean deleteLogin(Integer loginId)
    {
        return mDb.delete(DATABASE_LOGIN_TABLE, KEY_LOGINID + "=?", new String[]{"" + loginId}) > 0;
    }
    
    public boolean deleteLogin(String username)
    {
        return mDb.delete(DATABASE_LOGIN_TABLE, KEY_USERNAME + "=?", new String[]{"" + username}) > 0;
    }
    
    public boolean deleteLogin(LoginEntity login) 
    {
        return mDb.delete(DATABASE_LOGIN_TABLE, KEY_LOGINID + "=?", new String[]{"" + login.getLoginId()}) > 0;
    }
    
    /**
     *
     * @param phone
     * @param username
     * @param nickname
     * @param status
     * @param avatar
     * @param lastTime
     * @param statusMessage
     * @param lastTime
     * @return
     */
    public UserEntity createUser(Integer phone, String username, String nickname, Short status, Bitmap avatar, Calendar lastTime, String statusMessage, Checkin checkin) 
    {
        UserEntity uE = null;
        if (this.login != null)
        {
            ContentValues values = new ContentValues(); 
            values.put(KEY_USERNAME, username);
            values.put(KEY_NICKNAME, nickname);
            values.put(KEY_PHONE, phone);
            values.put(KEY_STATUS, status);
            values.put(KEY_LOGINID, this.login.getLoginId());
            if (avatar != null)
            {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                avatar.compress(Bitmap.CompressFormat.PNG, 100, stream);
                values.put(KEY_AVATAR, stream.toByteArray());
            }
            if (lastTime != null)
            {
                values.put(KEY_LAST_TIME, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS").format(lastTime.getTime()));
            }
            values.put(KEY_STATUS_MESSAGE, statusMessage);
            if (checkin != null)
            {
                values.put(KEY_CHECKIN_LAT, checkin.getLatitude());
                values.put(KEY_CHECKIN_LON, checkin.getLongitude());
            }
            long userId = mDb.insert(DATABASE_USER_TABLE, null, values); 
            uE = new UserEntity((int)userId, username, nickname, phone, status, avatar, lastTime, statusMessage, checkin);
        }
        return uE;
    }
    
    public boolean deleteUser(Integer userId) 
    {
        return mDb.delete(DATABASE_USER_TABLE, KEY_USERID + "=?", new String[]{"" + userId}) > 0;
    }
    
    public boolean deleteUser(UserEntity user) 
    {
        return mDb.delete(DATABASE_USER_TABLE, KEY_USERID + "=?", new String[]{"" + user.getUserId()}) > 0;
    }
    
    public List<UserEntity> fetchAllUsers() 
    {
        List<UserEntity> list = null;
        if (this.login != null)
        {
            Cursor cursor = mDb.query(DATABASE_USER_TABLE, new String[]{KEY_USERID, KEY_USERNAME, KEY_NICKNAME, KEY_PHONE, KEY_STATUS, KEY_AVATAR, KEY_LAST_TIME, KEY_STATUS_MESSAGE, KEY_CHECKIN_LAT, KEY_CHECKIN_LON}, KEY_LOGINID + "=?", new String[]{"" + this.login.getLoginId()}, null, null, KEY_USERID + " ASC");
            if (cursor != null && cursor.getCount() > 0)
            {
                list = new ArrayList<UserEntity>();
                cursor.moveToFirst();
                do
                {
                    byte[] img = cursor.getBlob(cursor.getColumnIndex(this.KEY_AVATAR));
                    UserEntity uE = new UserEntity(cursor.getInt(cursor.getColumnIndex(this.KEY_USERID)),
                            cursor.getString(cursor.getColumnIndex(this.KEY_USERNAME)),
                            cursor.getString(cursor.getColumnIndex(this.KEY_NICKNAME)),
                            cursor.getInt(cursor.getColumnIndex(this.KEY_PHONE)),
                            cursor.getShort(cursor.getColumnIndex(this.KEY_STATUS)),
                            (img == null) ? null : BitmapFactory.decodeByteArray(img , 0, img.length),
                            parseDate(cursor.getString(cursor.getColumnIndex(this.KEY_LAST_TIME))),
                            cursor.getString(cursor.getColumnIndex(this.KEY_STATUS_MESSAGE)),
                            new Checkin(cursor.getDouble(cursor.getColumnIndex(this.KEY_CHECKIN_LAT)), cursor.getDouble(cursor.getColumnIndex(this.KEY_CHECKIN_LON))));
                    list.add(uE);
                } while (cursor.moveToNext());
            }
        }
        return list;
    }
    
    public UserEntity fetchUser(Integer userId) throws SQLException 
    {
        UserEntity uE = null;
        Cursor cursor = mDb.query(true, DATABASE_USER_TABLE, new String [] 
             {KEY_USERID, KEY_USERNAME, KEY_NICKNAME, KEY_PHONE, KEY_STATUS, KEY_LOGINID, KEY_AVATAR, KEY_LAST_TIME, KEY_STATUS_MESSAGE, KEY_CHECKIN_LAT, KEY_CHECKIN_LON}, KEY_USERID + 
             "=?", new String[]{"" + userId}, null, null, null, null); 
        if (cursor != null && cursor.getCount() > 0) 
        {
            cursor.moveToFirst();
            byte[] img = cursor.getBlob(cursor.getColumnIndex(this.KEY_AVATAR));
            uE = new UserEntity(userId,
                    cursor.getString(cursor.getColumnIndex(KEY_USERNAME)),
                    cursor.getString(cursor.getColumnIndex(KEY_NICKNAME)),
                    cursor.getInt(cursor.getColumnIndex(KEY_PHONE)),
                    cursor.getShort(cursor.getColumnIndex(KEY_STATUS)),
                    (img == null) ? null : BitmapFactory.decodeByteArray(img , 0, img.length),
                    parseDate(cursor.getString(cursor.getColumnIndex(this.KEY_LAST_TIME))),
                    cursor.getString(cursor.getColumnIndex(this.KEY_STATUS_MESSAGE)),
                    new Checkin(cursor.getDouble(cursor.getColumnIndex(this.KEY_CHECKIN_LAT)), cursor.getDouble(cursor.getColumnIndex(this.KEY_CHECKIN_LON))));
        }
        return uE;
    }
    
    public UserEntity fetchUser(String userName) throws SQLException 
    {
        UserEntity uE = null;
        if (this.login != null)
        {
            Cursor cursor = mDb.query(true, DATABASE_USER_TABLE, new String [] 
                 {KEY_USERID, KEY_USERNAME, KEY_NICKNAME, KEY_PHONE, KEY_STATUS, KEY_AVATAR, KEY_LAST_TIME, KEY_STATUS_MESSAGE, KEY_CHECKIN_LAT, KEY_CHECKIN_LON}, KEY_USERNAME + 
                 "=? AND " + KEY_LOGINID + "=?", new String[]{"" + userName, "" + this.login.getLoginId()}, null, null, null, null);
            if (cursor != null && cursor.getCount() > 0) 
            { 
                cursor.moveToFirst();
                byte[] img = cursor.getBlob(cursor.getColumnIndex(this.KEY_AVATAR));
                uE = new UserEntity(cursor.getInt(cursor.getColumnIndex(this.KEY_USERID)),
                        userName,
                        cursor.getString(cursor.getColumnIndex(this.KEY_NICKNAME)),
                        cursor.getInt(cursor.getColumnIndex(this.KEY_PHONE)),
                        cursor.getShort(cursor.getColumnIndex(this.KEY_STATUS)),
                        (img == null) ? null : BitmapFactory.decodeByteArray(img , 0, img.length),
                        parseDate(cursor.getString(cursor.getColumnIndex(this.KEY_LAST_TIME))),
                        cursor.getString(cursor.getColumnIndex(this.KEY_STATUS_MESSAGE)),
                        new Checkin(cursor.getDouble(cursor.getColumnIndex(this.KEY_CHECKIN_LAT)), cursor.getDouble(cursor.getColumnIndex(this.KEY_CHECKIN_LON))));

            }
        }
        return uE;
    }
    
    public boolean updateUser(UserEntity user) 
    {
        if (this.login != null)
        {
            ContentValues values = new ContentValues(); 
            values.put(KEY_PHONE, user.getPhone()); 
            values.put(KEY_USERNAME, user.getUsername());
            values.put(KEY_NICKNAME, user.getNickname());
            values.put(KEY_STATUS, user.getStatus());
            if (user.getAvatar() != null)
            {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                user.getAvatar().compress(Bitmap.CompressFormat.PNG, 100, stream);
                values.put(KEY_AVATAR, stream.toByteArray());
            }
            if (user.getLastTime() != null)
            {
                values.put(KEY_LAST_TIME, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS").format(user.getLastTime().getTime()));
            }
            values.put(KEY_STATUS_MESSAGE, user.getStatusMessage());
            if (user.getCheckin() != null)
            {
                values.put(KEY_CHECKIN_LAT, user.getCheckin().getLatitude());
                values.put(KEY_CHECKIN_LON, user.getCheckin().getLongitude());
            }
            return mDb.update(DATABASE_USER_TABLE, values, KEY_USERID + "=? AND " + KEY_LOGINID + "=?", new String[]{"" + user.getUserId(), "" + this.login.getLoginId()}) > 0; 
        }            
        return false;
    }
    
    private ConversationEntity createConversation(Integer conversationId, UserEntity user, Calendar date)
    {
        if (this.login != null)
        {
            ContentValues initialValues = new ContentValues(); 
            if (date != null)
            {
                initialValues.put(KEY_DATE, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS").format(date.getTime()));
            }
            initialValues.put(KEY_CONVERSATIONID, conversationId);
            initialValues.put(KEY_USERID, user.getUserId());
            initialValues.put(KEY_LOGINID, this.login.getLoginId());
            mDb.insert(DATABASE_CONVERSATION_TABLE, null, initialValues);
        }
        return this.fetchConversation(conversationId);
    }
    
    public ConversationEntity createConversation(UserEntity user, Calendar date)
    {
        List<UserEntity> lista = new ArrayList<UserEntity>();
        lista.add(user);
        lista.add(this.getUserMe());
        return this.createConversation(lista, date);
    }
    
    public ConversationEntity createConversation(List<UserEntity> users, Calendar date) 
    {
        ConversationEntity result = null;
        if (users != null && this.login != null)
        {
            result = new ConversationEntity(this.fetchLastConversationId() + 1, date, users);
            ContentValues initialValues = new ContentValues(); 
            if (date != null)
            {
                initialValues.put(KEY_DATE, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS").format(date.getTime()));
            }
            initialValues.put(KEY_CONVERSATIONID, result.getConversationId());
            initialValues.put(KEY_LOGINID, this.login.getLoginId());
            
            for (UserEntity user : users)
            {
                initialValues.put(KEY_USERID, user.getUserId());
                mDb.insert(DATABASE_CONVERSATION_TABLE, null, initialValues);
                initialValues.remove(KEY_USERID);
            }
        }
        
        return result;
    }
    
    public boolean deleteConversation(ConversationEntity conversation) 
    { 
        return mDb.delete(DATABASE_CONVERSATION_TABLE, KEY_CONVERSATIONID + "=? AND " + KEY_LOGINID + "=?", new String[]{"" + conversation.getConversationId(), "" + this.login.getLoginId()}) > 0;
    }
    
    public List<ConversationEntity> fetchAllConversations() 
    {
        Cursor cursor = mDb.query(DATABASE_CONVERSATION_TABLE, new String[]{KEY_CONVERSATIONID, KEY_USERID, KEY_DATE}, KEY_LOGINID + "=?", new String[]{"" + this.login.getLoginId()}, null, null, KEY_DATE + " DESC");
        List<ConversationEntity> list = new ArrayList<ConversationEntity>();
        if (cursor != null && cursor.getCount() > 0)
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
            list.add(cE);
        }
        return list;
    }
    
    private ConversationEntity fetchConversation(List<UserEntity> list) throws SQLException 
    {
        String stringUsrList = "";
        for (UserEntity uE : list)
        {
            stringUsrList += String.valueOf(uE.getUserId()) + ",";
        }
        stringUsrList = stringUsrList.substring(0, stringUsrList.length() - 1);
        
        String query = KEY_CONVERSATIONID + " not in "
                + "(SELECT rconversationId FROM "
                + "(SELECT tbl1." + KEY_CONVERSATIONID + " as lconversationId, conv2." + KEY_CONVERSATIONID + " as rconversationId FROM "
                + DATABASE_CONVERSATION_TABLE + " as conv2 "
                + "LEFT JOIN (SELECT conv." + KEY_CONVERSATIONID + ", usr." + KEY_USERID + " FROM " + DATABASE_CONVERSATION_TABLE + " as conv, " + DATABASE_USER_TABLE + " as usr where usr." + KEY_USERID + " in (" + stringUsrList + ")) as tbl1 "
                + "ON conv2." + KEY_CONVERSATIONID + " = tbl1." + KEY_CONVERSATIONID + " "
                + "AND conv2." + KEY_USERID + " = tbl1." + KEY_USERID + ") as tbl "
                + "WHERE lconversationId is null);";
        
        Cursor cursor = mDb.query(true, DATABASE_CONVERSATION_TABLE, new String [] 
             {KEY_CONVERSATIONID}, query, null, null, null, null, null);
        
        ConversationEntity cE = null;
        if (cursor != null && cursor.getCount() > 0) 
        {
            cursor.moveToFirst();
            cE = this.fetchConversation(cursor.getInt(cursor.getColumnIndex(KEY_CONVERSATIONID)));
        }
        return cE;
    }
    
    public ConversationEntity fetchConversation(ConversationEntity conversation) throws SQLException 
    {
        ConversationEntity cE = null;
        if (conversation.getConversationId() == null)
        {
            if (conversation.getUsers() != null)
            {
                cE = this.fetchConversation(conversation.getUsers());
            }
        } 
        else 
        {
            cE = this.fetchConversation(conversation.getConversationId());
        }
        return cE;
    }

    private ConversationEntity fetchConversation(Integer conversationId) throws SQLException 
    {
        ConversationEntity cE = null;
        if (this.login != null)
        {
            Cursor cursor = mDb.query(true, DATABASE_CONVERSATION_TABLE, new String [] 
                 {KEY_CONVERSATIONID, KEY_USERID, KEY_DATE}, KEY_CONVERSATIONID + 
                 "=?", new String[]{"" + conversationId}, null, null, KEY_DATE + " DESC", null); 
            if (cursor != null && cursor.getCount() > 0) 
            {
                cursor.moveToFirst();
                cE = new ConversationEntity(conversationId, cursor.getString(cursor.getColumnIndex(KEY_DATE)));
                do
                {
                    cE.addUser(this.fetchUser(cursor.getInt(cursor.getColumnIndex(KEY_USERID))));
                }while (cursor.moveToNext());
            }
        }
        return cE;
    }
    
    private Integer fetchLastConversationId() throws SQLException { 
        Cursor cursor = mDb.query(true, DATABASE_CONVERSATION_TABLE, new String [] {KEY_CONVERSATIONID},
                null,
                null,
                null,
                null,
                KEY_CONVERSATIONID + " DESC",
                "1"); 
        if (cursor != null && cursor.getCount() > 0) 
        {
            cursor.moveToFirst();
            return cursor.getInt(0);
        }
        return 0;
    }
    
    public boolean updateConversation(ConversationEntity conversation) {
        if (this.login != null)
        {
            for(UserEntity user : conversation.getUsers())
            {
                ContentValues args = new ContentValues(); 
                args.put(KEY_USERID, user.getUserId()); 
                args.put(KEY_DATE, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS").format(conversation.getLast_message_time().getTime()));
                boolean result = mDb.update(DATABASE_CONVERSATION_TABLE, args, KEY_CONVERSATIONID + "=? AND " + KEY_LOGINID + "=?", new String[]{"" + conversation.getConversationId(), "" + this.login.getLoginId()}) > 0;
                if (!result) 
                {
                    this.createConversation(conversation.getConversationId(), user, conversation.getLast_message_time());
                }
            }
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    
    public MediaEntity createMedia(String location, Short type) {
        ContentValues values = new ContentValues(); 
        values.put(KEY_LOCATION, location);
        values.put(KEY_TYPE, type);
        long mediaId = mDb.insert(DATABASE_MEDIA_TABLE, null, values);
        return new MediaEntity((int) mediaId, location, type);
    }
    
    public boolean deleteMedia(Integer mediaId) 
    { 
        return mDb.delete(DATABASE_MEDIA_TABLE, KEY_MEDIAID + "=?",new String[]{"" + mediaId}) > 0;
    }
    
    public List<MediaEntity> fetchAllMedia() { 
        Cursor cursor = mDb.query(DATABASE_MEDIA_TABLE, new String[]{KEY_MEDIAID, KEY_LOCATION, KEY_TYPE}, null, null, null, null, KEY_MEDIAID + " ASC"); 
        List<MediaEntity> list = new ArrayList<MediaEntity>();
        if (cursor != null && cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            do
            {
                MediaEntity mE = new MediaEntity(cursor.getInt(cursor.getColumnIndex(this.KEY_MEDIAID)),
                        cursor.getString(cursor.getColumnIndex(this.KEY_LOCATION)),
                        cursor.getShort(cursor.getColumnIndex(this.KEY_TYPE)));
                list.add(mE);
            } while (cursor.moveToNext());
        }
        return list;
    }
    
    public MediaEntity fetchMedia(Integer mediaId) throws SQLException {
        Cursor cursor = mDb.query(true, DATABASE_MEDIA_TABLE, new String [] 
            {KEY_MEDIAID, KEY_LOCATION, KEY_TYPE}, KEY_MEDIAID + 
            "=?", new String[]{"" + mediaId}, null, null, null, null); 
        MediaEntity mE = null;
        if (cursor != null && cursor.getCount() > 0) 
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
              values.put(KEY_TIMESTAMP, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss::SS").format(date.getTime()));
          }
          else
          {
              values.put(KEY_TIMESTAMP, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS").format(Calendar.getInstance().getTime()));
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
    
    public boolean deleteMessage(ConversationEntity conversation, UserEntity user, Calendar date) 
    { 
        return this.mDb.delete(DATABASE_MESSAGE_TABLE, KEY_CONVERSATIONID + " =? AND " + KEY_USERID + " =? AND " + KEY_TIMESTAMP + " =? ", new String[]{"" + conversation.getConversationId(), "" + user.getUserId(), date.getTime().toString()}) > 0;
    }
    
    public boolean deleteMessages(ConversationEntity conversation)
    {
        return this.mDb.delete(DATABASE_MESSAGE_TABLE, KEY_CONVERSATIONID + " =? ", new String[]{"" + conversation.getConversationId()}) > 0;
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
              new String[]{"" + conversation.getConversationId()}, null, null, KEY_TIMESTAMP + " ASC", null);
        List<MessageEntity> list = new ArrayList<MessageEntity>();
        if (cursor != null && cursor.getCount() > 0)
        { 
            cursor.moveToFirst(); 
            do
            {
                ConversationEntity cE = this.fetchConversation(conversation);
                UserEntity uE = this.fetchUser(cursor.getInt(cursor.getColumnIndex(KEY_USERID)));
                MediaEntity mE = this.fetchMedia(cursor.getInt(cursor.getColumnIndex(KEY_MEDIAID)));
                Calendar cal = Calendar.getInstance();
                try
                {
                    cal.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS").parse(cursor.getString(cursor.getColumnIndex(KEY_TIMESTAMP))));
                }catch (ParseException pE)
                {
                    System.out.println(pE.getMessage());
                }
                list.add(new MessageEntity(cE, uE, mE, cal, cursor.getString(cursor.getColumnIndex(KEY_CONTENT)), cursor.getShort(cursor.getColumnIndex(KEY_STATUS))));
            } while (cursor.moveToNext());
        } 
        return list;
    }
    
    public List<MessageEntity> fetchMessages(String content) throws SQLException {
        Cursor cursor = this.mDb.query(true, DATABASE_MESSAGE_TABLE, new String [] 
             {KEY_CONVERSATIONID, KEY_USERID, KEY_CONTENT, KEY_MEDIAID, KEY_STATUS, KEY_TIMESTAMP},
              KEY_CONTENT + " like '%?%'", 
              new String[]{content}, null, null, null, KEY_TIMESTAMP + " ASC"); 
        List<MessageEntity> list = new ArrayList<MessageEntity>();
        if (cursor != null && cursor.getCount() > 0) 
        { 
            cursor.moveToFirst();
            do
            {
                ConversationEntity cE = this.fetchConversation(new ConversationEntity(cursor.getInt(cursor.getColumnIndex(KEY_CONVERSATIONID))));
                UserEntity uE = this.fetchUser(cursor.getInt(cursor.getColumnIndex(KEY_USERID)));
                MediaEntity mE = this.fetchMedia(cursor.getInt(cursor.getColumnIndex(KEY_MEDIAID)));
                Calendar cal = Calendar.getInstance();
                try
                {
                    cal.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS").parse(cursor.getString(cursor.getColumnIndex(KEY_TIMESTAMP))));
                }catch (ParseException pE)
                {
                    System.out.println(pE.getMessage());
                }
                list.add(new MessageEntity(cE, uE, mE, cal, cursor.getString(cursor.getColumnIndex(KEY_CONTENT)), cursor.getShort(cursor.getColumnIndex(KEY_STATUS))));
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

    public ConversationEntity fetchConversation(String userName){
	    UserEntity ue = fetchUser(userName);
	    if (ue == null){
		    //TODO: cambiar esto, el nickname tiene q ser posta
		    ue = createUser(0, userName, userName, DatabaseHelper.NORMAL, null, null, "Hola! Estoy en Whatsapp!", null);
	    }
	    ArrayList<UserEntity> list = new ArrayList<UserEntity>();
	    list.add(ue);
	    list.add(this.getUserMe());
	    ConversationEntity ce = fetchConversation(list);

	    ce = ( ce != null ) ? ce : createConversation(list,null);
	    return ce;
    }

    /**
     * @return the userMe
     */
    public UserEntity getUserMe() {
        return userMe;
    }

    /**
     * @param userMe the userMe to set
     */
    public void setUserMe(UserEntity userMe) {
        this.userMe = userMe;
    }
    
    private Calendar parseDate(String date)
    {
        Calendar cal = Calendar.getInstance();
        try {
            if (date != null)
            {
                cal.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date));
            }
        } catch (ParseException pE) 
        {
                System.out.println(pE.getCause().getMessage());
        }
        return cal;
    }
}