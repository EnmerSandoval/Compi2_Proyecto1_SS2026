package practica.compi2.ast.expressions.operations;

import lombok.Getter;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.types.Type;
import practica.compi2.semantic.types.TypeRules;

@Getter
public abstract class UnaryOperation extends Expression {

    protected Expression operand;
    protected String operator;

    public UnaryOperation(Position position, Expression operand, String operator) {
        super(position);
        this.operand = operand;
        this.operator = operator;
    }

    @Override
    public Type validate(SemanticContext context) {
        Type operandType = operand.validate(context);
        type = resultType(context.getRules(), operandType);
        if (type.isError() && !operandType.isError()) {
            context.error(position, operator, "No se puede aplicar '" + operator + "' a un valor " + operandType);
        }
        return type;
    }

    protected abstract Type resultType(TypeRules rules, Type operandType);

    @Override
    public void print(AstPrinter printer) {
        printer.value("operador", operator);
        printer.child("operando", operand);
    }
}
