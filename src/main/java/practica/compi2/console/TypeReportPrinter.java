package practica.compi2.console;

import practica.compi2.semantic.types.TypeTable;

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
        sb.append("================ TABLA DE COMPATIBILIDAD ================\n");
        sb.append(compatibilityPrinter.print());
        return sb.toString();
    }
}
