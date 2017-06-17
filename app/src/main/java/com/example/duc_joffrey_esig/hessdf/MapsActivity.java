package com.example.duc_joffrey_esig.hessdf;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker currentMarker;
    LatLng markerPosition;
    private Location location;
    private String[] ETRES = {"SDF homme", "SDF femme", "Chat errant", "Chien errant"};
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setTitle("Map");

        // Création de l'instance d'une RequestQueue
        queue = VolleySingleton.getInstance(this).getRequestQueue();

        // Ajout du bouton de retour dans l'ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        final SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Ajout du bouton FAB pour ajouter un marqueur dans la base de données
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_map_ajouter);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                // Lorsque l'on clique sur le bouton FAB

                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setTitle("Qui est-ce?");
                builder.setItems(ETRES, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogue, final int i) {

                        markerPosition = new LatLng(location.getLatitude(), location.getLongitude());
                        if (currentMarker != null)
                            currentMarker.remove();

                        currentMarker = mMap.addMarker(new MarkerOptions().position(markerPosition).draggable(true).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)).title(ETRES[i]));

                        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                            @Override
                            public void onMarkerDragStart(Marker marker) {
                            }

                            @Override
                            public void onMarkerDrag(Marker marker) {
                            }

                            @Override
                            public void onMarkerDragEnd(Marker marker) {
                                markerPosition = currentMarker.getPosition();
                            }
                        });

                        Snackbar snackbar = Snackbar.make(view, "Restez appuyer et déplacez le marqueur où se trouve le " + ETRES[i], Snackbar.LENGTH_INDEFINITE)
                                .setAction("AJOUTER", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String title = "Un";
                                        if (ETRES[i].equals("SDF femme"))
                                            title += "e";
                                        title += " " + ETRES[i] + " a été vu il y a moins d'une heure";

                                        mMap.addMarker(new MarkerOptions().title(title).position(markerPosition).draggable(false).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                        currentMarker.remove();

                                        insertMarqueur(markerPosition.latitude, markerPosition.longitude, ETRES[i]);

                                        Toast.makeText(MapsActivity.this, ETRES[i] + " ajouté. Merci !", Toast.LENGTH_LONG).show();
                                    }
                                });

                        snackbar.setActionTextColor(Color.GREEN);

                        // Le texte est trop grand pour le Snackbar, donc on augmente le nombre maximum de lignes à 3.
                        View sbView = snackbar.getView();
                        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                        textView.setMaxLines(3);

                        snackbar.show();
                    }
                });// Fin du builder alert dialogue

                AlertDialog alertDialogue = builder.create();
                alertDialogue.show();
            }
        });// Fin du fab

        // Ajout de tous les marqueurs qu'il y a dans la base de données sur la map
        recupererTousLesMarqueurs();

    }

    // Méthode qui est appelée une fois que la map est prête à être utilisée
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Si l'utilisateur a donné la permission à l'application d'avoir accès à sa position, alors on affiche sa position sur la map.
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            LocationManager mng = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            location = mng.getLastKnownLocation(mng.getBestProvider(new Criteria(), false));

            // On bouge la caméra à la position actuelle, et on zoom à 14 pour avoir une vue d'ensemble de Genève (compris entre 2 et 21, respectivement du plus loin au plus près)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 12));
        }

        mMap.setPadding(0, 0, 0, 250);

    }

    // Création du menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_map, menu);
        return true;
    }

    // Actions à faire lorqsue l'on clique sur un item du menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) { // On ferme l'activité lorsque l'on clique sur le bouton retour
            finish();
        } else if (id == R.id.menu_map_item_aide) { // On ouvre l'activité principale avec le fragment "aide" chargé
            FragmentEnCours.setFragmentEnCours(new AideFragment());
            startActivity(new Intent(this, MainActivity.class));
            finish();

        }

        return super.onOptionsItemSelected(item);
    }

    // Méthode qui ajoute tous les marqueurs sur la map
    private void recupererTousLesMarqueurs(){

        String url = "http://joffrey-duc.hopto.org/hessdf/get_marqueur.php";

        queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray marqueurs = null;
                try {
                    marqueurs = response.getJSONArray("marqueur");
                    for (int i = 0; i < marqueurs.length(); i++) {
                        JSONObject marqueur = marqueurs.getJSONObject(i);

                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-M-d H:m:s");

                        Date dateCreation = formatter.parse(marqueur.getString("date_creation"));
                        double longitude = marqueur.getDouble("longitude");
                        double latitude = marqueur.getDouble("latitude");
                        String etre = marqueur.getString("etre");

                        Date dateNow = new Date();
                        long diffHeures = (dateNow.getTime()-dateCreation.getTime())/(60*60*1000);

                        String nomMarker = "Un";
                        if(etre.equals("SDF femme"))
                            nomMarker += "e";
                        nomMarker += " " + etre + " a été vu il y a ";
                        if(diffHeures < 1)
                            nomMarker += "moins d'une heure";
                        else{
                            nomMarker += diffHeures + " heure";
                            if(diffHeures >= 2)
                                nomMarker += "s";
                        }

                        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title(nomMarker).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    }
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.append(error.getMessage());
            }
        });

        queue.add(jsonObjectRequest);

    }

    // Méthode qui insére un marqueur dans la base de données distante
    public void insertMarqueur(final double latitude, final double longitude, final String etre) {
        String url = "http://joffrey-duc.hopto.org/hessdf/insert_marqueur.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("APP", response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("APP", "ERROR = " + error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<>();
                map.put("latitude", String.valueOf(latitude));
                map.put("longitude", String.valueOf(longitude));
                map.put("etre", etre);

                return map;
            }
        };

        queue.add(request);
    }

}
