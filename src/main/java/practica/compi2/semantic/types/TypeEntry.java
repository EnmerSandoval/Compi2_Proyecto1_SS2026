package practica.compi2.semantic.types;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import practica.compi2.semantic.symbols.Symbol;

@Getter
public class TypeEntry {

    private String name;
    private TypeKind kind;
    private List<Symbol> members;

    public TypeEntry(String name, TypeKind kind, List<Symbol> members) {
        this.name = name;
        this.kind = kind;
        this.members = members;
    }

    public TypeEntry(TypeKind kind) {
        this(kind.getDescription(), kind, new ArrayList<>());
    }

    public boolean isPrimitive() {
        return kind != TypeKind.STRUCT && kind != TypeKind.CLASS;
    }

    public int getSize() {
        if (members.isEmpty()) {
            return 1;
        }
        return members.size();
    }
}
