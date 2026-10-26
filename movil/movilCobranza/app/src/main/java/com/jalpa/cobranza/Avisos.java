package com.jalpa.cobranza;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

/**
 * Mensajes al usuario con el mismo aspecto en todas las pantallas:
 * <ul>
 *     <li>{@link #exito}: Snackbar verde, para confirmar que algo salió bien.</li>
 *     <li>{@link #error}: diálogo con título, para errores y bloqueos (el texto completo se
 *     lee, a diferencia de los toasts de Android 12+, que se cortan a dos líneas).</li>
 *     <li>{@link #alCerrar}: toast, sólo para mensajes que se muestran justo antes de cerrar
 *     la pantalla (un Snackbar o un diálogo desaparecerían con ella).</li>
 * </ul>
 * Todos deben llamarse desde el hilo principal (onPostExecute, listeners, runOnUiThread).
 */
public final class Avisos {

    private Avisos() {
    }

    public static void exito(Activity activity, String mensaje) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        exito(activity.findViewById(android.R.id.content), mensaje);
    }

    public static void exito(View vista, String mensaje) {
        if (vista == null) {
            return;
        }
        Snackbar snackbar = Snackbar.make(vista, mensaje, Snackbar.LENGTH_LONG);
        snackbar.setBackgroundTint(ContextCompat.getColor(vista.getContext(), R.color.status_pagado_solid));
        snackbar.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public static void error(Activity activity, String titulo, String mensaje) {
        if (activity == null) {
            return;
        }
        if (activity.isFinishing() || activity.isDestroyed()) {
            alCerrar(activity, mensaje);
            return;
        }
        new MaterialAlertDialogBuilder(activity)
                .setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton(R.string.aceptar, null)
                .show();
    }

    public static void alCerrar(Context context, String mensaje) {
        Toast.makeText(context.getApplicationContext(), mensaje, Toast.LENGTH_LONG).show();
    }
}
