package com.bh.olga_pc.hostess;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bh.olga_pc.hostess.adapters.EventListAdapter;
import com.bh.olga_pc.hostess.decorators.DividerItemDecoration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.bh.olga_pc.hostess.beans.Event;
import com.bh.olga_pc.hostess.listeners.RecyclerViewClickListener;
import com.bh.olga_pc.hostess.listeners.RecyclerViewTouchListener;

import db.DBQuery;

/**
 * Created by Olga-PC on 7/8/2017.
 */

public class CalendarCustomView extends LinearLayout {
    private static final String TAG = CalendarCustomView.class.getSimpleName();
    private ImageView previousButton, nextButton;
    private TextView currentDate;
    private GridView calendarGridView;
    private RecyclerView recyclerView;
    private Button addEventButton;
    private static final int MAX_CALENDAR_COLUMN = 42;
    private int month, year;
    private SimpleDateFormat formatter = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
    private Calendar cal = Calendar.getInstance(Locale.ENGLISH);
    private Context context;
    private GridAdapter gridAdapter;
    private EventListAdapter eventAdapter;
    private OnEventListener onEventListener;

    public CalendarCustomView(Context context) {
        super(context);
    }

    public CalendarCustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initializeUILayout();
        setUpCalendarAdapter();
        setPreviousButtonClickEvent();
        setNextButtonClickEvent();
        setGridCellClickEvents();
        Log.d(TAG, "I need to call this method");
    }

    public CalendarCustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initializeUILayout() {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.calendar_layout, this);
        previousButton = (ImageView) view.findViewById(R.id.previous_month);
        nextButton = (ImageView) view.findViewById(R.id.next_month);
        currentDate = (TextView) view.findViewById(R.id.display_current_date);
        calendarGridView = (GridView) view.findViewById(R.id.calendar_grid);
        recyclerView = (RecyclerView) view.findViewById(R.id.event_list);
        //recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        eventAdapter = new EventListAdapter();
        recyclerView.setAdapter(eventAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getContext(), recyclerView, new RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Event e = eventAdapter.getValueAt(position);
                if (onEventListener != null)
                    onEventListener.onClick(e);
                Toast.makeText(getContext(), e.getClient() + " is clicked!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {
                Event e = eventAdapter.getValueAt(position);
                if (onEventListener != null)
                    onEventListener.onLongClick(e);
                Toast.makeText(getContext(), e.getClient() + " is long pressed!", Toast.LENGTH_SHORT).show();

            }
        }));
    }

    public OnEventListener getOnEventListener() {
        return onEventListener;
    }

    public void setOnEventListener(OnEventListener onEventListener) {
        this.onEventListener = onEventListener;
    }

    private void setPreviousButtonClickEvent() {
        previousButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cal.add(Calendar.MONTH, -1);
                setUpCalendarAdapter();
            }
        });
    }

    private void setNextButtonClickEvent() {
        nextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                cal.add(Calendar.MONTH, 1);
                setUpCalendarAdapter();
            }
        });
    }

    private void setGridCellClickEvents() {
        calendarGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Date date = gridAdapter.getItem(position);
                if (date != null) {
                    Toast.makeText(context, "Clicked " + new SimpleDateFormat("dd/MM/yyyy").format(date), Toast.LENGTH_LONG).show();
                    setSelectedDate(date);


                } else
                    Toast.makeText(context, "Clicked " + position, Toast.LENGTH_LONG).show();

            }
        });
        calendarGridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                Date date = gridAdapter.getItem(position);
                if (date != null) {
                    setSelectedDate(date);
                    Intent intent = new Intent(view.getContext(), DateTableActivity.class);
                    intent.putExtra("selectedDate", date);
                    view.getContext().startActivity(intent);
                }
                return true;
            }


        });
    }

    private void setSelectedDate(Date date) {
        gridAdapter.setSelectedDate(date);
        gridAdapter.notifyDataSetChanged();
        calendarGridView.invalidate();
        updateEventList(gridAdapter.getSelectedDate());
    }

    private void setUpCalendarAdapter() {
        List<Date> dayValueInCells = new ArrayList<>();
        Calendar mCal = (Calendar) cal.clone();
        mCal.set(Calendar.DAY_OF_MONTH, 1);

        List<Event> events = new DBQuery.Events(context).getAllFutureEvents(mCal.get(Calendar.YEAR), mCal.get(Calendar.MONTH), 1);


        int firstDayOfTheMonth = mCal.get(Calendar.DAY_OF_WEEK) - 1;
        mCal.add(Calendar.DAY_OF_MONTH, -firstDayOfTheMonth);
        while (dayValueInCells.size() < MAX_CALENDAR_COLUMN) {
            dayValueInCells.add(mCal.getTime());
            mCal.add(Calendar.DAY_OF_MONTH, 1);
        }
        Log.d(TAG, "Number of date " + dayValueInCells.size());
        String sDate = formatter.format(cal.getTime());
        currentDate.setText(sDate);
        gridAdapter = new GridAdapter(context, dayValueInCells, cal, events);
        calendarGridView.setAdapter(gridAdapter);
        updateEventList(gridAdapter.getSelectedDate());
    }

    private void updateEventList(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        eventAdapter.clear();
        eventAdapter.addAll(new DBQuery.Events(context).getCurrentDayEvents(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)));
        eventAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(eventAdapter);

    }

    public Date getSelectedDate() {
        return gridAdapter.getSelectedDate();
    }

    public void addEventCalendar(Event e) {
        gridAdapter.addEvent(e);
        gridAdapter.notifyDataSetChanged();
        eventAdapter.add(e);
        eventAdapter.notifyDataSetChanged();
    }

    public boolean updateEvent(Event e){
        boolean fl=eventAdapter.update(e);
        if(fl) {
            eventAdapter.notifyDataSetChanged();
        }
        return fl;
    }

    //////////////////////////////////////////
    //
    //  Listeners
    //
    /////////////////////////////////////////
    public static interface OnEventListener {
        void onClick(Event e);

        void onLongClick(Event e);
    }
}
