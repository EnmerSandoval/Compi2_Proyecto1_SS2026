package practica.compi2.vista.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import practica.compi2.vista.IdeConsole;
import practica.compi2.vista.editor.FileTemplates;

public class ProjectController implements TreeSelectionListener {

    private final JFrame frame;
    private final JTree tree;
    private final EditorController editorController;
    private final IdeConsole console;
    private final FileTreeBuilder treeBuilder;
    private Path projectFolder;

    public ProjectController(JFrame frame, JTree tree, EditorController editorController, IdeConsole console) {
        this.frame = frame;
        this.tree = tree;
        this.editorController = editorController;
        this.console = console;
        this.treeBuilder = new FileTreeBuilder();
        tree.addTreeSelectionListener(this);
        showEmptyTree();
    }

    public Path getProjectFolder() {
        return projectFolder;
    }

    public void createProject() {
        File parent = chooseFolder("Seleccione dónde crear el proyecto");
        if (parent == null) {
            return;
        }
        String name = askName("Nombre del proyecto:");
        if (name == null) {
            return;
        }
        Path folder = parent.toPath().resolve(name);
        if (Files.exists(folder)) {
            JOptionPane.showMessageDialog(frame, "Ya existe una carpeta con ese nombre.");
            return;
        }
        writeNewProject(folder);
    }

    private void writeNewProject(Path folder) {
        try {
            Files.createDirectories(folder);
            Path mainFile = folder.resolve("main.pig");
            Files.writeString(mainFile, FileTemplates.forFile("main.pig"));
            if (loadProject(folder)) {
                editorController.open(mainFile);
            }
            console.println("Proyecto creado en: " + folder.toAbsolutePath());
        } catch (IOException e) {
            console.println("No se pudo crear el proyecto: " + e.getMessage());
        }
    }

    public void openProject() {
        File folder = chooseFolder("Seleccione la carpeta del proyecto");
        if (folder != null && loadProject(folder.toPath())) {
            console.println("Proyecto abierto: " + folder.getAbsolutePath());
        }
    }

    public void closeProject() {
        if (projectFolder == null || !editorController.confirmDiscardChanges()) {
            return;
        }
        console.println("Proyecto cerrado: " + projectFolder.getFileName());
        projectFolder = null;
        editorController.close();
        showEmptyTree();
    }

    public void createFile() {
        if (projectFolder == null) {
            JOptionPane.showMessageDialog(frame, "Primero abra o cree un proyecto.");
            return;
        }
        String name = askName("Nombre del archivo (.pig, .y o .z):");
        if (name == null) {
            return;
        }
        if (!hasValidExtension(name)) {
            JOptionPane.showMessageDialog(frame, "La extensión debe ser .pig, .y o .z");
            return;
        }
        Path file = getSelectedFolder().resolve(name);
        if (Files.exists(file)) {
            JOptionPane.showMessageDialog(frame, "Ya existe un archivo con ese nombre.");
            return;
        }
        writeNewFile(file, name);
    }

    private void writeNewFile(Path file, String name) {
        try {
            Files.writeString(file, FileTemplates.forFile(name));
            refresh();
            editorController.open(file);
            console.println("Archivo creado: " + file.getFileName());
        } catch (IOException e) {
            console.println("No se pudo crear el archivo: " + e.getMessage());
        }
    }

    public void refresh() {
        if (projectFolder != null) {
            tree.setModel(new DefaultTreeModel(treeBuilder.build(projectFolder.toFile())));
        }
    }

    private boolean loadProject(Path folder) {
        if (!editorController.confirmDiscardChanges()) {
            return false;
        }
        projectFolder = folder;
        editorController.close();
        refresh();
        return true;
    }

    private String askName(String message) {
        String name = JOptionPane.showInputDialog(frame, message);
        if (name == null || name.isBlank()) {
            return null;
        }
        return name.trim();
    }

    private boolean hasValidExtension(String name) {
        return name.endsWith(".pig") || name.endsWith(".y") || name.endsWith(".z");
    }

    public Path getSelectedFolder() {
        FileNode node = getSelectedNode();
        if (node == null) {
            return projectFolder;
        }
        return node.isFolder() ? node.getPath() : node.getPath().getParent();
    }

    @Override
    public void valueChanged(TreeSelectionEvent event) {
        openSelectedFile();
    }

    private void openSelectedFile() {
        FileNode node = getSelectedNode();
        if (node != null && !node.isFolder()) {
            editorController.open(node.getPath());
        }
    }

    public FileNode getSelectedNode() {
        Object selected = tree.getLastSelectedPathComponent();
        if (selected instanceof DefaultMutableTreeNode) {
            Object value = ((DefaultMutableTreeNode) selected).getUserObject();
            if (value instanceof FileNode) {
                return (FileNode) value;
            }
        }
        return null;
    }

    private File chooseFolder(String title) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(title);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        }
        return null;
    }

    private void showEmptyTree() {
        tree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("Sin proyecto")));
    }
}
