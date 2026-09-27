package practica.compi2.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorType {
    LEXICO("Léxico"),
    SINTACTICO("Sintáctico"),
    SEMANTICO("Semántico");

    private final String description;
}
