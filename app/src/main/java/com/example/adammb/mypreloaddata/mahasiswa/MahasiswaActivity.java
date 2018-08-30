package com.example.adammb.mypreloaddata.mahasiswa;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.adammb.mypreloaddata.R;
import com.example.adammb.mypreloaddata.db.MahasiswaHelper;

import java.util.ArrayList;

public class MahasiswaActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    MahasiswaAdapter mahasiswaAdapter;
    MahasiswaHelper mahasiswaHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mahasiswa);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        mahasiswaHelper = new MahasiswaHelper(this);
        mahasiswaAdapter = new MahasiswaAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(mahasiswaAdapter);

        mahasiswaHelper.open();

        // Ambil semua data mahasiswa di database
        ArrayList<MahasiswaModel> mahasiswaModels = mahasiswaHelper.getAllData();

        mahasiswaHelper.close();
        Log.e("Cek Data",mahasiswaModels.get(0).getName());

        mahasiswaAdapter.addItem(mahasiswaModels);
    }
}
