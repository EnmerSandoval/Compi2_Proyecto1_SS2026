package practica.compi2.ast.expressions.operations;

import lombok.Getter;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.access.TemporalAccess;
import practica.compi2.c3d.cuartetas.Assignation3D;
import practica.compi2.c3d.cuartetas.Operation3D;
import practica.compi2.c3d.helpers.MemoryHelper;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.types.Type;
import practica.compi2.semantic.types.TypeRules;

@Getter
public class IncDecOperation extends UnaryOperation {

    private boolean prefix;

    public IncDecOperation(Position position, Expression operand, String operator, boolean prefix) {
        super(position, operand, operator);
        this.prefix = prefix;
    }

    public boolean isIncrement() {
        return operator.equals("++");
    }

    @Override
    public Type validate(SemanticContext sc) {
        super.validate(sc);
        if (!operand.isAssignable()) {
            sc.error(position, operator, "El operador '" + operator + "' solo se puede aplicar a variables");
            type = Type.ERROR;
        }
        return type;
    }

    @Override
    protected Type resultType(TypeRules rules, Type operandType) {
        return rules.increment(operandType);
    }

    @Override
    public MemoryAccess toIntermediateCode(IntermediateCodeUtils u) {
        MemoryAccess location = operand.toAddress(u);
        TemporalAccess oldValue = MemoryHelper.load(u, location);
        TemporalAccess newValue = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        String operation = isIncrement() ? "+" : "-";
        u.getCuartetas().add(new Operation3D(newValue, oldValue, new Literal3D(1), operation));
        u.getCuartetas().add(new Assignation3D(location, newValue));
        if (prefix) {
            return newValue;
        }
        return oldValue;
    }

    @Override
    public boolean canBeStatement() {
        return true;
    }

    @Override
    public void print(AstPrinter printer) {
        super.print(printer);
        printer.value("prefijo", prefix);
    }
}
