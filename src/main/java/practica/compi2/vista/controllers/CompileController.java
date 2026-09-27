package practica.compi2.vista.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import practica.compi2.c3d.CCodeGenerator;
import practica.compi2.compiler.CompilationResult;
import practica.compi2.compiler.Compiler;
import practica.compi2.console.ErrorPrinter;
import practica.compi2.vista.IdeConsole;
import practica.compi2.vista.reports.ErrorsDialog;
import practica.compi2.vista.reports.ResultsDialog;

public class CompileController {

    private final JFrame frame;
    private final EditorController editorController;
    private final ProjectController projectController;
    private final IdeConsole console;
    private final Compiler compiler;
    private final ErrorPrinter errorPrinter;
    private String lastCode;

    public CompileController(JFrame frame, EditorController editorController, ProjectController projectController,
            IdeConsole console) {
        this.frame = frame;
        this.editorController = editorController;
        this.projectController = projectController;
        this.console = console;
        this.compiler = new Compiler();
        this.errorPrinter = new ErrorPrinter();
    }

    public void compile() {
        if (editorController.getCurrentFile() != null && !editorController.save()) {
            return;
        }
        Path mainFile = findMainFile();
        if (mainFile == null) {
            JOptionPane.showMessageDialog(frame, "Abra un proyecto o un archivo .pig para compilar.");
            return;
        }
        console.println("Compilando: " + mainFile.getFileName());
        CompilationResult compilation = compiler.compile(mainFile);
        if (compilation.hasErrors()) {
            showErrors(compilation, mainFile);
            return;
        }
        console.println("No se encontraron errores.");
        if (!compilation.hasCode()) {
            console.println("El archivo no tiene errores, pero solo un archivo .pig genera código.");
            return;
        }
        lastCode = compilation.getCode();
        console.println(new CCodeGenerator().generateFile(lastCode, mainFile.getParent()));
        projectController.refresh();
        new ResultsDialog(frame, compilation).setVisible(true);
    }

    private void showErrors(CompilationResult compilation, Path mainFile) {
        console.println("Se encontraron " + compilation.getErrors().size() + " errores:");
        console.println(errorPrinter.print(compilation.getErrors()));
        new ErrorsDialog(frame, compilation.getErrors(), mainFile.getParent(), editorController).setVisible(true);
    }

    public void downloadCode() {
        if (lastCode == null) {
            JOptionPane.showMessageDialog(frame, "Primero compile un programa sin errores.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("programa.c"));
        if (chooser.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        Path file = chooser.getSelectedFile().toPath();
        try {
            Files.writeString(file, lastCode);
            console.println("Código de tres direcciones guardado en: " + file.toAbsolutePath());
        } catch (IOException e) {
            console.println("No se pudo guardar el código: " + e.getMessage());
        }
    }

    private Path findMainFile() {
        Path current = editorController.getCurrentFile();
        if (current != null && current.toString().endsWith(".pig")) {
            return current;
        }
        Path folder = projectController.getProjectFolder();
        if (folder == null) {
            return current;
        }
        Path pigFile = findPigFile(folder);
        if (pigFile == null) {
            return current;
        }
        return pigFile;
    }

    private Path findPigFile(Path folder) {
        if (Files.exists(folder.resolve("main.pig"))) {
            return folder.resolve("main.pig");
        }
        File[] files = folder.toFile().listFiles();
        if (files == null) {
            return null;
        }
        for (File file : files) {
            if (file.getName().endsWith(".pig")) {
                return file.toPath();
            }
        }
        return null;
    }
}
