package com.nhom6.sqliteapplication.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nhom6.sqliteapplication.DAO.PersonDAO;
import com.nhom6.sqliteapplication.DTO.Person;
import com.nhom6.sqliteapplication.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonViewHolder> {

    private final Context context;
    private ArrayList<Person> list;

    public PersonAdapter(Context context, ArrayList<Person> list){
        this.context= context;
        this.list = list;
    }
    @SuppressLint("NotifyDataSetChanged")
    public void setFilteredList(ArrayList<Person> filteredList){
        this.list=filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View studentView =
                inflater.inflate(R.layout.item_khach_hang, parent, false);
        return new PersonViewHolder(studentView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PersonViewHolder holder, int position) {
        Person person = list.get(position);
        holder.txtName.setText(list.get(position).getName());
        holder.txtPhone.setText(list.get(position).getPhone());
        holder.txtAddress.setText(list.get(position).getAddress());
        holder.txtSex.setText(list.get(position).getSex());
        holder.txtBirtday.setText(list.get(position).getBirthday());

        Calendar calendar =Calendar.getInstance();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        PersonDAO dao = new PersonDAO(context);
        holder.imgedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context);
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
                Button btnCancel = dialog.findViewById(com.nhom6.sqliteapplication.R.id.btnCancel);

                Button btnAdd = dialog.findViewById(R.id.btnAdd_KH);
                btnAdd.setText("CẬP NHẬT");
                EditText ed_name = dialog.findViewById(R.id.edName);
                EditText ed_phone = dialog.findViewById(R.id.edPhone);
                EditText ed_address = dialog.findViewById(R.id.edAddress);
                EditText ed_sex = dialog.findViewById(R.id.edSex);
                EditText ed_birthday = dialog.findViewById(R.id.edBirthday);
                TextView txt = dialog.findViewById(R.id.txtTitel);
                txt.setText("SỬA KHÁCH HÀNG");
                ed_name.setText(person.getName());
                ed_phone.setText(person.getPhone());
                ed_address.setText(person.getAddress());
                ed_sex.setText(person.getSex());
                ed_birthday.setText(person.getBirthday());
                ImageView img = dialog.findViewById(R.id.imgdate);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePickerDialog dialog1 = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
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
                        if(ed_name.getText().length()==0||
                                ed_phone.getText().length()==0||
                                ed_address.getText().length()==0||
                                ed_sex.getText().length()==0||
                                ed_birthday.getText().length()==0){
                            Toast.makeText(context,"Không được để trống",Toast.LENGTH_SHORT).show();
                        }else if(!(isValidFormat("dd/MM/yyyy",ed_birthday.getText().toString()))){
                            Toast.makeText(context,"Không đúng định dạng ngày",Toast.LENGTH_SHORT).show();
                        }
                        else {
                            person.setName(ed_name.getText().toString());
                            person.setPhone(ed_phone.getText().toString());
                            person.setSex(ed_sex.getText().toString());
                            person.setAddress(ed_address.getText().toString());
                            person.setBirthday(ed_birthday.getText().toString());
                            long res = dao.update(person);
                            if (res>0){
                                Toast.makeText(context,"Cập nhật thành công",Toast.LENGTH_SHORT).show();
                                list.clear();
                                list.addAll(dao.getAll());
                                notifyDataSetChanged();
                            }else {
                                Toast.makeText(context,"Cập nhật thất bại ",Toast.LENGTH_SHORT).show();
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
        holder.imgDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder= new AlertDialog.Builder(context);
                builder.setTitle("Bạn có chắc muốn xóa ?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int check = dao.delete(list.get(holder.getAdapterPosition()).getId());
                        switch (check){
                            case  1 :
                                list.clear();
                                list.addAll(dao.getAll());
                                notifyDataSetChanged();
                                Toast.makeText(context,"Xóa thành công",Toast.LENGTH_SHORT).show();
                                break;
                            case -1:
                                Toast.makeText(context,"Không thể xóa vì có khách hàng trong hóa đơn",Toast.LENGTH_SHORT).show();
                                break;
                            case 0 :
                                Toast.makeText(context,"Xóa không thành công",Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                break;
                        }

                    }
                });
                builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class PersonViewHolder extends RecyclerView.ViewHolder{
        TextView txtName , txtPhone , txtBirtday,txtAddress,txtSex;
        ImageView imgDel,imgedit;
        public PersonViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txtName);
            txtPhone = itemView.findViewById(R.id.txtPhone);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            txtSex = itemView.findViewById(R.id.txtSex);
            txtBirtday = itemView.findViewById(R.id.txtBirthday);

            imgDel = itemView.findViewById(R.id.imgdel);
            imgedit = itemView.findViewById(R.id.imgedit);
        }
    }

    public boolean isValidFormat(String format, String value) {
        Date date = null;
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = (Date) sdf.parse(value);
            if (!value.equals(sdf.format(date))) {
                date = null;
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return date != null;
    }
}
