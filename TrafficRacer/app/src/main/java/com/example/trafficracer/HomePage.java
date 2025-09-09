package com.example.trafficracer;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomePage extends AppCompatActivity implements View.OnClickListener {
    Button buttonPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            buttonPlay = findViewById(R.id.buttonPlay);
            buttonPlay.setOnClickListener(this);
            return insets;
        });
    }

    @Override
    public void onClick(View view) {
        if (view== buttonPlay){
            Intent intent = new Intent(HomePage.this, GameActivity.class);
            startActivity(intent);
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.MainPage)
        {
            Intent intent=new Intent(this, MainActivity.class);
            startActivity(intent);
            Toast.makeText(this, "you selected main page",Toast.LENGTH_LONG).show();
        }
        if(id==R.id.Exit)
        {
            finish();
            Toast.makeText(this, "Exit",Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }
}