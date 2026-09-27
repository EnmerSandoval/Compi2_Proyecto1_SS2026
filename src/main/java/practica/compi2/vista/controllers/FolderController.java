package practica.compi2.vista.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import practica.compi2.vista.IdeConsole;

public class FolderController {

    private final JFrame frame;
    private final ProjectController projectController;
    private final EditorController editorController;
    private final IdeConsole console;

    public FolderController(JFrame frame, ProjectController projectController, EditorController editorController,
            IdeConsole console) {
        this.frame = frame;
        this.projectController = projectController;
        this.editorController = editorController;
        this.console = console;
    }

    public void createFolder() {
        if (projectController.getProjectFolder() == null) {
            projectController.createProject();
            return;
        }
        String name = JOptionPane.showInputDialog(frame, "Nombre de la carpeta:");
        if (name == null || name.isBlank()) {
            return;
        }
        Path folder = projectController.getSelectedFolder().resolve(name.trim());
        if (Files.exists(folder)) {
            JOptionPane.showMessageDialog(frame, "Ya existe una carpeta o archivo con ese nombre.");
            return;
        }
        try {
            Files.createDirectories(folder);
            projectController.refresh();
            console.println("Carpeta creada: " + folder.getFileName());
        } catch (IOException e) {
            console.println("No se pudo crear la carpeta: " + e.getMessage());
        }
    }

    public void downloadFile() {
        Path file = selectedFile();
        if (file == null) {
            JOptionPane.showMessageDialog(frame, "Seleccione un archivo en el árbol o abra uno en el editor.");
            return;
        }
        saveOpenFile();
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Descargar archivo");
        chooser.setSelectedFile(new File(file.getFileName().toString()));
        if (chooser.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        Path destination = chooser.getSelectedFile().toPath();
        try {
            Files.copy(file, destination, StandardCopyOption.REPLACE_EXISTING);
            console.println("Archivo descargado en: " + destination.toAbsolutePath());
        } catch (IOException e) {
            console.println("No se pudo descargar el archivo: " + e.getMessage());
        }
    }

    public void downloadFolder() {
        Path folder = projectController.getSelectedFolder();
        if (folder == null) {
            JOptionPane.showMessageDialog(frame, "Primero abra un proyecto.");
            return;
        }
        File parent = chooseDestination(folder);
        if (parent == null) {
            return;
        }
        Path destination = parent.toPath().resolve(folder.getFileName().toString());
        if (!canCopyTo(folder, destination)) {
            return;
        }
        saveOpenFile();
        try {
            copyFolder(folder.toFile(), destination.toFile());
            console.println("Carpeta descargada en: " + destination.toAbsolutePath());
        } catch (IOException e) {
            console.println("No se pudo descargar la carpeta: " + e.getMessage());
        }
    }

    private boolean canCopyTo(Path folder, Path destination) {
        if (destination.toAbsolutePath().startsWith(folder.toAbsolutePath())) {
            JOptionPane.showMessageDialog(frame, "No se puede descargar una carpeta dentro de sí misma.");
            return false;
        }
        if (Files.exists(destination)) {
            JOptionPane.showMessageDialog(frame, "Ya existe " + destination.getFileName() + " en ese lugar.");
            return false;
        }
        return true;
    }

    private void copyFolder(File source, File destination) throws IOException {
        Files.createDirectories(destination.toPath());
        File[] children = source.listFiles();
        if (children == null) {
            return;
        }
        for (File child : children) {
            File target = new File(destination, child.getName());
            if (child.isDirectory()) {
                copyFolder(child, target);
            } else {
                Files.copy(child.toPath(), target.toPath());
            }
        }
    }

    private File chooseDestination(Path folder) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Seleccione dónde descargar la carpeta " + folder.getFileName());
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showDialog(frame, "Descargar") == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }

    private Path selectedFile() {
        FileNode node = projectController.getSelectedNode();
        if (node != null && !node.isFolder()) {
            return node.getPath();
        }
        return editorController.getCurrentFile();
    }

    private void saveOpenFile() {
        if (editorController.getCurrentFile() != null && editorController.isModified()) {
            editorController.save();
        }
    }
}
