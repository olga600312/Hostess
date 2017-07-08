package com.bh.olga_pc.hostess.tbls;

/**
 * Created by Olga-PC on 7/8/2017.
 */


import java.util.Calendar;

/**
 * Created by Raquib on 1/6/2015.
 */
public interface DateTimeInterpreter {
    String interpretDate(Calendar date);
    String interpretTime(int hour);
}
