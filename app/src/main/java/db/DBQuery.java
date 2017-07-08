package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import beans.Client;
import beans.Event;

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


    public static class Events {
        private DBHelper helper;

        public Events(Context context) {
            helper = new DBHelper(context);
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
            queryBuilder.appendWhere(DBHelper.EVENTS.TM_EVENT + ">= " + fromDate+" AND "+DBHelper.EVENTS.TM_EVENT + "<= "+toDate);
            Cursor cursor = queryBuilder.query(database, null, null, null, null, null, null);
            List<Event> arr = getEvents(cursor);
            cursor.close();
            return arr;
        }

        private ArrayList<Event> getEvents(Cursor cursor) {
            ArrayList<Event> events = new ArrayList<>();
            final int idColumn = cursor.getColumnIndex(DBHelper.EVENTS.ID);
            final int userIdColumn = cursor.getColumnIndex(DBHelper.EVENTS.USER_ID);
            final int clientIdColumn = cursor.getColumnIndex(DBHelper.EVENTS.CLIENT_ID);
            final int clientNameColumn = cursor.getColumnIndex(DBHelper.EVENTS.CLIENT_NAME);
            final int clientPhoneColumn = cursor.getColumnIndex(DBHelper.EVENTS.CLIENT_PHONE);
            final int tmEventColumn = cursor.getColumnIndex(DBHelper.EVENTS.TM_EVENT);
            final int tmCreateColumn = cursor.getColumnIndex(DBHelper.EVENTS.TM_CREATE);
            final int tmUpdateColumn = cursor.getColumnIndex(DBHelper.EVENTS.TM_UPDATE);
            final int memoColumn = cursor.getColumnIndex(DBHelper.EVENTS.MEMO);
            final int tblColumn = cursor.getColumnIndex(DBHelper.EVENTS.TBL);
            final int guestsColumn = cursor.getColumnIndex(DBHelper.EVENTS.GUESTS);
            final int guestsExtraColumn = cursor.getColumnIndex(DBHelper.EVENTS.GUESTS_EXTRA);
            final int typeColumn = cursor.getColumnIndex(DBHelper.EVENTS.TYPE);
            final int statusColumn = cursor.getColumnIndex(DBHelper.EVENTS.STATUS);
            do {
                Event e = new Event();
                e.setId(cursor.getInt(idColumn));
                Client client = new Client();
                client.setId(cursor.getInt(clientIdColumn));
                client.setName(cursor.getString(clientNameColumn));
                client.setPhone(cursor.getString(clientPhoneColumn));
                e.setClient(client);
                e.setDate(cursor.getLong(tmEventColumn));
                e.setDateCreate(cursor.getLong(tmCreateColumn));
                e.setDateUpdate(cursor.getLong(tmUpdateColumn));
                e.setGuests(cursor.getInt(guestsColumn));
                e.setGuestsExtra(cursor.getInt(guestsExtraColumn));
                e.setTbl(cursor.getInt(tblColumn));
                e.setType(cursor.getInt(typeColumn));
                e.setMemo(cursor.getString(memoColumn));
                events.add(e);
            }
            while (cursor.moveToNext());
            return events;
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
