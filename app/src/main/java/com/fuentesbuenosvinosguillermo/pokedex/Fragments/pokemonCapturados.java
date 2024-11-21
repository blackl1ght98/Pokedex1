package com.fuentesbuenosvinosguillermo.pokedex.Fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fuentesbuenosvinosguillermo.pokedex.R;
import com.fuentesbuenosvinosguillermo.pokedex.databinding.FragmentPokemonCapturadosBinding;


public class pokemonCapturados extends Fragment {



private FragmentPokemonCapturadosBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding= FragmentPokemonCapturadosBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }
    @Override
    public void onStart() {
        super.onStart();
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getContext().getString(R.string.pokemon_capturados));

        }
    }
}