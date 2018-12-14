package com.example.umarr.chessapp.chess;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.umarr.chessapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static ProgressBar progressBar;
    Context currContext = this;
    public static ArrayList<JSONObject> jsonObjects = new ArrayList<>();
    public static ArrayList<String> names = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Intent newGameIntent = new Intent(this, GameActivity.class);
        final Intent loadGameIntent = new Intent(this, LoadGameActivity.class);

        Button newGameButton = (Button)findViewById(R.id.newGameButton);
        Button loadGameButton = (Button)findViewById(R.id.loadGameButton);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        progressBar.setVisibility(View.GONE);

        newGameButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(newGameIntent);
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        try {

            File directory = currContext.getFilesDir();
            File file = new File(directory, "savedGames");

            String input;
            BufferedReader reader = new BufferedReader(new FileReader(file));
            while ((input = reader.readLine()) != null) {
                JSONObject jsonObject = new JSONObject(input);
                jsonObjects.add(jsonObject);
            }

            for (JSONObject e: jsonObjects){
                names.add(e.getString("Name"));
            }

                    /*ileInputStream inputStream = openFileInput("savedGames");
                    int c;
                    String temp="";
                    while( (c = inputStream.read()) != -1){
                        temp = temp + Character.toString((char)c);
                    }

                    JSONObject jsonObject = new JSONObject(temp);


                    System.out.println("!!!!   " + temp);*/
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("HUHHH");
            e.printStackTrace();
        } catch (JSONException e) {
            System.out.println("HUHHH");
            e.printStackTrace();
        }

        loadGameButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(loadGameIntent);

            }
        });

    }
}
