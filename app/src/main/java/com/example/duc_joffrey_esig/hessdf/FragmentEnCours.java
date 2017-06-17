package com.example.duc_joffrey_esig.hessdf;

import android.support.v4.app.Fragment;

/**
 * Created by DUC_JOFFREY-ESIG on 07.02.2017.
 */

// Utile pour le changement d'orientation du smartphone.
public class FragmentEnCours {

    //Initialisé à AccueilFragment.
    private static Fragment fragmentEnCours = new AccueilFragment();

    public static Fragment getFragmentEnCours() {
        return fragmentEnCours;
    }

    public static void setFragmentEnCours(Fragment fragmentEnCours) {
        FragmentEnCours.fragmentEnCours = fragmentEnCours;
    }

}
