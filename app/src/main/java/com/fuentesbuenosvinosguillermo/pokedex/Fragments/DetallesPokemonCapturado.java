package com.fuentesbuenosvinosguillermo.pokedex.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.Pokemon;
import com.fuentesbuenosvinosguillermo.pokedex.LogicaCapturaCompartida.SharedViewModel;
import com.fuentesbuenosvinosguillermo.pokedex.databinding.FragmentPruebaBinding;


public class DetallesPokemonCapturado extends Fragment {
private FragmentPruebaBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      binding= FragmentPruebaBinding.inflate(inflater,container,false);
        // Obtener el SharedViewModel
        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Observar los datos de los Pokémon capturados
        sharedViewModel.getCapturedPokemons().observe(getViewLifecycleOwner(), pokemons -> {
            if (pokemons != null && !pokemons.isEmpty()) {
                Log.d("PruebaFragment", "Pokémon observados: " + pokemons.toString());

                // Si quieres mostrar el primer Pokémon de la lista
                Pokemon pokemon = pokemons.get(0);  // Obtener el primer Pokémon de la lista

                // Hacer el binding directo a los TextViews
                binding.nombreDetallePokemon.setText(pokemon.getName());
                binding.pesoPesoPokemon.setText(String.valueOf(pokemon.getWeight()));
                binding.ordenDetallePokedex.setText(String.valueOf(pokemon.orderPokedex()));
                binding.alturaDetalleKemon.setText(String.valueOf(pokemon.getHeight()));
                Glide.with(requireContext())
                        .load(pokemon.getSprites().getFrontDefault())  // URL de la imagen
                        .into(binding.imagepokemon);
            } else {
                // Si no hay Pokémon, mostrar mensaje o dejar los campos vacíos
                Log.d("PruebaFragment", "No hay Pokémon capturados.");
            }
        });

        return binding.getRoot();
    }
}