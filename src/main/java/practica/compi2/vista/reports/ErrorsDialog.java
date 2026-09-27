package practica.compi2.vista.reports;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import practica.compi2.errors.CompilationError;
import practica.compi2.vista.controllers.EditorController;

public class ErrorsDialog extends JDialog {

    private static final String[] COLUMNS = {"Tipo", "Archivo", "Línea", "Columna", "Lexema", "Descripción"};

    private final List<CompilationError> errors;
    private final Path folder;
    private final EditorController editorController;
    private final JTable table;

    public ErrorsDialog(JFrame owner, List<CompilationError> errors, Path folder, EditorController editorController) {
        super(owner, "Reporte de errores", false);
        this.errors = errors;
        this.folder = folder;
        this.editorController = editorController;
        this.table = TableFactory.createTable(COLUMNS, rows());
        TableFactory.setWidths(table, 80, 170, 45, 60, 100, 500);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2) {
                    goToSelectedError();
                }
            }
        });
        add(new JLabel("  Doble clic sobre un error para ir a la línea."), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        setSize(950, 350);
        setLocationRelativeTo(owner);
    }

    private List<String[]> rows() {
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

    private void goToSelectedError() {
        int row = table.getSelectedRow();
        if (row == -1) {
            return;
        }
        CompilationError error = errors.get(row);
        Path file = folder.resolve(error.getFile()).toAbsolutePath().normalize();
        if (!Files.isRegularFile(file)) {
            return;
        }
        editorController.open(file);
        if (file.equals(editorController.getCurrentFile())) {
            editorController.goTo(error.getLine(), error.getColumn());
        }
    }
}
