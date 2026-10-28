package practica.compi2.ast.definitions;

import lombok.Getter;
import practica.compi2.ast.AstNode;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.ast.types.TypeReference;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.symbols.Symbol;
import practica.compi2.semantic.symbols.SymbolKind;
import practica.compi2.semantic.types.Type;

@Getter
public class Parameter extends AstNode {

    private TypeReference type;
    private String name;
    private boolean reference;

    public Parameter(Position position, TypeReference type, String name, boolean reference) {
        super(position);
        this.type = type;
        this.name = name;
        this.reference = reference;
    }

    public Symbol createSymbol(SemanticContext context) {
        Type resolved = context.resolveType(type);
        if (resolved.isStruct() && !reference) {
            context.error(position, name, "La estructura debe recibirse por referencia: {} " + resolved + " " + name);
        }
        return new Symbol(name, resolved, SymbolKind.PARAMETER, 0);
    }

    @Override
    public void print(AstPrinter printer) {
        printer.value("tipo", type);
        printer.value("nombre", name);
        printer.value("porReferencia", reference);
    }
}
