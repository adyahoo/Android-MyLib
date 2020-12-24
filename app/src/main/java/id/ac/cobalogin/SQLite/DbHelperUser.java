package id.ac.cobalogin.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelperUser extends SQLiteOpenHelper {
    private static final String TAG = "DbHelper";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "db_MyLib";
    private static final String TABLE_USER = "tb_user";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USER_NAME = "name";
    private static final String COLUMN_USER_LASTNAME = "lastname";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_PASSWORD = "password";
    private static final String COLUMN_USER_PHOTO = "photo";

    private static final String SQL_CREATE_TABLE_USER =
            "CREATE TABLE " + TABLE_USER + "("
                    + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_USER_NAME + " TEXT,"
                    + COLUMN_USER_LASTNAME + " TEXT,"
                    + COLUMN_USER_EMAIL + " TEXT,"
                    + COLUMN_USER_PASSWORD + " TEXT,"
                    + COLUMN_USER_PHOTO + " TEXT" + ")";

    private static final String SQL_DROP_TABLE_USER = "DROP TABLE IF EXISTS " + TABLE_USER;

    public DbHelperUser (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_TABLE_USER);
        onCreate(db);
    }

    public void inserDataUser(String name, String lastName, String email, String password, String photo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, name);
    }
}
