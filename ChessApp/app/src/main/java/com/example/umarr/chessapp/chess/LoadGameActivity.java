package com.example.umarr.chessapp.chess;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.umarr.chessapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.example.umarr.chessapp.chess.MainActivity.jsonObjects;

public class LoadGameActivity extends AppCompatActivity {

    Context context = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.load_games);

        ListView listView = (ListView)findViewById(R.id.listView);
        ArrayList<String> names = new ArrayList<>();

        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, MainActivity.names);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position, long arg3){
                String value = (String)adapter.getItemAtPosition(position);

                final Dialog dialog = new Dialog(this);
                dialog.setContentView(R.layout.promotion_spinner);

                // assuming string and if you want to get the value on click of list item
                // do what you intend to do on click of listview row
            }
        });
    }
}
