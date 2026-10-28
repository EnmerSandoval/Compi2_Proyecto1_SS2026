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
    public Type validate(SemanticContext context) {
        context.checkCondition(condition, "operador ternario");
        Type trueType = trueValue.validate(context);
        Type falseType = falseValue.validate(context);
        type = context.getRules().commonType(trueType, falseType);
        if (type.isError() && !trueType.isError() && !falseType.isError()) {
            context.error(position, "?", "Los dos valores del ternario deben ser compatibles: "
                    + trueType + " y " + falseType);
        }
        return type;
    }

    @Override
    public MemoryAccess toIntermediateCode(IntermediateCodeUtils utils) {
        TemporalAccess temp = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        int trueLabel = utils.getCounter().getAndIncrementLabelCount();
        int falseLabel = utils.getCounter().getAndIncrementLabelCount();
        int endLabel = utils.getCounter().getAndIncrementLabelCount();
        MemoryAccess conditionAccess = condition.toIntermediateCode(utils);
        ConditionHelper.jumpIfTrue(utils, conditionAccess, trueLabel, falseLabel);
        utils.getCuartetas().add(new LabelDefinition3D(trueLabel));
        utils.getCuartetas().add(new Assignation3D(temp, trueValue.toIntermediateCode(utils)));
        utils.getCuartetas().add(new Goto3D(endLabel));
        utils.getCuartetas().add(new LabelDefinition3D(falseLabel));
        utils.getCuartetas().add(new Assignation3D(temp, falseValue.toIntermediateCode(utils)));
        utils.getCuartetas().add(new LabelDefinition3D(endLabel));
        return temp;
    }

    @Override
    public void print(AstPrinter printer) {
        printer.child("condicion", condition);
        printer.child("siVerdadero", trueValue);
        printer.child("siFalso", falseValue);
    }
}
