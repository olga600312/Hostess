package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.bh.olga_pc.hostess.beans.Client;
import com.bh.olga_pc.hostess.beans.Event;
import com.bh.olga_pc.hostess.beans.Login;
import com.bh.olga_pc.hostess.beans.User;

/**
 * Created by Olga-PC on 7/1/2017.
 */

public class DBQuery {
    private final static String TAG = "DBQuery";


    /**
     * CRUD
     * Create
     * Retrieve
     * Update
     * Delete
     */

    public static class Users {
        private DBHelper helper;

        public Users(Context context) {
            helper = new DBHelper(context);
        }

        public Cursor getCursor() {
            SQLiteDatabase database = helper.getReadableDatabase();
            return database.query(DBHelper.USERS.TABLE_NAME, null, null, null, null, null, null);
        }

        public List<User> retrieve() {
            List<User> usersList = new ArrayList<>();
            SQLiteDatabase database = helper.getReadableDatabase();
            Cursor cursor = database.query(DBHelper.USERS.TABLE_NAME, null, null, null, null, null, DBHelper.USERS.NAME);
            if (cursor.moveToFirst()) {
                usersList = getUsers(cursor);
            }
            cursor.close();
            return usersList;
        }

        public User retrieve(int id) {
            SQLiteDatabase database = helper.getReadableDatabase();
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            queryBuilder.setTables(DBHelper.USERS.TABLE_NAME);
            queryBuilder.appendWhere(DBHelper.USERS.ID + " = " + id);
            Cursor cursor = queryBuilder.query(database, null, null, null, null, null, null);
            List<User> arr = getUsers(cursor);
            cursor.close();
            return arr.size() > 0 ? arr.get(0) : null;
        }

        public List<User> retrieve(String userName, String password) {
            SQLiteDatabase database = helper.getReadableDatabase();
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            queryBuilder.setTables(DBHelper.USERS.TABLE_NAME);
            queryBuilder.appendWhere(DBHelper.USERS.NAME + "= '" + userName + "' AND " + DBHelper.USERS.PASSWORD + "= '" + password + "'");
            Cursor cursor = queryBuilder.query(database, null, null, null, null, null, null);
            List<User> arr = getUsers(cursor);
            cursor.close();
            return arr;
        }

        private ArrayList<User> getUsers(Cursor cursor) {
            ArrayList<User> arr = new ArrayList<>();
            final int idColumn = cursor.getColumnIndex(DBHelper.USERS.ID);
            final int nameColumn = cursor.getColumnIndex(DBHelper.USERS.NAME);
            final int typeColumn = cursor.getColumnIndex(DBHelper.USERS.TYPE);
            final int userNameColumn = cursor.getColumnIndex(DBHelper.USERS.USERNAME);
            final int passwordColumn = cursor.getColumnIndex(DBHelper.USERS.PASSWORD);

            do {
                User user = new User();
                user.setId(cursor.getInt(idColumn));
                user.setType(cursor.getInt(typeColumn));
                user.setName(cursor.getString(nameColumn));
                Login login = new Login();
                login.setUserName(cursor.getString(userNameColumn));
                login.setPassword(cursor.getString(passwordColumn));
                arr.add(user);
            }
            while (cursor.moveToNext());
            return arr;
        }


    }

    public static class Events {
        private DBHelper helper;

        public Events(Context context) {
            helper = new DBHelper(context);
        }

        public List<Event> getAllFutureEvents() {
            // I use the rawQuery here only for example that I wouldn't forget in the future
            String query = "SELECT * FROM " + DBHelper.EVENTS.TABLE_NAME + " WHERE " + DBHelper.EVENTS.TM_START + ">= ?";
            Cursor cursor = helper.getReadableDatabase().rawQuery(query, new String[]{String.valueOf(System.currentTimeMillis())});
            List<Event> events = new ArrayList<>();
            if (cursor.moveToFirst()) {
                events = getEvents(cursor);

            }
            cursor.close();
            return events;
        }

        public List<Event> getAllFutureEvents(int year, int month, int day) {
            // I use the rawQuery here only for example that I wouldn't forget in the future
            Calendar from = Calendar.getInstance();
            from.setTimeInMillis(0);
            from.set(Calendar.YEAR, year);
            from.set(Calendar.MONTH, month);
            from.set(Calendar.DAY_OF_MONTH, day);
            String query = "SELECT * FROM " + DBHelper.EVENTS.TABLE_NAME + " WHERE " + DBHelper.EVENTS.TM_START + ">=?";
            Cursor cursor = helper.getReadableDatabase().rawQuery(query, new String[]{"" + from.getTimeInMillis()});
            List<Event> events = new ArrayList<>();

            events = getEvents(cursor);

            cursor.close();
            return events;
        }


