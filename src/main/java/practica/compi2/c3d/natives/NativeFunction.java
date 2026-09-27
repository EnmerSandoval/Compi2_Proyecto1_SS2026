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

    public abstract void generate(IntermediateCodeUtils u);

    protected TemporalAccess parameter(IntermediateCodeUtils u, int position) {
        return MemoryHelper.load(u, MemoryHelper.stackSlot(u, position));
    }

    protected void returnValue(IntermediateCodeUtils u, MemoryAccess value) {
        u.getCuartetas().add(new Assignation3D(new StackAccess(PointerAccess.STACK), value));
    }

    protected void readSign(IntermediateCodeUtils u, TemporalAccess ptr, TemporalAccess sign) {
        TemporalAccess first = MemoryHelper.load(u, new HeapAccess(ptr));
        int positiveLabel = u.getCounter().getAndIncrementLabelCount();
        u.getCuartetas().add(new Assignation3D(sign, new Literal3D(1)));
        u.getCuartetas().add(new Conditional3D(first, new Literal3D(45), positiveLabel, "!="));
        u.getCuartetas().add(new Assignation3D(sign, new Literal3D(-1)));
        u.getCuartetas().add(new Operation3D(ptr, ptr, new Literal3D(1), "+"));
        u.getCuartetas().add(new LabelDefinition3D(positiveLabel));
    }

    protected TemporalAccess readIntegerPart(IntermediateCodeUtils u, TemporalAccess ptr, TemporalAccess value) {
        TemporalAccess character = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        int loopLabel = u.getCounter().getAndIncrementLabelCount();
        int endLabel = u.getCounter().getAndIncrementLabelCount();
        u.getCuartetas().add(new Assignation3D(value, new Literal3D(0)));
        u.getCuartetas().add(new LabelDefinition3D(loopLabel));
        u.getCuartetas().add(new Assignation3D(character, new HeapAccess(ptr)));
        u.getCuartetas().add(new Conditional3D(character, new Literal3D(48), endLabel, "<"));
        u.getCuartetas().add(new Conditional3D(character, new Literal3D(57), endLabel, ">"));
        TemporalAccess digit = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        u.getCuartetas().add(new Operation3D(digit, character, new Literal3D(48), "-"));
        u.getCuartetas().add(new Operation3D(value, value, new Literal3D(10), "*"));
        u.getCuartetas().add(new Operation3D(value, value, digit, "+"));
        u.getCuartetas().add(new Operation3D(ptr, ptr, new Literal3D(1), "+"));
        u.getCuartetas().add(new Goto3D(loopLabel));
        u.getCuartetas().add(new LabelDefinition3D(endLabel));
        return character;
    }
}
