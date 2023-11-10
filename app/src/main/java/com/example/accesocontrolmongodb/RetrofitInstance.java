package com.example.accesocontrolmongodb;
// import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;


// This class can be acceded by other activities that need the same functions (retrofit client AND ApiServices AND ApiResponses)

public class RetrofitInstance { // client
    public static Retrofit retrofit;
    public static final String BASE_URL = "http://192.168.0.11:3000/";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;

        //public static ApiService getApiService() {
          //  return retrofit.create(ApiService.class);
        //}
    }

    public interface ApiService {
        @POST("api/auth/signin")
        Call<ApiResponse> signIn(@Body SignInActivity.SignInRequest request);

        @POST("api/auth/uploadform")
        Call<ApiResponse> uploadForm(@Body PlanillasActivity.PlanillaData planillaData);

        @POST("api/auth/lockreport")
        Call<ApiResponse> uploadCommand(@Body CommandActivity.CommandRequest commandRequest);

        @POST("api/auth/register")
        Call<ApiResponse> registerUser(@Body SignInActivity.RegisterRequest request);

    }

    public class ApiResponse {
        private boolean success;
        private String message;

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }

}