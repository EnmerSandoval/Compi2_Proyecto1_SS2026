package practica.compi2.ast.expressions.operations;

import lombok.Getter;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.access.TemporalAccess;
import practica.compi2.c3d.cuartetas.Assignation3D;
import practica.compi2.c3d.cuartetas.Goto3D;
import practica.compi2.c3d.cuartetas.LabelDefinition3D;
import practica.compi2.c3d.helpers.ConditionHelper;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.types.Type;

@Getter
public class TernaryOperation extends Expression {

    private Expression condition;
    private Expression trueValue;
    private Expression falseValue;

    public TernaryOperation(Position position, Expression condition, Expression trueValue, Expression falseValue) {
        super(position);
        this.condition = condition;
        this.trueValue = trueValue;
        this.falseValue = falseValue;
    }

    @Override
    public Type validate(SemanticContext sc) {
        sc.checkCondition(condition, "operador ternario");
        Type trueType = trueValue.validate(sc);
        Type falseType = falseValue.validate(sc);
        type = sc.getRules().commonType(trueType, falseType);
        if (type.isError() && !trueType.isError() && !falseType.isError()) {
            sc.error(position, "?", "Los dos valores del ternario deben ser compatibles: "
                    + trueType + " y " + falseType);
        }
        return type;
    }

    @Override
    public MemoryAccess toIntermediateCode(IntermediateCodeUtils u) {
        TemporalAccess temp = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        int trueLabel = u.getCounter().getAndIncrementLabelCount();
        int falseLabel = u.getCounter().getAndIncrementLabelCount();
        int endLabel = u.getCounter().getAndIncrementLabelCount();
        MemoryAccess conditionAccess = condition.toIntermediateCode(u);
        ConditionHelper.jumpIfTrue(u, conditionAccess, trueLabel, falseLabel);
        u.getCuartetas().add(new LabelDefinition3D(trueLabel));
        u.getCuartetas().add(new Assignation3D(temp, trueValue.toIntermediateCode(u)));
        u.getCuartetas().add(new Goto3D(endLabel));
        u.getCuartetas().add(new LabelDefinition3D(falseLabel));
        u.getCuartetas().add(new Assignation3D(temp, falseValue.toIntermediateCode(u)));
        u.getCuartetas().add(new LabelDefinition3D(endLabel));
        return temp;
    }

    @Override
    public void print(AstPrinter printer) {
        printer.child("condicion", condition);
        printer.child("siVerdadero", trueValue);
        printer.child("siFalso", falseValue);
    }
}
