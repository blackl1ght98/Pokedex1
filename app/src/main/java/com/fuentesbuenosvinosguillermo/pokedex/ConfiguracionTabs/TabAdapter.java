package com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionTabs;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class TabAdapter extends FragmentStateAdapter {
    /**
     * Esta clase es un adaptador personalizado para gestionar la navegación entre los diferentes fragmentos dentro de un `ViewPager2`.
     * Se encarga de almacenar y proporcionar los fragmentos y sus títulos correspondientes para cada página del `ViewPager2`.
     *
     * El adaptador mantiene dos listas:
     * - `fragmentList`: Contiene los fragmentos que se mostrarán en cada pestaña (tab).
     * - `fragmentTitleList`: Contiene los títulos de cada pestaña, los cuales pueden ser mostrados en el `TabLayout` o utilizados para otros fines.
     *
     * Los métodos claves de esta clase permiten:
     * - Agregar nuevos fragmentos y sus títulos asociados a las listas.
     * - Crear y proporcionar los fragmentos correspondientes a la posición seleccionada.
     * - Obtener el número total de fragmentos disponibles.
     * - Obtener el título del fragmento en una posición dada.
     *
     * Métodos principales:
     * - `addFragment(Fragment fragment, String title)`: Agrega un fragmento y su título a las listas internas.
     * - `createFragment(int position)`: Devuelve el fragmento correspondiente a una posición específica en el `ViewPager2`.
     * - `getItemCount()`: Devuelve la cantidad total de fragmentos disponibles.
     * - `getTitle(int position)`: Devuelve el título del fragmento en la posición especificada.
     */

    private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentTitleList = new ArrayList<>();

    public TabAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public void addFragment(Fragment fragment, String title) {
        fragmentList.add(fragment);
        fragmentTitleList.add(title);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }

    public String getTitle(int position) {
        return fragmentTitleList.get(position);
    }


}
