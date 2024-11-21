
package com.fuentesbuenosvinosguillermo.pokedex;
import android.os.Bundle;
import android.view.Menu;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.widget.ViewPager2;
import com.fuentesbuenosvinosguillermo.pokedex.Fragments.Ajustes;
import com.fuentesbuenosvinosguillermo.pokedex.Fragments.Pokedex;
import com.fuentesbuenosvinosguillermo.pokedex.Fragments.pokemonCapturados;
import com.fuentesbuenosvinosguillermo.pokedex.databinding.ActivityMainBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private NavController navController;
    private TabAdapter tabAdapter;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    // Configuración de la barra de acción
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // Uso del binding para llamar a la actividad principal
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Toolbar toolbar = binding.toolbar;
        setSupportActionBar(toolbar);

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

        // Configuración del contenedor de navegación entre fragmentos
      // navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        // Configuración de la barra de acción
        appBarConfiguration = new AppBarConfiguration.Builder(R.id.pokedex).build();
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


    }
/**
 *
 *
 *
 * */


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
}
