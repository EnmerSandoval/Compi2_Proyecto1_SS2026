package practica.compi2.semantic.symbols;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

public class Scope {

    @Getter
    private final Scope parent;
    private final Map<String, Symbol> variables;
    private final Map<String, StructSymbol> structs;

    public Scope(Scope parent) {
        this.parent = parent;
        this.variables = new HashMap<>();
        this.structs = new HashMap<>();
    }

    public boolean declare(Symbol symbol) {
        if (variables.containsKey(symbol.getName())) {
            return false;
        }
        variables.put(symbol.getName(), symbol);
        return true;
    }

    public Symbol lookup(String name) {
        Scope current = this;
        while (current != null) {
            if (current.variables.containsKey(name)) {
                return current.variables.get(name);
            }
            current = current.parent;
        }
        return null;
    }

    public boolean declareStruct(StructSymbol struct) {
        if (structs.containsKey(struct.getName())) {
            return false;
        }
        structs.put(struct.getName(), struct);
        return true;
    }

    public StructSymbol lookupStruct(String name) {
        Scope current = this;
        while (current != null) {
            if (current.structs.containsKey(name)) {
                return current.structs.get(name);
            }
            current = current.parent;
        }
        return null;
    }
}
