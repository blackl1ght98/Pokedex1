package com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit;

import com.google.gson.annotations.SerializedName;

public class Pokemon {
//Aqui es donde transformamos manualmente el json en una clase java
    private int id;
    private String name;

    @SerializedName("base_experience")
    private int baseExperience;

    @SerializedName("height")
    private int height;

    @SerializedName("weight")
    private int weight;
    // Agregado para manejar las imágenes
    @SerializedName("sprites")
    private Sprites sprites;

    //La id que tiene el pokemon
    public int getId() {
        return id;
    }
    //El nombre del pokemon
    public String getName() {
        return name;
    }
    //La experiencia del pokemon
    public int getBaseExperience() {
        return baseExperience;
    }
    //La altura del pokemon
    public int getHeight() {
        return height;
    }
    //El peso del pokemon
    public int getWeight() {
        return weight;
    }
//Caso especial de  la conversion a clase de un json cuando en el json hay un objeto que tiene varias propiedades esto se ve reflejado como una clase
    //y dicha clase tendra todas las propiedades que tenga el objeto
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
