package com.example.jesus.appcliente.interfaces;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.jesus.appcliente.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true; /** true -> el menu ya esta visible */
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);


    }

    public void btn_buscarProfesor(View view){
        Intent intent = new Intent(MainActivity.this, BuscarProfesor.class);
        startActivity(intent);
    }

    public void btn_ProfesorFormulario(View view){
        Intent intent = new Intent(MainActivity.this, ProfesorFormulario.class);
        intent.putExtra("operacion", "insertar");
        startActivity(intent);
    }


}
