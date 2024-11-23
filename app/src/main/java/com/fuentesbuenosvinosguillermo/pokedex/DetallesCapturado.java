package com.fuentesbuenosvinosguillermo.pokedex;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.bumptech.glide.Glide;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.Pokemon;
import com.fuentesbuenosvinosguillermo.pokedex.LogicaCapturaCompartida.SharedViewModel;
import com.fuentesbuenosvinosguillermo.pokedex.databinding.ActivityDetallesCapturadoBinding;

import java.util.ArrayList;
import java.util.List;

public class DetallesCapturado extends AppCompatActivity {
    private ActivityDetallesCapturadoBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityDetallesCapturadoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        // Recuperar el nombre del Pokémon desde el Intent
        String pokemonName = getIntent().getStringExtra("pokemonName");
        int pokemonPeso = getIntent().getIntExtra("pokemonPeso", 0);
        int pokemonIndice = getIntent().getIntExtra("pokemonIndice", 0);
        int pokemonAltura = getIntent().getIntExtra("pokemonAltura", 0);
        String pokemonImagenUrl = getIntent().getStringExtra("imagenPokemon");

        // Mostrar el nombre del Pokémon en el TextView usando binding
        binding.nombreDetallePokemon.setText(pokemonName);
        binding.pesoPesoPokemon.setText(String.valueOf(pokemonPeso));
        binding.ordenDetallePokedex.setText(String.valueOf(pokemonIndice));
        binding.alturaDetalleKemon.setText(String.valueOf(pokemonAltura));
        Glide.with(this)
                .load(pokemonImagenUrl)  // URL de la imagen
                .into(binding.imagepokemon);

     // binding.eliminarPokemon.setOnClickListener(v -> eliminarPokemon(pokemonName));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }





}

