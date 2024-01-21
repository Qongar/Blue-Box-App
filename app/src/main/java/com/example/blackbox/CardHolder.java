package com.example.blackbox;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class CardHolder extends RecyclerView.ViewHolder{
    ImageView img;
    TextView acc, pass, txt_Copy, title, txt_edit_row, txt_delete_row;
    Button btn_show_pass;
    CardView cardView;

    public CardHolder(@NonNull View itemView) {
        super(itemView);

        this.img = itemView.findViewById(R.id.img_Card);
        this.acc = itemView.findViewById(R.id.txt_Hid_Acc);
        this.pass = itemView.findViewById(R.id.txt_Hid_Pass);
        this.txt_Copy = itemView.findViewById(R.id.txt_Copy);
        this.txt_edit_row = itemView.findViewById(R.id.txt_edit);
        this.txt_delete_row = itemView.findViewById(R.id.txt_delete);
        this.title = itemView.findViewById(R.id.category_title);
        this.btn_show_pass = itemView.findViewById(R.id.btn_view_pass);
        this.cardView = itemView.findViewById(R.id.rowLayout);
    }
}
