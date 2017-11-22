package com.trashdudes.mapalarm;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by guilhermen on 11/1/17.
 */

public class InsertAlermsAsyncTask  extends AsyncTask<Void, Void, String> {

    private static final String HOST = "http://es.ft.unicamp.br/ulisses/si700/insert_data.php";
    private Context context;
    private AlarmModel alarm;

    public InsertAlermsAsyncTask(Context context, AlarmModel alarm){
        this.context = context;
        this.alarm = alarm;
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

            data +=  "&" +  URLEncoder.encode("latitude" , "UTF-8") + "=" +
                    URLEncoder.encode(this.alarm.getLatitude().toString(), "UTF-8");
            data +=  "&" +  URLEncoder.encode("longitude", "UTF-8") + "=" +
                    URLEncoder.encode(this.alarm.getLongitude().toString(), "UTF-8");
            data +=  "&" +  URLEncoder.encode("radius", "UTF-8") + "=" +
                    URLEncoder.encode(this.alarm.getRadius().toString(), "UTF-8");
            data +=  "&" +  URLEncoder.encode("notes" , "UTF-8") + "=" +
                    URLEncoder.encode(this.alarm.getNotes(), "UTF-8");

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
            OutputStreamWriter wr = new OutputStreamWriter(
                    httpURLConnection.getOutputStream());
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

        catch (IOException exception){
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
        Toast.makeText(this.context, result, Toast.LENGTH_LONG).show();
        InsertAlarmActivity insertAlarmActivity = (InsertAlarmActivity)this.context;
        insertAlarmActivity.requestFinished();
    }
}
