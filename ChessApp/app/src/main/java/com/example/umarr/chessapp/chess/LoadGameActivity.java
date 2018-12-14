package com.example.umarr.chessapp.chess;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.umarr.chessapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.umarr.chessapp.chess.MainActivity.jsonObjects;

public class LoadGameActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load_games);

        ListView listView = (ListView)findViewById(R.id.listView);
        ArrayList<String> names = new ArrayList<>();

        for (JSONObject e: jsonObjects){
            try {
                names.add(e.getString("Name"));
            } catch (JSONException e1) {

                e1.printStackTrace();
            }
        }
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.load_games, names);
        listView.setAdapter(adapter);
    }
}
