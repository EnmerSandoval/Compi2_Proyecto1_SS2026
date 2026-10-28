package practica.compi2.ast.expressions.operations;

import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.access.TemporalAccess;
import practica.compi2.c3d.cuartetas.Assignation3D;
import practica.compi2.c3d.cuartetas.Goto3D;
import practica.compi2.c3d.cuartetas.LabelDefinition3D;
import practica.compi2.c3d.helpers.ConditionHelper;
import practica.compi2.semantic.types.Type;
import practica.compi2.semantic.types.TypeRules;

public class OrOperation extends Operation {

    public OrOperation(Position position, Expression left, Expression right) {
        super(position, left, right, "||");
    }

    @Override
    protected Type resultType(TypeRules rules, Type leftType, Type rightType) {
        return rules.logical(leftType, rightType);
    }

    @Override
    public MemoryAccess toIntermediateCode(IntermediateCodeUtils utils) {
        TemporalAccess temp = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        int checkRightLabel = utils.getCounter().getAndIncrementLabelCount();
        int trueLabel = utils.getCounter().getAndIncrementLabelCount();
        int falseLabel = utils.getCounter().getAndIncrementLabelCount();
        int endLabel = utils.getCounter().getAndIncrementLabelCount();

        MemoryAccess leftMemoryAccess = left.toIntermediateCode(utils);
        ConditionHelper.jumpIfTrue(utils, leftMemoryAccess, trueLabel, checkRightLabel);
        utils.getCuartetas().add(new LabelDefinition3D(checkRightLabel));
        MemoryAccess rightMemoryAccess = right.toIntermediateCode(utils);
        ConditionHelper.jumpIfTrue(utils, rightMemoryAccess, trueLabel, falseLabel);

        utils.getCuartetas().add(new LabelDefinition3D(trueLabel));
        utils.getCuartetas().add(new Assignation3D(temp, new Literal3D(1)));
        utils.getCuartetas().add(new Goto3D(endLabel));
        utils.getCuartetas().add(new LabelDefinition3D(falseLabel));
        utils.getCuartetas().add(new Assignation3D(temp, new Literal3D(0)));
        utils.getCuartetas().add(new LabelDefinition3D(endLabel));
        return temp;
    }
}
