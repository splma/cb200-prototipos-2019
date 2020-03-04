package com.example.educq;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    Button btnCrearCuenta, btnIniciarSesion;
    EditText txtUsuario, txtContrasena;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnCrearCuenta = findViewById(R.id.btn_ir_crearCuenta);
        txtUsuario = findViewById(R.id.et_correoLoguin);
        txtContrasena = findViewById(R.id.et_contrasenaLogin);

        btnIniciarSesion = findViewById(R.id.iniciar_sesion);

        btnCrearCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, SignUp.class));
                finish();
            }
        });

        btnIniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificarUsuarioContrasena("https://alfirkti.com/webServices/validarDatosDeSesion.php?email=" + txtUsuario.getText() + "&contrasena=" + txtContrasena.getText() + "");
            }
        });

    }


    private void verificarUsuarioContrasena(String URL) {


        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        if (txtUsuario.getText().toString().equals(jsonObject.getString("email")) && txtContrasena.getText().toString().equals(jsonObject.getString("contrasena"))) {
                            startActivity(new Intent(Login.this, Quiz.class));
                            Toast.makeText(Login.this, "Bienvendio " + jsonObject.getString("nombre").toUpperCase() + "", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(Login.this, "Correo y/o contaseña invalidos", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "ERROR DE CONEXIÓN", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);

    }
}