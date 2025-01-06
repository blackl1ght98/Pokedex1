package com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConfiguracionRetrofit {
    /**
     * Esta clase contiene la configuracion de la libreria retrofit y la conexion con la api de pokeapi
     *
     * */
    private static final String BASE_URL="https://pokeapi.co/api/v2/";
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance(){
        if(retrofit==null){
            //Inicializa retrofit para hacer la peticion
            retrofit= new Retrofit.Builder()
                    //Recibe la url
                    .baseUrl(BASE_URL)
                    //Se hace uso de la libreria Gson para la conversion de json a objeto java
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return  retrofit;

    }
}
