package practica.compi2.console;

import practica.compi2.semantic.types.CompatibilityTable;
import practica.compi2.semantic.types.TypeKind;

public class CompatibilityPrinter {

    private static final String CELL = "%-14s";

    public String print(CompatibilityTable table) {
        StringBuilder sb = new StringBuilder();
        sb.append("---------------- Tabla de compatibilidad de ").append(table.getLanguage())
                .append(" ----------------\n");
        for (String group : CompatibilityTable.GROUPS) {
            if (table.hasRules(group)) {
                printGroup(sb, table, group);
            }
        }
        return sb.toString();
    }

    private void printGroup(StringBuilder sb, CompatibilityTable table, String group) {
        boolean assignment = group.equals("=");
        sb.append("\nOperador ").append(group);
        if (assignment) {
            sb.append("   (fila: tipo de la variable, columna: tipo del valor)\n");
        } else {
            sb.append("   (fila: operando izquierdo, columna: operando derecho)\n");
        }
        sb.append(String.format(CELL, ""));
        for (TypeKind right : CompatibilityTable.PRIMITIVES) {
            sb.append("| ").append(String.format(CELL, table.nameOf(right)));
        }
        sb.append("\n");
        for (TypeKind left : CompatibilityTable.PRIMITIVES) {
            sb.append(String.format(CELL, table.nameOf(left)));
            for (TypeKind right : CompatibilityTable.PRIMITIVES) {
                sb.append("| ").append(String.format(CELL, cellText(table, group, left, right)));
            }
            sb.append("\n");
        }
    }

    public String cellText(CompatibilityTable table, String group, TypeKind left, TypeKind right) {
        TypeKind resultKind = table.findInGroup(group, left, right);
        if (resultKind == null) {
            return "error";
        }
        if (group.equals("=")) {
            return "permitido";
        }
        return table.nameOf(resultKind);
    }
}
