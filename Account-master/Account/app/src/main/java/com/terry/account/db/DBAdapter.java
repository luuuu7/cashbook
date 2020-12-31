package com.terry.account.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

/**
 * 数据库sql操作类
 * 用来:
 * 创建数据库
 * 清空数据库
 * 插入数据等
 *
 * @author sjy
 */
public class DBAdapter extends SQLiteOpenHelper {
    private static final String TAG = DBAdapter.class.getSimpleName();

    private static final String DB_NAME = "smile.db";

    private static final int DB_VERSION = 2 << 6;

    public static String mSessionID;
    ;

    //资产的表
    private static final String create_account_tbl =
            "create table if not exists account_table("
                    + "id INTEGER PRIMARY KEY  AUTOINCREMENT, "
                    + "user varchar, "
                    + "money varchar, time varchar, "
                    + "type varchar, accountType varchar, "
                    + "fromType varchar, mark varchar "
                    + ")";


    //用戶的表
    private static final String create_user_tbl =
            "create table if not exists user_table("
                    + "name varchar PRIMARY KEY, pwd varchar,"
                    + "dayMax varchar, monthMax varchar,yearMax varchar"
                    + ")";

    //资产类型的表
    private static final String create_zichan_tbl =
            "create table if not exists zichan_table("
                    + "id INTEGER PRIMARY KEY  AUTOINCREMENT, "
                    + "user varchar, "
                    + "name varchar, "
                    + "type varchar"
                    + ")";

    //支付类型的表
    private static final String create_pay_tbl =
            "create table if not exists pay_table("
                    + "id INTEGER PRIMARY KEY  AUTOINCREMENT, "
                    + "user varchar, "
                    + "name varchar"
                    + ")";

    private SQLiteDatabase db;

    public DBAdapter(Context c) {
        super(c, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        db.beginTransaction();
        try {
            db.execSQL(create_account_tbl);
            db.execSQL(create_user_tbl);
            db.execSQL(create_zichan_tbl);
            db.execSQL(create_pay_tbl);

            initDefault();
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }

    }

    public void closeDb() {
        if (db != null) {
            db.close();
        }
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL("DROP TABLE IF EXISTS account_table");
            db.execSQL("DROP TABLE IF EXISTS user_table");
            db.execSQL("DROP TABLE IF EXISTS zichan_table");
            db.execSQL("DROP TABLE IF EXISTS pay_table");
            onCreate(db);
        } catch (Exception e) {
            Log.d(TAG, "onUpgrade Exception" + e.toString());
        }
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        Log.d(TAG, "onOpen onOpen");
        super.onOpen(db);
    }

    //查询资产
    public Cursor queryAccountFromDB() {
        Cursor queryAllAccounts = null;
        db = getWritableDatabase();
        try {
            queryAllAccounts = db.query("account_table", null, null, null, null,
                    null, null);
        } catch (Exception e) {
            Log.d(TAG, "queryDB Exception " + e.toString());
        }
        return queryAllAccounts;
    }

    public Cursor updateAccountInDB(ContentValues cv,
                                    String id) {
        Cursor queryAllAccounts = null;
        db = getWritableDatabase();
        try {
            db.update("account_table", cv,
                    "id=?",
                    new String[]{id});
        } catch (Exception e) {
            Log.d(TAG, "queryDB Exception " + e.toString());
        }
        return queryAllAccounts;
    }

    public Cursor queryAccountFromDB(String user, String time) {
        Cursor queryAllAccounts = null;
        db = getWritableDatabase();
        try {
            String sqlStr = "select * from account_table where user=" + "\"" + user + "\"" + " and time like " + "\"%" + time + "%\"";
            queryAllAccounts = db.rawQuery(sqlStr, null);
        } catch (Exception e) {
            Log.d(TAG, "queryDB Exception " + e.toString());
        }
        return queryAllAccounts;
    }


    public Cursor queryAccountFromDB(String user, String time, String accountType, String zcType) {
        Cursor queryAllAccounts = null;
        db = getWritableDatabase();
        String sqlStr ="";
        try {
            if(!TextUtils.isEmpty(accountType)&&TextUtils.isEmpty(zcType)){
                sqlStr = "select * from account_table where " +
                        "user=" + "\"" + user + "\"" + " and time like " + "\"%" + time + "%\"" + " and accountType=" + "\"" + accountType + "\"";
            }else if(!TextUtils.isEmpty(accountType)&&!TextUtils.isEmpty(zcType)){
                sqlStr = "select * from account_table where "
                        + "user=" + "\"" + user + "\"" + " and time like " + "\"%" + time + "%\""
                        + " and accountType= "+ "\"" + accountType + "\""
                        + " and type= "+ "\"" + zcType + "\"";
            }else if(TextUtils.isEmpty(accountType)&&TextUtils.isEmpty(zcType)){
                sqlStr = "select * from account_table where " +
                        "user=" + "\"" + user + "\"" + " and time like " + "\"%" + time + "%\"";
            }else if(TextUtils.isEmpty(accountType)&&!TextUtils.isEmpty(zcType)){
                sqlStr = "select * from account_table where " +
                        "user=" + "\"" + user + "\"" + " and time like " + "\"%" + time + "%\"" + " and type=" + "\"" + zcType + "\"";
            }
            queryAllAccounts = db.rawQuery(sqlStr, null);
        } catch (Exception e) {
            Log.d(TAG, "queryDB Exception " + e.toString());
        }
        return queryAllAccounts;
    }

