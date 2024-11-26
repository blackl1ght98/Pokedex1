package com.fuentesbuenosvinosguillermo.pokedex.RecyclerViewPokedex;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.Pokemon;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.PokemonResult;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.PokeApiService;
import com.fuentesbuenosvinosguillermo.pokedex.LogicaCapturaCompartida.CapturedPokemonManager;
import com.fuentesbuenosvinosguillermo.pokedex.LogicaCapturaCompartida.SharedViewModel;
import com.fuentesbuenosvinosguillermo.pokedex.databinding.PokedexCardviewBinding;

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
        // Obtener el Pokémon de la lista
        PokemonResult pokemonResult = pokemonList.get(position);

        // Realizar la llamada a la API para obtener los detalles del Pokémon
        pokeApiService.getPokemonDetails(pokemonResult.getName()).enqueue(new Callback<Pokemon>() {
            @Override
            public void onResponse(Call<Pokemon> call, Response<Pokemon> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Pokemon pokemon = response.body();
                    // Vincular los datos al ViewHolder
                    holder.bind(pokemon);

                    // Configurar el clic en el ítem
                    holder.itemView.setOnClickListener(v -> handlePokemonCapture(pokemon));
                }
            }

            @Override
            public void onFailure(Call<Pokemon> call, Throwable t) {
                Toast.makeText(context, "Error al cargar detalles del Pokémon", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handlePokemonCapture(Pokemon pokemon) {
        if (!CapturedPokemonManager.isCaptured(pokemon)) {
            // Capturar el Pokémon y notificar al ViewModel
            CapturedPokemonManager.addCapturedPokemon(pokemon);
        // FragmentActivity se utiliza para obtener el SharedViewModel asociado a la actividad actual,
        // lo que permite compartir datos entre la actividad y sus fragments.
            SharedViewModel viewModel = new ViewModelProvider(activity).get(SharedViewModel.class);
            viewModel.addCapturedPokemon(pokemon);
            viewModel.getCapturedPokemons();
            new AlertDialog.Builder(context)
                    .setTitle("¡Captura exitosa!")
                    .setMessage(pokemon.getName() + " ha sido capturado.")
                    .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
                    .show();
        } else {
            new AlertDialog.Builder(context)
                    .setTitle("Pokémon ya Capturado")
                    .setMessage(pokemon.getName() + " ya está capturado.")
                    .setPositiveButton("Aceptar", (dialog, which) -> dialog.dismiss())
                    .show();
        }
    }

    @Override
    public int getItemCount() {
        return pokemonList.size();
    }
}
