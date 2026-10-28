package practica.compi2.semantic;

import java.util.ArrayList;
import java.util.List;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.ast.expressions.InitializerList;
import practica.compi2.ast.expressions.Literal;
import practica.compi2.ast.expressions.ReadExpression;
import practica.compi2.semantic.symbols.StructSymbol;
import practica.compi2.semantic.symbols.Symbol;
import practica.compi2.semantic.types.Type;

public class AssignmentChecker {

    private final SemanticContext context;

    public AssignmentChecker(SemanticContext context) {
        this.context = context;
    }

    public void check(Expression value, Type expected, List<Expression> sizes) {
        checkValue(value, expected, sizes, 0);
    }

    private void checkValue(Expression value, Type expected, List<Expression> sizes, int level) {
        if (value instanceof InitializerList) {
            checkList((InitializerList) value, expected, sizes, level);
            return;
        }
        Type valueType = value.validate(context);
        if (value instanceof ReadExpression read && read.convertTo(expected)) {
            return;
        }
        if (!context.getRules().canAssign(expected, valueType)) {
            context.error(value.getPosition(), "",
                    "Se esperaba un valor de tipo " + expected + " pero se encontró " + valueType);
        }
    }

    private void checkList(InitializerList list, Type expected, List<Expression> sizes, int level) {
        list.setType(expected);
        if (expected.isError()) {
            return;
        }
        if (expected.isArray()) {
            checkArray(list, expected, sizes, level);
        } else if (expected.isStruct()) {
            checkStruct(list, expected);
        } else {
            context.error(list.getPosition(), "", "No se puede usar una lista {...} para un valor de tipo " + expected);
        }
    }

    private void checkArray(InitializerList list, Type expected, List<Expression> sizes, int level) {
        List<Expression> elements = list.getElements();
        int declaredSize = constantSize(sizes, level);
        if (declaredSize >= 0 && elements.size() > declaredSize) {
            context.error(list.getPosition(), "", "El arreglo tiene " + declaredSize
                    + " posiciones pero se dieron " + elements.size() + " valores");
        }
        for (Expression element : elements) {
            checkValue(element, expected.elementType(), sizes, level + 1);
        }
    }

    private void checkStruct(InitializerList list, Type expected) {
        StructSymbol struct = context.lookupStruct(expected.getName());
        List<Symbol> fields = struct.getFields();
        List<Expression> elements = list.getElements();
        if (elements.size() != fields.size()) {
            context.error(list.getPosition(), expected.getName(), "La estructura " + expected.getName()
                    + " tiene " + fields.size() + " campos pero se dieron " + elements.size() + " valores");
        }
        for (int i = 0; i < elements.size() && i < fields.size(); i++) {
            checkValue(elements.get(i), fields.get(i).getType(), new ArrayList<>(), 0);
        }
    }

    private int constantSize(List<Expression> sizes, int level) {
        if (level >= sizes.size() || !(sizes.get(level) instanceof Literal)) {
            return -1;
        }
        Object value = ((Literal) sizes.get(level)).getValue();
        if (value instanceof Integer) {
            return (Integer) value;
        }
        return -1;
    }
}