    /**
     * 模糊查找
     */
    public Cursor queryAccountFromDBVague(String user, String minTime,String maxTime, String accountType, String zcType) {
        Cursor queryAllAccounts = null;
        db = getWritableDatabase();
        String sqlStr ="";
        try {
            if(!TextUtils.isEmpty(accountType)&&TextUtils.isEmpty(zcType)){
                sqlStr = "select * from account_table where " +
                        "user=" + "\"" + user + "\""
                        + " and datetime(time) between datetime('" +minTime+"') and datetime('"+maxTime+"')"
                        + " and accountType=" + "\"" + accountType + "\"";
            }else if(!TextUtils.isEmpty(accountType)&&!TextUtils.isEmpty(zcType)){
                sqlStr = "select * from account_table where "
                        + "user=" + "\"" + user + "\""
                        + " and datetime(time) between datetime('" +minTime+"') and datetime('"+maxTime+"')"
                        + " and accountType= "+ "\"" + accountType + "\""
                        + " and type= "+ "\"" + zcType + "\"";
            }else if(TextUtils.isEmpty(accountType)&&TextUtils.isEmpty(zcType)){
                sqlStr = "select * from account_table where " +
                        "user=" + "\"" + user + "\""
                        + " and datetime(time) between datetime('" +minTime+"') and datetime('"+maxTime+"')";
            }else if(TextUtils.isEmpty(accountType)&&!TextUtils.isEmpty(zcType)){
                sqlStr = "select * from account_table where " +
                        "user=" + "\"" + user + "\""
                        + " and datetime(time) between datetime('" +minTime+"') and datetime('"+maxTime+"')"
                        + " and type=" + "\"" + zcType + "\"";
            }

            queryAllAccounts = db.rawQuery(sqlStr, null);
        } catch (Exception e) {
            Log.d(TAG, "queryDB Exception " + e.toString());
        }
        return queryAllAccounts;
    }


    public void insertAccount2DB(ContentValues values) {
        try {
            db = getWritableDatabase();
            if (values != null) {
                db.insert("account_table", null, values);
            }
        } finally {
            closeDb();
        }
    }

    public void deleteSingleAccount(String id) {
        try {
            db = getWritableDatabase();
            db.delete("account_table",
                    "id=?",
                    new String[]{id});
        } finally {
            closeDb();
        }
    }

    public void deleteAllAccount() {
        try {
            db = getWritableDatabase();
            db.delete("account_table", null, null);
        } finally {
            closeDb();
        }
    }


    public Cursor queryUserFromDB() {
        Cursor queryAllUsers = null;
        db = getWritableDatabase();
        try {
            queryAllUsers = db.query("user_table", null, null, null, null,
                    null, null);
        } catch (Exception e) {
            Log.d(TAG, "queryDB Exception " + e.toString());
        }
        return queryAllUsers;
    }

    public Cursor queryUserFromDB(String name, String pwd) {
        Cursor queryAllUsers = null;
        db = getWritableDatabase();
        try {
            String sqlStr = "select * from user_table where " +
                    "name=" + "\"" + name + "\"" + " and pwd=" + "\"" + pwd + "\"";
            queryAllUsers = db.rawQuery(sqlStr, null);
        } catch (Exception e) {
            Log.d(TAG, "queryDB Exception " + e.toString());
        }
        return queryAllUsers;
    }


    public boolean insertUser2DB(ContentValues values) {
        long result = -1;
        try {
            db = getWritableDatabase();
            if (values != null) {
                result = db.insert("user_table", null, values);
            }
        } finally {
            closeDb();
        }
        return result!=-1;
    }

    public boolean updateUserInDB(ContentValues cv,String name) {
        long result = -1;
        try {
            db = getWritableDatabase();
            result = db.update("user_table", cv, "name=?", new String[]{name});
        } catch (Exception e) {
            Log.d(TAG, "queryDB Exception " + e.toString());
        }
        return result!=-1;
    }

    public void deleteSingleUser(String user) {
        try {
            db = getWritableDatabase();
            db.delete("user_table",
                    "user=?",
                    new String[]{user});
        } finally {
            closeDb();
        }
    }

    public void deleteAllUser() {
        try {
            db = getWritableDatabase();
            db.delete("user_table", null, null);
        } finally {
            closeDb();
        }
    }


