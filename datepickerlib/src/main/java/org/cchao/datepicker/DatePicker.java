package org.cchao.datepicker;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by shucc on 17/6/9.
 * cc@cchao.org
 */
public class DatePicker extends PopupWindow {

    //打开关闭弹出框
    private final int OPEN_POP = 0;
    private final int HIDE_POP = 1;

    private TextView textYear;
    private TextView textMonth;
    private TextView textDay;
    private WheelView wvYear;
    private WheelView wvMonth;
    private WheelView wvDay;
    private TextView textCancel;
    private TextView textOk;

    private Activity activity;

    private int year = 1970;

    private int month = 1;

    private int day = 1;

    private OnDateChangeListener onDateChangeListener;

    private ArrayList<String> years = new ArrayList<>();
    private ArrayList<String> months = new ArrayList<>();
    private ArrayList<String> days = new ArrayList<>();

    public DatePicker(Activity activity, int year, int month, int day, OnDateChangeListener onDateChangeListener) {
        this.activity = activity;
        this.year = year;
        this.month = month;
        this.day = day;
        this.onDateChangeListener = onDateChangeListener;

        View view = LayoutInflater.from(activity).inflate(R.layout.pop_date_picker, null);
        bindView(view);
        bindEvent();

        setAnimationStyle(R.style.Pop_Style);
        setTouchable(true);
        setFocusable(true);
        setBackgroundDrawable(new BitmapDrawable());
        setContentView(view);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        initData();
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        setWindow(OPEN_POP);
        super.showAtLocation(parent, gravity, x, y);

    }

    @Override
    public void dismiss() {
        setWindow(HIDE_POP);
        super.dismiss();
    }

    private void bindView(View view) {
        textYear = (TextView) view.findViewById(R.id.text_year);
        textMonth = (TextView) view.findViewById(R.id.text_month);
        textDay = (TextView) view.findViewById(R.id.text_day);
        wvYear = (WheelView) view.findViewById(R.id.wv_year);
        wvMonth = (WheelView) view.findViewById(R.id.wv_month);
        wvDay = (WheelView) view.findViewById(R.id.wv_day);
        textCancel = (TextView) view.findViewById(R.id.text_cancel);
        textOk = (TextView) view.findViewById(R.id.text_ok);

        wvYear.setTextSize(14);
        wvYear.setVisibleItemCount(5);
        wvYear.setUseWeight(true);
        wvMonth.setTextSize(14);
        wvMonth.setVisibleItemCount(5);
        wvMonth.setUseWeight(true);
        wvDay.setTextSize(14);
        wvDay.setUseWeight(true);
        wvDay.setVisibleItemCount(5);
    }

    protected void bindEvent() {
        wvYear.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
            @Override
            public void onSelected(int index) {
                int preYear = year;
                year = 1900 + index;
                textYear.setText(String.valueOf(year));
                //如果当前是2月，且闰年平年之间进行了切换，月天数需要变化
                if (month == 2 && isChangeDaysByYear(preYear, year)) {
                    setDays();
                    wvDay.setItems(days);
                    if (day > getDays(2)) {
                        day = getDays(2);
                        wvDay.setSelectedIndex(day - 1);
                        textDay.setText(String.valueOf(day));
                    }
                }
            }
        });
        wvMonth.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
            @Override
            public void onSelected(int index) {
                int preMonth = month;
                month = 1 + index;
                textMonth.setText(String.valueOf(month < 10 ? "0" + month : month));
                setDays();
                wvDay.setItems(days);
                if (isChangeDaysByMonth(preMonth, month)) {
                    day = getDays(1 + index);
                    wvDay.setSelectedIndex(day - 1);
                    textDay.setText(String.valueOf(day));
                }
            }
        });
        wvDay.setOnItemSelectListener(new WheelView.OnItemSelectListener() {
            @Override
            public void onSelected(int index) {
                day = 1 + index;
                textDay.setText(String.valueOf(day < 10 ? "0" + day : day));

            }
        });
        textOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDateChangeListener.onChange(year, month, day);
                dismiss();
            }
        });
        textCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void initData() {
        for (int i = 1900; i < 2100; i++) {
            years.add(String.valueOf(i));
        }
        for (int i = 1; i <= 12; i++) {
            months.add(i < 10 ? "0".concat(String.valueOf(i)) : String.valueOf(i));
        }
        setDays();
        wvYear.setItems(years, year - 1900);
        wvMonth.setItems(months, month - 1);
        wvDay.setItems(days, day - 1);
        textYear.setText(String.valueOf(year));
        textMonth.setText(String.valueOf(month < 10 ? "0" + month : month));
        textDay.setText(String.valueOf(day < 10 ? "0" + day : day));
    }

    /**
     * 设置当前月天数
     */
    private void setDays() {
        int endDay = getDays(month);
        days.clear();
        for (int i = 1; i <= endDay; i++) {
            days.add(i < 10 ? "0".concat(String.valueOf(i)) : String.valueOf(i));
        }
    }

    /**
     * 判断月变化判断月天数是否变化
     * @param preMonth
     * @param nowMonth
     * @return
     */
    private boolean isChangeDaysByMonth(int preMonth, int nowMonth) {
        int preDays = getDays(preMonth);
        int nowDays = getDays(nowMonth);
        if (preDays == nowDays) {
            return false;
        }
        if (preDays > nowDays && day > nowDays) {
            return true;
        }
        return false;
    }

    /**
     * 根据年份变化判断月天数是否变化
     * @param preYear
     * @param nowYear
     * @return
     */
    private boolean isChangeDaysByYear(int preYear, int nowYear) {
        boolean preIsLeapYear = (preYear % 4 == 0 && preYear % 100 != 0 || preYear % 400 == 0);
        boolean nowIsLeapYear = (nowYear % 4 == 0 && nowYear % 100 != 0 || nowYear % 400 == 0);
        return preIsLeapYear != nowIsLeapYear;
    }

    /**
     * 根据年月获取天数
     * @param preMonth
     * @return
     */
    private int getDays(int preMonth) {
        int preDays = 31;
        if (preMonth == 4 || preMonth == 6 || preMonth == 9 || preMonth == 11) {
            preDays = 30;
        } else if (preMonth == 2) {
            if(year % 4 == 0 && year % 100 != 0 || year % 400 == 0) {
                preDays = 29;
            } else {
                preDays = 28;
            }
        }
        return preDays;
    }

    /**
     * 设置弹出框出现与隐藏时背景透明度变化
     *
     * @param type
     */
    public void setWindow(int type) {
        //设置背景颜色变暗
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        if (type == OPEN_POP) {
            lp.alpha = 0.6f;
        } else {
            lp.alpha = 1.0f;
        }
        activity.getWindow().setAttributes(lp);
    }
}
