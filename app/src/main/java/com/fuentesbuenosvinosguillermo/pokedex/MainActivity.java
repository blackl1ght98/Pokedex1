
package com.fuentesbuenosvinosguillermo.pokedex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import android.os.Bundle;
import android.util.Log;
import android.view.View;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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

import java.util.Locale;


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
        //El motivo por el cual llamamos al metodo que recupera el idioma antes de que la vista se muestre es para que se aplique de forma correcta
        applySavedLanguage();
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
        tabAdapter.addFragment(new pokemonCapturados(), getString(R.string.pokemon_capturado));
        tabAdapter.addFragment(new Ajustes(), getString(R.string.ajustes));

        viewPager.setAdapter(tabAdapter);

        // Vincular TabLayout y ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabAdapter.getTitle(position))
        ).attach();
        // Vincular TabLayout y ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabAdapter.getTitle(position))
        ).attach();

        // Agregar el listener para detectar cambios de tab
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Restaurar al cambiar de tab
                onTabChanged(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // Cuando el usuario regresa al fragmento anterior, mostrar el ViewPager2 nuevamente
        binding.viewPager.setVisibility(View.VISIBLE);
    }
    //Metodo para recuperar el idioma guardado en shared preferences
    private void applySavedLanguage() {
        // Obtener las preferencias guardadas
        SharedPreferences prefs = getSharedPreferences("AppSettings", Context.MODE_PRIVATE);

        // Recuperar el idioma guardado (null si no existe)
        String savedLanguage = prefs.getString("language", null);

        // Solo aplicar cambios si existe un idioma guardado
        if (savedLanguage != null) {
            Locale locale = new Locale(savedLanguage);
            Locale.setDefault(locale);

            Resources resources = getResources();
            Configuration config = resources.getConfiguration();

            config.setLocale(locale);
            resources.updateConfiguration(config, resources.getDisplayMetrics());
        }
    }

    // Método que se llama al cambiar de tab
    private void onTabChanged(int position) {
        // Verificar si el fragmento de detalles está activo
        Fragment detallesFragment = getSupportFragmentManager().findFragmentByTag("DetallesFragment");
        if (detallesFragment != null) {
            // Eliminar el fragmento de detalles
            getSupportFragmentManager().beginTransaction()
                    .remove(detallesFragment)
                    .commit();
        }

        // Restaurar la visibilidad del ViewPager
        findViewById(R.id.viewPager).setVisibility(View.VISIBLE);
    }



}
