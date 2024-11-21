package com.fuentesbuenosvinosguillermo.pokedex.RecyclerViewCapturados;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.Pokemon;
import com.fuentesbuenosvinosguillermo.pokedex.databinding.PokemonCapturadosCardviewBinding;

import java.util.List;

public class AdapterCapturados extends RecyclerView.Adapter<ViewHolderCapturados> {
    private final List<Pokemon> capturadosList;

    // Constructor para recibir la lista de Pokémon capturados
    public AdapterCapturados(List<Pokemon> capturadosList) {
        this.capturadosList = capturadosList;
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
    }

    @Override
    public int getItemCount() {
        return capturadosList.size();
    }
}
