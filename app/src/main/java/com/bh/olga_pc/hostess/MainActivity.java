package com.bh.olga_pc.hostess;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.HapticFeedbackConstants;
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

import com.android.colorpicker.ColorPickerDialog;
import com.android.colorpicker.ColorPickerSwatch;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.bh.olga_pc.hostess.beans.Client;
import com.bh.olga_pc.hostess.beans.Event;
import com.bh.olga_pc.hostess.listeners.RecyclerViewClickListener;
import com.bh.olga_pc.hostess.listeners.RecyclerViewTouchListener;

import db.DBQuery;

import static android.text.InputType.TYPE_CLASS_NUMBER;
import static android.text.InputType.TYPE_NUMBER_FLAG_SIGNED;
import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "Hostess.MainActivity";
    private CalendarCustomView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_event);
        initOnClickListener(fab);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        calendarView = (CalendarCustomView) findViewById(R.id.custom_calendar);
        calendarView.setOnEventListener(new CalendarCustomView.OnEventListener() {
            @Override
            public void onClick(Event e) {
                if (e != null) {
                    calendarView.performHapticFeedback(HapticFeedbackConstants.CONTEXT_CLICK);
                    showUpdateEventDialog(e);
                } else {
                    Toast.makeText(MainActivity.this, "Can't update event (null)!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLongClick(Event e) {
                if (e != null) {
                    calendarView.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
                    showUpdateEventDialog(e);
                } else {
                    Toast.makeText(MainActivity.this, "Can't update event (null)!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initOnClickListener(FloatingActionButton fab) {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                showNewEventDialog();
            }
        });

    }

    private void showNewEventDialog() {
        showEventDialog(null);
    }

    private void showUpdateEventDialog(Event event) {
        showEventDialog(event);
    }

    private void showEventDialog(Event originEvent) {
        final Event event;
        if (originEvent != null) {
            try {
                event = (Event) originEvent.clone();
            } catch (CloneNotSupportedException e1) {
                Log.e(TAG, e1.getLocalizedMessage());
                Toast.makeText(this, "Invalid Event", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            event = null;
        }
        final Dialog dialog = new Dialog(this, R.style.AppTheme_Holo_Light_DarkActionBar);
        dialog.setContentView(R.layout.new_event_layout);
        dialog.getWindow().getAttributes().windowAnimations = R.style.animationdialog;


        Date date = event != null ? new Date(event.getStartTime()) : calendarView.getSelectedDate();
        if (date != null) {
            dialog.setTitle(SimpleDateFormat.getDateInstance().format(date));
        }
        EditText teClient = (EditText) dialog.findViewById(R.id.client_name);
        if (event != null) {
            teClient.setText(event.getClient().getName());
        }
        EditText tePhone = (EditText) dialog.findViewById(R.id.phone);
        if (event != null) {
            tePhone.setText(event.getClient().getPhone());
        }

        final TextView fromTime = (TextView) dialog.findViewById(R.id.tvFromDate);
        final TextView toTime = (TextView) dialog.findViewById(R.id.tvToDate);
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        fromTime.setText(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(new Date(event != null ? event.getStartTime() : calendar.getTimeInMillis())));
        calendar.add(Calendar.HOUR, 3);
        toTime.setText(SimpleDateFormat.getTimeInstance(DateFormat.SHORT).format(new Date(event != null ? event.getEndTime() : calendar.getTimeInMillis())));

        fromTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                TimePickerDialog mTimePicker;
                Calendar calendar = Calendar.getInstance();

                if (event != null) {
                    calendar.setTimeInMillis(event.getStartTime());
                } else {
                    calendar.set(Calendar.HOUR_OF_DAY, 15);
                    calendar.set(Calendar.MINUTE, 0);
                }
                mTimePicker = new TimePickerDialog(dialog.getContext(), new OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        calendar.set(Calendar.MINUTE, selectedMinute);
                        calendar.set(Calendar.SECOND, 0);
                        fromTime.setText(SimpleDateFormat.getTimeInstance(DateFormat.SHORT, Locale.UK).format(new Date(calendar.getTimeInMillis())));
                        calendar.add(Calendar.HOUR, 3);
                        toTime.setText(SimpleDateFormat.getTimeInstance(DateFormat.SHORT, Locale.UK).format(new Date(calendar.getTimeInMillis())));
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        toTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub


                Calendar calendar = Calendar.getInstance();
                if (event != null) {
                    calendar.setTimeInMillis(event.getEndTime());
                } else {
                    calendar.set(Calendar.HOUR_OF_DAY, 15);
                    calendar.set(Calendar.MINUTE, 0);
                }
                TimePickerDialog mTimePicker = new TimePickerDialog(dialog.getContext(), new OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                        calendar.set(Calendar.MINUTE, selectedMinute);
                        calendar.set(Calendar.SECOND, 0);
                        toTime.setText(SimpleDateFormat.getTimeInstance(DateFormat.SHORT, Locale.UK).format(new Date(calendar.getTimeInMillis())));

                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        final EditText teGuests = (EditText) dialog.findViewById(R.id.teGuests);
        teGuests.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_SIGNED);
        if (event != null) {
            teGuests.setText(String.valueOf(event.getGuests()));
        }

        final EditText teLocation = (EditText) dialog.findViewById(R.id.teLocation);
        teLocation.setInputType(TYPE_CLASS_NUMBER | TYPE_NUMBER_FLAG_SIGNED);
        if (event != null) {
            teLocation.setText(String.valueOf(event.getTbl()));
        }

        final Button btnColor = (Button) dialog.findViewById(R.id.btnColorPicker);
        if (event != null && event.getColor() != 0) {
            btnColor.setBackgroundColor(event.getColor());
        }
        btnColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ColorPickerDialog colorPickerDialog = new ColorPickerDialog();
                colorPickerDialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int color) {
                        btnColor.setBackgroundColor(color);
                    }
                });
                Drawable background = btnColor.getBackground();
                int rgb = 0;
                if (background instanceof ColorDrawable) {
                    rgb = ((ColorDrawable) background).getColor();
                }
                int colors[] = Utilities.getColorPalette(MainActivity.this, TAG);
                colorPickerDialog.initialize(R.string.color, colors, rgb, 5, colors.length);
                colorPickerDialog.show(getFragmentManager(), "ColorPicker");

            }
        });

        Button btnOk = (Button) dialog.findViewById(R.id.dialogButtonSave);
        if (event != null) {
            btnOk.setText(R.string.update);
        }
        // if button is clicked, close the custom dialog
        btnOk.setOnClickListener(new View.OnClickListener()

        {
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
                Event e = null;
                try {
                    e = event != null ? (Event) event.clone() : new Event();
                } catch (CloneNotSupportedException e1) {
                    Log.e(TAG, e1.getLocalizedMessage());
                    Toast.makeText(dialog.getContext(), "Invalid Event", Toast.LENGTH_SHORT).show();
                    return;
                }

                e.setGuests(guests);
                e.setMemo(memo);
                e.setType(1);
                e.setDateCreate(System.currentTimeMillis());
                e.setDateUpdate(e.getDateCreate());
                String tbl = teLocation.getText().toString();
                e.setTbl(tbl.isEmpty() ? 0 : Integer.parseInt(tbl));
                Drawable background = btnColor.getBackground();
                int rgb = 0;
                if (background instanceof ColorDrawable) {
                    rgb = ((ColorDrawable) background).getColor();
                }
                e.setColor(rgb);
                Date date = calendarView.getSelectedDate();
                try {
                    // DateUtils.formatDateTime(getContext(), timeInMillis, DateUtils.FORMAT_SHOW_TIME);
                    Date dStart = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).parse(from);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(dStart);
                    Calendar cStart = Calendar.getInstance();
                    cStart.setTimeInMillis(date.getTime());// for date only
                    cStart.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
                    cStart.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
                    e.setStartTime(cStart.getTimeInMillis());

                    Date dEnd = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT).parse(to);
                    calendar = Calendar.getInstance();
                    calendar.setTime(dEnd);
                    Calendar cEnd = Calendar.getInstance();
                    cEnd.setTimeInMillis(date.getTime());// for date only
                    cEnd.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
                    cEnd.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
                    e.setEndTime(cEnd.getTimeInMillis());

                } catch (ParseException e1) {
                    Log.e(TAG, e1.getLocalizedMessage());
                    Toast.makeText(dialog.getContext(), "Invalid time", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (e.getEndTime() - e.getStartTime() <= 0 || e.getEndTime() - e.getStartTime() > 15 * 60 * 60 * 1000) {
                    Toast.makeText(dialog.getContext(), "Invalid time", Toast.LENGTH_LONG).show();
                    return;
                }

                c = event != null ? event.getClient() : new DBQuery.Clients(dialog.getContext()).create(c);
                e.setClient(c);
                e = event != null ? new DBQuery.Events(dialog.getContext()).update(e) : new DBQuery.Events(dialog.getContext()).create(e);
                if (e != null) {
                   if (event == null) {
                        calendarView.addEventCalendar(e);
                    } else {
                        if (!calendarView.updateEvent(e)) {
                            Toast.makeText(dialog.getContext(), "Can't update event!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    Toast.makeText(dialog.getContext(), R.string.success, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    Toast.makeText(dialog.getContext(), "Can't " + (event != null ? "update" : "insert") + " event!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Button btnCancel = (Button) dialog.findViewById(R.id.dialogButtonCancel);
        // if button is clicked, close the custom dialog
        btnCancel.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
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
