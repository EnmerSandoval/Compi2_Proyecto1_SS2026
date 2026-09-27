package practica.compi2.c3d;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import practica.compi2.c3d.cuartetas.Cuarteta;
import practica.compi2.c3d.cuartetas.FunctionEnd3D;
import practica.compi2.c3d.cuartetas.FunctionStart3D;
import practica.compi2.c3d.cuartetas.LabelDefinition3D;

public class CCodeGenerator {

    public String generateCode(IntermediateCodeUtils u) {
        StringBuilder sb = new StringBuilder();
        sb.append("""
                #include <stdio.h>

                double stack[1000000];
                double heap[10000000];
                int P = 0;
                int H = 1;

                """);
        for (Cuarteta cuarteta : u.getCuartetas()) {
            if (cuarteta instanceof FunctionStart3D && !((FunctionStart3D) cuarteta).isMain()) {
                sb.append("void ").append(((FunctionStart3D) cuarteta).getName()).append("();\n");
            }
        }
        sb.append("\n");
        for (Cuarteta cuarteta : u.getCuartetas()) {
            if (isInstruction(cuarteta)) {
                sb.append("    ");
            }
            cuarteta.toCCode(sb);
        }
        return sb.toString();
    }

    public String generateFile(String code, Path folder) {
        Path file = folder.resolve("programa.c");
        try {
            Files.writeString(file, code);
        } catch (IOException e) {
            return "No se pudo guardar el archivo C: " + e.getMessage();
        }
        return "Archivo C generado en: " + file.toAbsolutePath() + "\n" + compile(folder);
    }

    private String compile(Path folder) {
        StringBuilder log = new StringBuilder();
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("gcc", "programa.c", "-o", "programa");
            processBuilder.directory(folder.toFile());
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.append(line).append("\n");
                }
            }
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                log.append("Compilación exitosa, se generó el ejecutable 'programa'.");
            } else {
                log.append("La compilación falló con el código de salida: ").append(exitCode);
            }
        } catch (IOException e) {
            log.append("No se encontró gcc. Compile manualmente con: gcc programa.c -o programa");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return log.toString();
    }

    private boolean isInstruction(Cuarteta cuarteta) {
        return !(cuarteta instanceof FunctionStart3D)
                && !(cuarteta instanceof FunctionEnd3D)
                && !(cuarteta instanceof LabelDefinition3D);
    }
}
