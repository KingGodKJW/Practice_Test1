package com.example.practice_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences pref = getSharedPreferences("isFirst", Activity.MODE_PRIVATE);
        boolean First = pref.getBoolean("isFirst",false);
       int state = pref.getInt("First",-1);
        if(First == false){
            Log.d("First Launch?","true");
            Log.d("www","www");
            Log.d("ttt","ttt");
          //  SharedPreferences.Editor editor = pref.edit();
           // editor.commit();
           // SharedPreferences firstPw = getSharedPreferences("firstPw", MODE_PRIVATE);
          //  SharedPreferences.Editor edt = firstPw.edit();
          //  edt.putString("firstPw","123456");
         //   edt.commit();
            Intent intent = new Intent(getApplicationContext(),AuthFingerprintActivity.class);
            startActivity(intent);


        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater  = getMenuInflater();
        inflater.inflate(R.menu.menu_sample,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {

            case R.id.setting_applock:
                Toast.makeText(this,"setting",Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}