        public List<Event> getCurrentDayEvents(int year, int month, int day) {
            // I use the rawQuery here only for example that I wouldn't forget in the future
            Calendar from = Calendar.getInstance();
            from.setTimeInMillis(0);
            from.set(Calendar.YEAR, year);
            from.set(Calendar.MONTH, month);
            from.set(Calendar.DAY_OF_MONTH, day);
            Calendar to = Calendar.getInstance();
            to.setTimeInMillis(from.getTimeInMillis());
            to.set(Calendar.HOUR_OF_DAY, 23);
            to.set(Calendar.MINUTE, 59);
            to.set(Calendar.SECOND, 59);
            String query = "SELECT * FROM " + DBHelper.EVENTS.TABLE_NAME + " WHERE " + DBHelper.EVENTS.TM_START + ">=? AND " + DBHelper.EVENTS.TM_START + "<=?";
            Cursor cursor = helper.getReadableDatabase().rawQuery(query, new String[]{
                    "" + from.getTimeInMillis(), "" + to.getTimeInMillis()});
            List<Event> events = new ArrayList<>();
            if (cursor.moveToFirst()) {
                events = getEvents(cursor);

            }
            cursor.close();
            return events;
        }

        public List<Event> getCurrentDayEvents(Calendar c) {
            // I use the rawQuery here only for example that I wouldn't forget in the future
            Calendar from = (Calendar) c.clone();
            from.set(Calendar.HOUR_OF_DAY, 0);
            from.set(Calendar.MINUTE, 0);
            from.set(Calendar.SECOND, 0);
            Calendar to = Calendar.getInstance();
            to.setTimeInMillis(from.getTimeInMillis());
            to.set(Calendar.HOUR_OF_DAY, 23);
            to.set(Calendar.MINUTE, 59);
            to.set(Calendar.SECOND, 59);
            String query = "SELECT * FROM " + DBHelper.EVENTS.TABLE_NAME + " WHERE " + DBHelper.EVENTS.TM_START + ">=? AND " + DBHelper.EVENTS.TM_START + "<=?";
            Cursor cursor = helper.getReadableDatabase().rawQuery(query, new String[]{
                    "" + from.getTimeInMillis(), "" + to.getTimeInMillis()});
            List<Event> events = new ArrayList<>();
            if (cursor.moveToFirst()) {
                events = getEvents(cursor);

            }
            cursor.close();
            return events;
        }

