package com.jalpa.cobranza;

import org.mindrot.jbcrypt.BCrypt;

/** Verificación local (sin conexión) de la contraseña del recaudador. */
public final class Contrasenas {
    private Contrasenas() {
    }

    /**
     * El servidor manda el hash BCrypt de la contraseña. Un valor que no es hash es una
     * contraseña en texto plano descargada por una versión anterior de la app; se acepta
     * hasta que se vuelvan a descargar los recaudadores.
     */
    public static boolean coincide(String password, String guardada) {
        if (password == null || guardada == null || guardada.isEmpty()) {
            return false;
        }
        if (guardada.startsWith("$2")) {
            try {
                return BCrypt.checkpw(password, guardada);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
        return guardada.equals(password);
    }
}
