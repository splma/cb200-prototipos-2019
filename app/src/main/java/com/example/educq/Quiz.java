package com.example.educq;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

public class Quiz extends AppCompatActivity implements View.OnClickListener {

    private Button comprobar_Respuesta;
    public static final int RESULT_ENABLE = 11;
    private DevicePolicyManager devicePolicyManager;
    private ActivityManager activityManager;
    private ComponentName compName;
    private RadioButton radio_uno, radio_dos, radio_tres, radio_cuatro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        devicePolicyManager = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        compName = new ComponentName(this, MyAdmin.class);


        radio_uno = findViewById(R.id.radio_uno);
        radio_dos = findViewById(R.id.radio_dos);
        radio_tres = findViewById(R.id.radio_tres);
        radio_cuatro = findViewById(R.id.radio_cuatro);
        findViewById(R.id.comprobar_Respuesta).setOnClickListener(this);

        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "La función principal de la aplicación es regular el uso del dispositivo de una manera divertidad a traves de preguntas que te ayudaran a reforzar tus conocimientos de una cierta area. Necesitamos este permiso para poder continuar.");
        startActivityForResult(intent, RESULT_ENABLE);


    }

    @Override
    public void onClick(View v) {

        int color;

        View contenedor = v.getRootView();

        switch (v.getId()) {
            case R.id.comprobar_Respuesta:
                color = Color.parseColor("#80CBC4"); // Verde azulado
                break;
            default:
                color = Color.WHITE; // Blano
        }

        contenedor.setBackgroundColor(color);
    }


    @Override
    protected void onResume() {
        super.onResume();
        boolean isActive = devicePolicyManager.isAdminActive(compName);
        if (Settings.canDrawOverlays(this)) {

            // Launch service right away - the user has already previously granted permission
            launchMainService();
        } else {

            // Check that the user has granted permission, and prompt them if not
            checkDrawOverlayPermission();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_ENABLE:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(Quiz.this, "\n" + "Ha habilitado las funciones  de administración del dispositivo", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Quiz.this, "\n" + "Problema para habilitar las funciones de administración del dispositivo", Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);


        // Compruebe si se recibe un código de solicitud que coincida con el que proporcionamos para la solicitud de sorteo de superposición
        if (requestCode == REQUEST_CODE) {

            //Vuelva a verificar que el usuario lo haya otorgado y no haya descartado la solicitud.
            if (Settings.canDrawOverlays(this)) {

                //Lanzar el servicio
                launchMainService();
            } else {

                Toast.makeText(this, "\n" +"Lo siento, No se pueden dibujar superposiciones sin permiso ...", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private int intentos = 1;


    public void bloquearSiSuperaIntentos() {

        boolean active = devicePolicyManager.isAdminActive(compName);
        if (active) {
            if (intentos == 3) {

                devicePolicyManager.lockNow();
                intentos = 1;
            } else {

                Toast.makeText(Quiz.this, "Te quedan " + (3 - intentos) + " intentos", Toast.LENGTH_SHORT).show();
                intentos++;
            }
        } else {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, compName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "La función principal de la aplicación es regular el uso del dispositivo de una manera divertidad a traves de preguntas que te ayudaran a reforzar tus conocimientos de una cierta area. Necesitamos este permiso para poder continuar.");
            startActivityForResult(intent, RESULT_ENABLE);
        }
    }




    /*%%%%%%%%*/


    private void launchMainService() {

        Intent svc = new Intent(this, MainService.class);

        stopService(svc);
        startService(svc);

        finish();
    }

    private final static int REQUEST_CODE = 10101;

    private void checkDrawOverlayPermission() {


        // Comprueba si la aplicación ya tiene permiso para dibujar superposiciones
        if (!Settings.canDrawOverlays(this)) {

            // If not, form up an Intent to launch the permission request
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));

            // Intento de lanzamiento, con el código de solicitud proporcionado
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    /*%%%%%%%%*/
}