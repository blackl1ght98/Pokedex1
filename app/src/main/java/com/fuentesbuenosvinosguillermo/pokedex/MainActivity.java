
package com.fuentesbuenosvinosguillermo.pokedex;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionTabs.TabAdapter;
import com.fuentesbuenosvinosguillermo.pokedex.Fragments.Ajustes;
import com.fuentesbuenosvinosguillermo.pokedex.Fragments.Pokedex;
import com.fuentesbuenosvinosguillermo.pokedex.Fragments.DetallesPokemonCapturado;
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
        // Agregar el fragmento de prueba DetallesPokemonCapturado de manera temporal
        viewPager.postDelayed(() -> {
            // Agregar fragmento de prueba al ViewPager2
            tabAdapter.addFragment(new DetallesPokemonCapturado(), "Fragmento de prueba");

            // Navegar al nuevo fragmento (índice será el último, es decir, el fragmento de prueba)
            viewPager.setCurrentItem(tabAdapter.getItemCount() - 1, false);

            // Después de un pequeño retardo, redirigir al fragmento Pokedex
            viewPager.postDelayed(() -> {
                // Volver al primer fragmento: Pokedex
                viewPager.setCurrentItem(0, true); // Cambia a Pokedex
            }, 1); // 2 segundos de espera antes de redirigir a Pokedex

        }, 1); // Retardo para simular el tiempo de carga o acción previa


    }
    public void redirectToFragment(int position) {
        // Cambiar la página en el ViewPager2
        viewPager.setCurrentItem(position, true);

        // Mostrar el ViewPager2 si es necesario
        viewPager.setVisibility(View.VISIBLE);
    }

}
