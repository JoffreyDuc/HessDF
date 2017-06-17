package com.example.duc_joffrey_esig.hessdf;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.android.volley.RequestQueue;

import java.util.ArrayList;


public class AssociationsActivity extends AppCompatActivity {

    GridView gridView;
    ArrayList<Association> list;
    AssociationsListAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_associations);
        setTitle("Associations");

        // Ajout du bouton de retour dans l'ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Ajout du GridView et de son adapter
        gridView = (GridView) findViewById(R.id.gridViewAssociations);
        list = new ArrayList<>();
        adapter = new AssociationsListAdapter(this, R.layout.items_associations, list);
        gridView.setAdapter(adapter);

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

            list.add(new Association(id, name, image, site, latitude, longitude, 0));
        }

        // Et on met à jour l'adapter (qui contient la liste)
        adapter.notifyDataSetChanged();

        // Ajout du listener sur le gridView pour ouvrir le site Web de l'association sur laquelle l'utilisateur clique
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri uriUrl = Uri.parse(list.get(position).getSite());
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                startActivity(launchBrowser);
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

}