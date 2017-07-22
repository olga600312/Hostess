package com.bh.olga_pc.hostess;

/**
 * Created by Olga-PC on 7/8/2017.
 */

import android.content.Context;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import beans.Event;

public class GridAdapter extends ArrayAdapter<Date> {
    private static final String TAG = GridAdapter.class.getSimpleName();
    private LayoutInflater mInflater;
    private List<Date> monthlyDates;
    private Calendar currentDate;
    private List<Event> allEvents;
    private Date selectedDate;


    GridAdapter(Context context, List<Date> monthlyDates, Calendar currentDate, List<Event> allEvents) {
        super(context, R.layout.single_cell_layout);
        this.monthlyDates = monthlyDates;
        this.currentDate = currentDate;
        this.allEvents = allEvents;
        mInflater = LayoutInflater.from(context);
        selectedDate=new Date();

    }

    public Date getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(Date selectedDate) {
        this.selectedDate = selectedDate;
    }
    public void addEvent(Event e){
        allEvents.add(e);
    }

    private Calendar getSelectedCalendar() {
        Calendar c = null;
        if (selectedDate != null) {
            c = Calendar.getInstance();
            c.setTime(selectedDate);
        }
        return c;
    }

    private boolean isNow(Calendar c) {
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.YEAR) == c.get(Calendar.YEAR) && now.get(Calendar.MONTH) == c.get(Calendar.MONTH) && now.get(Calendar.DAY_OF_MONTH) == c.get(Calendar.DAY_OF_MONTH);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Date mDate = monthlyDates.get(position);
        Calendar dateCal = Calendar.getInstance();
        dateCal.setTime(mDate);
        int dayValue = dateCal.get(Calendar.DAY_OF_MONTH);
        int displayMonth = dateCal.get(Calendar.MONTH) + 1;
        int displayYear = dateCal.get(Calendar.YEAR);
        int currentMonth = currentDate.get(Calendar.MONTH) + 1;
        int currentYear = currentDate.get(Calendar.YEAR);
        View view = convertView;
        if (view == null) {
            view = mInflater.inflate(R.layout.single_cell_layout, parent, false);
        }
        Calendar c = getSelectedCalendar();
        if (c != null && displayMonth == c.get(Calendar.MONTH) + 1
                && displayYear == c.get(Calendar.YEAR) && dayValue == c.get(Calendar.DAY_OF_MONTH)) {
            view.setBackgroundResource(R.drawable.border_selected_date);
        } else if (displayMonth == currentMonth && displayYear == currentYear) {
            if (isNow(dateCal)) {
                view.setBackgroundResource(R.drawable.border_now);
            } else
                view.setBackgroundResource(R.color.primary_light);
        } else {
            view.setBackgroundResource(R.color.white);
            //view.setBackgroundColor(Color.parseColor("#cccccc"));
        }

        //Add day to calendar
        TextView cellNumber = (TextView) view.findViewById(R.id.calendar_date_id);
        cellNumber.setText(String.valueOf(dayValue));
        //Add events to the calendar
        TextView eventIndicator = (TextView) view.findViewById(R.id.event_id);
        int count=getEventsCount(displayYear,displayMonth,dayValue);
        //eventIndicator.setBackgroundColor(Color.parseColor("#FF4081"));
        eventIndicator.setText(""+(count>0?count:""));
        return view;
    }

    private int getEventsCount(int displayYear,int displayMonth,int dayValue){
        int c=0;
        Calendar eventCalendar = Calendar.getInstance();
        for (int i = 0; i < allEvents.size(); i++) {
            eventCalendar.setTimeInMillis(allEvents.get(i).getStartTime());
            if (dayValue == eventCalendar.get(Calendar.DAY_OF_MONTH) && displayMonth == eventCalendar.get(Calendar.MONTH) + 1
                    && displayYear == eventCalendar.get(Calendar.YEAR)) {
                //eventIndicator.setBackgroundColor(Color.parseColor("#FF4081"));
                c++;
            }
        }

        return c;
    }

    @Override
    public int getCount() {
        return monthlyDates.size();
    }

    @Nullable
    @Override
    public Date getItem(int position) {
        return monthlyDates.get(position);
    }

    @Override
    public int getPosition(Date item) {
        return monthlyDates.indexOf(item);
    }
}