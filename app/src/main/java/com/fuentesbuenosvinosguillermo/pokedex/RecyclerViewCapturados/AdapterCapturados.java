package com.fuentesbuenosvinosguillermo.pokedex.RecyclerViewCapturados;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.Pokemon;
import com.fuentesbuenosvinosguillermo.pokedex.Fragments.DetallesPokemonCapturado;
import com.fuentesbuenosvinosguillermo.pokedex.Fragments.Pokedex;
import com.fuentesbuenosvinosguillermo.pokedex.Fragments.pokemonCapturados;
import com.fuentesbuenosvinosguillermo.pokedex.LogicaCapturaCompartida.SharedViewModel;
import com.fuentesbuenosvinosguillermo.pokedex.MainActivity;
import com.fuentesbuenosvinosguillermo.pokedex.R;
import com.fuentesbuenosvinosguillermo.pokedex.databinding.PokemonCapturadosCardviewBinding;

import java.util.List;

public class AdapterCapturados extends RecyclerView.Adapter<ViewHolderCapturados> {
    private final List<Pokemon> capturadosList;
    private MainActivity activity;

    public AdapterCapturados(List<Pokemon> capturadosList, MainActivity activity) {
        this.capturadosList = capturadosList;
        this.activity = activity;  // Guardamos la referencia a MainActivity
    }


    public void updateData(List<Pokemon> newPokemonList) {
        capturadosList.clear(); // Limpia la lista actual
        capturadosList.addAll(newPokemonList); // Agrega los nuevos datos
        notifyDataSetChanged(); // Notifica al RecyclerView que los datos han cambiado
    }

    @NonNull
    @Override
    public ViewHolderCapturados onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el layout utilizando View Binding
        PokemonCapturadosCardviewBinding binding = PokemonCapturadosCardviewBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ViewHolderCapturados(binding);
    }


@Override
public void onBindViewHolder(@NonNull ViewHolderCapturados holder, int position) {
    // Obtener el Pokémon de la lista
    Pokemon pokemon = capturadosList.get(position);

    SharedViewModel sharedViewModel = new ViewModelProvider(activity).get(SharedViewModel.class);

    // Vincular los datos del Pokémon al ViewHolder
    holder.bind(pokemon);

    // Mostrar un Toast al hacer clic en el item
//    holder.itemView.setOnClickListener(v -> {
//        // Mostrar un mensaje Toast con el nombre del Pokémon
//        Toast.makeText(holder.itemView.getContext(),
//                "Has seleccionado a: " + pokemon.getName(),
//                Toast.LENGTH_SHORT).show();
//        StringBuilder tipos = new StringBuilder();
//        for (Pokemon.TypeSlot typeSlot : pokemon.getTypes()) {
//            if (typeSlot.getType() != null && typeSlot.getType().getName() != null) {
//                tipos.append(typeSlot.getType().getName()).append(", ");
//            }
//        }
//
//        // Eliminar la última coma y espacio extra
//        if (tipos.length() > 0) {
//            tipos.setLength(tipos.length() - 2);
//        }
//        Bundle bundle = new Bundle();
//        bundle.putString("pokemonName", pokemon.getName());
//        bundle.putInt("pokemonPeso", pokemon.getWeight());
//        bundle.putInt("pokemonIndice", pokemon.orderPokedex());
//        bundle.putInt("pokemonAltura", pokemon.getHeight());
//        bundle.putString("imagenPokemon", pokemon.getSprites().getFrontDefault());
//        bundle.putString("pokemonTipos", tipos.toString());
//        DetallesPokemonCapturado detallesFragment =  DetallesPokemonCapturado.newInstance(bundle);
//        detallesFragment.setArguments(bundle);
//        if (activity != null) {
//            activity.getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.main_container, detallesFragment)
//                    .addToBackStack(null)
//
//                    .commit();
//            activity.findViewById(R.id.viewPager).setVisibility(View.GONE);
//        }
//    });
    holder.itemView.setOnClickListener(v -> {
        sharedViewModel.setSelectedPokemon(pokemon);

        DetallesPokemonCapturado detallesFragment = new DetallesPokemonCapturado();
        if (activity != null) {
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container, detallesFragment)
                    .addToBackStack(null)
                    .commit();
            activity.findViewById(R.id.viewPager).setVisibility(View.GONE);
        }
    });

}

    @Override
    public int getItemCount() {
        return capturadosList.size();
    }


}
