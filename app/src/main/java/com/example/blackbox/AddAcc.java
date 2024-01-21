package com.example.blackbox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.Debug;
import androidx.core.view.GravityCompat;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import net.sqlcipher.database.SQLiteDatabase;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class AddAcc extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    // user input
    private boolean validate;
    EditText edt_txt_pass, edt_txt_acc, edt_txt_title;
    TextInputLayout input_layout_title, input_layout_acc, input_layout_pass;
    String title;
    // img index for icons
    int img_index;

    // button
    Button btn_save;
    Button btn_generatePass;

    CheckBox checkBox_number;
    CheckBox checkBox_uppercase;
    CheckBox checkBox_punctuation;

    // dropdown spinner
    Spinner spin_category;
    ArrayList<SpinModel> spinList;

    //
    private final String mail = "E-Mail";
    private final String other = "Other";
    private final String debit_card = "Debit Card";
    private final String phone = "Phone";
    private final String computer = "Computer";
    private final String social_media = "Social Media";
    private final String website = "Website";
    private final String wifi = "Wifi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_acc);

        edt_txt_acc = findViewById(R.id.edt_txt_acc);
        edt_txt_pass = findViewById(R.id.edt_txt_pass);
        edt_txt_title = findViewById(R.id.edt_txt_title);
        input_layout_acc = findViewById(R.id.input_layout_acc);
        input_layout_pass = findViewById(R.id.input_layout_pass);
        input_layout_title = findViewById(R.id.input_layout_title);
        btn_save = findViewById(R.id.btn_save);
        btn_generatePass = findViewById(R.id.btn_generatePass);
        checkBox_number = findViewById(R.id.checkBox_number);
        checkBox_uppercase = findViewById(R.id.checkBox_uppercase);
        checkBox_punctuation = findViewById(R.id.checkBox_punctuation);

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateFields()) {
                    SQLiteDatabase.loadLibs(AddAcc.this);
                    DataBaseHelperSqlCipher db = new DataBaseHelperSqlCipher(AddAcc.this);

                    CardModel cardModel = new CardModel();
                    cardModel.setAcc(edt_txt_acc.getText().toString());

                    cardModel.setPass(edt_txt_pass.getText().toString());
                    cardModel.setImg(img_index);
                    if(img_index == 4)
                        title = edt_txt_title.getText().toString();
                    cardModel.setTitle(title);
                    //AccountTable.AddAccount(cardModel, view.getContext());
                    db.AddAccount(cardModel);

                    onBackPressed();
                }
            }
        });

        btn_generatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PasswordGenerator passwordGenerator = new PasswordGenerator.PasswordGeneratorBuilder()
                        .useDigits(checkBox_number.isChecked())
                        .useUpper(checkBox_uppercase.isChecked())
                        .usePunctuation(checkBox_punctuation.isChecked())
                        .build();
                String password = passwordGenerator.generate(8);
                edt_txt_pass.setText(password);
            }
        });

        // spinner1
        spin_category = findViewById(R.id.spin_category);
        spinList = getSpinList();
        SpinAdapter spinAdapter = new SpinAdapter(this, spinList);
        if (spin_category != null) {
            spin_category.setAdapter(spinAdapter);
            spin_category.setOnItemSelectedListener(this);
        }

        edt_txt_acc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(edt_txt_acc.getText().length() >= 1)
                    input_layout_acc.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(edt_txt_acc.getText().length() >= 1)
                    input_layout_acc.setError(null);
                else
                    input_layout_acc.setError("Enter Your Account Name");
            }
        });

        edt_txt_pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(edt_txt_pass.getText().length() >= 1)
                    input_layout_pass.setError(null);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(edt_txt_pass.getText().length() >= 1)
                    input_layout_pass.setError(null);
                else
                    input_layout_pass.setError("Enter Your Password");
            }
        });
    }

    // if empty error
    private boolean validateFields() {
        int yourDesiredLength = 1;
        if(edt_txt_pass.getText().length() < yourDesiredLength || edt_txt_acc.getText().length() < yourDesiredLength)
            validate = false;
        else
            validate = true;
        if (edt_txt_acc.getText().length() < yourDesiredLength) {
            input_layout_acc.setError("Enter Your Account Name");
        }
        if (edt_txt_pass.getText().length() < yourDesiredLength) {
            input_layout_pass.setError("Enter Your Password");
        }
        return validate;
    }

    private ArrayList<SpinModel> getSpinList() {
        spinList = new ArrayList<>();
        spinList.add(new SpinModel(social_media, R.drawable.social_media_24dpi));
        spinList.add(new SpinModel(mail, R.drawable.e_mail_24dpi));
        spinList.add(new SpinModel(debit_card, R.drawable.debit_card_24dpi));
        spinList.add(new SpinModel(website, R.drawable.website_24dpi));
        spinList.add(new SpinModel(wifi, R.drawable.wifi_24dpi));
        spinList.add(new SpinModel(computer, R.drawable.computer_24dpi));
        spinList.add(new SpinModel(phone, R.drawable.phone_24dpi));
        spinList.add(new SpinModel(other, R.drawable.title_24dpi));
        return spinList;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        SpinModel item = (SpinModel) adapterView.getSelectedItem();
        getIndex(item.getCategory());
        //Toast.makeText(this, item.getCategory(), Toast.LENGTH_SHORT).show();
        if(img_index == 4) {
            //edt_txt_title.setEnabled(true);
            //text_input_layout_title.setFocusable(true);
        }
        else{
            //edt_txt_title.setEnabled(false);
            //text_input_layout_title.setFocusable(false);
        }
    }

    // passing arbitrary index instead of storing the img as blob
    private void getIndex(String category) {
        switch (category) {
            case mail:
                img_index = 0;
                title = "E-Mail";
                break;
            case social_media:
                img_index = 1;
                title = "Social Media";
                break;
            case computer:
                img_index = 2;
                title = "Computer";
                break;
            case phone:
                img_index = 3;
                title = "Phone";
                break;
            case other:
                img_index = 4;
                break;
            case debit_card:
                img_index = 5;
                title = "Debit Card";
                break;
            case website:
                img_index = 6;
                title = "Website";
                break;
            case wifi:
                img_index = 7;
                title = "Wifi";
                break;

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        super.finish();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }
}