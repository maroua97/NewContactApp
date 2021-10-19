package com.example.myihmapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

public class DateActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    TextView datePickText;
    String TAG = "DateActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);

        datePickText = findViewById(R.id.datePickText);

        Intent intent = getIntent();

        //Set the TextView to the date in the main activity
        String date = intent.getStringExtra("Date");
        if (date != null){
            datePickText.setText(date);
        }
        // Call the date picker dialog
        showDatePickerDialog(date);
    }

    //onClick method for validate
    public void onValidateDate(View view){
        Intent intent = new Intent(this, MainActivity.class);
        if (datePickText.getText() != null) {
            intent.putExtra("Date", datePickText.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    //onClick method for cancel
    public void onCancelDate(View view){
        Intent intent = new Intent(this, MainActivity.class);
        if (datePickText.getText() != null) {
            intent.putExtra("Date", datePickText.getText().toString());
            setResult(RESULT_CANCELED, intent);
            finish();
        }
    }

    //Modify showDatePickerDialog so it can be set to the date in the main activity
    public void showDatePickerDialog(String date) {
       if (date!= null) {
            String[] dateList = date.split("/");
            int day = Integer.parseInt(dateList[0]);
            int month = Integer.parseInt(dateList[1]);
            int year = Integer.parseInt(dateList[2]);
            Log.i("DatePicker set on", String.valueOf(dateList));
           DatePickerDialog datepickerDialog = new DatePickerDialog(this, this, year, month, day);
           datepickerDialog.show();
       }
       //if the date in the main activity is null, show today's date
       else {
           DatePickerDialog datepickerDialog = new DatePickerDialog(this, this,
                   Calendar.getInstance().get(Calendar.YEAR),
                   Calendar.getInstance().get(Calendar.MONTH),
                   Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
           datepickerDialog.show();
       }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = dayOfMonth + "/" + month + "/" + year;
        datePickText.setText(date);
    }
}
