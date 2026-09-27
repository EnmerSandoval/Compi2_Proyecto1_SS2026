package practica.compi2.errors;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import practica.compi2.ast.Position;

@Getter
public class ErrorManager {

    private List<CompilationError> errors;

    public ErrorManager() {
        this.errors = new ArrayList<>();
    }

    public void addError(CompilationError error) {
        errors.add(error);
    }

    public void addSemanticError(String file, Position position, String lexeme, String description) {
        int line = 0;
        int column = 0;
        if (position != null) {
            line = position.getLine();
            column = position.getColumn();
        }
        errors.add(new CompilationError(ErrorType.SEMANTICO, lexeme, file, line, column, description));
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public boolean hasSyntaxErrors() {
        for (CompilationError error : errors) {
            if (error.getType() != ErrorType.SEMANTICO) {
                return true;
            }
        }
        return false;
    }
}
