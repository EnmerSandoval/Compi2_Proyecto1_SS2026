package practica.compi2.c3d.natives;

import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.HeapAccess;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.access.PointerAccess;
import practica.compi2.c3d.access.StackAccess;
import practica.compi2.c3d.access.TemporalAccess;
import practica.compi2.c3d.cuartetas.Assignation3D;
import practica.compi2.c3d.cuartetas.Conditional3D;
import practica.compi2.c3d.cuartetas.Goto3D;
import practica.compi2.c3d.cuartetas.LabelDefinition3D;
import practica.compi2.c3d.cuartetas.Operation3D;
import practica.compi2.c3d.helpers.MemoryHelper;

public abstract class NativeFunction {

    public abstract void generate(IntermediateCodeUtils utils);

    protected TemporalAccess parameter(IntermediateCodeUtils utils, int position) {
        return MemoryHelper.load(utils, MemoryHelper.stackSlot(utils, position));
    }

    protected void returnValue(IntermediateCodeUtils utils, MemoryAccess value) {
        utils.getCuartetas().add(new Assignation3D(new StackAccess(PointerAccess.STACK), value));
    }

    protected void readSign(IntermediateCodeUtils utils, TemporalAccess ptr, TemporalAccess sign) {
        TemporalAccess first = MemoryHelper.load(utils, new HeapAccess(ptr));
        int positiveLabel = utils.getCounter().getAndIncrementLabelCount();
        utils.getCuartetas().add(new Assignation3D(sign, new Literal3D(1)));
        utils.getCuartetas().add(new Conditional3D(first, new Literal3D(45), positiveLabel, "!="));
        utils.getCuartetas().add(new Assignation3D(sign, new Literal3D(-1)));
        utils.getCuartetas().add(new Operation3D(ptr, ptr, new Literal3D(1), "+"));
        utils.getCuartetas().add(new LabelDefinition3D(positiveLabel));
    }

    protected TemporalAccess readIntegerPart(IntermediateCodeUtils utils, TemporalAccess ptr, TemporalAccess value) {
        TemporalAccess character = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        int loopLabel = utils.getCounter().getAndIncrementLabelCount();
        int endLabel = utils.getCounter().getAndIncrementLabelCount();
        utils.getCuartetas().add(new Assignation3D(value, new Literal3D(0)));
        utils.getCuartetas().add(new LabelDefinition3D(loopLabel));
        utils.getCuartetas().add(new Assignation3D(character, new HeapAccess(ptr)));
        utils.getCuartetas().add(new Conditional3D(character, new Literal3D(48), endLabel, "<"));
        utils.getCuartetas().add(new Conditional3D(character, new Literal3D(57), endLabel, ">"));
        TemporalAccess digit = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        utils.getCuartetas().add(new Operation3D(digit, character, new Literal3D(48), "-"));
        utils.getCuartetas().add(new Operation3D(value, value, new Literal3D(10), "*"));
        utils.getCuartetas().add(new Operation3D(value, value, digit, "+"));
        utils.getCuartetas().add(new Operation3D(ptr, ptr, new Literal3D(1), "+"));
        utils.getCuartetas().add(new Goto3D(loopLabel));
        utils.getCuartetas().add(new LabelDefinition3D(endLabel));
        return character;
    }
}
