package com.fuentesbuenosvinosguillermo.pokedex.RecyclerViewCapturados;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.Pokemon;
import com.fuentesbuenosvinosguillermo.pokedex.Fragments.DetallesPokemonCapturado;
import com.fuentesbuenosvinosguillermo.pokedex.MainActivity;
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

        // Vincular los datos del Pokémon al ViewHolder
        holder.bind(pokemon);
        holder.itemView.setOnClickListener(v->{
            Toast.makeText(holder.itemView.getContext(), "Clic en " + pokemon.getName(), Toast.LENGTH_SHORT).show();

            // Crear un Bundle para pasar los datos al fragmento
            Bundle bundle = new Bundle();
            bundle.putString("pokemonName", pokemon.getName());
            bundle.putDouble("pokemonPeso", pokemon.getWeight());
            bundle.putInt("pokemonIndice", pokemon.orderPokedex());
            bundle.putDouble("pokemonAltura", pokemon.getHeight());
            bundle.putString("imagenPokemon", pokemon.getSprites().getFrontDefault());
            // Asegurarse de que el contexto sea de tipo MainActivity
            if (holder.itemView.getContext() instanceof MainActivity) {
                MainActivity activity = (MainActivity) holder.itemView.getContext();
                DetallesPokemonCapturado detallesPokemonCapturadoFragment = new DetallesPokemonCapturado();
                detallesPokemonCapturadoFragment.setArguments(bundle);  // Pasar los datos al fragmento
                activity.redirectToFragment(3);  // Llamar al método que maneja la transacción
            }


        });

    }
    @Override
    public int getItemCount() {
        return capturadosList.size();
    }


}
