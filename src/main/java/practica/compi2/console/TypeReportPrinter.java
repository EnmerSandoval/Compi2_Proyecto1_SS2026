package practica.compi2.console;

import java.util.List;
import practica.compi2.semantic.types.CompatibilityTable;
import practica.compi2.semantic.types.PigLatinCompatibilityTable;
import practica.compi2.semantic.types.TypeTable;
import practica.compi2.semantic.types.YCompatibilityTable;
import practica.compi2.semantic.types.ZetarianoCompatibilityTable;

public class TypeReportPrinter {

    private final TypeTablePrinter typeTablePrinter;
    private final CompatibilityPrinter compatibilityPrinter;

    public TypeReportPrinter() {
        this.typeTablePrinter = new TypeTablePrinter();
        this.compatibilityPrinter = new CompatibilityPrinter();
    }

    public String print(TypeTable typeTable) {
        StringBuilder sb = new StringBuilder();
        sb.append("================ TABLA DE TIPOS ================\n");
        sb.append(typeTablePrinter.print(typeTable)).append("\n");
        sb.append("================ TABLAS DE COMPATIBILIDAD ================\n");
        List<CompatibilityTable> tables = List.of(
                new YCompatibilityTable(),
                new ZetarianoCompatibilityTable(),
                new PigLatinCompatibilityTable());
        for (CompatibilityTable table : tables) {
            sb.append(compatibilityPrinter.print(table)).append("\n");
        }
        return sb.toString();
    }
}
