package DataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by duy on 4/2/2015.
 */
public class DBUsers {

    static final String KEY_ROWID = "_id";
    static final String KEY_USERNAME = "username";
    static final String KEY_PASSWORD = "password";
    static final String KEY_EMAIL = "email";
    static final String KEY_GENDER = "gender";
    static final String KEY_IDAVA = "idAva";
    static final String KEY_PRESENTMONEY = "presentMoney";

    static final String TAG = "DBUsers";
    static final String DATABASE_NAME = "Users";
    static final String DATABASE_TABLE = "Summary";
    static final int DATABASE_VERSION = 1;

    static final String DATABASE_CREATE = "create table Summary (_id integer primary key autoincrement," +
            " username text not null, password text not null, email text not null, gender text not null," +
            " idAva integer not null, presentMoney Decimal not null);";

    final Context context;

    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    public DBUsers(Context ct) {
        this.context = ct;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(DATABASE_CREATE);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG,"Upgrading database from version " + oldVersion+" to " + newVersion+", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS Summary");
            onCreate(db);
        }
    }

    //===========  open the database ============
    public DBUsers open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //============  close the database  =========
    public void close(){
        DBHelper.close();
    }

    //===========  insert an user into the database  =============
    public long insertUser(String username, String password, String email, String gender, int idAva, double presentMoney){
        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, username);
        values.put(KEY_PASSWORD, password);
        values.put(KEY_EMAIL, email);
        values.put(KEY_GENDER, gender);
        values.put(KEY_IDAVA, idAva);
        values.put(KEY_PRESENTMONEY, presentMoney);
        return db.insert(DATABASE_TABLE, null, values);
    }

    //============  delete a particular user ===============
    public boolean deleteUser(long rowId){
        return db.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null)>0;
    }

    //============ retrieves all the users  ============
    public Cursor getAllUsers(){
        return db.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_USERNAME, KEY_PASSWORD, KEY_EMAIL, KEY_GENDER, KEY_IDAVA, KEY_PRESENTMONEY},
                null, null, null, null, null);
    }

    //============  retrieves a particular user  ================
    public Cursor getUser(String username) throws SQLException{
        Cursor cursor = db.query(true, DATABASE_TABLE,
                new String[] {KEY_ROWID, KEY_USERNAME, KEY_PASSWORD, KEY_EMAIL, KEY_GENDER, KEY_IDAVA, KEY_PRESENTMONEY},
                KEY_USERNAME + "=\"" + username + "\"", null, null, null, null, null);
        if(cursor!=null){
            cursor.moveToFirst();
        }
        return cursor;
    }

    //============  update a user  ====================
    public boolean updateUser(String username, String password, String email, String gender, int idAva, double presentMoney){
        ContentValues values = new ContentValues();
        values.put(KEY_PASSWORD, password);
        values.put(KEY_EMAIL, email);
        values.put(KEY_GENDER, gender);
        values.put(KEY_IDAVA, idAva);
        values.put(KEY_PRESENTMONEY, presentMoney);
        return db.update(DATABASE_TABLE, values, KEY_USERNAME + "=\"" + username + "\"", null)>0;
    }

    public boolean updateAva(String username, int idAva){
        ContentValues values = new ContentValues();
        values.put(KEY_IDAVA, idAva);
        return db.update(DATABASE_TABLE, values, KEY_USERNAME + "=\"" + username + "\"", null)>0;
    }

    public boolean updatePresentMoney(String username, double presentMoney){
        ContentValues values = new ContentValues();
        values.put(KEY_PRESENTMONEY, presentMoney);
        return db.update(DATABASE_TABLE, values, KEY_USERNAME + "=\"" + username + "\"", null)>0;
    }


}
