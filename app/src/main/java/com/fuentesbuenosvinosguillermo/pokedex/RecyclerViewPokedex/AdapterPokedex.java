package com.fuentesbuenosvinosguillermo.pokedex.RecyclerViewPokedex;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.Pokemon;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.PokemonResult;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.PokeApiService;

import com.fuentesbuenosvinosguillermo.pokedex.LogicaCapturaCompartida.SharedViewModel;

import com.fuentesbuenosvinosguillermo.pokedex.MainActivity;
import com.fuentesbuenosvinosguillermo.pokedex.databinding.PokedexCardviewBinding;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterPokedex extends RecyclerView.Adapter<ViewHolderPokedex> {
    private final List<PokemonResult> pokemonList;
    private final PokeApiService pokeApiService;
    private final Context context;
    private final FragmentActivity activity;
    private PokedexCardviewBinding binding;

    // Constructor
    public AdapterPokedex(List<PokemonResult> pokemonList, PokeApiService pokeApiService, Context context, FragmentActivity activity) {
        this.pokemonList = pokemonList;
        this.pokeApiService = pokeApiService;
        this.context = context;
        this.activity = activity;

    }

    @NonNull
    @Override
    public ViewHolderPokedex onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflar el layout utilizando View Binding
         binding = PokedexCardviewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolderPokedex(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderPokedex holder, int position) {
        PokemonResult pokemonResult = pokemonList.get(position);
        SharedViewModel sharedViewModel = new ViewModelProvider(activity).get(SharedViewModel.class);



        sharedViewModel.fetchPokemons(pokemonResult.getName());

        sharedViewModel.getSelectedPokemon().observe(activity, pokemon -> {
            if (pokemon != null && pokemon.getName().equals(pokemonResult.getName())) {
                holder.bind(pokemon); // Método para vincular datos al ViewHolder
                holder.itemView.setOnClickListener(v -> handlePokemonCapture(pokemon));
            }
        });
    }
    private void handlePokemonCapture(Pokemon pokemon) {
        SharedViewModel viewModel = new ViewModelProvider(activity).get(SharedViewModel.class);
        viewModel.capturePokemon(pokemon, context);
    }


    @Override
    public int getItemCount() {
        return pokemonList.size();
    }
}
