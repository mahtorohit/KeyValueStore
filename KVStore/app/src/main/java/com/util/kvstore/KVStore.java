package com.util.kvstore;


import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by Rohit on 08/05/16.
 */
public class KVStore {
    protected static DatabaseHelper helper;
    private static SQLiteDatabase database;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();
    private static KVStore sInstance;
    private  Context context ;

    public synchronized static KVStore getInstance(Context ctx) {
        if (sInstance == null) {
            sInstance = new KVStore(ctx.getApplicationContext());
        }
        return sInstance;
    }

    KVStore(Context context) {
        this.context = context;
        helper = DatabaseHelper.getInstance(context);
    }

    void openDB() {
            if (helper != null && (database == null || !database.isOpen())) {
                database = helper.getWritableDatabase();
            }
    }

    void closeDB() {

            if (helper != null) {
                database.close();
            }

            if (database != null && database.isOpen()) {
                database.close();
                database = null;
            }

    }

    /**
     *
     * @param key : Key for KV Store
     * @param val : value against the Key ( int )
     */
    public void putInt(String key, int val) {
        if (TextUtils.isEmpty(key)) {
            Log.d("not alowed", "Empty data");
            return;
        }
        this.sync(key , String.valueOf(val), KVUtil.DATATYPE.INT);
    }

    /**
     *
     * @param key : Key for KV Store
     * @param val : value against the Key ( Long )
     */
    public void putLong(String key, long val) {
        if (TextUtils.isEmpty(key)) {
            Log.d("not alowed", "Empty data");
            return;
        }
        this.sync(key , String.valueOf(val), KVUtil.DATATYPE.LONG);
    }

    /**
     *
     * @param key : Key for KV Store
     * @param val : value against the Key ( Boolean )
     */
    public void putBool(String key, Boolean val) {
        if (TextUtils.isEmpty(key)) {
            Log.d("not alowed", "Empty data");
            return;
        }
        this.sync(key , val.toString(), KVUtil.DATATYPE.BOOL);
    }

