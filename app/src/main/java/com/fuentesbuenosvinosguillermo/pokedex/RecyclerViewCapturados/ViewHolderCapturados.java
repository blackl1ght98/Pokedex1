package com.fuentesbuenosvinosguillermo.pokedex.RecyclerViewCapturados;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit.Pokemon;
import com.fuentesbuenosvinosguillermo.pokedex.databinding.PokemonCapturadosCardviewBinding;
/**
 * Clase que tambien es parte de la configuración del recyclerview
 * */
public class ViewHolderCapturados extends RecyclerView.ViewHolder {
    private final PokemonCapturadosCardviewBinding binding;

    // Constructor
    public ViewHolderCapturados(PokemonCapturadosCardviewBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    /**
     * Metodo que vincula los datos al layout
     * */
    public void bind(Pokemon pokemon) {
        // Establecer el nombre del Pokémon
        binding.nombrepokemon.setText(pokemon.getName());
        //Mover a un fragment de detalles
        StringBuilder tipos = new StringBuilder();
        for (Pokemon.TypeSlot typeSlot : pokemon.getTypes()) {
            if (typeSlot.getType() != null && typeSlot.getType().getName() != null) {
                tipos.append(typeSlot.getType().getName()).append(", ");
            }
        }
        // Eliminar la última coma y espacio extra
        if (tipos.length() > 0) {
            tipos.setLength(tipos.length() - 2);
        }
       binding.tipopokemon.setText(tipos.toString());
        // Cargar la imagen del Pokémon (usando Glide)
        Glide.with(binding.getRoot().getContext())
                .load(pokemon.getSprites().getFrontDefault()) // URL de la imagen
                .into(binding.imagepokemon);
    }
}
