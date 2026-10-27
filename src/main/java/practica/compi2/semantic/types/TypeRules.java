package practica.compi2.semantic.types;

import lombok.Setter;
import practica.compi2.semantic.symbols.GlobalTable;

public class TypeRules {

    private final CompatibilityTable table;
    @Setter
    private GlobalTable classes;

    public TypeRules(CompatibilityTable table) {
        this.table = table;
    }

    public Type arithmetic(String operator, Type left, Type right) {
        return fromTable(operator, left, right);
    }

    public Type relational(String operator, Type left, Type right) {
        if (hasError(left, right)) {
            return Type.ERROR;
        }
        if (isEquality(operator) && !bothPrimitive(left, right)) {
            return compareReferences(left, right);
        }
        return fromTable(operator, left, right);
    }

    public Type logical(Type left, Type right) {
        return fromTable("&&", left, right);
    }

    public Type not(Type operand) {
        if (operand.isBoolean()) {
            return Type.BOOLEAN;
        }
        return Type.ERROR;
    }

    public Type negative(Type operand) {
        if (operand.is(TypeKind.FLOAT)) {
            return Type.FLOAT;
        }
        if (operand.isNumeric()) {
            return Type.INTEGER;
        }
        return Type.ERROR;
    }

    public Type increment(Type operand) {
        if (operand.isNumeric()) {
            return operand;
        }
        return Type.ERROR;
    }

    public boolean canCompare(Type left, Type right) {
        if (hasError(left, right)) {
            return true;
        }
        if (bothPrimitive(left, right)) {
            return !fromTable("==", left, right).isError();
        }
        if (left.isNull() || right.isNull()) {
            return canAssign(left, right) || canAssign(right, left);
        }
        return left.sameAs(right) || isSubclass(left, right) || isSubclass(right, left);
    }

    public boolean canAssign(Type target, Type value) {
        if (hasError(target, value)) {
            return true;
        }
        if (value.isNull()) {
            return acceptsNull(target);
        }
        if (bothPrimitive(target, value)) {
            return table.find("=", target.getKind(), value.getKind()) != null;
        }
        return target.sameAs(value) || isSubclass(value, target);
    }

    public Type commonType(Type first, Type second) {
        if (canAssign(first, second)) {
            return first;
        }
        if (canAssign(second, first)) {
            return second;
        }
        return Type.ERROR;
    }

    private boolean isSubclass(Type child, Type parent) {
        return classes != null && child.isClass() && parent.isClass()
                && child.getDimensions() == 0 && parent.getDimensions() == 0
                && classes.isSubclass(child.getName(), parent.getName());
    }

    private Type fromTable(String operator, Type left, Type right) {
        if (hasError(left, right) || !bothPrimitive(left, right)) {
            return Type.ERROR;
        }
        TypeKind resultKind = table.find(operator, left.getKind(), right.getKind());
        if (resultKind == null) {
            return Type.ERROR;
        }
        return Type.fromKind(resultKind);
    }

    private Type compareReferences(Type left, Type right) {
        if (canCompare(left, right)) {
            return Type.BOOLEAN;
        }
        return Type.ERROR;
    }

    private boolean hasError(Type left, Type right) {
        return left.isError() || right.isError();
    }

    private boolean bothPrimitive(Type left, Type right) {
        return left.isPrimitive() && right.isPrimitive();
    }

    private boolean isEquality(String operator) {
        return operator.equals("==") || operator.equals("!=");
    }

    private boolean acceptsNull(Type target) {
        return target.isClass() || target.isArray() || target.isString();
    }
}
