package com.jalpa.cobranza;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Token del servidor para sincronizar. El inicio de sesión del recaudador es local (sin
 * conexión); el token sólo se pide al enviar o recibir datos, con las credenciales que el
 * recaudador escribió al entrar. Éstas viven sólo en memoria; el token (que expira) se
 * guarda en SharedPreferences para que ServiceUpload lo siga usando.
 */
public final class Sesion {
    private static final String PREFERENCIAS = "sesion";
    private static final String TOKEN = "token";
    private static final String EXPIRA = "expira";
    private static final String USUARIO = "usuario";
    //margen para no mandar un token que expire a media petición
    private static final long MARGEN_MS = 60_000;

    private static Context contexto;
    private static String usuario;
    private static String password;

    private Sesion() {
    }

    public static synchronized void init(Context context) {
        if (contexto == null && context != null) {
            contexto = context.getApplicationContext();
        }
    }

    /** Credenciales del recaudador que inició sesión; un token de otro recaudador se descarta. */
    public static synchronized void setCredenciales(String usuarioActivo, String passwordActivo) {
        usuario = usuarioActivo;
        password = passwordActivo;
        SharedPreferences prefs = preferencias();
        if (prefs != null && usuarioActivo != null && !usuarioActivo.equals(prefs.getString(USUARIO, null))) {
            invalidarToken();
        }
    }

    public static synchronized void cerrar() {
        usuario = null;
        password = null;
    }

    static synchronized String getUsuario() {
        return usuario;
    }

    static synchronized String getPassword() {
        return password;
    }

    /** Token vigente o null. */
    static synchronized String getToken() {
        SharedPreferences prefs = preferencias();
        if (prefs == null || prefs.getLong(EXPIRA, 0) <= System.currentTimeMillis() + MARGEN_MS) {
            return null;
        }
        return prefs.getString(TOKEN, null);
    }

    static synchronized void guardarToken(String token, long expira, String usuarioToken) {
        SharedPreferences prefs = preferencias();
        if (prefs != null) {
            prefs.edit().putString(TOKEN, token).putLong(EXPIRA, expira).putString(USUARIO, usuarioToken).apply();
        }
    }

    static synchronized void invalidarToken() {
        SharedPreferences prefs = preferencias();
        if (prefs != null) {
            prefs.edit().remove(TOKEN).remove(EXPIRA).remove(USUARIO).apply();
        }
    }

    private static SharedPreferences preferencias() {
        return contexto != null ? contexto.getSharedPreferences(PREFERENCIAS, Context.MODE_PRIVATE) : null;
    }
}
