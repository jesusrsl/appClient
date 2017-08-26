package com.example.jesus.appcliente.interfaces;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
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
    private long fecha;
    private TabLayout tabLayout;
    private ViewPager viewPager;
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
        this.fecha = parametros.getLong("fecha");   //fecha actual

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = (ViewPager) inflatedView.findViewById(R.id.viewpager);
        viewPager.setAdapter(new PagerAdapter(getChildFragmentManager(), getActivity()));

        // Give the TabLayout the ViewPager
        tabLayout = (TabLayout) inflatedView.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

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
        final int PAGE_COUNT = 2;
        private String tabTitles[] = new String[] { "Disposición", "Listado"};
        private Context context;

        public PagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public Fragment getItem(int position) {

            Intent intent = getActivity().getIntent();
            intent.putExtra("idAsignatura", idAsignatura);
            intent.putExtra("fecha", fecha);
            switch (position) {
                case 0:
                    DetalleAsignaturaFragment tab1 = new DetalleAsignaturaFragment();
                    tab1.setArguments(intent.getExtras());
                    return tab1;
                case 1:
                    DetalleAsignaturaFragment tab2 = new DetalleAsignaturaFragment();
                    tab2.setArguments(intent.getExtras());
                    return tab2;
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
