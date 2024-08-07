package com.nhom6.sqliteapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nhom6.sqliteapplication.Adapter.PersonAdapter;
import com.nhom6.sqliteapplication.DAO.PersonDAO;
import com.nhom6.sqliteapplication.DTO.Person;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {

    private PersonDAO dao ;
    private PersonAdapter adapter;
    private ArrayList<Person> list ;

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView rcv = findViewById(R.id.rcvPerson);
        FloatingActionButton btnAddPerson = findViewById(R.id.btnAddPerson);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcv.setLayoutManager(linearLayoutManager);
        dao = new PersonDAO(this);
        list = dao.getAll();

        SearchView searchView = findViewById(R.id.searchPerson);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                FinterList(newText);
                return true;
            }
        });
        adapter = new PersonAdapter(this,list);
        rcv.setAdapter(adapter);
        Calendar calendar =Calendar.getInstance();

        btnAddPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(MainActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_add_person);
                Window window = dialog.getWindow();
                if(window==null){
                    return;
                }
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                WindowManager.LayoutParams windowacc = window.getAttributes();
                windowacc.gravity = Gravity.NO_GRAVITY ;
                window.setAttributes(windowacc);

                Button btnCancel = dialog.findViewById(R.id.btnCancel);
                Button btnAdd = dialog.findViewById(R.id.btnAdd_KH);
                EditText ed_name = dialog.findViewById(R.id.edName);
                EditText ed_phone = dialog.findViewById(R.id.edPhone);
                EditText ed_address = dialog.findViewById(R.id.edAddress);
                EditText ed_sex = dialog.findViewById(R.id.edSex);
                EditText ed_birthday = dialog.findViewById(R.id.edBirthday);
                ImageView img = dialog.findViewById(R.id.imgdate);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePickerDialog dialog1 = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                GregorianCalendar c = new GregorianCalendar(year, month, dayOfMonth);
                                ed_birthday.setText(sdf.format(c.getTime()));
                            }
                        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE));
                        dialog1.show();
                    }
                });
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onClick(View v) {
                        if(ed_name.getText().toString().isEmpty()){
                            ed_name.setError("Tên không được để trống");
                        } if (ed_phone.getText().toString().isEmpty()) {
                            ed_phone.setError("Số điện thoại không được để trống");
                        }  if (ed_address.getText().toString().isEmpty()) {
                            ed_address.setError("Địa chỉ không được để trống");
                        } if (ed_sex.getText().toString().isEmpty()) {
                            ed_sex.setError("Giớ tính không được để trống");
                        } if (ed_birthday.getText().toString().isEmpty()) {
                            ed_birthday.setError("Ngày Sinh không được để trống");
                        }else if(!(isValidFormat("dd/MM/yyyy",ed_birthday.getText().toString()))){
                            ed_birthday.setError("Không đúng định dạng ngày");
                        }else  if(!(checkPhone(ed_phone.getText().toString()))){
                            ed_phone.setError("Không đúng định dạng điện thoại");
                        }
                        else {
                            Person person = new Person();
                            person.setName(ed_name.getText().toString());
                            person.setPhone(ed_phone.getText().toString());
                            person.setAddress(ed_address.getText().toString());
                            person.setSex(ed_sex.getText().toString());
                            person.setBirthday(ed_birthday.getText().toString());
                            long res = dao.insert(person);
                            if (res>0){
                                Toast.makeText(MainActivity.this,"Thêm thành công",Toast.LENGTH_SHORT).show();
                                list.clear();
                                list.addAll(dao.getAll());
                                adapter.notifyDataSetChanged();
                            }else {
                                Toast.makeText(MainActivity.this,"Thêm thất bại",Toast.LENGTH_SHORT).show();
                            }
                            dialog.dismiss();
                        }
                    }
                });
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
        });
    }

    private void FinterList(String text) {
        ArrayList<Person> filteredList=new ArrayList<>();
//        list=dao.getAll();

        for (Person person: list){
            if (person.getName().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(person);
            }

        }
        if (filteredList.isEmpty()){
            Toast.makeText(MainActivity.this, "no data", Toast.LENGTH_SHORT).show();
        }else {
            adapter.setFilteredList(filteredList);
        }
    }
    public boolean isValidFormat(String format, String value) {
        Date date = null;
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(value);
            if (!value.equals(sdf.format(date))) {
                date = null;
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return date != null;
    }
    public boolean checkPhone(String str){
        // Bieu thuc chinh quy mo ta dinh dang so dien thoai
        String reg = "^(0|\\+84)(\\s|\\.)?((3[2-9])|(5[689])|(7[06-9])|(8[1-689])|(9[0-46-9]))(\\d)(\\s|\\.)?(\\d{3})(\\s|\\.)?(\\d{3})$";

        // Kiem tra dinh dang
        boolean kt = str.matches(reg);

        if (!kt) {
            return  false ;
        } else {
            return  true ;
        }
    }
}