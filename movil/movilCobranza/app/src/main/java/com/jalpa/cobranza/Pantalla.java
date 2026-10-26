package com.jalpa.cobranza;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/** Configuración común de la barra superior (MaterialToolbar con id {@code toolbar}). */
public final class Pantalla {

    private Pantalla() {
    }

    /**
     * Registra la barra como action bar de la Activity. Con {@code conRegreso} muestra la
     * flecha de regreso, que hace lo mismo que el botón "atrás" del sistema.
     */
    public static Toolbar configurarBarra(final AppCompatActivity activity, boolean conRegreso) {
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        if (toolbar == null) {
            return null;
        }
        activity.setSupportActionBar(toolbar);
        if (conRegreso && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> activity.getOnBackPressedDispatcher().onBackPressed());
        }
        return toolbar;
    }
}