        private Date convertStringToDate(String dateInString) {
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            Date date = null;
            try {
                date = format.parse(dateInString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return date;
        }

        public Cursor getCursor() {
            SQLiteDatabase database = helper.getReadableDatabase();
            return database.query(DBHelper.EVENTS.TABLE_NAME, null, null, null, null, null, null);
        }

        public List<Event> retrieve() {
            List<Event> events = new ArrayList<>();
            SQLiteDatabase database = helper.getReadableDatabase();
            Cursor cursor = database.query(DBHelper.EVENTS.TABLE_NAME, null, null, null, null, null, DBHelper.CLIENTS.NAME);
            if (cursor.moveToFirst()) {
                events = getEvents(cursor);
            }
            cursor.close();
            return events;
        }

        public Event retrieve(int id) {
            SQLiteDatabase database = helper.getReadableDatabase();
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            queryBuilder.setTables(DBHelper.EVENTS.TABLE_NAME);
            queryBuilder.appendWhere(DBHelper.EVENTS.ID + " = " + id);
            Cursor cursor = queryBuilder.query(database, null, null, null, null, null, null);
            List<Event> arr = getEvents(cursor);
            cursor.close();
            return arr.size() > 0 ? arr.get(0) : null;
        }

        public List<Event> retrieve(long fromDate, long toDate) {
            SQLiteDatabase database = helper.getReadableDatabase();
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            queryBuilder.setTables(DBHelper.EVENTS.TABLE_NAME);
            queryBuilder.appendWhere(DBHelper.EVENTS.TM_START + ">= " + fromDate + " AND " + DBHelper.EVENTS.TM_START + "<= " + toDate);
            Cursor cursor = queryBuilder.query(database, null, null, null, null, null, null);
            List<Event> arr = getEvents(cursor);
            cursor.close();
            return arr;
        }

        private ArrayList<Event> getEvents(Cursor cursor) {
            ArrayList<Event> events = new ArrayList<>();
            if (cursor.moveToFirst()) {
                final int idColumn = cursor.getColumnIndex(DBHelper.EVENTS.ID);
                final int userIdColumn = cursor.getColumnIndex(DBHelper.EVENTS.USER_ID);
                final int clientIdColumn = cursor.getColumnIndex(DBHelper.EVENTS.CLIENT_ID);
                final int clientNameColumn = cursor.getColumnIndex(DBHelper.EVENTS.CLIENT_NAME);
                final int clientPhoneColumn = cursor.getColumnIndex(DBHelper.EVENTS.CLIENT_PHONE);
                final int tmStartEventColumn = cursor.getColumnIndex(DBHelper.EVENTS.TM_START);
                final int tmEndEventColumn = cursor.getColumnIndex(DBHelper.EVENTS.TM_END);
                final int tmCreateColumn = cursor.getColumnIndex(DBHelper.EVENTS.TM_CREATE);
                final int tmUpdateColumn = cursor.getColumnIndex(DBHelper.EVENTS.TM_UPDATE);
                final int memoColumn = cursor.getColumnIndex(DBHelper.EVENTS.MEMO);
                final int tblColumn = cursor.getColumnIndex(DBHelper.EVENTS.TBL);
                final int guestsColumn = cursor.getColumnIndex(DBHelper.EVENTS.GUESTS);
                final int guestsExtraColumn = cursor.getColumnIndex(DBHelper.EVENTS.GUESTS_EXTRA);
                final int typeColumn = cursor.getColumnIndex(DBHelper.EVENTS.TYPE);
                final int statusColumn = cursor.getColumnIndex(DBHelper.EVENTS.STATUS);
                final int colorColumn = cursor.getColumnIndex(DBHelper.EVENTS.COLOR);
                do {
                    Event e = new Event();
                    e.setId(cursor.getInt(idColumn));
                    Client client = new Client();
                    client.setId(cursor.getInt(clientIdColumn));
                    client.setName(cursor.getString(clientNameColumn));
                    client.setPhone(cursor.getString(clientPhoneColumn));
                    e.setClient(client);
                    e.setStartTime(cursor.getLong(tmStartEventColumn));
                    e.setEndTime(cursor.getLong(tmEndEventColumn));
                    e.setDateCreate(cursor.getLong(tmCreateColumn));
                    e.setDateUpdate(cursor.getLong(tmUpdateColumn));
                    e.setGuests(cursor.getInt(guestsColumn));
                    e.setGuestsExtra(cursor.getInt(guestsExtraColumn));
                    e.setTbl(cursor.getInt(tblColumn));
                    e.setType(cursor.getInt(typeColumn));
                    e.setMemo(cursor.getString(memoColumn));
                    e.setColor(cursor.getInt(colorColumn));
                    events.add(e);
                }
                while (cursor.moveToNext());
            }
            return events;
        }

        public Event create(Event e) {
            Client client = e.getClient();
            Event event = null;
            if (client != null) {
                ContentValues values = initContentValues(e, client);
                SQLiteDatabase database = helper.getWritableDatabase();
                database.insert(DBHelper.EVENTS.TABLE_NAME, null, values);
                event = e;
            }
            return event;
        }

        public Event update(Event e) {
            Client client = e.getClient();
            Event event = null;
            if (client != null) {
                ContentValues values = initContentValues(e, client);
                SQLiteDatabase database = helper.getWritableDatabase();
                if (database.update(DBHelper.EVENTS.TABLE_NAME, values,  DBHelper.EVENTS.ID + "=?", new String[]{"" + e.getId()}) > 0)
                    event = e;
            }
            return event;
        }

        private ContentValues initContentValues(Event e, Client client) {
            //TYPE, CLIENT_ID, CLIENT_NAME, CLIENT_PHONE, TM_START, TM_END,
            // TM_CREATE, TM_UPDATE, USER_ID, TBL, GUESTS, GUESTS_EXTRA, STATUS, MEMO,COLOR
            ContentValues values = new ContentValues();
            values.put(DBHelper.EVENTS.TYPE, e.getType());
            values.put(DBHelper.EVENTS.CLIENT_ID, client.getId());
            values.put(DBHelper.EVENTS.CLIENT_NAME, client.getName());
            values.put(DBHelper.EVENTS.CLIENT_PHONE, client.getPhone());
            values.put(DBHelper.EVENTS.TM_START, e.getStartTime());
            values.put(DBHelper.EVENTS.TM_END, e.getEndTime());

            values.put(DBHelper.EVENTS.TM_CREATE, e.getDateCreate());
            values.put(DBHelper.EVENTS.TM_UPDATE, e.getDateUpdate());
            values.put(DBHelper.EVENTS.USER_ID, 1);
            values.put(DBHelper.EVENTS.TBL, e.getTbl());
            values.put(DBHelper.EVENTS.GUESTS, e.getGuests());
            values.put(DBHelper.EVENTS.GUESTS_EXTRA, e.getGuestsExtra());
            values.put(DBHelper.EVENTS.STATUS, e.getStatus());
            values.put(DBHelper.EVENTS.MEMO, e.getMemo());
            values.put(DBHelper.EVENTS.COLOR, e.getColor());
            return values;
        }


    }

    public static class Clients {
        private int version;
        private DBHelper helper;

        public Clients(Context context) {
            helper = new DBHelper(context);
        }

        public Cursor getCursor() {
            SQLiteDatabase database = helper.getReadableDatabase();
            return database.query(DBHelper.CLIENTS.TABLE_NAME, null, null, null, null, null, null);
        }

        public List<Client> retrieve() {
            List<Client> clients = new ArrayList<>();
            SQLiteDatabase database = helper.getReadableDatabase();
            Cursor cursor = database.query(DBHelper.CLIENTS.TABLE_NAME, null, null, null, null, null, DBHelper.CLIENTS.NAME);
            if (cursor.moveToFirst()) {
                clients = getClients(cursor);
            }
            cursor.close();
            return clients;
        }

        private ArrayList<Client> getClients(Cursor cursor) {
            ArrayList<Client> clients = new ArrayList<>();
            final int idColumn = cursor.getColumnIndex(DBHelper.CLIENTS.ID);
            final int nameColumn = cursor.getColumnIndex(DBHelper.CLIENTS.NAME);
            final int phoneColumn = cursor.getColumnIndex(DBHelper.CLIENTS.PHONE);
            final int memoColumn = cursor.getColumnIndex(DBHelper.CLIENTS.MEMO);
            final int lastEventColumn = cursor.getColumnIndex(DBHelper.CLIENTS.LAST_EVENT);
            do {
                Client client = new Client();
                client.setId(cursor.getInt(idColumn));
                client.setName(cursor.getString(nameColumn));
                client.setPhone(cursor.getString(phoneColumn));
                client.setMemo(cursor.getString(memoColumn));
                client.setLastEvent(cursor.getLong(lastEventColumn));

                clients.add(client);
            }
            while (cursor.moveToNext());
            return clients;
        }

        public Client retrieveClient(int id) {
            Client item = null;
            SQLiteDatabase database = helper.getReadableDatabase();
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            queryBuilder.setTables(DBHelper.CLIENTS.TABLE_NAME);
            queryBuilder.appendWhere(DBHelper.CLIENTS.ID + " = " + id);
            Cursor cursor = queryBuilder.query(database, null, null, null, null, null, null);
            List<Client> arr = getClients(cursor);
            cursor.close();
            return arr.size() > 0 ? arr.get(0) : null;
        }

        public boolean replace(Client client) {
            SQLiteDatabase database = helper.getWritableDatabase();
            ContentValues values = new ContentValues();
            int id = client.getId();
            values.put(DBHelper.CLIENTS.ID, id);
            values.put(DBHelper.CLIENTS.NAME, client.getName());
            values.put(DBHelper.CLIENTS.PHONE, client.getPhone());
            values.put(DBHelper.CLIENTS.MEMO, client.getMemo());
            values.put(DBHelper.CLIENTS.LAST_EVENT, client.getLastEvent());

            return database.replaceOrThrow(DBHelper.CLIENTS.TABLE_NAME, null, values) > 0;
        }

        public boolean delete(Client client) {
            int id = client.getId();
            SQLiteDatabase database = helper.getWritableDatabase();
            database.delete(DBHelper.CLIENTS.TABLE_NAME, DBHelper.CLIENTS.ID + " = ?", new String[]{"" + id});
            return true;
        }


        public Client create(Client client) {
            SQLiteDatabase database = helper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DBHelper.CLIENTS.ID, client.getId());
            values.put(DBHelper.CLIENTS.NAME, client.getName());
            values.put(DBHelper.CLIENTS.PHONE, client.getPhone());
            values.put(DBHelper.CLIENTS.MEMO, client.getMemo());
            values.put(DBHelper.CLIENTS.LAST_EVENT, client.getLastEvent());
            database.insert(DBHelper.CLIENTS.TABLE_NAME, null, values);
            return client;
        }
    }
}
