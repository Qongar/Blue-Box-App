package com.example.blackbox;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputLayout;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;

import javax.crypto.SecretKey;

public class CardAdapter extends RecyclerView.Adapter<CardHolder>{

    Context c;
    ArrayList<CardModel> cards; // this array list create a list of array which parameters define in the cardmodel class
    CardModel selectedCardModel; // selected to edit*
    MainActivity mainActivity;

    EditText editUsername, editPassword, editTitle;
    TextInputLayout editInputUsername, editInputPassword;
    Button editButton;
    TextView txt_edit_account_title, txt_edit_account_username, txt_edit_account_password;
    ImageView img_edit_account_img;
    //constructor
    public CardAdapter(Context c, ArrayList<CardModel> cards) {
        this.c = c;
        this.cards = cards;
        this.mainActivity = (MainActivity)c;
    }

    @NonNull
    @Override
    public CardHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row, null); //inflate the row

        return new CardHolder(view); // return the view to cardholder
    }

    @Override
    public void onBindViewHolder(@NonNull CardHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.acc.setText(cards.get(holder.getAbsoluteAdapterPosition()).getAcc());
        holder.title.setText(cards.get(holder.getAbsoluteAdapterPosition()).getTitle());
        holder.pass.setText("**********");
        holder.img.setImageResource(cards.get(holder.getAbsoluteAdapterPosition()).getImg());

        // copy the password
        holder.txt_Copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("txtCopy", cards.get(holder.getAbsoluteAdapterPosition()).getPass());
                clipboard.setPrimaryClip(clip);
                //Toast.makeText(view.getContext(), "Password Copied", Toast.LENGTH_SHORT);

                SQLiteDatabase.loadLibs(view.getContext());
                DataBaseHelperSqlCipher db = new DataBaseHelperSqlCipher(view.getContext());
                if(cards.get(holder.getAbsoluteAdapterPosition()).getOrder() == 1)
                    return; // do nothing
                else {
                    //AccountTable.FixOrder(cards.get(holder.getAbsoluteAdapterPosition()).getOrder(), view.getContext());
                    db.FixOrder(cards.get(holder.getAbsoluteAdapterPosition()).getOrder());
                    if(mainActivity.toolbar.getTitle().equals("Recent")){
                        mainActivity.getCards();
                    }
                }
                db.close();
            }
        });
        // show password
        holder.btn_show_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.pass.setText(cards.get(holder.getAbsoluteAdapterPosition()).getPass());

                //holder.btn_show_pass.setEnabled(false);
                //holder.btn_show_pass.setVisibility(View.INVISIBLE);
            }
        });
        // delete row
        holder.txt_delete_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder deleteDialogue = new AlertDialog.Builder(v.getContext());
                deleteDialogue.setTitle("Are you sure?");
                deleteDialogue.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase.loadLibs(v.getContext());
                        DataBaseHelperSqlCipher dbHelper = new DataBaseHelperSqlCipher(v.getContext());

                        dbHelper.DeleteAccount(cards.get(holder.getAbsoluteAdapterPosition()));
                        deleteItem(holder.getAbsoluteAdapterPosition());
                        notifyItemRemoved(holder.getAbsoluteAdapterPosition());

                        for(CardModel item : cards){
                            if(holder.getAbsoluteAdapterPosition() < item.getOrder()){
                                item.setOrder(item.getOrder()-1);
                            }else{
                                // do nothn
                            }
                        }
                    }
                });
                deleteDialogue.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                deleteDialogue.show();
            }
        });
        // edit row
        holder.txt_edit_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.cardView.performLongClick();
            }
        });
        //
        holder.acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputFilter[] fArray = new InputFilter[1];
                fArray[0] = new InputFilter.LengthFilter(30);
                holder.acc.setFilters(fArray);
                holder.acc.setText(cards.get(position).getAcc());
            }
        });

        // hold to update cardView
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                /*Intent intent = new Intent(view.getContext(), UpdateAcc.class);

                Bundle bundle = new Bundle();
                bundle.putString("masterKey", mainActivity.edt_txt_master_key.getText().toString());
                bundle.putSerializable("card", cards.get(position));
                intent.putExtras(bundle);

                c.startActivity(intent);*/
                selectedCardModel = cards.get(holder.getAbsoluteAdapterPosition());
                EditAccountDialog(holder, view.getContext());
                return true;
            }
        });
    }

    public void deleteItem(int position){
        cards.remove(position);
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    private void EditAccountDialog(CardHolder holder, Context context)
    {
        Dialog editAccountDialog = new Dialog(context);
        editAccountDialog.setContentView(R.layout.dialog_edit_account);
        editAccountDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        txt_edit_account_title = editAccountDialog.findViewById(R.id.category_title_update1);
        txt_edit_account_username = editAccountDialog.findViewById(R.id.txt_Hid_Acc_update1);
        txt_edit_account_password = editAccountDialog.findViewById(R.id.txt_Hid_Pass_update1);
        img_edit_account_img = editAccountDialog.findViewById(R.id.img_Card_update1);

        txt_edit_account_title.setText(selectedCardModel.getTitle());
        txt_edit_account_username.setText(selectedCardModel.getAcc());
        txt_edit_account_password.setText(selectedCardModel.getPass());
        img_edit_account_img.setImageResource(selectedCardModel.getImg());


        editTitle = editAccountDialog.findViewById(R.id.edt_txt_title_update1);
        editUsername = editAccountDialog.findViewById(R.id.edt_txt_acc_update1);
        editPassword = editAccountDialog.findViewById(R.id.edt_txt_pass_update1);
        editButton = editAccountDialog.findViewById(R.id.btn_update);

        editTitle.setText(selectedCardModel.getTitle());
        editUsername.setText(selectedCardModel.getAcc());
        editPassword.setText(selectedCardModel.getPass());

        editInputPassword = editAccountDialog.findViewById(R.id.input_layout_pass_update1);
        editInputUsername = editAccountDialog.findViewById(R.id.input_layout_acc_update1);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean validate;

                int usernameDesiredLength = 1;
                int passwordDesiredLength = 4;
                if(editUsername.getText().length() < usernameDesiredLength || editPassword.getText().length() < passwordDesiredLength)
                    validate = false;
                else
                    validate = true;
                if (editUsername.getText().length() < usernameDesiredLength) {
                    editInputUsername.setError("Enter Your Username");
                }
                if (editPassword.getText().length() < passwordDesiredLength) {
                    editInputPassword.setError("Enter Your Password");
                }

                SQLiteDatabase.loadLibs(editAccountDialog.getContext());
                DataBaseHelperSqlCipher db = new DataBaseHelperSqlCipher(editAccountDialog.getContext());

                selectedCardModel.setAcc(editUsername.getText().toString());
                selectedCardModel.setPass(editPassword.getText().toString());
                //card.setImg(img_index);
                selectedCardModel.setTitle(editTitle.getText().toString());

                //AccountTable.UpdateAccount(card, view.getContext());
                db.UpdateAccount(selectedCardModel);

                holder.title.setText(cards.get(holder.getAbsoluteAdapterPosition()).getTitle());
                holder.acc.setText(cards.get(holder.getAbsoluteAdapterPosition()).getAcc());
                holder.pass.setText("**********");
                editAccountDialog.dismiss();
            }
        });

        editAccountDialog.show();
    }
}
