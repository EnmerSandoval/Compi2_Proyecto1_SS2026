package practica.compi2.parsers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import practica.compi2.ast.Ast;
import practica.compi2.ast.definitions.ImportDeclaration;
import practica.compi2.ast.programs.PigLatinProgram;
import practica.compi2.ast.programs.YProgram;
import practica.compi2.ast.programs.ZetarianoProgram;
import practica.compi2.errors.ErrorManager;

public class ProgramLoader {

    private final Path projectFolder;
    private final ErrorManager em;
    private final YFileParser yParser;
    private final ZetarianoFileParser zetarianoParser;
    private final PigLatinFileParser pigLatinParser;
    private final Set<Path> loadedFiles;

    public ProgramLoader(Path projectFolder, ErrorManager em) {
        this.projectFolder = projectFolder;
        this.em = em;
        this.yParser = new YFileParser(em);
        this.zetarianoParser = new ZetarianoFileParser(em);
        this.pigLatinParser = new PigLatinFileParser(em);
        this.loadedFiles = new HashSet<>();
    }

    public Ast load(Path file) {
        Ast ast = new Ast();
        String name = file.getFileName().toString();
        if (name.endsWith(".pig")) {
            loadPigLatin(file, ast);
        } else if (name.endsWith(".y")) {
            loadY(file, ast);
        } else if (name.endsWith(".z")) {
            loadZetariano(file, ast);
        } else {
            em.addSemanticError(name, null, name, "Extensión de archivo no soportada");
        }
        return ast;
    }

    private void loadPigLatin(Path file, Ast ast) {
        String code = readFile(file);
        if (code == null) {
            return;
        }
        PigLatinProgram program = pigLatinParser.parse(fileName(file), code);
        ast.setMain(program);
        if (program == null) {
            return;
        }
        for (ImportDeclaration importDeclaration : program.getImports()) {
            loadImport(importDeclaration, program.getFileName(), ast);
        }
    }

    private void loadImport(ImportDeclaration importDeclaration, String mainFile, Ast ast) {
        String relativePath = importDeclaration.getRelativePath();
        Path file = projectFolder.resolve(relativePath).normalize();
        if (!Files.exists(file)) {
            em.addSemanticError(mainFile, importDeclaration.getPosition(), relativePath,
                    "No se encontró el archivo importado");
            return;
        }
        if (importDeclaration.getExtension().equals("y")) {
            loadY(file, ast);
        } else if (importDeclaration.getExtension().equals("z")) {
            loadZetariano(file, ast);
        } else {
            em.addSemanticError(mainFile, importDeclaration.getPosition(), relativePath,
                    "Solo se pueden importar archivos .y o .z");
        }
    }

    private void loadY(Path file, Ast ast) {
        String code = readFile(file);
        if (code == null) {
            return;
        }
        YProgram program = yParser.parse(fileName(file), code);
        if (program != null) {
            ast.addYProgram(program);
        }
    }

    private void loadZetariano(Path file, Ast ast) {
        String code = readFile(file);
        if (code == null) {
            return;
        }
        ZetarianoProgram program = zetarianoParser.parse(fileName(file), code);
        if (program != null) {
            ast.addClass(program);
        }
    }

    private String readFile(Path file) {
        if (loadedFiles.contains(file)) {
            return null;
        }
        loadedFiles.add(file);
        try {
            String code = Files.readString(file);
            if (code.startsWith("\uFEFF")) {
                code = code.substring(1);
            }
            return code;
        } catch (IOException e) {
            em.addSemanticError(fileName(file), null, "", "No se pudo leer el archivo");
            return null;
        }
    }

    private String fileName(Path file) {
        return projectFolder.relativize(file).toString().replace("\\", "/");
    }
}
