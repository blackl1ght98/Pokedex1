package com.fuentesbuenosvinosguillermo.pokedex.RecyclerViewCapturados;

import androidx.recyclerview.widget.RecyclerView;

import com.fuentesbuenosvinosguillermo.pokedex.databinding.PokedexCardviewBinding;
import com.fuentesbuenosvinosguillermo.pokedex.databinding.PokemonCapturadosCardviewBinding;

public class ViewHolderCapturados  extends RecyclerView.ViewHolder{
    private PokemonCapturadosCardviewBinding binding;
    public ViewHolderCapturados(PokemonCapturadosCardviewBinding binding){
        super(binding.getRoot());
        this.binding=binding;
    }
}
