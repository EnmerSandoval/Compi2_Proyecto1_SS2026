package practica.compi2.ast.expressions.operations;

import lombok.Getter;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.types.Type;
import practica.compi2.semantic.types.TypeRules;

@Getter
public abstract class Operation extends Expression {

    protected Expression left;
    protected Expression right;
    protected String operator;

    public Operation(Position position, Expression left, Expression right, String operator) {
        super(position);
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public Type validate(SemanticContext context) {
        Type leftType = left.validate(context);
        Type rightType = right.validate(context);
        type = resultType(context.getRules(), leftType, rightType);
        if (type.isError() && !leftType.isError() && !rightType.isError()) {
            context.error(position, operator, "No se puede aplicar '" + operator + "' entre "
                    + leftType + " y " + rightType);
        }
        return type;
    }

    protected abstract Type resultType(TypeRules rules, Type leftType, Type rightType);

    @Override
    public void print(AstPrinter printer) {
        printer.value("operador", operator);
        printer.child("izquierda", left);
        printer.child("derecha", right);
    }

    public static Operation create(Position position, Expression left, Expression right, String operator) {
        switch (operator) {
            case "+", "-", "*", "/", "%" -> {
                return new ArithmeticOperation(position, left, right, operator);
            }
            case "<", ">", "<=", ">=", "==", "!=" -> {
                return new RelationalOperation(position, left, right, operator);
            }
            case "&&" -> {
                return new AndOperation(position, left, right);
            }
            case "||" -> {
                return new OrOperation(position, left, right);
            }
            default -> throw new IllegalArgumentException("Operador no reconocido: " + operator);
        }
    }
}
