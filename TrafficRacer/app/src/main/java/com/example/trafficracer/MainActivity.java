package com.example.trafficracer;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    EditText etEmail, etPassword, etRegEmail, etRegPassword;
    Button btnLogin, btnRegister, btnSave, btnFa;
    Dialog d;
    FirebaseAuth myAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            myAuth = FirebaseAuth.getInstance();

            etEmail = findViewById(R.id.etEmail);
            etPassword = findViewById(R.id.etPassword);

            btnLogin = findViewById(R.id.btnLogin);
            btnRegister = findViewById(R.id.btnRegister);
            btnFa = findViewById(R.id.btnFa);

            btnLogin.setOnClickListener(this);
            btnRegister.setOnClickListener(this);
            btnFa.setOnClickListener(this);
            return insets;
        });
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

    @Override
    public void onClick(View v) {
        if (v == btnLogin) {
            myAuth.signInWithEmailAndPassword(etEmail.getText().toString(), etPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Sing in successful", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(MainActivity.this, HomePage.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Sing in failed", Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
        if (v == btnRegister) {
            d = new Dialog(this);
            d.setContentView(R.layout.register);
            d.setTitle("Registration");
            d.show();
            etRegEmail = d.findViewById(R.id.etRegEmail);
            etRegPassword = d.findViewById(R.id.etRegPassword);
            btnSave = d.findViewById(R.id.btnSave);
            btnSave.setOnClickListener(this);
            d.show();

        }
        if (v == btnSave) {
            d.dismiss();
            myAuth.createUserWithEmailAndPassword(etRegEmail.getText().toString(), etRegPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Registration successful", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Registration failed", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        if (v == btnFa) {
            Intent intent = new Intent(MainActivity.this, HomePage.class);
            startActivity(intent);
        }
    }
}