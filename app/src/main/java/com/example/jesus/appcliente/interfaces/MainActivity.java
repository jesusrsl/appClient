package com.example.jesus.appcliente.interfaces;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.jesus.appcliente.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private FragmentManager fragmentManager;

    //visibilidad de los fragments
    protected static boolean isMainShown = false;
    protected static boolean isOtherFragmentShown = false;
    protected NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            //Create global configuration and initialize ImageLoader with this config --> se inicializa la primera vez
            ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
            ImageLoader.getInstance().init(config);


            //Fragment por defecto
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            DiarioClaseMainFragment mainFragment = new DiarioClaseMainFragment();
            transaction.replace(R.id.container, mainFragment);
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.addToBackStack(null).commit();
            getSupportFragmentManager().executePendingTransactions();
        }




        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();

                FragmentTransaction transaction = fragmentManager.beginTransaction();

                switch (menuItem.getItemId()) {
                    case R.id.navigation_item_profesorado:
                        /*Intent intent = new Intent(MainActivity.this, ListarProfesores.class);
                        startActivity(intent);*/
                        ListarProfesoresFragment fragmentProfesores = new ListarProfesoresFragment();
                        transaction.replace(R.id.container, fragmentProfesores);
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        transaction.addToBackStack(null).commit();
                        getSupportFragmentManager().executePendingTransactions();
                        return true;
                    case R.id.navigation_item_grupos:
                        ListarGruposFragment fragmentGrupos = new ListarGruposFragment();
                        transaction.replace(R.id.container, fragmentGrupos);
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        transaction.addToBackStack(null).commit();
                        getSupportFragmentManager().executePendingTransactions();
                        return true;
                    case R.id.navigation_item_asignaturas:
                        ListarAsignaturasFragment fragmentAsignaturas = new ListarAsignaturasFragment();
                        transaction.replace(R.id.container, fragmentAsignaturas);
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        transaction.addToBackStack(null).commit();
                        getSupportFragmentManager().executePendingTransactions();
                        return true;
                    case R.id.navigation_item_diario_clase:
                        DiarioClaseMainFragment fragmentMain = new DiarioClaseMainFragment();
                        transaction.replace(R.id.container, fragmentMain);
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        transaction.addToBackStack(null).commit();
                        getSupportFragmentManager().executePendingTransactions();
                        return true;
                    case R.id.navigation_item_tutoria:
                        TabLayoutTutoriaFragment fragmentTabLayoutTutoria = new TabLayoutTutoriaFragment();
                        transaction.replace(R.id.container, fragmentTabLayoutTutoria);
                        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        transaction.addToBackStack(null).commit();
                        getSupportFragmentManager().executePendingTransactions();
                        return true;
                    case R.id.navigation_item_acerca_de:
                        acercade();
                        return true;
                    case R.id.navigation_item_salir:
                        salir();

                }

                return true;
            }
        });

        /*FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(findViewById(R.id.coordinator), "I'm a Snackbar", Snackbar.LENGTH_LONG).setAction("Action", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "Snackbar Action", Toast.LENGTH_LONG).show();
                    }
                }).show();
            }
        });*/


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

        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            /*case R.id.action_settings:
                return true;*/
        }

        return super.onOptionsItemSelected(item);

    }


    @Override
    public void onBackPressed() {
        if(isMainShown){
            // We're in the MAIN Fragment.
            salir();
        }
        else{
            if (getFragmentManager().getBackStackEntryCount() > 0) {
                getFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }
    }


    public void acercade(){
        new AlertDialog.Builder(this)
                .setTitle("aNota")
                .setMessage(getResources().getString(R.string.acercade))
                .show();
    }

    public void salir(){
        new AlertDialog.Builder(this)
                .setTitle("Salir de aNota")
                .setMessage("¿Está seguro de que desea salir de la aplicación?")
                .setPositiveButton("Sí",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                finish();
                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                            }
                        }).show();
    }


}
