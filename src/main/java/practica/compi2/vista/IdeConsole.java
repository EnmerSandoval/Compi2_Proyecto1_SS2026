package practica.compi2.vista;

import java.awt.Font;
import javax.swing.JTextArea;

public class IdeConsole {

    private final JTextArea area;

    public IdeConsole(JTextArea area) {
        this.area = area;
        area.setEditable(false);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
    }

    public void println(String text) {
        print(text + "\n");
    }

    public void print(String text) {
        area.append(text);
        area.setCaretPosition(area.getDocument().getLength());
    }
}
