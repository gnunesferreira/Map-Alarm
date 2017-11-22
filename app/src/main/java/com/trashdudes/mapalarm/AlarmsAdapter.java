package com.trashdudes.mapalarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by guilhermen on 11/1/17.
 */

public class AlarmsAdapter extends BaseAdapter {

    private Context context;
    private List<AlarmModel> alarms;


    public AlarmsAdapter(Context context, List<AlarmModel> players) {
        this.context = context;
        this.alarms = players;
    }

    @Override
    public int getCount() {
        return alarms.size();
    }

    @Override
    public Object getItem(int position) {
        return alarms.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Passo 1
        AlarmModel alarm = alarms.get(position);

        // Passo 2
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_list_alarm, null);

            holder = new ViewHolder();

            holder.notes   = (TextView)convertView.findViewById(R.id.itemListAlarmNoteTextView);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        // Passo 3
        TextView notesTextView = holder.notes;

        notesTextView.setText(alarm.getNotes().toString());

        // Passo 4
        return convertView;
    }

    static class ViewHolder{
        TextView notes;
    }
}
