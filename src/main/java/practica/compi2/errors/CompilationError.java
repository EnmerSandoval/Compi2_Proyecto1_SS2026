package practica.compi2.errors;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CompilationError {

    private ErrorType type;
    private String lexeme;
    private String file;
    private int line;
    private int column;
    private String description;
}
