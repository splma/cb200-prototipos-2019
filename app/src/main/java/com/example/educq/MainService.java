package com.example.educq;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;


/**
 * Created by matt on 08/08/2016.
 */

public class MainService extends Service implements View.OnTouchListener {

    private static final String TAG = MainService.class.getSimpleName();

    private WindowManager windowManager;

    private View floatyView;

    Button btnRespuesta;
    RadioButton rdbUno, rdbDos,rdbTres,rdbCuatro;
    RadioGroup rdbGroup;
    TextView txvPregunta;
    RequestQueue requestQueue;
    String strNumRespuestaOk;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        addOverlayView();

         btnRespuesta= floatyView.findViewById(R.id.comprobar_Respuesta);
         rdbUno=floatyView.findViewById(R.id.radio_uno);
         rdbDos= floatyView.findViewById(R.id.radio_dos);
         rdbTres= floatyView.findViewById(R.id.radio_tres);
         rdbCuatro= floatyView.findViewById(R.id.radio_cuatro);
         rdbGroup= floatyView.findViewById(R.id.radioGroupRespuestas);
          txvPregunta=floatyView.findViewById(R.id.txv_pregunta);

        btnRespuesta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random r = new Random();
                int numero = r.nextInt(15)+1;  // Entre 0 y 15, más 1.
                solicitarPreunta("https://alfirkti.com/webServices/buscar.php?numero="+numero+"");
            }
        });

    }


    private void solicitarPreunta(String URL) {
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        String strPregunta= jsonObject.getString("pregunta");
                        txvPregunta.setText(strPregunta);
                        strNumRespuestaOk = jsonObject.getString("respuestaCorrecta");
                        solicitarRespuestaCorrecta("https://alfirkti.com/webServices/respuestaCorrecta.php?numero="+strNumRespuestaOk+"");
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "ERROR DE CONEXION.", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);
    }

    private void solicitarRespuestaCorrecta(String URL){
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        String strRespuestaCorrecta = jsonObject.getString("respuesta");
                        rdbUno.setText(strRespuestaCorrecta);
                        solicitarRespuestasAleatorias("https://alfirkti.com/webServices/respuestaIncorrecta.php");

                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "ERROR DE CONEXION.", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);

    }

    private void solicitarRespuestasAleatorias(String URL){
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                JSONObject jsonObject = null;
                for (int i = 0; i < response.length(); i++) {
                    try {
                        jsonObject = response.getJSONObject(i);
                        String strRespuestaCorrecta = jsonObject.getString("respuesta");
                        if(i==0)
                            rdbDos.setText(strRespuestaCorrecta);
                        else if(i==1)
                            rdbTres.setText(strRespuestaCorrecta);
                        else if(i==2)
                            rdbCuatro.setText(strRespuestaCorrecta);
                    } catch (JSONException e) {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "ERROR DE CONEXION.", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);

    }

    private void addOverlayView() {

        final WindowManager.LayoutParams params;
        int layoutParamsType;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            layoutParamsType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParamsType = WindowManager.LayoutParams.TYPE_PHONE;
        }

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                layoutParamsType,
                0,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.CENTER | Gravity.START;
        params.x = 0;
        params.y = 0;

        FrameLayout interceptorLayout = new FrameLayout(this) {

            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {

                //Solo active el evento ACTION_DOWN, u obtendrá dos eventos (uno para _DOWN, uno para _UP)
                if (event.getAction() == KeyEvent.ACTION_DOWN) {

                    // Comprueba si el botón INICIO está presionado
                    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {

                        Log.v(TAG, "BACK Button Pressed");

                        // A medida que tomamos medidas, volveremos a true para evitar que otras aplicaciones también consuman el evento
                        return true;
                    }
                }

                // De lo contrario, no interceptes el evento
                return super.dispatchKeyEvent(event);
            }
        };

        LayoutInflater inflater = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));

        if (inflater != null) {
            floatyView = inflater.inflate(R.layout.activity_quiz, interceptorLayout);
            floatyView.setOnTouchListener(this);
            windowManager.addView(floatyView, params);
        } else {
            Log.e("SAW-example", "El diseño del servicio del inflador es nulo; no se puede inflar y mostrar R.layout.floating_view");
        }
    }

    @Override
    public void onDestroy() {

        super.onDestroy();

        if (floatyView != null) {

            windowManager.removeView(floatyView);

            floatyView = null;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        view.performClick();

        Log.v(TAG, "onTouch...");

        // Kill service
        //onDestroy();

        Toast.makeText(this, "Selecciona una respuesta", Toast.LENGTH_SHORT).show();
        return true;
    }
}