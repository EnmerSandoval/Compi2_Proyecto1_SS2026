package practica.compi2.c3d.helpers;

import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.access.TemporalAccess;
import practica.compi2.c3d.cuartetas.Assignation3D;
import practica.compi2.c3d.cuartetas.Conditional3D;
import practica.compi2.c3d.cuartetas.Goto3D;
import practica.compi2.c3d.cuartetas.LabelDefinition3D;

public class ConditionHelper {

    public static void jumpIfTrue(IntermediateCodeUtils utils, MemoryAccess condition, int trueLabel, int falseLabel) {
        utils.getCuartetas().add(new Conditional3D(condition, new Literal3D(1), trueLabel, "=="));
        utils.getCuartetas().add(new Goto3D(falseLabel));
    }

    public static TemporalAccess compare(IntermediateCodeUtils utils, MemoryAccess left, MemoryAccess right,
            String operator) {
        TemporalAccess temp = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        int trueLabel = utils.getCounter().getAndIncrementLabelCount();
        int endLabel = utils.getCounter().getAndIncrementLabelCount();
        utils.getCuartetas().add(new Conditional3D(left, right, trueLabel, operator));
        utils.getCuartetas().add(new Assignation3D(temp, new Literal3D(0)));
        utils.getCuartetas().add(new Goto3D(endLabel));
        utils.getCuartetas().add(new LabelDefinition3D(trueLabel));
        utils.getCuartetas().add(new Assignation3D(temp, new Literal3D(1)));
        utils.getCuartetas().add(new LabelDefinition3D(endLabel));
        return temp;
    }
}
