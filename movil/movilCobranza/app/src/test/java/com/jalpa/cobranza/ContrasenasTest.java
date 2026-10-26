package com.jalpa.cobranza;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ContrasenasTest {
    //generado por el backend (Spring BCryptPasswordEncoder) para "admin123", sin el prefijo {bcrypt}
    private static final String HASH_SPRING = "$2a$10$BO.cmcsZkk2fL3Kus.gBq.UNvUkDCuB4dHzq2udkf7PGUCxavNlNa";

    @Test
    public void verificaHashGeneradoPorElBackend() {
        assertTrue(Contrasenas.coincide("admin123", HASH_SPRING));
        assertFalse(Contrasenas.coincide("admin124", HASH_SPRING));
    }

    @Test
    public void aceptaTextoPlanoHeredado() {
        assertTrue(Contrasenas.coincide("1234", "1234"));
        assertFalse(Contrasenas.coincide("12345", "1234"));
    }

    @Test
    public void rechazaVaciosYHashMalFormado() {
        assertFalse(Contrasenas.coincide("1234", null));
        assertFalse(Contrasenas.coincide("1234", ""));
        assertFalse(Contrasenas.coincide(null, HASH_SPRING));
        assertFalse(Contrasenas.coincide("1234", "$2a$roto"));
    }
}
