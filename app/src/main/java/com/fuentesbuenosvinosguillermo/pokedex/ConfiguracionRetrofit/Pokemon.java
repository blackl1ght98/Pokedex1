package com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Pokemon {
    /**
     * Esta clase representa la estructura de un Pokémon obtenida desde la API en formato JSON.
     * Se encarga de mapear los datos de la API a una clase Java utilizando la anotación `@SerializedName`
     * de Gson para facilitar la conversión automática entre JSON y objetos de Java.
     *
     * - Cada propiedad de la clase corresponde a un campo del JSON devuelto por la API.
     * - Si un campo del JSON contiene un objeto o una lista, se define como una clase anidada dentro de esta clase.
     * - La conversión de JSON a objeto Java la maneja Retrofit junto con Gson automáticamente.
     *
     * La clase `Pokemon` incluye:
     * - Información básica del Pokémon como su nombre, peso, altura y experiencia base.
     * - Imágenes del Pokémon en diferentes vistas (frontal, trasera, normal y shiny).
     * - Tipos a los que pertenece el Pokémon (agua, fuego, planta, etc.).
     *
     * Además, se incluye una propiedad `firestoreId` para la integración con Firebase Firestore.
     *
     * Métodos:
     * - Getters y Setters para cada atributo, permitiendo acceder y modificar los valores.
     * - Un método `toString()` para representar el objeto de manera legible.
     *
     * Clases anidadas:
     * - `Sprites`: Contiene las URLs de las imágenes del Pokémon.
     * - `TypeSlot`: Representa la relación entre el Pokémon y su tipo, incluyendo la posición del tipo.
     * - `TypeDetail`: Almacena el nombre y la URL del tipo de Pokémon.
     */
    @SerializedName("order")
    private int order;
    @SerializedName("name")
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
    private String firestoreId;
    public void setFirestoreId(String firestoreId) {
        this.firestoreId = firestoreId;
    }


    // Getters
    public int getorderPokedex() {
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
