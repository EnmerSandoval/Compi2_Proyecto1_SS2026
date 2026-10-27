package practica.compi2.vista.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import practica.compi2.vista.IdeConsole;
import practica.compi2.vista.editor.SyntaxHighlighter;

public class EditorController implements DocumentListener {

    private final JFrame frame;
    private final JTextPane editor;
    private final IdeConsole console;
    private final SyntaxHighlighter highlighter;
    private Path currentFile;
    private boolean modified;
    private boolean loading;

    public EditorController(JFrame frame, JTextPane editor, IdeConsole console) {
        this.frame = frame;
        this.editor = editor;
        this.console = console;
        this.highlighter = new SyntaxHighlighter(editor);
        editor.getDocument().addDocumentListener(this);
        updateTitle();
    }

    public Path getCurrentFile() {
        return currentFile;
    }

    public boolean isModified() {
        return modified;
    }

    public void open(Path path) {
        Path file = path.toAbsolutePath().normalize();
        if (file.equals(currentFile) || !confirmDiscardChanges()) {
            return;
        }
        try {
            String code = Files.readString(file);
            if (code.startsWith("\uFEFF")) {
                code = code.substring(1);
            }
            showCode(file, code);
        } catch (IOException e) {
            console.println("No se pudo abrir el archivo: " + e.getMessage());
        }
    }

    public void openWithChooser(Path folder) {
        JFileChooser chooser = new JFileChooser(folder == null ? null : folder.toFile());
        chooser.setFileFilter(new FileNameExtensionFilter("Archivos del proyecto (.pig, .y, .z)", "pig", "y", "z"));
        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            open(chooser.getSelectedFile().toPath());
        }
    }

    public boolean save() {
        if (currentFile == null) {
            return saveAs();
        }
        try {
            Files.writeString(currentFile, editor.getText());
            modified = false;
            updateTitle();
            return true;
        } catch (IOException e) {
            console.println("No se pudo guardar el archivo: " + e.getMessage());
            return false;
        }
    }

    public boolean saveAs() {
        JFileChooser chooser = new JFileChooser(currentFile == null ? null : currentFile.getParent().toFile());
        if (currentFile != null) {
            chooser.setSelectedFile(currentFile.toFile());
        }
        if (chooser.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION) {
            return false;
        }
        File selected = chooser.getSelectedFile();
        currentFile = selected.toPath();
        highlighter.setExtension(extensionOf(currentFile));
        console.println("Archivo guardado como: " + selected.getAbsolutePath());
        return save();
    }

    public boolean confirmDiscardChanges() {
        if (!modified) {
            return true;
        }
        int option = JOptionPane.showConfirmDialog(frame, "¿Desea guardar los cambios de " + fileName() + "?",
                "Cambios sin guardar", JOptionPane.YES_NO_CANCEL_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            return save();
        }
        return option == JOptionPane.NO_OPTION;
    }

    public void close() {
        showCode(null, "");
    }

    public static String extensionOf(Path file) {
        String name = file.getFileName().toString();
        int dot = name.lastIndexOf('.');
        return dot == -1 ? "" : name.substring(dot + 1);
    }

    private void showCode(Path file, String code) {
        loading = true;
        currentFile = file;
        editor.setText(code);
        editor.setCaretPosition(0);
        highlighter.setExtension(file == null ? "" : extensionOf(file));
        modified = false;
        loading = false;
        updateTitle();
    }

    private String fileName() {
        return currentFile == null ? "Sin título" : currentFile.getFileName().toString();
    }

    private void updateTitle() {
        frame.setTitle("IDE - " + fileName() + (modified ? " *" : ""));
    }

    private void markModified() {
        if (!loading && !modified) {
            modified = true;
            updateTitle();
        }
    }

    @Override
    public void insertUpdate(DocumentEvent event) {
        markModified();
    }

    @Override
    public void removeUpdate(DocumentEvent event) {
        markModified();
    }

    @Override
    public void changedUpdate(DocumentEvent event) {
    }
}
