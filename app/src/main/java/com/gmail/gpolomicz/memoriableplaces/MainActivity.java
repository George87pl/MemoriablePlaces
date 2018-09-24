package com.gmail.gpolomicz.memoriableplaces;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    static ArrayList<String> placesArray = new ArrayList<>();
    static ArrayList<LatLng> locationsArray = new ArrayList<>();
    static ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView placesListView = findViewById(R.id.placesListView);
        placesArray.add("Add a new place...");
        locationsArray.add(new LatLng(0,0));

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, placesArray);
        placesListView.setAdapter(adapter);
        placesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                    intent.putExtra("position", position);
                    startActivity(intent);

            }
        });
    }








}
