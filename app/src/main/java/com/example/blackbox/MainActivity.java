package com.example.blackbox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import net.sqlcipher.database.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static String UserPassword;

    SharedPreferences prefs = null;
    boolean firstRun;

    // toolbar
    Toolbar toolbar;
    // recycler view, card adapter, swipe helper
    RecyclerView recyclerView;
    CardAdapter cardAdapter;
    ItemTouchHelper itemTouchHelper;
    // floating button
    FloatingActionButton btn_add;
    Button btn_toMainActivity;
    Button btn_open_help;
    Button btn_createMasterPass;
    FloatingActionButton btn_master_key;
    // database
    DataBaseHelperSqlCipher dbHelper;
    // navigation
    DrawerLayout drawer;
    NavigationView nav;
    // view switcher
    ViewSwitcher viewSwitcher;
    // master key check
    EditText edt_txt_master_key;
    TextView txt_create_master_key_text;

    ArrayList<CardModel> cards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("com.zeroPixel.bluelock", MODE_PRIVATE);
        firstRun = prefs.getBoolean("firstrun", true);

        // view switcher, buttons and masterKey check
        viewSwitcher = (ViewSwitcher)findViewById(R.id.viewSwitcher);

        edt_txt_master_key = findViewById(R.id.edt_txt_master_key);
        edt_txt_master_key.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                UserPassword = edt_txt_master_key.getText().toString();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        UserPassword = edt_txt_master_key.getText().toString();

        // C:\Program Files\Android\Android Studio\plugins\android\resources\images\asset_studio\ic_launcher_foreground.xml
        btn_toMainActivity = findViewById(R.id.btn_toMainActivity);
        btn_toMainActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(firstRun)
                {
                    if (firstRun) {
                        prefs.edit().putBoolean("firstrun", false).commit();
                        firstRun = false;
                    }
                    btn_toMainActivity.callOnClick();
                }
                else
                {
                    // password can't be empty
                    if(UserPassword.length() > 3) {
                        // if password correct
                        if(dbHelper.CheckPassword()) {
                            //ArrayList<CardModel> newCards = AccountTable.GetAllCards(view.getContext());
                            ArrayList<CardModel> newCards = dbHelper.GetAllCards();
                            // if db is not empty
                            if(!newCards.isEmpty()){
                                btn_master_key.setEnabled(false);
                                btn_master_key.hide();
                                btn_add.setEnabled(true);
                                btn_add.show();

                                itemTouchHelper = new ItemTouchHelper(simpleCallback);
                                itemTouchHelper.attachToRecyclerView(recyclerView);

                                getCards();
                                viewSwitcher.setInAnimation(view.getContext(), R.anim.slide_in_right);
                                viewSwitcher.setOutAnimation(view.getContext(), R.anim.slide_out_left);
                                viewSwitcher.showNext();
                            }
                            // if db is empty
                            else{
                                itemTouchHelper = null;
                                btn_master_key.setEnabled(false);
                                btn_master_key.hide();
                                btn_add.setEnabled(true);
                                btn_add.show();

                                //getCards();
                                viewSwitcher.setInAnimation(view.getContext(), R.anim.slide_in_right);
                                viewSwitcher.setOutAnimation(view.getContext(), R.anim.slide_out_left);
                                viewSwitcher.showNext();
                            }
                            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                        }
                        else {
                            Toast.makeText(view.getContext(), "Password Incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(view.getContext(), "Password must be at least 4 characters", Toast.LENGTH_SHORT).show();
                        btn_master_key.setEnabled(true);
                        btn_master_key.show();
                        btn_add.setEnabled(false);
                        btn_add.hide();
                    }
                }

            }
        });
        if(firstRun) {
            btn_toMainActivity.setText("Create Password");
            btn_toMainActivity.setTextSize(10);
            txt_create_master_key_text = findViewById(R.id.txt_CreateMasterKeyUnderText);
            txt_create_master_key_text.setVisibility(View.VISIBLE);
        }

        // help menu
        /*btn_open_help = findViewById(R.id.btn_help);
        btn_open_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(MainActivity.this, );
                //startActivity(intent);
            }
        });
        // create master password
        btn_createMasterPass = findViewById(R.id.btn_create_master_pass);
        btn_createMasterPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateMasterPassword.class);
                startActivity(intent);
            }
        });*/
        // new acc
        btn_add = findViewById(R.id.btn_add_acc);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddAcc.class);
                startActivity(intent);
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
        });
        btn_master_key = findViewById(R.id.btn_master_key);
        btn_master_key.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewSwitcher.setInAnimation(view.getContext(), R.anim.slide_in_left);
                viewSwitcher.setOutAnimation(view.getContext(), R.anim.slide_out_right);
                viewSwitcher.showPrevious();
            }
        });

        // toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Recent");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.getNavigationIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
        setSupportActionBar(toolbar);

        // database
        SQLiteDatabase.loadLibs(this);
        dbHelper = new DataBaseHelperSqlCipher(this);

        // navigation drawer
        nav = findViewById(R.id.nav_view);
        nav.setNavigationItemSelectedListener(this);
        nav.setItemIconTintList(null);
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        // card populate
        recyclerView = findViewById(R.id.recyclerView);
    }

    public void getCards() {

        cards = new ArrayList<>();

        //ArrayList<CardModel> newCards = AccountTable.GetAllCards(this);
        ArrayList<CardModel> newCards = dbHelper.GetAllCards();

        /*try {
            AES.encryptFile(edt_txt_master_key.getText().toString(), this.getDatabasePath("crypto.db").getAbsolutePath());
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        } catch (BadPaddingException e) {
            throw new RuntimeException(e);
        } catch (IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }*/

        for(CardModel item : newCards){
            CardModel cardModel = new CardModel();

            //decryption
            /*String decrypted = "";
            try {
                String salt = item.getSalt();
                String masterKey = edt_txt_master_key.getText().toString();
                SecretKey key = AES.getKeyFromPassword(masterKey, salt);  //salt and masterkey will generate the secret key
                String iv = item.getIv();
                decrypted = AES.decrypt(db.getPassword(item.getId()), key, iv);
            } catch (Exception e) {
                //Toast.makeText(getApplicationContext(), "getCards() DECRYPT FAILURE", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }*/

            cardModel.setId(item.getId());
            cardModel.setAcc(item.getAcc());
            //String str = decrypted.replaceAll(".", "*");
            cardModel.setPass(item.getPass());
            cardModel.setTitle(item.getTitle());
            cardModel.setOrder(item.getOrder());
            //Toast.makeText(getApplicationContext(),"cardOrder= " + cardModel.getOrder(),Toast.LENGTH_SHORT).show();
            switch (item.getImg()) {
                case 0:
                    cardModel.setImg(R.drawable.e_mail);
                    break;
                case 1:
                    cardModel.setImg(R.drawable.social_media);
                    break;
                case 2:
                    cardModel.setImg(R.drawable.computer);
                    break;
                case 3:
                    cardModel.setImg(R.drawable.phone);
                    break;
                case 4:
                    cardModel.setImg(R.drawable.title);
                    break;
                case 5:
                    cardModel.setImg(R.drawable.debit_card);
                    break;
                case 6:
                    cardModel.setImg(R.drawable.website);
                    break;
                case 7:
                    cardModel.setImg(R.drawable.wifi);
                    break;
            }
            cards.add(cardModel);
        }

        Collections.sort(cards, CardModel.byOrder);

        cardAdapter = new CardAdapter(this, cards);
        recyclerView.setAdapter(cardAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cardAdapter.notifyDataSetChanged();
    }

    private void getCardsByCategory (int catIndex) {

        cards = new ArrayList<>();

        //ArrayList<CardModel> newCards = AccountTable.GetByCategory(catIndex, this);
        ArrayList<CardModel> newCards = dbHelper.GetByCategory(catIndex);
        for(CardModel item : newCards){
            CardModel cardModel = new CardModel();

            cardModel.setId(item.getId());
            cardModel.setAcc(item.getAcc());
            cardModel.setPass(item.getPass());
            cardModel.setTitle(item.getTitle());
            cardModel.setOrder(item.getOrder());
            switch (item.getImg()) {
                case 0:
                    cardModel.setImg(R.drawable.e_mail);
                    break;
                case 1:
                    cardModel.setImg(R.drawable.social_media);
                    break;
                case 2:
                    cardModel.setImg(R.drawable.computer);
                    break;
                case 3:
                    cardModel.setImg(R.drawable.phone);
                    break;
                case 4:
                    cardModel.setImg(R.drawable.title);
                    break;
                case 5:
                    cardModel.setImg(R.drawable.debit_card);
                    break;
                case 6:
                    cardModel.setImg(R.drawable.website);
                    break;
                case 7:
                    cardModel.setImg(R.drawable.wifi);
                    break;
            }
            cards.add(cardModel);
        }

        Collections.sort(cards, CardModel.byOrder);

        cardAdapter = new CardAdapter(this, cards);
        recyclerView.setAdapter(cardAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        cardAdapter.notifyDataSetChanged();
    }

    // remove by swipe
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            //AccountTable.DeleteAccount(cardAdapter.cards.get(position), viewHolder.itemView.getContext());
            dbHelper.DeleteAccount(cardAdapter.cards.get(position));
            cardAdapter.deleteItem(position);
            cardAdapter.notifyItemRemoved(position);

            for(CardModel item : cards){
                if(position < item.getOrder()){
                    item.setOrder(item.getOrder()-1);
                }else{
                    // do nothn
                }
            }
        }
    };

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
        if(drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(UserPassword.length() != 0) {
            if(dbHelper.CheckPassword()) {
                getCards();
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
        }
        toolbar.setTitle("Recent");

        // first time adding accounts
        //ToDo: not properly working, master wrong but still can delete
        if(itemTouchHelper == null) {
            itemTouchHelper = new ItemTouchHelper(simpleCallback);
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_recent:
                getCards();
                toolbar.setTitle("Recent");
                break;
            case R.id.nav_mail:
                getCardsByCategory(0);
                toolbar.setTitle("E-Mail");
                break;
            case R.id.nav_social:
                getCardsByCategory(1);
                toolbar.setTitle("Social Media");
                break;
            case R.id.nav_computer:
                getCardsByCategory(2);
                toolbar.setTitle("Computer");
                break;
            case R.id.nav_phone:
                getCardsByCategory(3);
                toolbar.setTitle("Phone");
                break;
            case R.id.nav_credit_card:
                getCardsByCategory(5);
                toolbar.setTitle("Debit Card");
                break;
            case R.id.nav_website:
                getCardsByCategory(6);
                toolbar.setTitle("Website");
                break;
            case R.id.nav_wifi:
                getCardsByCategory(7);
                toolbar.setTitle("Wifi");
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}