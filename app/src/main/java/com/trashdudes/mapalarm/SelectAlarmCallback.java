package com.trashdudes.mapalarm;

import java.util.List;

/**
 * Created by guilhermen on 11/1/17.
 */

public interface SelectAlarmCallback {
    public void didGetItens(List<AlarmModel> alarmModels);
}
