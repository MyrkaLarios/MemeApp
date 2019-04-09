package com.example.memeapp;

import android.app.ProgressDialog;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private EditText email_input;
    private EditText password_input;
    private Button sign_in_input;
    private ConstraintLayout view_main;
    private TextView signup_input;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email_input = findViewById(R.id.email_input);
        password_input = findViewById(R.id.password_input);
        sign_in_input = findViewById(R.id.sign_in_input);
        view_main = findViewById(R.id.view_main);
        signup_input = findViewById(R.id.signup_input);

        //inicializar android networking
//        AndroidNetworking
        //Método al dar click en el btn
        sign_in_input.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                loginUser();
                v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.click_animation));
            }
        });

        email_input.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                enableDisableButton(event);
                return false;
            }
        });

        password_input.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                enableDisableButton(event);
                return false;
            }
        });


        password_input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_GO){
                    loginUser();
                }
                return false;
            }
        });
    }

    public void loginUser(){
        String email = email_input.getText().toString();
        String password = password_input.getText().toString();

        if(userCanLogin(email, password)){
            authenticateUser(email, password);
        }
    }

    public boolean userCanLogin(String email, String password){
        if(email.length() < 1){
            email_input.setError("El campo no puede estar vacío");
        }
        if(password.length() < 1){
            email_input.setError("El campo no puede estar vacío");
        }

        return (password.length() > 0 && email.length() > 0);
    }

    public boolean authenticateUser(String email, String password){
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("iniciando sesión");
        progressDialog.setCancelable(false);
        progressDialog.show();

        AndroidNetworking.post("https://calm-headland-52897.herokuapp.com")
                .addBodyParameter("email", email)
                .addBodyParameter("password", password)
                .setTag("test")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    String userName= "null";
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        try{
                            userName = response.getString("nombre");
                            Log.e("Response", response.toString());
                        } catch(JSONException e){
                            e.printStackTrace();
                            Snackbar.make(view_main, "Error inciando sesión", Snackbar.LENGTH_LONG).show();
                        }

                        if(userName == "null"){
                            Snackbar.make(view_main, "Hola bb", Snackbar.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(MainActivity.this,"Autenticado correctamente", Toast.LENGTH_LONG).show();
//                            changeActivity();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Error", anError.toString());
                        Snackbar.make(view_main, "Error iniciando sesión", Snackbar.LENGTH_LONG).show();
                    }
                });
        return false;
    }

    public void enableDisableButton(KeyEvent event){
        if(event.getAction() == KeyEvent.ACTION_UP){
            if (email_input.getText().toString().length() > 0
                    && password_input.getText().toString().length() > 0){
                sign_in_input.setEnabled(true);
            } else{
                sign_in_input.setEnabled(false);
            }
        }
    }
}
