package practica.compi2.semantic.types;

import java.util.List;

public class ZetarianoCompatibilityTable extends CompatibilityTable {

    public ZetarianoCompatibilityTable() {
        setTypeName(TypeKind.INTEGER, "int");
        setTypeName(TypeKind.FLOAT, "double");
        setTypeName(TypeKind.CHAR, "char");
        setTypeName(TypeKind.BOOLEAN, "boolean");
        setTypeName(TypeKind.STRING, "String");
        addCommonRules();
        List<TypeKind> integers = List.of(TypeKind.INTEGER, TypeKind.CHAR);
        for (TypeKind left : integers) {
            for (TypeKind right : integers) {
                addRule("%", left, right, TypeKind.INTEGER);
            }
        }
    }

    @Override
    public String getLanguage() {
        return "Zetariano";
    }
}
