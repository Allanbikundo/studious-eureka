package com.scalum.starter.domain.evolution;

import com.scalum.starter.domain.evolution.dto.ConnectInstanceResponse;
import com.scalum.starter.domain.evolution.dto.CreateInstanceRequest;
import com.scalum.starter.domain.evolution.dto.CreateInstanceResponse;
import com.scalum.starter.domain.evolution.dto.EvolutionTextRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface EvolutionApi {

    @POST("message/sendText/{instance}")
    Call<Object> sendText(
        @Path("instance") String instance,
        @Header("apikey") String apiKey,
        @Body EvolutionTextRequest request
    );

    @POST("instance/create")
    Call<CreateInstanceResponse> createInstance(
        @Header("apikey") String apiKey,
        @Body CreateInstanceRequest request
    );

    @GET("instance/connect/{instance}")
    Call<ConnectInstanceResponse> connectInstance(
        @Path("instance") String instance,
        @Header("apikey") String apiKey
    );
}
