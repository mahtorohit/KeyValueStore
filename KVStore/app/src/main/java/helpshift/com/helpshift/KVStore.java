package helpshift.com.helpshift;


import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
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
     * @param val : value against the Key ( long )
     */
    public void putNumber(String key, long val) {
        if (TextUtils.isEmpty(key)) {
            Log.d("not alowed", "Empty data");
            return;
        }
        try {
            writeLock.lock();
            openDB();
            SQLiteStatement stmt = database.compileStatement("INSERT OR REPLACE INTO data(k,v) VALUES(?,?) ");
            stmt.bindString(1, key);
            stmt.bindLong(2, val);
            stmt.executeInsert();
            stmt.clearBindings();
            Log.d("Insert on Thread"+ Thread.currentThread().getName() ,"Inserted new Key :"+key+ " with Value : "+val);
        }finally{
            closeDB();
            writeLock.unlock();
        }

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
        try {
            writeLock.lock();
            openDB();
            SQLiteStatement stmt = database.compileStatement("INSERT OR REPLACE INTO data(k,v) VALUES(?,?) ");
            stmt.bindString(1, key);
            stmt.bindString(2, val);
            stmt.executeInsert();
            stmt.clearBindings();
            Log.d("Insert on Thread"+ Thread.currentThread().getName() ,"Inserted new Key :"+key+ " with Value : "+val);
        }finally{
            closeDB();
            writeLock.unlock();
        }

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
                SQLiteStatement stmt = database.compileStatement("INSERT OR REPLACE INTO data(k,v) VALUES(?,?) ");
                stmt.bindString(1, data.getK());
                stmt.bindString(2, data.getV());
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
     * @return : String values for the key
     */
    public String getStringForKey(String key) {
        if (TextUtils.isEmpty(key)) {
            Log.d("not alowed", "Empty data");
            return null;
        }
        String value="";
        try{
            readLock.lock();
            openDB();
            String sql = "SELECT v FROM data where k = ('"+key+"')";
            Cursor c = database.rawQuery(sql,null);
            while (c.moveToFirst()) {
                value =  c.getString(c.getColumnIndex("v"));
                break;
            }
            Log.d("Getting on Thread "+ Thread.currentThread().getName() ," Reding for Keys :"+ key);
            return value;
        }finally{
            closeDB();
            readLock.unlock();
        }
    }

    /**
     *
     * @param key : Keys which need to be searched
     * @return : Long values for the key
     */
    public Long getNumberForKey(String key) {
        if (TextUtils.isEmpty(key)) {
            Log.d("not alowed", "Empty data");
            return null;
        }
        String value="";
        try{
            readLock.lock();
            openDB();
            String sql = "SELECT v FROM data where k = ('"+key+"')";
            Cursor c = database.rawQuery(sql,null);
            while (c.moveToFirst()) {
                value = c.getString(c.getColumnIndex("v"));
                if(KVUtil.isNumeric(value)){
                    return Long.parseLong(value);
                }
                break;
            }
            Log.d("Getting on Thread "+ Thread.currentThread().getName() ," Reding for Keys :"+ key);
            return null;
        }finally{
            closeDB();
            readLock.unlock();
        }
    }


///////

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
                    "CREATE TABLE data (k TEXT primary key not null, " +
                            "v TEXT)";
            sqLiteDatabase.execSQL(createSessionTable);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("drop table data");
            onCreate(sqLiteDatabase);
        }

    }
}
