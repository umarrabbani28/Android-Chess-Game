package com.example.umarr.chessapp.chess;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.umarr.chessapp.R;

public class MainActivity extends AppCompatActivity {

    public static ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Intent newGameIntent = new Intent(this, GameActivity.class);

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

        loadGameButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
              //  startActivity(intent);
            }
        });

    }
}
