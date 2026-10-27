package practica.compi2.semantic.types;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import practica.compi2.semantic.symbols.ClassSymbol;
import practica.compi2.semantic.symbols.StructSymbol;

public class TypeTable {

    private final Map<String, TypeEntry> entries;

    public TypeTable() {
        this.entries = new LinkedHashMap<>();
        for (TypeKind kind : CompatibilityTable.PRIMITIVES) {
            TypeEntry entry = new TypeEntry(kind);
            entries.put(entry.getName(), entry);
        }
    }

    public void registerStruct(StructSymbol struct) {
        entries.put(struct.getName(), new TypeEntry(struct.getName(), TypeKind.STRUCT, struct.getFields()));
    }

    public void registerClass(ClassSymbol classSymbol) {
        entries.put(classSymbol.getName(), new TypeEntry(classSymbol));
    }

    public TypeEntry get(String name) {
        return entries.get(name);
    }

    public int sizeOf(String name) {
        return entries.get(name).getSize();
    }

    public Collection<TypeEntry> getEntries() {
        return entries.values();
    }
}
