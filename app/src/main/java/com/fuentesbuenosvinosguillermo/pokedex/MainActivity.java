
package com.fuentesbuenosvinosguillermo.pokedex;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.window.SplashScreen;
import android.window.SplashScreenView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionTabs.TabAdapter;
import com.fuentesbuenosvinosguillermo.pokedex.Fragments.Ajustes;
import com.fuentesbuenosvinosguillermo.pokedex.Fragments.Pokedex;

import com.fuentesbuenosvinosguillermo.pokedex.Fragments.pokemonCapturados;
import com.fuentesbuenosvinosguillermo.pokedex.LogicaCapturaCompartida.SharedViewModel;
import com.fuentesbuenosvinosguillermo.pokedex.databinding.ActivityMainBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    private TabAdapter tabAdapter;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // Uso del binding para llamar a la actividad principal
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        SharedViewModel sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

         // Observar los datos de los Pokémon capturados
        sharedViewModel.getCapturedPokemons().observe(MainActivity.this, pokemons -> {
            if (pokemons != null && !pokemons.isEmpty()) {
                Log.d("Activity", "Pokémon observados: " + pokemons.toString());

            } else {
                Log.d("Activity", "No hay Pokémon capturados.");
            }
        });
        // Inicializar ViewPager2 y TabLayout
        viewPager = binding.viewPager;
        tabLayout = binding.tabLayout;

        // Configurar el adaptador
        tabAdapter = new TabAdapter(this);
        tabAdapter.addFragment(new Pokedex(), "Pokedex");
        tabAdapter.addFragment(new pokemonCapturados(), "Pokemon capturados");
        tabAdapter.addFragment(new Ajustes(), "Ajustes");

        viewPager.setAdapter(tabAdapter);

        // Vincular TabLayout y ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabAdapter.getTitle(position))
        ).attach();


    }
    public void redirectToFragment(int position) {
        if (position >= 0 && position < tabAdapter.getItemCount()) {
            viewPager.setUserInputEnabled(false); // Deshabilitar entrada temporal
            viewPager.setCurrentItem(position, true); // Navegar al fragmento
            viewPager.postDelayed(() -> viewPager.setUserInputEnabled(true), 500); // Rehabilitar entrada
            viewPager.setVisibility(View.VISIBLE);
        } else {
            Log.e("Redirect", "Índice de fragmento fuera de límites: " + position);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // Cuando el usuario regresa al fragmento anterior, mostrar el ViewPager2 nuevamente
        binding.viewPager.setVisibility(View.VISIBLE);
    }


}
