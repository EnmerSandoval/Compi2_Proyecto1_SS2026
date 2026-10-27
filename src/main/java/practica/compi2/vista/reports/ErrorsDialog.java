package practica.compi2.vista.reports;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import practica.compi2.errors.CompilationError;

public class ErrorsDialog extends JDialog {

    private static final String[] COLUMNS = {"Tipo", "Archivo", "Línea", "Columna", "Lexema", "Descripción"};

    public ErrorsDialog(JFrame owner, List<CompilationError> errors) {
        super(owner, "Reporte de errores", false);
        JTable table = TableFactory.createTable(COLUMNS, rows(errors));
        TableFactory.setWidths(table, 80, 170, 45, 60, 100, 500);
        add(new JScrollPane(table), BorderLayout.CENTER);
        setSize(950, 350);
        setLocationRelativeTo(owner);
    }

    private List<String[]> rows(List<CompilationError> errors) {
        List<String[]> rows = new ArrayList<>();
        for (CompilationError error : errors) {
            rows.add(new String[]{
                error.getType().getDescription(),
                error.getFile(),
                String.valueOf(error.getLine()),
                String.valueOf(error.getColumn()),
                error.getLexeme(),
                error.getDescription()
            });
        }
        return rows;
    }
}
