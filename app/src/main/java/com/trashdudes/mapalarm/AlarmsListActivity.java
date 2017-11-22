package com.trashdudes.mapalarm;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.List;

public class AlarmsListActivity extends AppCompatActivity implements SelectAlarmCallback {

    private ListView alarmsListView;
    private ProgressBar progressBar;
    private FloatingActionButton floatingActionButton;
    private AlarmManager alarmManager;
    private Cursor selectCursor;
    private List<AlarmModel> alarms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarms_list);

        setTitle("Lista de Alarmes");

        this.alarmManager = new AlarmManager(this);
        this.selectCursor = this.alarmManager.getSelectCursor();

        this.loadUI();
        this.loadActions();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //        this.getLocalAlarms();
        this.getServerAlarms();
    }

    private void loadUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.progressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.floatingActionButton = (FloatingActionButton) findViewById(R.id.floatActionButtonAdd);
        this.alarmsListView = (ListView) findViewById(R.id.alarmsListView);
        this.loadListViewProperties();
    }

    private void loadActions() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AlarmsListActivity.this, InsertAlarmActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loadListViewProperties(){
        this.alarmsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                AlarmModel selectedAlarm = (AlarmModel) AlarmsListActivity.this.alarms.get(i);

//                selectCursor.moveToPosition(i);
//                Integer selectedCursorId = selectCursor.getInt(selectCursor.getColumnIndex("_id"));
//
                Intent intent = new Intent(AlarmsListActivity.this, AlarmDetailActivity.class);
                intent.putExtra("alarm", selectedAlarm);
                AlarmsListActivity.this.startActivity(intent);
            }
        });
    }

    private void getServerAlarms() {
        this.progressBar.setVisibility(View.VISIBLE);
        SelectAlarmsAsyncTask selectAlarmsAsyncTask = new SelectAlarmsAsyncTask(this);
        selectAlarmsAsyncTask.execute();
    }

    private void getLocalAlarms() {
        Cursor newCursor = this.alarmManager.getSelectCursor();
        SimpleCursorAdapter cursorAdapter = (SimpleCursorAdapter) this.alarmsListView.getAdapter();
        cursorAdapter.changeCursor(newCursor);
        cursorAdapter.setViewBinder(new CustomViewBinder());
        ((SimpleCursorAdapter) this.alarmsListView.getAdapter()).notifyDataSetChanged();
        this.selectCursor.close();
        this.selectCursor = newCursor;
    }

    private class CustomViewBinder implements SimpleCursorAdapter.ViewBinder {

        @Override
        public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

            if (columnIndex == cursor.getColumnIndex("latitude")) {
                Double latitude = cursor.getDouble(columnIndex);
                TextView textView = (TextView)view;
                textView.setText("" + latitude);
                return true;
            }
            else if (columnIndex == cursor.getColumnIndex("longitude")) {
                Double longitude = cursor.getDouble(columnIndex);
                TextView textView = (TextView)view;
                textView.setText("" + longitude);
                return true;
            }
            else if (columnIndex == cursor.getColumnIndex("radius")) {
                Double radius= cursor.getDouble(columnIndex);
                TextView textView = (TextView)view;
                textView.setText("" + radius);
                return true;
            }
            else if (columnIndex == cursor.getColumnIndex("notes")) {
                String notes = cursor.getString(columnIndex);
                TextView textView = (TextView)view;
                textView.setText("" + notes);
                return true;
            }

            return false;
        }
    }

    @Override
    public void didGetItens(List<AlarmModel> alarmModels) {
        this.progressBar.setVisibility(View.GONE);
        this.alarms = alarmModels;
        AlarmsAdapter adapter = new AlarmsAdapter(this, alarmModels);
        this.alarmsListView.setAdapter(adapter);
    }
}
