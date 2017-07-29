package com.bh.olga_pc.hostess;

import android.content.Intent;
import android.graphics.RectF;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bh.olga_pc.hostess.customViews.DateRestView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.bh.olga_pc.hostess.beans.Event;
import db.DBQuery;

public class DateTableActivity extends AppCompatActivity {

    private DateRestView dateRestView;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_table);
        // Get a reference for the week view in the layout.
        dateRestView = (DateRestView) findViewById(R.id.date_rest_view);
        dateRestView.setAllDayEventEnable(false);

// Set an action when any event is clicked.
        dateRestView.setOnEventClickListener(new DateRestView.EventClickListener() {
            @Override
            public void onEventClick(Event event, RectF eventRect) {

            }
        });

// The week view has infinite scrolling horizontally. We have to provide the events of a
// month every time the month changes on the week view.
        dateRestView.setRegionChangeListener(new RegionLoader.RegionChangeListener() {
            @Override
            public List<? extends Event> onRegionChange(int region, Calendar date) {
                if(region!=1){
                    return new ArrayList<>();
                }

                return new DBQuery.Events(DateTableActivity.this).getCurrentDayEvents(date);
            }
        });

// Set long press listener for events.
        dateRestView.setEventLongPressListener(new DateRestView.EventLongPressListener() {
            @Override
            public void onEventLongPress(Event event, RectF eventRect) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent = getIntent();
        Date date = (Date) intent.getSerializableExtra("selectedDate");
        Calendar c=Calendar.getInstance();
        c.setTime(date);
        dateRestView.setSelectedDate(c);
    }
}
