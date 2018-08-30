package com.example.adammb.mypreloaddata;

import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import com.example.adammb.mypreloaddata.db.MahasiswaHelper;
import com.example.adammb.mypreloaddata.mahasiswa.MahasiswaActivity;
import com.example.adammb.mypreloaddata.mahasiswa.MahasiswaModel;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        new LoadData().execute();
    }

    private class LoadData extends AsyncTask<Void, Integer, Void> {
        final String TAG = LoadData.class.getSimpleName();
        MahasiswaHelper mahasiswaHelper;
        AppPreference appPreference;
        double progress;
        double maxprogress = 100;


        @Override
        protected void onPreExecute() {
            mahasiswaHelper = new MahasiswaHelper(MainActivity.this);
            appPreference = new AppPreference(MainActivity.this);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Boolean firstRun = appPreference.getFirstRun();

            if (firstRun) {
                ArrayList<MahasiswaModel> mahasiswaModels = preLoadRaw();

                mahasiswaHelper.open();
                progress = 30;
                publishProgress((int) progress);
                Double progressMaxInsert = 80.0;
                Double progressDiff = (progressMaxInsert - progress) / mahasiswaModels.size();

//                for (MahasiswaModel model : mahasiswaModels) {
//                    mahasiswaHelper.insert(model);
//                    progress += progressDiff;
//                    publishProgress((int) progress);
//                }

                mahasiswaHelper.beginTransaction();

                try{
                    for(MahasiswaModel model:mahasiswaModels){
                        mahasiswaHelper.insertTransaction(model);
                        progress+=progressDiff;
                        publishProgress((int)progress);
                    }

                    //jika semua proses telah diset success maka akan di commit ke database
                    mahasiswaHelper.setTransactionSuccess();
                }catch(Exception e){
                    //Jika gagal maka do nothing
                    Log.e(TAG, "doInBackground: Exception");
                }

                mahasiswaHelper.endTransaction();

                mahasiswaHelper.close();

                appPreference.setFirstRun(false);

                publishProgress((int) maxprogress);
            } else {
                try {
                    synchronized (this) {
                        this.wait(2000);

                        publishProgress(50);

                        this.wait(2000);
                        publishProgress((int) maxprogress);
                    }
                } catch (Exception e) {

                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Intent i=new Intent(MainActivity.this, MahasiswaActivity.class);
            
            startActivity(i);
            finish();
        }

        private ArrayList<MahasiswaModel> preLoadRaw() {
            ArrayList<MahasiswaModel> mahasiswaModels = new ArrayList<>();
            String line = null;
            BufferedReader reader;

            try {
                Resources res = getResources();
                InputStream raw_dict = res.openRawResource(R.raw.data_mahasiswa);

                reader = new BufferedReader(new InputStreamReader(raw_dict));
                int count = 0;
                do {
                    line = reader.readLine();
                    String[] splitstr = line.split("\t");

                    MahasiswaModel mahasiswaModel;

                    mahasiswaModel = new MahasiswaModel(splitstr[0], splitstr[1]);
                    mahasiswaModels.add(mahasiswaModel);
                    count++;
                } while (line != null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mahasiswaModels;
        }
    }
}
