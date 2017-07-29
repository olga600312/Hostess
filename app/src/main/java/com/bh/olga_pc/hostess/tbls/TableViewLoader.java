package com.bh.olga_pc.hostess.tbls;

/**
 * Created by Olga-PC on 7/8/2017.
 */


import java.util.Calendar;
        import java.util.List;

import com.bh.olga_pc.hostess.beans.Event;

public interface TableViewLoader {
    /**
     * Convert a date into a double that will be used to reference when you're loading data.
     *
     * All periods that have the same integer part, define one period. Dates that are later in time
     * should have a greater return value.
     *
     * @param instance the date
     * @return The period index in which the date falls (floating point number).
     */
    double toWeekViewPeriodIndex(Calendar instance);

    /**
     * Load the events within the period
     * @param periodIndex the period to load
     * @return A list with the events of this period
     */
    List<? extends Event> onLoad(int periodIndex);
}
