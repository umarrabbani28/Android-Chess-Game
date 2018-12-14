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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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


                try {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.game_steps);

                    JSONObject selectedObject = MainActivity.jsonObjects.get(position);
                    JSONArray instructions = selectedObject.getJSONArray("Instructions");

                    ArrayList<String> instructionList = new ArrayList<>();
                    ListView stepsList = (ListView)dialog.findViewById(R.id.stepsList);


                    for (int i = 0; i < instructions.length(); i++) {
                        String info = instructions.getString(i);
                        instructionList.add(info);
                    }

                    ArrayAdapter stepsAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, instructionList);
                    stepsList.setAdapter(stepsAdapter);

                    dialog.show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }
}
