package com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit;

import com.google.gson.annotations.SerializedName;

public class Pokemon {

    private int id;
    public String name;

    @SerializedName("base_experience")
    private int baseExperience;

    @SerializedName("height")
    private int height;

    @SerializedName("weight")
    private int weight;

    @SerializedName("sprites")
    private Sprites sprites;  // Agregado para manejar las imágenes

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getBaseExperience() {
        return baseExperience;
    }

    public int getHeight() {
        return height;
    }

    public int getWeight() {
        return weight;
    }

    public Sprites getSprites() {
        return sprites;
    }

    public static class Sprites {
        @SerializedName("front_default")
        private String frontDefault;
        @SerializedName("back_default")
        private String backDefault;
        @SerializedName("front_shiny")
        private String frontShiny;
        @SerializedName("back_shiny")
        private String backShiny;

        public String getFrontDefault() {
            return frontDefault;
        }

        public String getBackDefault() {
            return backDefault;
        }

        public String getFrontShiny() {
            return frontShiny;
        }

        public String getBackShiny() {
            return backShiny;
        }
    }
}
