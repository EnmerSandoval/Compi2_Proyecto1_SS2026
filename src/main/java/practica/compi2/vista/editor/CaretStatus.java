package practica.compi2.vista.editor;

import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.Element;

public class CaretStatus {

    private final JTextPane editor;
    private final JLabel label;

    public CaretStatus(JTextPane editor, JLabel label) {
        this.editor = editor;
        this.label = label;
        editor.addCaretListener(event -> SwingUtilities.invokeLater(this::update));
        update();
    }

    private void update() {
        Element root = editor.getDocument().getDefaultRootElement();
        int position = editor.getCaretPosition();
        int line = root.getElementIndex(position);
        int column = position - root.getElement(line).getStartOffset();
        int lines = root.getElementCount();
        label.setText("Línea " + (line + 1) + ", Columna " + (column + 1) + "  |  " + lines
                + (lines == 1 ? " línea" : " líneas"));
    }
}
