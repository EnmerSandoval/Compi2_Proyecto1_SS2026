package practica.compi2.console;

import java.util.ArrayList;
import java.util.List;
import practica.compi2.semantic.symbols.Symbol;
import practica.compi2.semantic.types.TypeEntry;
import practica.compi2.semantic.types.TypeKind;
import practica.compi2.semantic.types.TypeTable;

public class TypeTablePrinter {

    private static final String FORMAT = "%-14s | %-11s | %-7s | %-42s | %s%n";

    public String print(TypeTable table) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(FORMAT, "Tipo", "Categoría", "Tamaño", "Cómo se guarda", "Miembros (posición)"));
        for (TypeEntry entry : table.getEntries()) {
            sb.append(String.format(FORMAT, entry.getName(), categoryOf(entry), entry.getSize(),
                    storageOf(entry), membersOf(entry)));
        }
        sb.append("Arreglos: puntero a un bloque en el heap con [dimensiones..., datos...] (aplanado por filas)\n");
        return sb.toString();
    }

    private String categoryOf(TypeEntry entry) {
        if (entry.getKind() == TypeKind.STRUCT) {
            return "estructura";
        }
        if (entry.getKind() == TypeKind.CLASS) {
            return "clase";
        }
        return "primitivo";
    }

    private String storageOf(TypeEntry entry) {
        if (entry.getKind() == TypeKind.STRING) {
            return "puntero a los caracteres en el heap";
        }
        if (entry.isPrimitive()) {
            return "valor directo en la celda";
        }
        return "puntero a un bloque de " + entry.getSize() + " celdas en el heap";
    }

    private String membersOf(TypeEntry entry) {
        List<String> names = new ArrayList<>();
        for (Symbol member : entry.getMembers()) {
            names.add(member.getName() + "(" + member.getOffset() + ")");
        }
        return String.join(", ", names);
    }
}
