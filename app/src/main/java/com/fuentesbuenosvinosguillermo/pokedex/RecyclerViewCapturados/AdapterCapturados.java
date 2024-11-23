package com.fuentesbuenosvinosguillermo.pokedex.RecyclerViewCapturados;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.Pokemon;
import com.fuentesbuenosvinosguillermo.pokedex.DetallesCapturado;
import com.fuentesbuenosvinosguillermo.pokedex.databinding.PokemonCapturadosCardviewBinding;

import java.util.List;

public class AdapterCapturados extends RecyclerView.Adapter<ViewHolderCapturados> {
    private final List<Pokemon> capturadosList;
    private FragmentActivity activity;

    public AdapterCapturados(List<Pokemon> capturadosList) {
        this.capturadosList = capturadosList;

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
            Intent intent = new Intent(holder.itemView.getContext(), DetallesCapturado.class);
            intent.putExtra("pokemonName", pokemon.getName());
            intent.putExtra("pokemonPeso", pokemon.getWeight());
            intent.putExtra("pokemonIndice",pokemon.orderPokedex());
            intent.putExtra("pokemonAltura",pokemon.getHeight());
            intent.putExtra("imagenPokemon",pokemon.getSprites().getFrontDefault());
            holder.itemView.getContext().startActivity(intent);


        });

    }
    @Override
    public int getItemCount() {
        return capturadosList.size();
    }


}
