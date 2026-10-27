package practica.compi2.vista.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import practica.compi2.c3d.CCodeGenerator;
import practica.compi2.compiler.CompilationResult;
import practica.compi2.compiler.Compiler;
import practica.compi2.console.ErrorPrinter;
import practica.compi2.riscv.RiscVGenerator;
import practica.compi2.riscv.simulador.RiscVException;
import practica.compi2.riscv.simulador.RiscVRunner;
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
    private String lastRiscV;
    private SwingWorker<String, Void> simulation;

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
            showErrors(compilation);
            return;
        }
        console.println("No se encontraron errores.");
        if (!compilation.hasCode()) {
            console.println("El archivo no tiene errores, pero solo un archivo .pig genera código.");
            return;
        }
        lastCode = compilation.getCode();
        lastRiscV = compilation.getRiscVCode();
        console.println(new CCodeGenerator().generateFile(lastCode, mainFile.getParent()));
        console.println(new RiscVGenerator().generateFile(lastRiscV, mainFile.getParent()));
        projectController.refresh();
        new ResultsDialog(frame, compilation).setVisible(true);
    }

    private void showErrors(CompilationResult compilation) {
        console.println("Se encontraron " + compilation.getErrors().size() + " errores:");
        console.println(errorPrinter.print(compilation.getErrors()));
        new ErrorsDialog(frame, compilation.getErrors()).setVisible(true);
    }

    public void downloadCode() {
        saveCode(lastCode, "programa.c", "Código de tres direcciones");
    }

    public void downloadRiscV() {
        saveCode(lastRiscV, "programa.s", "Código RISC-V");
    }

    public void runRiscV() {
        if (lastRiscV == null) {
            JOptionPane.showMessageDialog(frame, "Primero compile un programa sin errores.");
            return;
        }
        if (simulation != null && !simulation.isDone()) {
            JOptionPane.showMessageDialog(frame, "El simulador todavía está ejecutando el programa anterior.");
            return;
        }
        console.println("================ SIMULADOR RISC-V ================");
        ConsoleSimulatorIO io = new ConsoleSimulatorIO(frame, console);
        String code = lastRiscV;
        simulation = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                try {
                    long steps = RiscVRunner.run(code, io);
                    return "[Programa terminado, " + steps + " instrucciones ejecutadas]";
                } catch (RiscVException e) {
                    return "[Error en el simulador] " + e.getMessage();
                } finally {
                    io.flush();
                }
            }

            @Override
            protected void done() {
                try {
                    console.println("\n" + get());
                } catch (InterruptedException | ExecutionException e) {
                    console.println("\n[Error en el simulador] " + e.getMessage());
                }
            }
        };
        simulation.execute();
    }

    private void saveCode(String code, String defaultName, String description) {
        if (code == null) {
            JOptionPane.showMessageDialog(frame, "Primero compile un programa sin errores.");
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File(defaultName));
        if (chooser.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        Path file = chooser.getSelectedFile().toPath();
        try {
            Files.writeString(file, code);
            console.println(description + " guardado en: " + file.toAbsolutePath());
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
