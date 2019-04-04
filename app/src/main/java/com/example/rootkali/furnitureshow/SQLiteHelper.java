package com.example.rootkali.furnitureshow;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class SQLiteHelper extends SQLiteOpenHelper {
    //Constructor
    SQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version){
        super(context,name,factory,version);
    }


    public void queryData(String sql){
        SQLiteDatabase database = getReadableDatabase();
        database.execSQL(sql);
    }
    //Insert Data
    public void insertData(String name,String price,byte[] image){
        SQLiteDatabase database= getReadableDatabase();
        //Query to insert product in database table
        String sql ="INSERT INTO PRODUCT VALUES(NULL,?,?,?)";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();

        statement.bindString(1,name);
        statement.bindString(2,price);
        statement.bindBlob(3,image);
        statement.executeInsert();
    }
    //UpdateData
    public void updateData(String name,String price,byte[] image,int id){
        SQLiteDatabase database= getReadableDatabase();
        //Query to Update product in database table
        String sql ="UPDATE PRODUCT SET name=?, price=?, image=? WHERE id=?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.bindString(1,name);
        statement.bindString(2,price);
        statement.bindBlob(3,image);
        statement.bindDouble(4,(double)id);
        statement.execute();
        database.close();
    }
    //DeleteData
    public void deleteData(int id){
        SQLiteDatabase database= getReadableDatabase();
        //Query to delete product
        String sql= "DELETE FROM PRODUCT WHERE id=?";
        SQLiteStatement statement = database.compileStatement(sql);
        statement.clearBindings();
        statement.bindDouble(1,(double) id);

        statement.execute();
        database.close();
    }
    public Cursor getData(String sql){
        SQLiteDatabase database= getReadableDatabase();
        return database.rawQuery(sql,null);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
