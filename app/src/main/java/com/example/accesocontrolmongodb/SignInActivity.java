package com.example.accesocontrolmongodb;

import static android.content.ContentValues.TAG;

import static com.example.accesocontrolmongodb.SignInActivity.RetrofitClient.BASE_URL;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class SignInActivity extends AppCompatActivity {


    private EditText editTextName, editTextPassword;
    private Button buttonSignIn;

    // ids:
    //id/edit_name
    //edit_password
    //btn_sign_in


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        editTextName = findViewById(R.id.edit_name);
        editTextPassword = findViewById(R.id.edit_password);
        buttonSignIn = findViewById(R.id.btn_sign_in);

        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = editTextName.getText().toString();
                String password = editTextPassword.getText().toString();

                // Call API with email and password
                // Handle API response

                performSignIn(email, password);
            }
        });


    }


    // The Request is made and it is decided if it was successful or not.
    private void performSignIn(String email, String password) {
        ApiService apiService = RetrofitClient.getApiService();


        String apiUrl = BASE_URL + "api/auth/signin";
        // Toast.makeText(SignInActivity.this,"API URL: " + apiUrl, Toast.LENGTH_LONG).show(); //LLEGA ACA, ME TIRA LA IP
        Log.d(TAG, "API URL: " + apiUrl);
        Call<ApiResponse> call = apiService.signIn(new SignInRequest(email, password)); //aca se menciona que funcion se usa

        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                // The logic is the following: I send data from here, the server returns a response(res) number
                // (404, 500, etc) and here i identify which one it is depending on the function it is asociated with.

                int httpStatusCode = response.code();

                if (httpStatusCode == 200) {

                    // Sign-in was successful
                    ApiResponse apiResponse = response.body();
                    if (apiResponse != null && apiResponse.isSuccess()) { // Both factors are checked for added security.
                        // Handle the success scenario
                        Log.d(TAG, "Sign in successful");
                        Toast.makeText(SignInActivity.this, "Sign-in successful", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(SignInActivity.this, HomeMainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Handle the case where the server reports success as false
                        Log.d(TAG, "Sign in not successful");
                        Toast.makeText(SignInActivity.this, "Sign-in not successful", Toast.LENGTH_LONG).show();
                    }
                } else if (httpStatusCode == 404) {
                    // Handle 404 (Not Found) error for invalid credentials
                    Log.d(TAG, "Invalid credentials");
                    Toast.makeText(SignInActivity.this, "Invalid credentials", Toast.LENGTH_LONG).show();
                } else if (httpStatusCode == 500) {
                    // Handle 500 (Internal Server Error) or other server errors
                    Log.d(TAG, "Server error");
                    Toast.makeText(SignInActivity.this, "Server error", Toast.LENGTH_LONG).show();
                } else {
                    // Handle other HTTP errors
                    Log.d(TAG, "HTTP error: " + httpStatusCode);
                    Toast.makeText(SignInActivity.this, "HTTP error: " + httpStatusCode, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                // Handle network or other errors
                Toast.makeText(SignInActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    // Retrofit Client info

    public class RetrofitClient {
        public static final String BASE_URL = "http://192.168.0.11:3000/"; //specific ip for emulator and port number
        //It doesnt ask for an APIKey, only URL


        private static Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        public static ApiService getApiService() {
            return retrofit.create(ApiService.class);
        }

    }

    //ApiService Interface
    public interface ApiService {
        @POST("api/auth/signin")
        Call<ApiResponse> signIn(@Body SignInRequest request);
    }

    //Models

    public class SignInRequest {
        private String email;
        private String password;

        public SignInRequest(String email, String password) {

            this.email = email;
            this.password = password;
        }
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