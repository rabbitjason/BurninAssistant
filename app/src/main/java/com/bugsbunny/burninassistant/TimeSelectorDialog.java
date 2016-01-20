package com.bugsbunny.burninassistant;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

/**
 * Created by Administrator on 2015/12/11.
 */
public class TimeSelectorDialog extends BaseActivity implements View.OnClickListener {

    public static final String TITLE = "title";
    public static final String HH = "hh";
    public static final String MM = "mm";
    public static final String REQUEST_CODE = "request_code";

    public final static int REQUEST_DURATION = 1;
    public final static int REQUEST_INTERVAL = 2;
    private int requestCode = 0;

    private TextView tvTitle;
    private NumberPicker hourPicker, minutePicker;
    private View llHour, llMinute;
    private int hour, minute;
    private String title;
    private Button btnOk, btnCancel;

    public static void actionStart(Context context, String title, int hour, int minute, int requestCode) {
        Intent intent = new Intent(context, TimeSelectorDialog.class);
        intent.putExtra(TITLE, title);
        intent.putExtra(HH, hour);
        intent.putExtra(MM, minute);
        intent.putExtra(REQUEST_CODE, requestCode);
        ((Activity)context).startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_time_selector);
        Intent intent = getIntent();
        title = intent.getStringExtra(TITLE);
        hour = intent.getIntExtra(HH, 0);
        minute = intent.getIntExtra(MM, 0);
        requestCode = intent.getIntExtra(REQUEST_CODE, 0);
        initView();
    }

    private void initView() {
        tvTitle = (TextView)findViewById(R.id.tvTitle);
        tvTitle.setText(title);

        llHour = findViewById(R.id.llHour);
        hourPicker = (NumberPicker)findViewById(R.id.hourPicker);
        hourPicker.setMaxValue(4);
        hourPicker.setValue(hour);

        llMinute = findViewById(R.id.llMinute);
        minutePicker = (NumberPicker)findViewById(R.id.minutePicker);
        minutePicker.setMaxValue(59);
        minutePicker.setValue(minute);
        if (REQUEST_INTERVAL == requestCode) {
            minutePicker.setMaxValue(30);
            minutePicker.setMinValue(15);
            llHour.setVisibility(View.GONE);
        }

        btnOk = (Button)findViewById(R.id.btnOk);
        btnOk.setOnClickListener(this);

        btnCancel = (Button)findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOk:
                Intent intent = new Intent();
                if (REQUEST_DURATION == requestCode) {
                    intent.putExtra(HH, hourPicker.getValue());
                    intent.putExtra(MM, minutePicker.getValue());
                    setResult(RESULT_OK, intent);
                } else if (REQUEST_INTERVAL == requestCode) {
                    intent.putExtra(MM, minutePicker.getValue());
                    setResult(RESULT_OK, intent);
                }
                break;
            case R.id.btnCancel:
                break;
            default:
                break;
        }
        finish();
    }
}
