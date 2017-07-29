package com.bh.olga_pc.hostess;

import com.bh.olga_pc.hostess.customViews.DateRestView;

import java.util.Calendar;
import java.util.List;

import com.bh.olga_pc.hostess.beans.Event;

/**
 * Created by Olga-PC on 7/28/2017.
 */

public  class RegionLoader implements DateRestView.TableViewLoader {

    private RegionChangeListener regionChangeListener;

    public RegionLoader(RegionChangeListener listener){
        this.regionChangeListener = listener;
    }

    @Override
    public int toRegionViewPeriodIndex(int tbl){
        return 1;
    }

    @Override
    public List<? extends Event> onLoad(int tbl, Calendar date){
        return regionChangeListener.onRegionChange(tbl,date);
    }

    public RegionChangeListener getOnRegionChangeListener() {
        return regionChangeListener;
    }

    public void setOnMonthChangeListener(RegionChangeListener regionChangeListener) {
        this.regionChangeListener = regionChangeListener;
    }

    public interface RegionChangeListener {
        /**
         * Very important interface, it's the base to load events in the calendar.
         * This method is called three times: once to load the previous month, once to load the next month and once to load the current month.<br/>
         * <strong>That's why you can have three times the same event at the same place if you mess up with the configuration</strong>
         * @param tbl : tbl on the events required by the view.
         * @param date : date of the events required by the view
         * @return a list of the events happening <strong>during the specified date on the specified table</strong>.
         */
        List<? extends Event> onRegionChange(int tbl,Calendar date);
    }
}
