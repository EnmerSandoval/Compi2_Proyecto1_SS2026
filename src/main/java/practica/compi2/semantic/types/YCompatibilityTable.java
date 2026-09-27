package practica.compi2.semantic.types;

public class YCompatibilityTable extends CompatibilityTable {

    public YCompatibilityTable() {
        setTypeName(TypeKind.INTEGER, "entero");
        setTypeName(TypeKind.FLOAT, "flotante");
        setTypeName(TypeKind.CHAR, "caracter");
        setTypeName(TypeKind.BOOLEAN, "bool");
        setTypeName(TypeKind.STRING, "cadena");
        addCommonRules();
        addRule("+", TypeKind.CHAR, TypeKind.CHAR, TypeKind.STRING);
    }

    @Override
    public String getLanguage() {
        return "Y?";
    }
}
