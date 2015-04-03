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
public class DBStock {
    static final String KEY_ROWID = "rowId";
    static final String KEY_PORFOLIOSNAME = "porfolioName";
    static final String KEY_SYMBOL = "symbol";
    static final String KEY_PURCHASESELL = "purchaseSell";
    static final String KEY_QUANTITY = "quantity";
    static final String KEY_COST = "cost";
    static final String KEY_TIME = "time";
    static final String KEY_AVERAGEPURCHASE = "averagePurchase";

    static final String TAG = "DBStock";
    static String DATABASE_NAME;
    static final String DATABASE_TABLE_PORFOLIOS = "Porfolio";
    static final String DATABASE_TABLE_HISTORY = "History";
    static final int DATABASE_VERSION = 1;

    static final String PORFOLIOS_DATABASE_CREATE = "create table Porfolio (_id integer primary key autoincrement, " +
            "porfolioName text not null);";
    static final String HISTORY_DATABASE_CREATE = "create table History (_id integer primary key autoincrement, " +
            "symbol text not null, porfolioName text not null,  purchaseSell text not null, quantity integer not null, cost Decimal not null, " +
            "time text not null);";

    final Context context;
    DataBaseHelper DBHelper;
    SQLiteDatabase db;

    public DBStock(Context ct, String username) {
        this.context = ct;
        DATABASE_NAME = username;
        DBHelper = new DataBaseHelper(context);
    }