    public Cursor queryZichanFromDB(String name) {
        Cursor qryCursor = null;
        db = getWritableDatabase();
        try {

//            String sqlStr = "select * from zichan_table where name=" + "\"" + name + "\" ";
            String sqlStr = "select * from zichan_table ";
            qryCursor = db.rawQuery(sqlStr, null);

        } catch (Exception e) {
            Log.d(TAG, "queryDB Exception " + e.toString());
        }
        return qryCursor;
    }

    public void insertZichan2DB(ContentValues values) {
        try {
            db = getWritableDatabase();
            if (values != null) {
                db.insert("zichan_table", null, values);
            }
        } finally {
            closeDb();
        }
    }

    public Cursor queryZichanFromDB(String name, String type) {
        Cursor qryCursor = null;
        db = getWritableDatabase();
        try {
//            String sqlStr = "select * from zichan_table where name=" + "\"" + name + "\" and type=" + "\"" + type + "\"";
           String sqlStr = "select * from zichan_table where type=" + "\"" + type + "\"";
            qryCursor = db.rawQuery(sqlStr, null);
        } catch (Exception e) {
            Log.d(TAG, "queryDB Exception " + e.toString());
        }
        return qryCursor;
    }

    public Cursor updateSingleZichanInDB(ContentValues cv,
                                    String id) {
        Cursor queryAllAccounts = null;
        db = getWritableDatabase();
        try {
            db.update("zichan_table", cv,
                    "id=? ",
                    new String[]{id});
        } catch (Exception e) {
            Log.d(TAG, "queryDB Exception " + e.toString());
        }
        return queryAllAccounts;
    }


    public void deleteSingleZichan(String user, String name) {
        try {
            db = getWritableDatabase();
//            db.delete("zichan_table", "user=? and name=?", new String[]{user, name});
            db.delete("zichan_table", "name=?", new String[]{name});
        } finally {
            closeDb();
        }
    }




    public Cursor queryPayFromDB() {
        Cursor qryCursor = null;
        db = getWritableDatabase();
        try {
            String sqlStr = "select * from pay_table ";
            qryCursor = db.rawQuery(sqlStr, null);

        } catch (Exception e) {
            Log.d(TAG, "queryDB Exception " + e.toString());
        }
        return qryCursor;
    }

    public void insertPay2DB(ContentValues values) {
        try {
            db = getWritableDatabase();
            if (values != null) {
                db.insert("pay_table", null, values);
            }
        } finally {
            closeDb();
        }
    }

    public Cursor updateSinglePayInDB(ContentValues cv, String id) {
        Cursor queryAllAccounts = null;
        db = getWritableDatabase();
        try {
            db.update("pay_table", cv,
                    "id=? ",
                    new String[]{id});
        } catch (Exception e) {
            Log.d(TAG, "queryDB Exception " + e.toString());
        }
        return queryAllAccounts;
    }


    public void deleteSinglePay(String name) {
        try {
            db = getWritableDatabase();
            db.delete("pay_table", "name=?", new String[]{name});
        } finally {
            closeDb();
        }
    }



    private void initDefault() {
        ContentValues cv = new ContentValues();
        cv.put("name", "工资收入");
        cv.put("type", "income");
        db.insert("zichan_table", null, cv);

        cv = new ContentValues();
        cv.put("name", "股票收入");
        cv.put("type", "income");
        cv.put("id", 2);
        db.insert("zichan_table", null, cv);

        cv = new ContentValues();
        cv.put("name", "其他收入");
        cv.put("type", "income");
        db.insert("zichan_table", null, cv);

        cv = new ContentValues();
        cv.put("name", "日常购物");
        cv.put("type", "outcome");
        db.insert("zichan_table", null, cv);

        cv = new ContentValues();
        cv.put("name", "餐饮开销");
        cv.put("type", "outcome");
        db.insert("zichan_table", null, cv);

        cv = new ContentValues();
        cv.put("name", "购置衣物");
        cv.put("type", "outcome");
        db.insert("zichan_table", null, cv);

        cv = new ContentValues();
        cv.put("name", "娱乐开销");
        cv.put("type", "outcome");
        db.insert("zichan_table", null, cv);

        cv = new ContentValues();
        cv.put("name", "交通出行");
        cv.put("type", "outcome");
        db.insert("zichan_table", null, cv);

        cv = new ContentValues();
        cv.put("name", "水电煤气");
        cv.put("type", "outcome");
        db.insert("zichan_table", null, cv);

        cv = new ContentValues();
        cv.put("name", "其他花费");
        cv.put("type", "outcome");
        db.insert("zichan_table", null, cv);

        cv = new ContentValues();
        cv.put("name", "支付宝");
        db.insert("pay_table", null, cv);

        cv = new ContentValues();
        cv.put("name", "微信");
        db.insert("pay_table", null, cv);
    }
}
