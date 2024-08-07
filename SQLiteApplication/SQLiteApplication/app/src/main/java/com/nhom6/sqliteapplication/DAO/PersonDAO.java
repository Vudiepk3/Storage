package com.nhom6.sqliteapplication.DAO;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.nhom6.sqliteapplication.DTO.Person;
import com.nhom6.sqliteapplication.Database.DbHelper;

import java.util.ArrayList;
import java.util.List;

public class PersonDAO {
    DbHelper dbHelper;
    SQLiteDatabase db ;
    public PersonDAO(Context context){
        dbHelper = new DbHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public long insert(Person person){
        ContentValues contentValues = new ContentValues();
        contentValues.put("name",person.getName());
        contentValues.put("phone",person.getPhone());
        contentValues.put("address",person.getAddress());
        contentValues.put("sex",person.getSex());
        contentValues.put("birthday",String.valueOf(person.getBirthday()));
        return db.insert("Persons",null,contentValues);
    }

    public long update(Person person){
        ContentValues contentValues = new ContentValues();
        contentValues.put("name",person.getName());
        contentValues.put("phone",person.getPhone());
        contentValues.put("address",person.getAddress());
        contentValues.put("sex",person.getSex());
        contentValues.put("birthday",String.valueOf(person.getBirthday()));
        return db.update("Persons",contentValues,"id=?",new String[]{person.getId()+""});
    }

    public int delete(int id){
        long  check = db.delete("Persons","id=?",new String[]{String.valueOf(id)});
        if(check==-1){
            return  0 ;
        }
        return 1 ;
    }

    public ArrayList<Person> getAll(){
        String sql="SELECT * FROM Persons";
        return (ArrayList<Person>) getData(sql);
    }

    public Person getID(String id){
        String sql = "SELECT * FROM Persons WHERE id=?";
        List<Person> list = getData(sql,id);
        return list.get(0);
    }

    @SuppressLint("Range")
    private List<Person> getData(String sql, String...selectionArgs) {

        List<Person> list = new ArrayList<>();
        @SuppressLint("Recycle") Cursor c = db.rawQuery(sql,selectionArgs);
        while (c.moveToNext()){
            Person obj = new Person();
            obj.setId(Integer.parseInt(c.getString(c.getColumnIndex("id"))));
            obj.setName(c.getString(c.getColumnIndex("name")));
            obj.setPhone(c.getString(c.getColumnIndex("phone")));
            obj.setAddress(c.getString(c.getColumnIndex("address")));
            obj.setSex(c.getString(c.getColumnIndex("sex")));
            obj.setBirthday(c.getString(c.getColumnIndex("birthday")));

            list.add(obj);
        }
        return list;
    }

}
