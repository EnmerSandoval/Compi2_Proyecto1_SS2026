package practica.compi2.console;

import java.util.List;
import practica.compi2.errors.CompilationError;

public class ErrorPrinter {

    private static final String FORMAT = "%-11s | %-24s | %-5s | %-7s | %-18s | %s%n";

    public String print(List<CompilationError> errors) {
        if (errors.isEmpty()) {
            return "No se encontraron errores.\n";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(FORMAT, "Tipo", "Archivo", "Línea", "Columna", "Lexema", "Descripción"));
        for (CompilationError error : errors) {
            sb.append(String.format(FORMAT,
                    error.getType().getDescription(),
                    error.getFile(),
                    error.getLine(),
                    error.getColumn(),
                    error.getLexeme(),
                    error.getDescription()));
        }
        return sb.toString();
    }
}
