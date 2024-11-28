package com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Objects;

public class Pokemon {
    private int order;
    private String name;

    @SerializedName("base_experience")
    private int baseExperience;

    @SerializedName("height")
    private int height;

    @SerializedName("weight")
    private int weight;

    @SerializedName("sprites")
    private Sprites sprites;

    @SerializedName("types")
    private List<TypeSlot> types;

    // Getters
    public int orderPokedex() {
        return order;
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

    public List<TypeSlot> getTypes() {
        return types;
    }

    // Setters
    public void setOrder(int order) {
        this.order = order;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBaseExperience(int baseExperience) {
        this.baseExperience = baseExperience;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public void setSprites(Sprites sprites) {
        this.sprites = sprites;
    }

    public void setTypes(List<TypeSlot> types) {
        this.types = types;
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

        // Getters
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

        // Setters
        public void setFrontDefault(String frontDefault) {
            this.frontDefault = frontDefault;
        }

        public void setBackDefault(String backDefault) {
            this.backDefault = backDefault;
        }

        public void setFrontShiny(String frontShiny) {
            this.frontShiny = frontShiny;
        }

        public void setBackShiny(String backShiny) {
            this.backShiny = backShiny;
        }
    }

    public static class TypeSlot {
        private int slot;

        @SerializedName("type")
        private TypeDetail type;

        // Getters
        public int getSlot() {
            return slot;
        }

        public TypeDetail getType() {
            return type;
        }

        // Setters
        public void setSlot(int slot) {
            this.slot = slot;
        }

        public void setType(TypeDetail type) {
            this.type = type;
        }
    }

    public static class TypeDetail {
        private String name;
        private String url;

        // Getters
        public String getName() {
            return name;
        }

        public String getUrl() {
            return url;
        }

        // Setters
        public void setName(String name) {
            this.name = name;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    @Override
    public String toString() {
        return "Pokemon{name='" + name + "', weight=" + weight + ", height=" + height + "}";
    }

}
