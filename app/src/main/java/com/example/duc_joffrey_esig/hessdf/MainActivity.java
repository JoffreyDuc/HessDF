package com.example.duc_joffrey_esig.hessdf;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    NavigationView navigationView = null;
    Toolbar toolbar = null;
    private static final int PERMISSIONS_MULTIPLE_REQUEST = 1;
    private static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 2;
    private static final int MY_PERMISSION_REQUEST_CALL_PHONE = 3;
    private boolean localisationPermissionIsGranted;
    private boolean callPhonePermissionIsGranted;
    public static SQLiteHelper sqLiteHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Charger le fragment initial
        Fragment fragment = FragmentEnCours.getFragmentEnCours();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();

        //Ajout de la Toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Ajout du DrawerLayout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Ajout du NavigationView (qui est à l'intérieur du DrawerLayout)
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Ajout du titre en fonction du fragment en cours
        actualiserLeTitreEtFragmentEnCours(fragment);

        // Pour demander l'autorisation si jamais téléphoner et/ou la localisation est désactivée
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) + ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Si la version de l'application est supérieure ou égale à Marshmallow (23) alors on demande l'autorisation si elle n'est pas autorisée
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_MULTIPLE_REQUEST);
                } else {
                    // Sinon cela veut dire que l'utilisateur a déjà accepté la permission lors de l'installation, car les versions antérieures à Marshmallow demandent les autorisations lors de l'installation.
                    // Et si on refuse, l'application ne s'installe même pas. Donc comme elle est installée, ça veut dire que l'utilisateur a déjà accepté.
                    localisationPermissionIsGranted = true;
                    callPhonePermissionIsGranted = true;
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_MULTIPLE_REQUEST);
                } else {
                    localisationPermissionIsGranted = true;
                    callPhonePermissionIsGranted = true;
                }
            }
        } else {
            // Si c'est déjà accepté alors on met les variables booléennes à true.
            localisationPermissionIsGranted = true;
            callPhonePermissionIsGranted = true;
        }

        // Création de la base de données pour les associations
        sqLiteHelper = new SQLiteHelper(this, "AssociationsDB.sqlite",null,1);
        sqLiteHelper.queryData("CREATE TABLE IF NOT EXISTS ASSOCIATIONS(id INTEGER PRIMARY KEY AUTOINCREMENT, name VARCHAR, image BLOB, site VARCHAR, latitude DOUBLE, longitude DOUBLE)"); /*, latitude REAL, longitude REAL*/
        Cursor c = sqLiteHelper.getData("SELECT * FROM ASSOCIATIONS");
        // S'il n'a a aucune donnée dans la table, alors on les insère
        if (c.getCount() == 0){
            try{
                sqLiteHelper.insertData("SPA Genève", imageFromDrawableToByte(R.drawable.img_sgpa), "http://www.sgpa.ch/", 46.171698, 6.052155);
                sqLiteHelper.insertData("Emmaüs Genève", imageFromDrawableToByte(R.drawable.img_logo_emmaus), "http://emmaus-ge.ch/", 46.179512, 6.139907);
                sqLiteHelper.insertData("Croix-Rouge genevoise", imageFromDrawableToByte(R.drawable.img_logo_croix_rouge), "http://www.croix-rouge-ge.ch/",46.192566, 6.137606);
                sqLiteHelper.insertData("Centre Genevois du Volontariat", imageFromDrawableToByte(R.drawable.img_cgv), "http://www.volontariat-ge.org/index.ags", 46.202413, 6.157775);
                sqLiteHelper.insertData("Hospice général", imageFromDrawableToByte(R.drawable.img_hospice_general), "http://www.hospicegeneral.ch/", 46.201227, 6.153732);
                sqLiteHelper.insertData("Association le Trialogue", imageFromDrawableToByte(R.drawable.img_logo_trialogue), "http://www.letrialogue.com/", 46.215796, 6.126676);
                sqLiteHelper.insertData("PEA - Pour l'égalité animale", imageFromDrawableToByte(R.drawable.img_logo_pea), "https://www.asso-pea.ch/fr/", 46.195057, 6.191645);
                sqLiteHelper.insertData("Centre Social Protestant", imageFromDrawableToByte(R.drawable.img_logo_csp), "https://csp.ch/geneve/", 46.200503, 6.135426);

                Toast.makeText(getApplicationContext(), "Added successfully!", Toast.LENGTH_SHORT).show();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    // Méthode qui gère la réponse de l'utilisateur après la demande de permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Aide de http://stackoverflow.com/questions/34040355/how-to-check-the-multiple-permission-at-single-request-in-android-m
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CALL_PHONE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                    callPhonePermissionIsGranted = true;
                } else {
                    //permission denied
                    callPhonePermissionIsGranted = false;
                    Toast.makeText(getApplicationContext(), R.string.appPhoneNotGranted, Toast.LENGTH_LONG).show();
                }
                break;
            case MY_PERMISSION_REQUEST_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted
                    localisationPermissionIsGranted = true;
                } else {
                    //permission denied
                    localisationPermissionIsGranted = false;
                    Toast.makeText(getApplicationContext(), R.string.localisationNotGranted, Toast.LENGTH_LONG).show();
                }
                break;
            case PERMISSIONS_MULTIPLE_REQUEST:
                if (grantResults.length > 0) {

                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        // write your logic here
                        localisationPermissionIsGranted = true;
                        callPhonePermissionIsGranted = true;
                    }
                } else {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_MULTIPLE_REQUEST);
                    } else {
                        localisationPermissionIsGranted = true;
                        callPhonePermissionIsGranted = true;
                    }
                }
                break;
        }
    }

    // Méthode servant à actualiser
    private void actualiserLeTitreEtFragmentEnCours(Fragment fragment) {
        if (fragment instanceof AccueilFragment) {
            setTitle(getString(R.string.menu_item_accueil));
            navigationView.setCheckedItem(R.id.menu_item_accueil);
        }else if (fragment instanceof ContactFragment) {
            setTitle(getString(R.string.menu_item_contact));
            navigationView.setCheckedItem(R.id.menu_item_contact);
        } else if (fragment instanceof AideFragment) {
            setTitle(getString(R.string.menu_item_aide));
            navigationView.setCheckedItem(R.id.menu_item_aide);
        } else if (fragment instanceof TelephoneFragment) {
            setTitle(getString(R.string.menu_item_footer));
            navigationView.setCheckedItem(R.id.footer_item_1);
        }
    }

    // Méthodes pour l'utilisation du menu
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //Si on clique sur un certain item du menu alors on remplace le fragment_container de la page principale par le fragment correspondant à l'item sélectionné
        if (id == R.id.menu_item_accueil) {
            // Accueil
            changerDeFragment(getString(R.string.menu_item_accueil), new AccueilFragment());

        } else if (id == R.id.menu_item_map) {
            // Map
            if (localisationPermissionIsGranted) {
                startActivity(new Intent(this, MapsActivity.class));
            } else {
                Snackbar snackbar = Snackbar.make(this.findViewById(android.R.id.content), R.string.localisationNotGranted, Snackbar.LENGTH_INDEFINITE).setAction("AUTORISER", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_FINE_LOCATION);
                        }
                    }
                });
                snackbar.setActionTextColor(Color.GREEN);
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setMaxLines(3);
                snackbar.show();
            }

        } else if (id == R.id.menu_item_autourDeMoi) {
            // Autour de moi
            if (localisationPermissionIsGranted) {
                startActivity(new Intent(this, AutourActivity.class));
            } else {
                Snackbar snackbar = Snackbar.make(this.findViewById(android.R.id.content), R.string.localisationNotGranted, Snackbar.LENGTH_INDEFINITE).setAction("AUTORISER", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_FINE_LOCATION);
                        }
                    }
                });
                snackbar.setActionTextColor(Color.GREEN);
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setMaxLines(3);
                snackbar.show();
            }

        } else if (id == R.id.menu_item_associations) {
            //Associations
            startActivity(new Intent(this, AssociationsActivity.class));


        } else if (id == R.id.menu_item_contact) {
            // Contact
            changerDeFragment(getString(R.string.menu_item_contact), new ContactFragment());

        } else if (id == R.id.menu_item_aide) {
            // Aide
            changerDeFragment(getString(R.string.menu_item_aide), new AideFragment());

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Méthode qui change de fragment
    public void changerDeFragment(String titre, Fragment fragment) {
        setTitle(titre);
        android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment).commit();
        FragmentEnCours.setFragmentEnCours(fragment);
    }

    // Méthode qui ouvre le fragment téléphone
    public void ouvrirNumerosUtile(View view) {
        // On appelle le fragment téléphone et on ferme manuellement le menu (car le bouton "Numéros utiles" n'est pas un item du menu)
        changerDeFragment(getString(R.string.menu_item_footer), new TelephoneFragment());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    // Méthode appelée lorsque qu'on clique sur le bouton "Appeller" dans le fragment Téléphone
    public void call112(View view) {
        /* Si l'utilisateur n'a pas donné la permission CALL_PHONE alors on lui demande, sinon on ouvre l'application téléphone. */
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Snackbar snackbar = Snackbar.make(this.findViewById(android.R.id.content),
                    "Veuillez autoriser l'application à accéder au téléphone",
                    Snackbar.LENGTH_INDEFINITE).setAction("AUTORISER",
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, MY_PERMISSION_REQUEST_CALL_PHONE);
                            }
                        }
                    });
            snackbar.setActionTextColor(Color.GREEN);
            snackbar.show();
        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:112"));
            startActivity(callIntent);
        }

    }

    // Méthode qui renvoie un tableau de byte[] avec une image passée en paramètre
    public byte[] imageFromDrawableToByte(int image) {
        Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), image);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        return byteArray;
    }

}