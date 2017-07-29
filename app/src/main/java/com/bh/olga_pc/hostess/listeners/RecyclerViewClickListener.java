package com.bh.olga_pc.hostess.listeners;

import android.view.View;

/**
 * Created by Olga-PC on 7/29/2017.
 */

public interface RecyclerViewClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
