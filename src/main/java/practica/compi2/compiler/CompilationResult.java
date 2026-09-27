package practica.compi2.compiler;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import practica.compi2.ast.Ast;
import practica.compi2.c3d.cuartetas.Cuarteta;
import practica.compi2.errors.CompilationError;
import practica.compi2.semantic.symbols.Symbol;
import practica.compi2.semantic.types.TypeTable;

@Getter
@Setter
public class CompilationResult {

    private Ast ast;
    private List<CompilationError> errors;
    private List<Symbol> symbols;
    private TypeTable typeTable;
    private List<Cuarteta> cuartetas;
    private String code;

    public CompilationResult() {
        this.errors = new ArrayList<>();
        this.symbols = new ArrayList<>();
        this.cuartetas = new ArrayList<>();
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public boolean hasCode() {
        return code != null;
    }
}
