package practica.compi2.console;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import practica.compi2.semantic.types.CompatibilityTable;
import practica.compi2.semantic.types.PigLatinCompatibilityTable;
import practica.compi2.semantic.types.TypeKind;
import practica.compi2.semantic.types.YCompatibilityTable;
import practica.compi2.semantic.types.ZetarianoCompatibilityTable;

public class CompatibilityPrinter {

    public static final String[] COLUMNS = {"Operador", "Tipo izquierdo", "Tipo derecho", "Resultado", "Lenguaje"};
    private static final String FORMAT = "%-10s | %-14s | %-14s | %-10s | %s%n";

    private final List<CompatibilityTable> tables;
    private final Map<TypeKind, String> typeNames;

    public CompatibilityPrinter() {
        this.tables = List.of(new YCompatibilityTable(), new ZetarianoCompatibilityTable(),
                new PigLatinCompatibilityTable());
        this.typeNames = Map.of(
                TypeKind.INTEGER, "entero",
                TypeKind.FLOAT, "decimal",
                TypeKind.CHAR, "caracter",
                TypeKind.BOOLEAN, "booleano",
                TypeKind.STRING, "cadena");
    }

    public String print() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(FORMAT, (Object[]) COLUMNS));
        for (String[] row : rows()) {
            sb.append(String.format(FORMAT, (Object[]) row));
        }
        return sb.toString();
    }

    public List<String[]> rows() {
        List<String[]> rows = new ArrayList<>();
        for (String group : CompatibilityTable.GROUPS) {
            for (TypeKind left : CompatibilityTable.PRIMITIVES) {
                for (TypeKind right : CompatibilityTable.PRIMITIVES) {
                    addRows(rows, group, left, right);
                }
            }
        }
        return rows;
    }

    private void addRows(List<String[]> rows, String group, TypeKind left, TypeKind right) {
        Map<String, List<String>> languagesByResult = new LinkedHashMap<>();
        for (CompatibilityTable table : tables) {
            TypeKind result = table.findInGroup(group, left, right);
            if (result == null) {
                continue;
            }
            String resultText = group.equals("=") ? "permitido" : typeNames.get(result);
            if (!languagesByResult.containsKey(resultText)) {
                languagesByResult.put(resultText, new ArrayList<>());
            }
            languagesByResult.get(resultText).add(table.getLanguage());
        }
        for (String resultText : languagesByResult.keySet()) {
            List<String> languages = languagesByResult.get(resultText);
            String languageText = languages.size() == tables.size() ? "todos" : String.join(", ", languages);
            rows.add(new String[]{group, typeNames.get(left), typeNames.get(right), resultText, languageText});
        }
    }
}
