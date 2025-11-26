package supermetromendeley;  // <-- muy importante

import ui.MainWindow;

/**
 * Entry point of the app.
 * Punto de entrada de la app.
 */
public class SuperMetroMendeley {

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new MainWindow().setVisible(true);  // abre la ventana
        });
    }
}
