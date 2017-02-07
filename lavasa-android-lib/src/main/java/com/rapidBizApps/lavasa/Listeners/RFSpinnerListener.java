package com.rapidBizApps.lavasa.Listeners;

import java.util.List;

/**
 * Created by kkalluri on 2/3/2016.
 */

public interface RFSpinnerListener {

    public void onDoneClick(List<String> list);

    public void onDoneClick(int selectedPosition);
}
