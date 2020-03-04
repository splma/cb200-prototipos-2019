package com.example.educq;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {


    EditText edt_matricula, edt_nombre, edt_Apaterno, edt_Amaterno, edt_email, edt_nombre_usuario, edt_contrasena, edt_materia, edt_tutor, edt_fecha_nacimiento, edt_fk_rol_estudiante, edt_fk_record_estudiante;
    Button btn_ir_inicioSesion, crear_cuenta;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        edt_Amaterno = findViewById(R.id.et_apellidoMaterno);
        edt_Apaterno = findViewById(R.id.et_apellidoPaterno);
        edt_contrasena = findViewById(R.id.et_contrasena);
        edt_email = findViewById(R.id.et_correo);
        edt_fecha_nacimiento = findViewById(R.id.et_fechaNacimiento);
        edt_fk_record_estudiante = findViewById(R.id.et_record);
        edt_fk_rol_estudiante = findViewById(R.id.et_rol);
        edt_materia = findViewById(R.id.et_materia);
        edt_matricula = findViewById(R.id.et_matricula);
        edt_nombre = findViewById(R.id.et_nombre);
        edt_nombre_usuario = findViewById(R.id.et_nombreUsuario);
        edt_tutor = findViewById(R.id.et_tutor);

        btn_ir_inicioSesion = findViewById(R.id.btn_ir_inicioSesion);
        crear_cuenta = findViewById(R.id.crear_cuenta);


        btn_ir_inicioSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUp.this, Login.class));
                finish();
            }
        });

        crear_cuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Toast.makeText(SignUp.this, "Registrar Usuario", Toast.LENGTH_SHORT).show();
                //Aquí codigo
                if (isValidEmail(edt_email.getText().toString())) {
                    if (isValidPassword(edt_contrasena.getText().toString())) {
                        ejecutarServicio("https://alfirkti.com/webServices/registrarAlumno.php");
                    }else{
                        Toast.makeText(getApplicationContext(), "Ingresa una contraseña valida, \nmyor de cuatro dígitos", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Ingresa un correo valido", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void ejecutarServicio(String URL) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Operación Exitosa", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("matricula", edt_matricula.getText().toString());
                parametros.put("nombre", edt_nombre.getText().toString());
                parametros.put("Apaterno", edt_Apaterno.getText().toString());
                parametros.put("Amaterno", edt_Amaterno.getText().toString());
                parametros.put("email", edt_email.getText().toString());
                parametros.put("nombre_usuario", edt_nombre_usuario.getText().toString());
                parametros.put("contrasena", edt_contrasena.getText().toString());
                parametros.put("materia", edt_materia.getText().toString());
                parametros.put("tutor", edt_tutor.getText().toString());
                parametros.put("fecha_nacimiento", edt_fecha_nacimiento.getText().toString());
                parametros.put("fk_rol_estudiante", edt_fk_rol_estudiante.getText().toString());
                parametros.put("fk_record_estudiante", edt_fk_record_estudiante.getText().toString());

                return parametros;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    //validamos que el correo tenga el formato que debe tener
    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    //validamos que la contraseña cumpla con cietos requisitos de formato
    private boolean isValidPassword(String password) {
        return password.length() > 4;
    }


}
