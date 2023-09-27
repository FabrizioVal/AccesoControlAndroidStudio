package com.example.accesocontrolmongodb;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

        Call<ApiResponse> call = apiService.signIn(new SignInRequest(email, password)); //aca se menciona que funcion se usa
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    ApiResponse apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        // Sign-in was successful, navigate to main activity

                        Toast.makeText(SignInActivity.this, "Sign-in successful", Toast.LENGTH_SHORT).show();

                        Log.d(TAG, "Sign in successful");

                        Intent intent = new Intent(SignInActivity.this, HomeMainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Sign-in failed, show error message
                        String errorMessage = apiResponse.getMessage();
                        Toast.makeText(SignInActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Sign in not successful");
                    }
                } else {
                    // Show error message
                    Toast.makeText(SignInActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Network error");
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
        private static final String BASE_URL = "http://10.0.2.2:3000/"; //specific ip for emulator and port number
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