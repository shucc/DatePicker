package org.cchao.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.cchao.datepicker.DatePicker;
import org.cchao.datepicker.OnDateChangeListener;

public class MainActivity extends AppCompatActivity {

    private Button button;

    private TextView textDate;

    private DatePicker datePicker;

    private int year = 1994;

    private int month = 4;

    private int day = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);
        textDate = (TextView) findViewById(R.id.text_date);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });
    }

    private void showDatePicker() {
        if (null == datePicker) {
            datePicker = new DatePicker(this, year, month, day, new OnDateChangeListener() {
                @Override
                public void onChange(int year, int month, int day) {
                    textDate.setText(String.valueOf(year) + "/" + month + "/" + day);
                }
            });
        }
        datePicker.showAtLocation(button, Gravity.BOTTOM, 0, 0);
    }
}