    private static class DataBaseHelper extends SQLiteOpenHelper{
        public DataBaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(PORFOLIOS_DATABASE_CREATE);
                db.execSQL(HISTORY_DATABASE_CREATE);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS Porfolio");
            db.execSQL("DROP TABLE IF EXISTS History");
            onCreate(db);
        }
    }

    //===========  open the database =================
    public DBStock open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    //=============  close the database =============
    public void close(){
        DBHelper.close();
    }

    //============ insert a porfolio into the database =============
    public long insertPorfolio(String porfolioName){
        ContentValues values = new ContentValues();
        values.put(KEY_PORFOLIOSNAME, porfolioName);
        //=====  create table porfolioName  ==========
        try {
            db.execSQL("create table " + porfolioName + " (_id integer primary key autoincrement, " +
                    "symbol text not null, quantity integer not null, averagePurchase Decimal not null);");
        }catch (SQLException e){
            e.printStackTrace();
        }
        return db.insert(DATABASE_TABLE_PORFOLIOS, null, values);
    }
    //=========== insert a symbol into porfolioName table ============
    public long insertIntoPorfolioName(String porfolioNameTable, String symbol, int quantity, double averagePurchase){
        ContentValues values = new ContentValues();
        values.put(KEY_SYMBOL, symbol);
        values.put(KEY_QUANTITY, quantity);
        values.put(KEY_AVERAGEPURCHASE, averagePurchase);
        return db.insert(porfolioNameTable, null, values);
    }
    //============insert a history into the database  ===========
    public long insertHistory(String symbol, String porfolioName, String purchaseSell, int quantity, double cost, String time){
        ContentValues values = new ContentValues();
        values.put(KEY_SYMBOL, symbol);
        values.put(KEY_PORFOLIOSNAME, porfolioName);
        values.put(KEY_PURCHASESELL, purchaseSell);
        values.put(KEY_QUANTITY, quantity);
        values.put(KEY_COST, cost);
        values.put(KEY_TIME, time);
        return db.insert(DATABASE_TABLE_HISTORY, null, values);
    }


    //================== delete a particular porfolio  ================
    public boolean deletePorfolio(String porfolio){
        db.execSQL("DROP TABLE IF EXISTS " + porfolio);
        return db.delete(DATABASE_TABLE_PORFOLIOS, KEY_PORFOLIOSNAME + "=\"" + porfolio + "\"", null)>0;
    }
    //=============  delete a particular into porfolioName table ============
    public boolean deleteIntoPorfolioName(String porfolioNameTable,String symbol){
        return db.delete(porfolioNameTable, KEY_SYMBOL + "=\"" + symbol + "\"", null)>0;
    }
    //============ delete a particular history  ==============
    public boolean deleteHistory(String symbol){
        return db.delete(DATABASE_TABLE_HISTORY, KEY_SYMBOL + "=\"" + symbol + "\"",null)>0;
    }


    //===========  retrieves all porfolio ====================
    public Cursor getAllPorfolio(){
        return db.query(DATABASE_TABLE_PORFOLIOS, new String[] {KEY_ROWID, KEY_PORFOLIOSNAME}, null, null, null, null, null);
    }
    //=========  retrieves all symbol into porfolioName table ========
    public Cursor getAllIntoPorfolioName(String porfolioNameTable){
        return db.query(porfolioNameTable, new String[]{KEY_ROWID, KEY_SYMBOL, KEY_QUANTITY, KEY_AVERAGEPURCHASE}, null, null, null, null, null);
    }
    //==========  retrieves all history  ===============
    public Cursor getAllHistory(){
        return db.query(DATABASE_TABLE_HISTORY, new String[]{KEY_ROWID, KEY_SYMBOL, KEY_PORFOLIOSNAME, KEY_PURCHASESELL, KEY_QUANTITY,
        KEY_COST, KEY_TIME}, null, null, null, null, null);
    }


    //==============  retrieve a particular porfolio ==============
    public Cursor getPorfolio(long rowId)throws SQLException{
        Cursor cursor = db.query(true, DATABASE_TABLE_PORFOLIOS, new String[]{KEY_ROWID, KEY_PORFOLIOSNAME},
                KEY_ROWID + "=" + rowId, null, null, null, null, null);
        if(cursor!=null){
            cursor.moveToFirst();
        }
        return cursor;
    }
    //============  retrieve a particular into porfolioName table ========
    public Cursor getIntoPorfolioName(String porfolioNameTable, String symbol){
        Cursor cursor = db.query(true, porfolioNameTable, new String[]{KEY_ROWID, KEY_SYMBOL, KEY_QUANTITY, KEY_AVERAGEPURCHASE},
                KEY_SYMBOL + "=\"" + symbol + "\"", null, null, null, null, null);
        if(cursor!=null){
            cursor.moveToFirst();
        }
        return cursor;
    }
    //===========  retrieve a particular history ============
    public Cursor getHistory(String symbol){
        Cursor cursor = db.query(true, DATABASE_TABLE_HISTORY, new String[]{KEY_ROWID, KEY_SYMBOL, KEY_PORFOLIOSNAME,
                KEY_PURCHASESELL, KEY_QUANTITY, KEY_COST, KEY_TIME}, KEY_SYMBOL + "=\"" + symbol + "\"", null, null, null, null, null);
        if(cursor!=null){
            cursor.moveToFirst();
        }
        return cursor;
    }


    //=============  update a porfolio ====================
    public boolean updatePorfolio(long rowId, String porfolioName){
        ContentValues values = new ContentValues();
        values.put(KEY_PORFOLIOSNAME, porfolioName);
        return db.update(DATABASE_TABLE_PORFOLIOS, values, KEY_ROWID + "=" + rowId, null)>0;
    }
    //=============  update a symbol into porfolioName table ===========
    public boolean updateIntoPorfolioName(String porfolioNameTable, String symbol, int quantity, double averagePurchase){
        ContentValues values = new ContentValues();
        values.put(KEY_QUANTITY, quantity);
        values.put(KEY_AVERAGEPURCHASE, averagePurchase);
        return db.update(porfolioNameTable, values, KEY_SYMBOL + "=\"" + symbol + "\"", null)>0;
    }
    //=============  update a history ===================
    public boolean updateHistory(String symbol, String porfolioName, String purchaseSell, int quantity, double cost, String time){
        ContentValues values = new ContentValues();
        values.put(KEY_PORFOLIOSNAME, porfolioName);
        values.put(KEY_PURCHASESELL, purchaseSell);
        values.put(KEY_QUANTITY, quantity);
        values.put(KEY_COST, cost);
        values.put(KEY_TIME, time);
        return db.update(DATABASE_TABLE_HISTORY, values, KEY_SYMBOL + "=\"" + symbol + "\"", null)>0;
    }
}
