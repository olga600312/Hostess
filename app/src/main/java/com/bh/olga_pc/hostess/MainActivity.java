package com.bh.olga_pc.hostess;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import beans.Client;
import beans.Event;
import db.DBQuery;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_NUMBER_FLAG_SIGNED;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private CalendarCustomView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_event);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                final Dialog dialog = new Dialog(view.getContext(), R.style.AppTheme_Holo_Light_DarkActionBar);
                dialog.setContentView(R.layout.new_event_layout);
                dialog.getWindow().getAttributes().windowAnimations = R.style.animationdialog;
                Date date = calendarView.getSelectedDate();
                if (date != null) {
                    dialog.setTitle(new SimpleDateFormat("dd/MM/yyyy").format(date));
                }

                // set the custom dialog components - text and button


                final TextView fromTime = (TextView) dialog.findViewById(R.id.tvFromDate);
                final TextView toTime = (TextView) dialog.findViewById(R.id.tvToDate);
                fromTime.setText("15:00");
                toTime.setText("18:00");
                fromTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub

                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(dialog.getContext(), new OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                fromTime.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                                toTime.setText(String.format("%02d:%02d", selectedHour + 3, selectedMinute));
                            }
                        }, 15, 0, true);//Yes 24 hour time
                        mTimePicker.setTitle("Select Time");
                        mTimePicker.show();

                    }
                });

                toTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub

                        TimePickerDialog mTimePicker;
                        mTimePicker = new TimePickerDialog(dialog.getContext(), new OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                toTime.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                            }
                        }, 18, 0, true);//Yes 24 hour time
                        mTimePicker.setTitle("Select Time");
                        mTimePicker.show();

                    }
                });

                final EditText teGuests = (EditText) dialog.findViewById(R.id.teGuests);
                teGuests.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_SIGNED);

                final EditText teLocation = (EditText) dialog.findViewById(R.id.teLocation);
                teLocation.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_SIGNED);

                Button btnOk = (Button) dialog.findViewById(R.id.dialogButtonSave);
                // if button is clicked, close the custom dialog
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText teClient = (EditText) dialog.findViewById(R.id.client_name);
                        EditText phone = (EditText) dialog.findViewById(R.id.phone);

                        String clientName = teClient.getText().toString();
                        if (clientName.trim().isEmpty()) {
                            teClient.setError("Invalid client name");
                            teClient.selectAll();
                            teClient.requestFocus();
                            return;
                        }

                        if (phone.getText().toString().trim().isEmpty()) {
                            phone.setError("Invalid phone number");
                            phone.selectAll();
                            phone.requestFocus();
                            return;
                        }
                        int guests = 0;
                        try {
                            guests = Integer.parseInt(teGuests.getText().toString());
                        } catch (Exception ex) {
                            teGuests.setError(ex.getLocalizedMessage());
                            teGuests.selectAll();
                            teGuests.requestFocus();
                            return;
                        }
                        if (guests <= 0) {
                            teGuests.setError("Invalid guests number");
                            teGuests.selectAll();
                            teGuests.requestFocus();
                            return;
                        }
                        String from = fromTime.getText().toString();
                        String to = toTime.getText().toString();
                        String memo = ((EditText) dialog.findViewById(R.id.client_name)).getText().toString();
                        Client c = new Client();
                        c.setName(clientName);
                        c.setPhone(phone.getText().toString());
                        Event e = new Event();

                        e.setGuests(guests);
                        e.setMemo(memo);
                        e.setType(1);
                        e.setDateCreate(System.currentTimeMillis());
                        e.setDateUpdate(e.getDateCreate());

                        String arr[] = from.split(":");
                        if (arr.length != 2) {
                            Toast.makeText(dialog.getContext(), "Invalid time", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Date date = calendarView.getSelectedDate();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(date);
                        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(arr[0]));
                        calendar.set(Calendar.MINUTE, Integer.parseInt(arr[1]));
                        e.setStartTime(calendar.getTimeInMillis());

                        arr = to.split(":");
                        if (arr.length != 2) {
                            Toast.makeText(dialog.getContext(), "Invalid time", Toast.LENGTH_LONG).show();
                            return;
                        }
                        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(arr[0]));
                        calendar.set(Calendar.MINUTE, Integer.parseInt(arr[1]));
                        e.setEndTime(calendar.getTimeInMillis());
                        if (e.getEndTime() - e.getStartTime() <= 0 || e.getEndTime() - e.getStartTime() > 15 * 60 * 60 * 1000) {
                            Toast.makeText(dialog.getContext(), "Invalid time", Toast.LENGTH_LONG).show();
                            return;
                        }

                        c = new DBQuery.Clients(dialog.getContext()).create(c);
                        e.setClient(c);
                        e = new DBQuery.Events(dialog.getContext()).create(e);
                        Toast.makeText(dialog.getContext(), R.string.success, Toast.LENGTH_SHORT);
                        calendarView.addEventCalendar(e);
                        dialog.dismiss();
                    }
                });
                Button btnCancel = (Button) dialog.findViewById(R.id.dialogButtonCancel);
                // if button is clicked, close the custom dialog
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        calendarView = (CalendarCustomView) findViewById(R.id.custom_calendar);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_history) {
            // Handle the camera action
        } else if (id == R.id.nav_tbl_layout) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