    /**
     *
     * @param key : Key for KV Store
     * @param val : value against the Key ( Date )
     */
    public void putDate(String key, Date val) {
        if (TextUtils.isEmpty(key) && val != null) {
            Log.d("not alowed", "Empty data");
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        String dateStr = sdf.format(val);
        this.sync(key , dateStr , KVUtil.DATATYPE.DATE);
    }

    /**
     *
     * @param key : Key for KV Store
     * @param val : value against the Key ( String )
     */
    public void putString(String key, String val) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(val)) {
            Log.d("not alowed", "Empty data");
            return;
        }
        this.sync(key,val, KVUtil.DATATYPE.STRING);
    }

    /**
     *
     * @param Keys : Object list of KVPair
     */
    public  void putStringSet(List<KVPair> Keys) {
        if(Keys == null || Keys.size() ==0)
        {
            return;
        }
        writeLock.lock();
        openDB();
        database.beginTransaction();
        try {
            for (KVPair data : Keys) {
                if(TextUtils.isEmpty(data.getV())){
                    continue;
                }
                SQLiteStatement stmt = database.compileStatement("INSERT OR REPLACE INTO data(k,v,t) VALUES(?,?,?) ");
                stmt.bindString(1, data.getK());
                stmt.bindString(2, data.getV());
                stmt.bindLong(3, KVUtil.DATATYPE.STRING.getValue());
                stmt.executeInsert();
                stmt.clearBindings();
                Log.d("Insert on Thread "+ Thread.currentThread().getName() ,"Inserted new Key :"+data.getK()+ " with Value : "+data.getV());
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
            closeDB();
            writeLock.unlock();
        }
    }

    /**
     *
     * @param k : Key for KV Store
     * @param v : value against the Key ( T )
     * @param t : data type
     */
    private void sync(String k , String v , KVUtil.DATATYPE t){
        try {
            writeLock.lock();
            openDB();
            SQLiteStatement stmt = database.compileStatement("INSERT OR REPLACE INTO data(k,v,t) VALUES(?,?,?) ");
            stmt.bindString(1, k);
            stmt.bindString(2, v);
            stmt.bindLong(3, t.getValue());
            stmt.executeInsert();
            stmt.clearBindings();
            Log.d("Insert on Thread"+ Thread.currentThread().getName() ,"Inserted new Key :"+k+ " with Value : "+v);
        }finally{
            closeDB();
            writeLock.unlock();
        }
    }

    ////////// GET SECTION

    /**
     *
     * @param keys : All keys which need to be searched
     * @return List of KVPairs along with all values
     */
    public List<KVPair> getStringSet(List<String> keys) {
        if (keys == null || keys.size() == 0) {
            Log.d("not alowed", "Empty data");
            return null;
        }
        List<KVPair> list =new ArrayList<KVPair>();
        try{
            readLock.lock();
            openDB();
            String Keys = KVUtil.formatedArray(keys);
            String sql = "SELECT * FROM data where k in ("+Keys+")";
            Cursor c = database.rawQuery(sql,null);
            while (c.moveToNext()) {
                KVPair data = new KVPair(c.getString(c.getColumnIndex("k")), c.getString(c.getColumnIndex("v")));
                list.add(data);

            }
            Log.d("Getting on Thread "+ Thread.currentThread().getName() ," Reding for Keys :"+keys.toString());
            return list;
        }finally{
            closeDB();
            readLock.unlock();
        }
    }


    /**
     *
     * @param key : Keys which need to be searched
     * @param defaultVal : default expected values if key is not exist
     * @return : String values for the key
     */
    public String getString(String key , String defaultVal) {
        if (TextUtils.isEmpty(key)) {
            Log.d("not alowed", "Empty data");
            return null;
        }
        String val = this.getValue(key, KVUtil.DATATYPE.INT.STRING);
       if(val != null){
            return val;
        }
        return defaultVal;
    }

    /**
     *
     * @param key : Keys which need to be searched
     * @param defaultVal : default expected values if key is not exist
     * @return : boolean values for the key
     */
    public boolean getBool(String key , Boolean defaultVal) {
        if (TextUtils.isEmpty(key)) {
            Log.d("not alowed", "Empty data");
            return defaultVal;
        }
        String val = this.getValue(key, KVUtil.DATATYPE.INT.BOOL);
        if(val != null){
            return Boolean.parseBoolean(val);
        }
        return defaultVal;
    }

    /**
     *
     * @param key : Keys which need to be searched
     * @param defaultVal : default expected values if key is not exist
     * @return : int values for the key
     */
    public int getInt(String key , int defaultVal) {
        if (TextUtils.isEmpty(key)) {
            Log.d("not alowed", "Empty data");
            return defaultVal;
        }
        String val = this.getValue(key, KVUtil.DATATYPE.INT.INT);
        if(val != null){
            return Integer.parseInt(val);
        }
        return defaultVal;
    }

    /**
     *
     * @param key : Keys which need to be searched
     * @param defaultVal : default expected values if key is not exist
     * @return : long values for the key
     */
    public long getLong(String key , long defaultVal) {
        if (TextUtils.isEmpty(key)) {
            Log.d("not alowed", "Empty data");
            return defaultVal;
        }
        String val = this.getValue(key, KVUtil.DATATYPE.INT.LONG);
        if(val != null){
            return Integer.parseInt(val);
        }
        return defaultVal;
    }

    /**
     *
     * @param key : Keys which need to be searched
     * @param defaultVal : default expected values if key is not exist
     * @return : Date values for the key
     */
    public Date getDate(String key , Date defaultVal) {
        if (TextUtils.isEmpty(key)) {
            Log.d("not alowed", "Empty data");
            return defaultVal;
        }
        String val = this.getValue(key, KVUtil.DATATYPE.INT.DATE);
        if(val != null){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            try {
                return sdf.parse(val);
            } catch(ParseException e){
            }
        }
        return defaultVal;
    }

    /**
     *
     * @param K : Keys which need to be searched
     * @param T : expected Data Type
     * @return : Value for the key
     */
    private String getValue(String K, KVUtil.DATATYPE T){
        try{
            readLock.lock();
            openDB();
            String sql = "SELECT v FROM data where k = '"+K+"' and t = "+T.getValue();
            Cursor c = database.rawQuery(sql,null);
            while (c.moveToFirst()) {
                return c.getString(c.getColumnIndex("v"));
            }
            Log.d("Getting on Thread "+ Thread.currentThread().getName() ," Reding for Keys :"+ K);
            return null;
        }finally{
            closeDB();
            readLock.unlock();
        }
    }


    /**
     * To read number of keys it has
     * @return : long key count
     */
    public long getKeyCount() {
        readLock.lock();
        try {
            openDB();
            return DatabaseUtils.queryNumEntries(database, "data");
        }finally {
            closeDB();
            readLock.unlock();
        }
    }



    ////// DELETE SECTION
    /**
     *
     * @param key : Key name which should be deleted
     */
    public void deleteValueForKey(String key){
        if (TextUtils.isEmpty(key)) {
            Log.d("not alowed", "Empty data");
            return;
        }
        try{
            writeLock.lock();
            openDB();
            SQLiteStatement stmt = database.compileStatement("delete from data where k = ? ");
            stmt.bindString(1, key);
            stmt.execute();
        }finally{
            closeDB();
            writeLock.unlock();
        }
    }

    /**
     *  Will delete all Keys
     */
    public void clearAllKeyValues(){
        try{
            writeLock.lock();
            openDB();
            SQLiteStatement stmt = database.compileStatement("delete from data");
            stmt.execute();
        }finally{
            closeDB();
            writeLock.unlock();
        }
    }


    //////// SQLiteOpenHelper


    static class DatabaseHelper extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "helpshift.db";
        private static final int DATABASE_VERSION = 1;


        public synchronized static DatabaseHelper getInstance(Context context) {
            if (helper == null) {
                helper = new DatabaseHelper(context.getApplicationContext());
            }
            return helper;
        }

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            String createSessionTable =
                    "CREATE TABLE data (k TEXT primary key not null, v TEXT,t INTEGER) ";
            sqLiteDatabase.execSQL(createSessionTable);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("drop table data");
            onCreate(sqLiteDatabase);
        }

    }
}
