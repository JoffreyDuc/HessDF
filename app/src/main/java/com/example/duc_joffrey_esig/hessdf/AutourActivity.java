package com.example.duc_joffrey_esig.hessdf;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class AutourActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Association> list;
    private AutourListAdapter adapter = null;
    private Location location;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autour);
        setTitle("Autour de moi");

        // Ajout du bouton de retour dans l'ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Remplissage de la ListView
        refresh();

        // Ajout du listener sur la ListView pour ouvrir le site Web de l'association sur laquelle l'utilisateur clique
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri uriMap = Uri.parse("geo:0,0?q=\""+list.get(position).getLatitude()+","+list.get(position).getLongitude()+"\"");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, uriMap);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                        refresh();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                },1500);
            }
        });

    }

    // Action à faire lorqsue l'on clique sur le bouton retour
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // On ferme l'activité lorsque l'on clique sur le bouton retour
        if (id == android.R.id.home) finish();

        return super.onOptionsItemSelected(item);
    }

    private void refresh(){
        // Ajout du ListView et de son adapter
        listView = (ListView) findViewById(R.id.listViewAutour);
        list = new ArrayList<>();
        adapter = new AutourListAdapter(this, R.layout.items_autour, list);
        listView.setAdapter(adapter);

        // On insère les données de sqlite dans une liste
        Cursor cursor = MainActivity.sqLiteHelper.getData("SELECT * FROM ASSOCIATIONS");
        list.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            byte[] image = cursor.getBlob(2);
            String site = cursor.getString(3);
            double latitude = cursor.getDouble(4);
            double longitude = cursor.getDouble(5);
            double distance = 0;

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                LocationManager mng = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
                location = mng.getLastKnownLocation(mng.getBestProvider(new Criteria(), false));

                Location locationAssociation = new Location(name);
                locationAssociation.setLatitude(latitude);
                locationAssociation.setLongitude(longitude);

                distance = location.distanceTo(locationAssociation);
            }

            list.add(new Association(id, name, image, site, latitude, longitude, distance));
        }

        // On crée un Comparator pour trier l'ArrayList par distance
        Comparator<Association> comparatorDistance = new Comparator<Association>() {
            @Override
            public int compare(Association a1, Association a2) {
                double d1 = a1.getDistance();
                double d2 = a2.getDistance();
                return Double.compare(d1, d2);
            }
        };
        Collections.sort(list, comparatorDistance);

        // Et on met à jour l'adapter (qui contient la liste)
        adapter.notifyDataSetChanged();
    }

}
