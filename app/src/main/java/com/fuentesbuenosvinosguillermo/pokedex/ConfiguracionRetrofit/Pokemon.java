package com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Pokemon {
//Aqui es donde transformamos manualmente el json en una clase java
    private int order;
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
    @SerializedName("types")
    private List<TypeSlot> types;
    // Método getter para la lista de tipos
    public List<TypeSlot> getTypes() {
        return types;
    }
    //La id que tiene el pokemon
    public int orderPokedex() {
        return order;
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
    // Clase interna para manejar el arreglo "types"
    public static class TypeSlot {
        private int slot;

        @SerializedName("type")
        private TypeDetail type;

        public int getSlot() {
            return slot;
        }

        public TypeDetail getType() {
            return type;
        }
    }

    // Clase interna para manejar el objeto "type" dentro de cada elemento del arreglo "types"
    public static class TypeDetail {
        private String name;
        private String url;

        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }
    }
    @Override
    public String toString() {
        return "Pokemon{name='" + name + "', weight=" + weight + ", height=" + height + "}";
    }

}
