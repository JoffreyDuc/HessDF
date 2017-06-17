package com.example.duc_joffrey_esig.hessdf;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by DUC_JOFFREY-ESIG on 20.03.2017.
 */

public class AutourListAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<Association> associationsList;
    private Location location;
    private float distance = 0;

    public AutourListAdapter(Context context, int layout, ArrayList<Association> associationsList) {
        this.context = context;
        this.layout = layout;
        this.associationsList = associationsList;
    }

    @Override
    public int getCount() {
        return associationsList.size();
    }

    @Override
    public Object getItem(int position) {
        return associationsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        ImageView imgAssociationAutour;
        TextView tvNomAssociationAutour;
        TextView tvDistance;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {


        View row = view;
        ViewHolder holder = new ViewHolder();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.imgAssociationAutour = (ImageView) row.findViewById(R.id.imgAssociationAutour);
            holder.tvNomAssociationAutour = (TextView) row.findViewById(R.id.tvNomAssociationAutour);
            holder.tvDistance = (TextView) row.findViewById(R.id.tvDistance);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Association association = associationsList.get(position);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager mng = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            location = mng.getLastKnownLocation(mng.getBestProvider(new Criteria(), false));

            Location locationAssociation = new Location(association.getName());
            locationAssociation.setLatitude(association.getLatitude());
            locationAssociation.setLongitude(association.getLongitude());

            distance = location.distanceTo(locationAssociation);
        }

        holder.tvNomAssociationAutour.setText(association.getName());
        holder.tvDistance.setText(convertirDistance(distance));

        byte[] associationImage = association.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(associationImage, 0, associationImage.length);
        holder.imgAssociationAutour.setImageBitmap(bitmap);

        return row;
    }

    private String convertirDistance(double distanceMetres){
        String message = "";

        if(distanceMetres < 1000){
            int metres = (int) distanceMetres;
            message += metres + " mètres";
        }else{
            double km = distanceMetres/1000;
            message += ((int)(km*100))/100. + " km";
        }

        return message;
    }
}
