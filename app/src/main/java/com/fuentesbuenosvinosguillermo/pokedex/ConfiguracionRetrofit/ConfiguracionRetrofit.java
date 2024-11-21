package com.fuentesbuenosvinosguillermo.pokedex.ConfiguracionRetrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConfiguracionRetrofit {
    private static final String BASE_URL="https://pokeapi.co/api/v2/";
    private static Retrofit retrofit;
    //Configuración de retrofit
    public static Retrofit getRetrofitInstance(){
        if(retrofit==null){
            //Inicializa retrofit para hacer la peticion
            retrofit= new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return  retrofit;

    }
}
