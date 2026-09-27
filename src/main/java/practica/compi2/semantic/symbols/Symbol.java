package practica.compi2.semantic.symbols;

import lombok.Getter;
import lombok.Setter;
import practica.compi2.semantic.types.Type;

@Getter
@Setter
public class Symbol {

    private String name;
    private Type type;
    private SymbolKind kind;
    private int offset;
    private String scope;
    private String file;

    public Symbol(String name, Type type, SymbolKind kind, int offset) {
        this.name = name;
        this.type = type;
        this.kind = kind;
        this.offset = offset;
    }
}
