package com.example.blackbox;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import net.sqlcipher.database.SQLiteDatabase;

import javax.crypto.SecretKey;

public class UpdateAcc extends AppCompatActivity {

    // user input
    private boolean validate;
    EditText edt_txt_pass_update1, edt_txt_acc_update1, edt_txt_title_update1;
    TextInputLayout input_layout_title_update1, input_layout_acc_update1, input_layout_pass_update1;

    TextView category_title_update1, txt_Hid_Acc_update1, txt_Hid_Pass_update1;
    ImageView img_Card_update1;
    // img index for icons
    int img_index;

    // button
    Button btn_update;
    Button btn_generatePass_update1;

    CheckBox checkBox_number_update1;
    CheckBox checkBox_uppercase_update1;
    CheckBox checkBox_punctuation_update1;

    CardModel card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_acc);

        category_title_update1 = findViewById(R.id.category_title_update1);
        txt_Hid_Acc_update1 = findViewById(R.id.txt_Hid_Acc_update1);
        txt_Hid_Pass_update1 = findViewById(R.id.txt_Hid_Pass_update1);
        img_Card_update1 = findViewById(R.id.img_Card_update1);

        edt_txt_acc_update1 = findViewById(R.id.edt_txt_acc_update1);
        edt_txt_pass_update1 = findViewById(R.id.edt_txt_pass_update1);
        edt_txt_title_update1 = findViewById(R.id.edt_txt_title_update1);
        input_layout_acc_update1 = findViewById(R.id.input_layout_acc_update1);
        input_layout_pass_update1 = findViewById(R.id.input_layout_pass_update1);
        input_layout_title_update1 = findViewById(R.id.input_layout_title_update1);
        btn_update = findViewById(R.id.btn_update);
        btn_generatePass_update1 = findViewById(R.id.btn_generatePass_update1);
        checkBox_number_update1 = findViewById(R.id.checkBox_number_update1);
        checkBox_uppercase_update1 = findViewById(R.id.checkBox_uppercase_update1);
        checkBox_punctuation_update1 = findViewById(R.id.checkBox_punctuation_update1);

        Bundle bundle = getIntent().getExtras();
        card = (CardModel)bundle.getSerializable("card");
        category_title_update1.setText(card.getTitle());

        if(card.getAcc().length() > 13)
            txt_Hid_Acc_update1.setText(card.getAcc().substring(0, 13) + "...");
        else
            txt_Hid_Acc_update1.setText(card.getAcc());

        //decryption
        /*String decrypted = "";
        try {
            SQLiteDatabase.loadLibs(this);
            DataBaseHelperSqlCipher db = new DataBaseHelperSqlCipher(this);
            AES aes = new AES();
            String salt = card.getSalt();
            String masterKey = bundle.getString("masterKey");
            SecretKey key = aes.getKeyFromPassword(masterKey, salt);  //salt and masterkey will generate the secret key
            String iv = card.getIv();
            decrypted = aes.decrypt(db.getPassword(card.getId()), key, iv);
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        txt_Hid_Pass_update1.setText(card.getPass());
        img_Card_update1.setImageResource(card.getImg());

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateFields() == true)
                {
                    try {
                        SQLiteDatabase.loadLibs(view.getContext());
                        DataBaseHelperSqlCipher db = new DataBaseHelperSqlCipher(view.getContext());

                        card.setAcc(edt_txt_acc_update1.getText().toString());

                        /*AES aes = new AES();

                        //create secret key from master key and random generated salt
                        String salt = aes.generateSalt();
                        String masterKey = bundle.getString("masterKey");
                        SecretKey key = aes.getKeyFromPassword(masterKey, salt);  //salt and masterkey to generate secretKey
                        String iv = aes.generateIv();

                        card.setSalt(salt);
                        card.setIv(iv);
                        card.setPass(aes.encrypt(edt_txt_pass_update1.getText().toString(), key, iv));*/

                        card.setPass(edt_txt_pass_update1.getText().toString());
                        card.setImg(img_index);
                        card.setTitle(edt_txt_title_update1.getText().toString());

                        //AccountTable.UpdateAccount(card, view.getContext());
                        db.UpdateAccount(card);
                    } catch (Exception e) {
                        Toast.makeText(view.getContext(), "ENCRYPTE FAILURE", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        });

        btn_generatePass_update1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PasswordGenerator passwordGenerator = new PasswordGenerator.PasswordGeneratorBuilder()
                        .useDigits(checkBox_number_update1.isChecked())
                        .useUpper(checkBox_uppercase_update1.isChecked())
                        .usePunctuation(checkBox_punctuation_update1.isChecked())
                        .build();
                String password = passwordGenerator.generate(8);
                edt_txt_pass_update1.setText(password);
            }
        });

        txt_Hid_Acc_update1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputFilter[] fArray = new InputFilter[1];
                fArray[0] = new InputFilter.LengthFilter(30);
                txt_Hid_Acc_update1.setFilters(fArray);
                txt_Hid_Acc_update1.setText(card.getAcc());
            }
        });

        edt_txt_acc_update1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(edt_txt_acc_update1.getText().length() >= 1)
                    input_layout_acc_update1.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(edt_txt_acc_update1.getText().length() >= 1)
                    input_layout_acc_update1.setError(null);
                else
                    input_layout_acc_update1.setError("Enter Your Account Name");
            }
        });

        edt_txt_pass_update1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(edt_txt_pass_update1.getText().length() >= 1)
                    input_layout_pass_update1.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(edt_txt_pass_update1.getText().length() >= 1)
                    input_layout_pass_update1.setError(null);
                else
                    input_layout_pass_update1.setError("Enter Your Password");
            }
        });
    }

    private boolean validateFields() {
        int yourDesiredLength = 1;
        if(edt_txt_pass_update1.getText().length() < yourDesiredLength || edt_txt_acc_update1.getText().length() < yourDesiredLength)
            validate = false;
        else
            validate = true;
        if (edt_txt_acc_update1.getText().length() < yourDesiredLength) {
            input_layout_acc_update1.setError("Enter Your Account Name");
        }
        if (edt_txt_pass_update1.getText().length() < yourDesiredLength) {
            input_layout_pass_update1.setError("Enter Your Password");
        }
        return validate;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.finish();
                break;
        }
        return true;
    }
}