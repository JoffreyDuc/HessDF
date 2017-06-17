package com.example.duc_joffrey_esig.hessdf;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by DUC_JOFFREY-ESIG on 14.03.2017.
 */

public class AssociationsListAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<Association> associationsList;

    public AssociationsListAdapter(Context context, int layout, ArrayList<Association> associationsList) {
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
        ImageView imageView;
        TextView tvNomAssociation;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        View row = view;
        ViewHolder holder = new ViewHolder();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.tvNomAssociation = (TextView) row.findViewById(R.id.tvNomAssociation);
            holder.imageView = (ImageView) row.findViewById(R.id.imgAssociation);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        Association association = associationsList.get(position);

        holder.tvNomAssociation.setText(association.getName());

        byte[] associationImage = association.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(associationImage, 0, associationImage.length);
        holder.imageView.setImageBitmap(bitmap);

        return row;
    }
}
