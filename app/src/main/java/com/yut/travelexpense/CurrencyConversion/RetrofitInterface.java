package com.yut.travelexpense.CurrencyConversion;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RetrofitInterface {
    @GET("v6/bbcde17e1661a4b419a07f4f/latest/{homeCurrency}")
    Call<JsonObject> getExchangeRate(@Path("homeCurrency") String homeCurrency);
}