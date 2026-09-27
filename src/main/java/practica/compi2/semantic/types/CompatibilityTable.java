package practica.compi2.semantic.types;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CompatibilityTable {

    public static final List<String> GROUPS = List.of("+", "- * /", "%", "< > <= >=", "== !=", "&& ||", "=");
    public static final List<TypeKind> PRIMITIVES = List.of(
            TypeKind.INTEGER, TypeKind.FLOAT, TypeKind.CHAR, TypeKind.BOOLEAN, TypeKind.STRING);
    protected static final List<TypeKind> NUMERIC = List.of(TypeKind.INTEGER, TypeKind.FLOAT, TypeKind.CHAR);

    private final Map<String, TypeKind> rules;
    private final Map<TypeKind, String> typeNames;

    public CompatibilityTable() {
        this.rules = new HashMap<>();
        this.typeNames = new HashMap<>();
    }

    public abstract String getLanguage();

    public TypeKind find(String operator, TypeKind left, TypeKind right) {
        return findInGroup(groupOf(operator), left, right);
    }

    public TypeKind findInGroup(String group, TypeKind left, TypeKind right) {
        return rules.get(key(group, left, right));
    }

    public boolean hasRules(String group) {
        for (String key : rules.keySet()) {
            if (key.startsWith(group + "|")) {
                return true;
            }
        }
        return false;
    }

    public String nameOf(TypeKind kind) {
        return typeNames.get(kind);
    }

    protected void setTypeName(TypeKind kind, String name) {
        typeNames.put(kind, name);
    }

    protected void addRule(String group, TypeKind left, TypeKind right, TypeKind resultKind) {
        rules.put(key(group, left, right), resultKind);
    }

    protected void addCommonRules() {
        for (TypeKind left : NUMERIC) {
            for (TypeKind right : NUMERIC) {
                TypeKind widest = widest(left, right);
                addRule("+", left, right, widest);
                addRule("- * /", left, right, widest);
                addRule("< > <= >=", left, right, TypeKind.BOOLEAN);
                addRule("== !=", left, right, TypeKind.BOOLEAN);
            }
        }
        for (TypeKind other : PRIMITIVES) {
            addRule("+", TypeKind.STRING, other, TypeKind.STRING);
            addRule("+", other, TypeKind.STRING, TypeKind.STRING);
        }
        addRule("== !=", TypeKind.STRING, TypeKind.STRING, TypeKind.BOOLEAN);
        addRule("== !=", TypeKind.BOOLEAN, TypeKind.BOOLEAN, TypeKind.BOOLEAN);
        addRule("&& ||", TypeKind.BOOLEAN, TypeKind.BOOLEAN, TypeKind.BOOLEAN);
        addAssignmentRules();
    }

    private void addAssignmentRules() {
        for (TypeKind kind : PRIMITIVES) {
            addRule("=", kind, kind, kind);
        }
        addRule("=", TypeKind.FLOAT, TypeKind.INTEGER, TypeKind.FLOAT);
        addRule("=", TypeKind.FLOAT, TypeKind.CHAR, TypeKind.FLOAT);
        addRule("=", TypeKind.INTEGER, TypeKind.CHAR, TypeKind.INTEGER);
    }

    private TypeKind widest(TypeKind left, TypeKind right) {
        if (left == TypeKind.FLOAT || right == TypeKind.FLOAT) {
            return TypeKind.FLOAT;
        }
        return TypeKind.INTEGER;
    }

    public static String groupOf(String operator) {
        switch (operator) {
            case "-", "*", "/" -> {
                return "- * /";
            }
            case "<", ">", "<=", ">=" -> {
                return "< > <= >=";
            }
            case "==", "!=" -> {
                return "== !=";
            }
            case "&&", "||" -> {
                return "&& ||";
            }
            default -> {
                return operator;
            }
        }
    }

    private String key(String group, TypeKind left, TypeKind right) {
        return group + "|" + left + "|" + right;
    }
}
