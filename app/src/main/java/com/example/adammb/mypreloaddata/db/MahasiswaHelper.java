package com.example.adammb.mypreloaddata.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.example.adammb.mypreloaddata.mahasiswa.MahasiswaModel;

import java.util.ArrayList;

import static android.provider.BaseColumns._ID;
import static com.example.adammb.mypreloaddata.db.DatabaseContract.MahasiswaColumns.NAMA;
import static com.example.adammb.mypreloaddata.db.DatabaseContract.MahasiswaColumns.NIM;
import static com.example.adammb.mypreloaddata.db.DatabaseContract.TABLE_NAME;

public class MahasiswaHelper {

    private Context context;
    private DatabaseHelper databaseHelper;

    private SQLiteDatabase database;

    public MahasiswaHelper(Context context){
        this.context=context;
    }

    public MahasiswaHelper open() throws SQLException{
        databaseHelper=new DatabaseHelper(context);
        database=databaseHelper.getWritableDatabase();

        return this;
    }

    public void close(){
        databaseHelper.close();
    }

    public ArrayList<MahasiswaModel> getDataByName(String nama) {
        String result = "";
        Cursor cursor = database.query(TABLE_NAME, null, NAMA + " LIKE ?",
                new String[]{nama}, null, null, _ID + " ASC", null);
        cursor.moveToFirst();
        ArrayList<MahasiswaModel> arrayList = new ArrayList<>();
        MahasiswaModel mahasiswaModel;

        if (cursor.getCount() > 0) {
            do {
                mahasiswaModel = new MahasiswaModel();
                mahasiswaModel.setId(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)));
                mahasiswaModel.setName(cursor.getString(cursor.getColumnIndexOrThrow(NAMA)));
                mahasiswaModel.setNim(cursor.getString(cursor.getColumnIndexOrThrow(NIM)));

                arrayList.add(mahasiswaModel);
                cursor.moveToNext();
            } while (!cursor.isAfterLast());
        }

        cursor.close();

        return arrayList;
    }

    public ArrayList<MahasiswaModel> getAllData(){
        Cursor cursor = database.query(TABLE_NAME,null,null,null,null,null,_ID+ " ASC",null);
        cursor.moveToFirst();
        ArrayList<MahasiswaModel> arrayList = new ArrayList<>();
        MahasiswaModel mahasiswaModel;
        if (cursor.getCount()>0) {
            do {
                mahasiswaModel = new MahasiswaModel();
                mahasiswaModel.setId(cursor.getInt(cursor.getColumnIndexOrThrow(_ID)));
                mahasiswaModel.setName(cursor.getString(cursor.getColumnIndexOrThrow(NAMA)));
                mahasiswaModel.setNim(cursor.getString(cursor.getColumnIndexOrThrow(NIM)));


                arrayList.add(mahasiswaModel);
                cursor.moveToNext();


            } while (!cursor.isAfterLast());
        }
        cursor.close();
        return arrayList;
    }

    public long insert(MahasiswaModel mahasiswaModel){
        ContentValues initialValues =  new ContentValues();
        initialValues.put(NAMA, mahasiswaModel.getName());
        initialValues.put(NIM, mahasiswaModel.getNim());
        return database.insert(TABLE_NAME, null, initialValues);
    }

    public void beginTransaction(){
        database.beginTransaction();
    }

    public void setTransactionSuccess(){
        database.setTransactionSuccessful();
    }

    public void endTransaction(){
        database.endTransaction();
    }

    public void insertTransaction(MahasiswaModel mahasiswaModel){
        String sql = "INSERT INTO "+TABLE_NAME+" ("+NAMA+", "+NIM
                +") VALUES (?, ?)";
        SQLiteStatement stmt = database.compileStatement(sql);
        stmt.bindString(1, mahasiswaModel.getName());
        stmt.bindString(2, mahasiswaModel.getNim());
        stmt.execute();
        stmt.clearBindings();

    }

    public int update(MahasiswaModel mahasiswaModel){
        ContentValues args = new ContentValues();
        args.put(NAMA, mahasiswaModel.getName());
        args.put(NIM, mahasiswaModel.getNim());
        return database.update(TABLE_NAME, args, _ID + "= '" + mahasiswaModel.getId() + "'", null);
    }


    public int delete(int id){
        return database.delete(TABLE_NAME, _ID + " = '"+id+"'", null);
    }
}
