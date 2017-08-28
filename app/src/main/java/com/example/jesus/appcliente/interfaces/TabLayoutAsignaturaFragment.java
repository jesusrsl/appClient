package com.example.jesus.appcliente.interfaces;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jesus.appcliente.R;

/**
 * Created by jesus on 23/08/17.
 */

public class TabLayoutAsignaturaFragment extends Fragment {

    private Bundle parametros;
    private int idAsignatura;
    private String nombreAsignatura, nombreGrupo;
    private long fecha;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PagerAdapter adaptador;
    public static String POSITION = "POSITION";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //visibilidad de los fragments
        MainActivity.isMainShown = false;
        MainActivity.isOtherFragmentShown = true;

        // Inflate the layout for this fragment
        View inflatedView = inflater.inflate(R.layout.fragment_tablayout, container, false);
        parametros = getActivity().getIntent().getExtras();
        this.idAsignatura = parametros.getInt("idAsignatura");
        this.nombreAsignatura = parametros.getString("nombreAsignatura");
        this.nombreGrupo = parametros.getString("nombreGrupo");
        this.fecha = parametros.getLong("fecha");   //fecha actual

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = (ViewPager) inflatedView.findViewById(R.id.viewpager);
        adaptador = new PagerAdapter(getChildFragmentManager(), getActivity());
        viewPager.setAdapter(adaptador);

        // Give the TabLayout the ViewPager
        tabLayout = (TabLayout) inflatedView.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);
                        viewPager.setCurrentItem(tab.getPosition());
                        if (tab.getPosition() == 0) {

                            LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getActivity());
                            Intent i = new Intent("TAG_REFRESH_0");
                            lbm.sendBroadcast(i);
                        }
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });

        return inflatedView;
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);
        if(state!=null){
            viewPager.setCurrentItem(state.getInt(POSITION));
        }


    }



    public class PagerAdapter extends FragmentStatePagerAdapter {
        final int PAGE_COUNT = 3;
        private String tabTitles[] = new String[] { "Disposición", "Listado", "Anotaciones"};
        private Context context;

        public PagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {

            Intent intent = getActivity().getIntent();
            intent.putExtra("idAsignatura", idAsignatura);
            intent.putExtra("nombreAsignatura", nombreAsignatura);
            intent.putExtra("nombreGrupo", nombreGrupo);
            intent.putExtra("fecha", fecha);
            switch (position) {
                case 0:
                    DetalleAsignaturaFragment tab1 = new DetalleAsignaturaFragment();
                    tab1.setArguments(intent.getExtras());
                    return tab1;
                case 1:
                    ListarProfesoresFragment tab2 = new ListarProfesoresFragment();
                    //tab2.setArguments(intent.getExtras());
                    return tab2;
                case 2:
                    VerAnotacionesFragment tab3 = new VerAnotacionesFragment();
                    tab3.setArguments(intent.getExtras());
                    return tab3;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION, tabLayout.getSelectedTabPosition());
    }

}
