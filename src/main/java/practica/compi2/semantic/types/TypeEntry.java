package practica.compi2.semantic.types;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import practica.compi2.semantic.symbols.ClassSymbol;
import practica.compi2.semantic.symbols.Symbol;

@Getter
public class TypeEntry {

    private String name;
    private TypeKind kind;
    private List<Symbol> members;
    private ClassSymbol classSymbol;

    public TypeEntry(String name, TypeKind kind, List<Symbol> members) {
        this.name = name;
        this.kind = kind;
        this.members = members;
    }

    public TypeEntry(TypeKind kind) {
        this(kind.getDescription(), kind, new ArrayList<>());
    }

    public TypeEntry(ClassSymbol classSymbol) {
        this(classSymbol.getName(), TypeKind.CLASS, new ArrayList<>());
        this.classSymbol = classSymbol;
    }

    public boolean isPrimitive() {
        return kind != TypeKind.STRUCT && kind != TypeKind.CLASS;
    }

    public List<Symbol> getMembers() {
        if (classSymbol != null) {
            return classSymbol.getAllAttributes();
        }
        return members;
    }

    public int getSize() {
        if (classSymbol != null) {
            return classSymbol.getSize();
        }
        if (members.isEmpty()) {
            return 1;
        }
        return members.size();
    }
}
