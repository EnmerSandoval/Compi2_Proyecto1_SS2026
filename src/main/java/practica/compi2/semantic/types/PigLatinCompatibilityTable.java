package practica.compi2.semantic.types;

public class PigLatinCompatibilityTable extends CompatibilityTable {

    public PigLatinCompatibilityTable() {
        setTypeName(TypeKind.INTEGER, "numerus");
        setTypeName(TypeKind.FLOAT, "decimalis");
        setTypeName(TypeKind.CHAR, "littera");
        setTypeName(TypeKind.BOOLEAN, "verum/falsus");
        setTypeName(TypeKind.STRING, "textum");
        addCommonRules();
        addRule("+", TypeKind.CHAR, TypeKind.CHAR, TypeKind.STRING);
    }

    @Override
    public String getLanguage() {
        return "Pig Latin";
    }
}
