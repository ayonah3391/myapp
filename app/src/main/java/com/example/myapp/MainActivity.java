package com.example.myapp;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapp.databinding.ActivityMainBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private DBHandler dbHandler;
    private String cardsFileName = "cards.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // stuff for the navbar on the left side of the screen
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        // end for navbar

        // create database
        dbHandler = new DBHandler(MainActivity.this);

        // create method for random question volley
        init();
    }

    public void init() {
        final String url = "https://www.qbreader.org/api/random-question";
        RequestQueue requestQueue = Volley.newRequestQueue(this);


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("questionType", "tossup");
            jsonObject.put("categories", "Science");
            jsonObject.put("difficulties", "5");
            jsonObject.put("number", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Handle the response
                        // This method is called when the request is successful
                        try {
                            for(int i = 0; i < response.length(); i++) {
                                JSONObject questObj = (JSONObject) response.get(i);
                                String ID_COL, QTEXT_COL, ATEXT_COL, SETNAME_COL, CATEGORY_COL;

                                ID_COL = questObj.get("_id").toString();
                                QTEXT_COL = questObj.get("question").toString();
                                ATEXT_COL = questObj.get("answer").toString();
                                SETNAME_COL = questObj.get("setName").toString();
                                CATEGORY_COL = questObj.get("subcategory").toString();

                                ID_COL = format(ID_COL);
                                System.out.println("ID_COL: " + ID_COL);
                                QTEXT_COL = format(QTEXT_COL);
                                System.out.println("QTEXT_COL: " + QTEXT_COL);
                                ATEXT_COL = format(ATEXT_COL);
                                System.out.println("ATEXT_COL: " + ATEXT_COL);
                                SETNAME_COL = format(SETNAME_COL);
                                System.out.println("SETNAME_COL: " + SETNAME_COL);
                                CATEGORY_COL = format(CATEGORY_COL);
                                System.out.println("CATEGORY_COL: " + CATEGORY_COL);

                                // write the stuff to a csv file
                                dbHandler.addNewQuestion(ID_COL, QTEXT_COL, ATEXT_COL, SETNAME_COL, CATEGORY_COL);
                                dbHandler.randomQuestion();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error
                        // This method is called when there is an error with the request
                        error.printStackTrace();
                    }
                }) {
            @Override
            public byte[] getBody() {
                return jsonObject.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        requestQueue.add(jsonArrayRequest);
    }

    public String readFromFile(String fileName) throws IOException {
        File path = getApplicationContext().getFilesDir();
        File readFrom = new File(path, fileName);
        FileInputStream stream = new FileInputStream(readFrom);
        byte[] content = new byte[(int) readFrom.length()];

        stream.read(content);
        return new String(content);
    }

    public void writeToFile(String fileName, String content) {
        File path = getApplicationContext().getFilesDir();
        try {
            FileOutputStream writer = new FileOutputStream(new File(path, fileName));
            writer.write(content.getBytes());
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String format(String s) {
        // System.out.println(s);
        s = s.replaceAll("\"", "");
        s = s.replaceAll("'", "");
        s = s.replaceAll("\\[", "").replaceAll("\\]","");

        // System.out.println(s);
        return s;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}