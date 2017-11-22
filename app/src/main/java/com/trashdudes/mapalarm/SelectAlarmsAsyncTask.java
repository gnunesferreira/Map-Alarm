package com.trashdudes.mapalarm;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by guilhermen on 11/1/17.
 */

public class SelectAlarmsAsyncTask extends AsyncTask<Void, Void, String> {

    private static final String HOST = "http://es.ft.unicamp.br/ulisses/si700/select_data.php";
    private Context context;

    public SelectAlarmsAsyncTask(Context context){
        this.context  = context;
    }

    @Override
    protected String doInBackground(Void... objects) {
        HttpURLConnection httpURLConnection = null;
        try {
            /*
               Preparando os dados para envio via post
             */
            String data =
                    URLEncoder.encode("database","UTF-8")+"="+
                            URLEncoder.encode("ra169097","UTF-8")+"&"+
                            URLEncoder.encode("table","UTF-8")+"="+
                            URLEncoder.encode("Pontos","UTF-8");

            /*
               Abrindo uma conexão com o servidor
             */
            URL url = new URL(HOST);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);

            /*
               Enviando os dados via post
             */
            OutputStreamWriter wr = new OutputStreamWriter(httpURLConnection.getOutputStream());
            wr.write( data );
            wr.flush();

            /*
                Lendo a resposta do servidor
             */
            BufferedReader reader = new BufferedReader(new
                    InputStreamReader(httpURLConnection.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null) {
                sb.append(line);
                break;
            }
            return sb.toString();

        }
        catch (Exception exception){
            exception.printStackTrace();
            return "Exception: " + exception.getMessage();
        }

        finally {
            if (httpURLConnection != null){
                httpURLConnection.disconnect();
            }
        }
    }


    @Override
    protected  void onPostExecute(String result){
        /*
           Convertendo JSONArray para ArrayList
         */

        ArrayList<AlarmModel> alarms = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {

                AlarmModel alarmModel = new AlarmModel();

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Double latitude = jsonObject.getDouble("latitude");
                Double longitude = jsonObject.getDouble("longitude");
                Double radius = jsonObject.getDouble("radius");
                String notes = jsonObject.getString("notes");

                alarmModel.setLatitude(latitude);
                alarmModel.setLongitude(longitude);
                alarmModel.setRadius(radius);
                alarmModel.setNotes(notes);

                alarms.add(alarmModel);
            }
        } catch (JSONException exception){
            exception.printStackTrace();
        }

        SelectAlarmCallback alarmsListActivity = (SelectAlarmCallback)context;
        alarmsListActivity.didGetItens(alarms);
    }
}
