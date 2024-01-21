package com.example.blackbox;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class SpinAdapter extends ArrayAdapter<SpinModel> {
    public SpinAdapter(@NonNull Context context, ArrayList<SpinModel> spinList) {
        super(context, 0, spinList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.spinner_layout, parent, false);
        }
        SpinModel spinItem = getItem(position);
        ImageView spinImg = convertView.findViewById(R.id.img_spin_category);
        TextView spinTxt = convertView.findViewById(R.id.txt_spin_category);
        if (spinItem != null) {
            spinImg.setImageResource(spinItem.getImg());
            spinTxt.setText(spinItem.getCategory());
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if(convertView == null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.dropdown_category, parent, false);
        }
        SpinModel categoryItem = getItem(position);
        ImageView categoryImg = convertView.findViewById(R.id.img_dropdown_category);
        TextView categoryTxt = convertView.findViewById(R.id.txt_dropdown_category);
        if (categoryItem != null) {
            categoryImg.setImageResource(categoryItem.getImg());
            categoryTxt.setText(categoryItem.getCategory());
        }
        return convertView;
    }
}
