package id.ac.cobalogin.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import id.ac.cobalogin.Models.Book;

public class DbHelper extends SQLiteOpenHelper {
    private static final String TAG = "DbHelper";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "db_MyLib";
    private static final String TABLE_NAME = "tb_book";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_COVER = "cover";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_FILE_PATH = "file_path";
    private static final String COLUMN_AUTHOR = "author";
    private static final String COLUMN_DESC = "description";
    private static final String COLUMN_USER = "user_id";

    private static final String SQL_CREATE_TABLE_BOOK =
            "CREATE TABLE " + TABLE_NAME + "("
                    + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COLUMN_COVER + " TEXT,"
                    + COLUMN_TITLE + " TEXT,"
                    + COLUMN_AUTHOR + " TEXT,"
                    + COLUMN_FILE_PATH + " TEXT,"
                    + COLUMN_DESC + " TEXT,"
                    + COLUMN_USER + " INTEGER" + ")";

    private static final String SQL_DROP_TABLE_BOOK = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DbHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_BOOK);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_TABLE_BOOK);
        onCreate(db);
    }

    public boolean insertData(Book book){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COVER, book.getCover());
        values.put(COLUMN_TITLE, book.getTitle());
        values.put(COLUMN_FILE_PATH, book.getFile_path());
        values.put(COLUMN_AUTHOR, book.getAuthor());
        values.put(COLUMN_DESC, book.getDesc());
        values.put(COLUMN_USER, book.getUser_id());
        long result = database.insert(TABLE_NAME, null, values);

        if(result==-1)
            return false;
        else
            return true;
    }

    public List<Book> findUserBook(int userId){
        List<Book> listBook = new ArrayList<Book>();
//        String query = "SELECT * FROM " + TABLE_NAME;
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_USER + " = " + userId;

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(query, null);

        if(cursor.moveToFirst()){
            do{
                Book book = new Book();
                book.setId(Integer.valueOf(cursor.getString(0)));
                book.setTitle(cursor.getString(2));
                book.setUser_id(Integer.valueOf(cursor.getString(6)));
                listBook.add(book);
            }while (cursor.moveToNext());
        }
        return listBook;
    }

//    public Cursor getAllData(){
//        SQLiteDatabase database = this.getWritableDatabase();
//        Cursor res = database.rawQuery("SELECT * FROM " + TABLE_NAME);
//        return res;
//    }

    public boolean updateData(Book book){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COVER, book.getCover());
        values.put(COLUMN_TITLE, book.getTitle());
        values.put(COLUMN_FILE_PATH, book.getFile_path());
        values.put(COLUMN_AUTHOR, book.getAuthor());
        values.put(COLUMN_DESC, book.getDesc());
        String whereClause = COLUMN_ID + "=?";
        long result = database.update(TABLE_NAME, values, whereClause, new String[]{String.valueOf(book.getId())});
        if(result==-1)
            return false;
        else
            return true;
    }

    public Integer deleteData(String id){
        SQLiteDatabase database = this.getWritableDatabase();
        String whereClause = COLUMN_ID + " = ?";
        return database.delete(TABLE_NAME, whereClause, new String[] {id});
    }

}
