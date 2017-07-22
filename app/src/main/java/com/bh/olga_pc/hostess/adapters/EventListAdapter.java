package com.bh.olga_pc.hostess.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bh.olga_pc.hostess.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import beans.Client;
import beans.Event;


/**
 * Created by Olga-PC on 7/22/2017.
 */

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.ItemViewHolder> {
    private List<Event> data = new ArrayList<>();

    public void updateData(List<Event> data) {
        this.data = data;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View vDefault = inflater.inflate(R.layout.event_list_item, parent, false);
        return new ItemViewHolder(vDefault);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Event event = getValueAt(position);
        Client client = event.getClient();
        holder.mBoundString = event.getId();
        holder.tvClient.setText(client.getName());
        holder.tvPhone.setText(client.getPhone());
        holder.tvStartTime.setText(new SimpleDateFormat("HH:mm").format(event.getStartTime()));
        holder.tvGuest.setText(""+event.getGuests());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // onItemPressed(holder.mBoundString);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public Event getValueAt(int position) {
        return data.get(position);
    }

    public void clear() {
        data.clear();
    }

    public void addAll(List<Event> currentDayEvents) {
        data.addAll(currentDayEvents);
    }

    public void add(Event e) {
        data.add(e);
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        private int mBoundString;

        private final View mView;
        //  public final ImageView mImageView;
        private final TextView tvClient;
        private final TextView tvPhone;
        private final TextView tvStartTime;
        private final TextView tvGuest;

        private ItemViewHolder(View view) {
            super(view);
            mView = view;
            // mImageView = (ImageView) view.findViewById(R.id.avatar);
            tvClient = (TextView) view.findViewById(R.id.tvClient);
            tvPhone = (TextView) view.findViewById(R.id.tvPhone);
            tvStartTime = (TextView) view.findViewById(R.id.tvStartTime);
            tvGuest=(TextView)view.findViewById(R.id.tvGst);
        }


    }
}
