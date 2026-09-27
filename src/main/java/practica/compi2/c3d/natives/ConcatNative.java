package practica.compi2.c3d.natives;

import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.HeapAccess;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.PointerAccess;
import practica.compi2.c3d.access.TemporalAccess;
import practica.compi2.c3d.cuartetas.Assignation3D;
import practica.compi2.c3d.cuartetas.Conditional3D;
import practica.compi2.c3d.cuartetas.Goto3D;
import practica.compi2.c3d.cuartetas.LabelDefinition3D;
import practica.compi2.c3d.cuartetas.Operation3D;
import practica.compi2.c3d.helpers.MemoryHelper;

public class ConcatNative extends NativeFunction {

    public static final String NAME = "nativeConcatenar";

    @Override
    public void generate(IntermediateCodeUtils u) {
        u.startFunction(NAME, 3);
        TemporalAccess first = parameter(u, 1);
        TemporalAccess second = parameter(u, 2);
        TemporalAccess temp = MemoryHelper.load(u, PointerAccess.HEAP);
        copyCharacters(u, first);
        copyCharacters(u, second);
        MemoryHelper.pushToHeap(u, new Literal3D(0));
        returnValue(u, temp);
        u.endFunction();
    }

    private void copyCharacters(IntermediateCodeUtils u, TemporalAccess ptr) {
        TemporalAccess character = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        int loopLabel = u.getCounter().getAndIncrementLabelCount();
        int endLabel = u.getCounter().getAndIncrementLabelCount();
        u.getCuartetas().add(new LabelDefinition3D(loopLabel));
        u.getCuartetas().add(new Assignation3D(character, new HeapAccess(ptr)));
        u.getCuartetas().add(new Conditional3D(character, new Literal3D(0), endLabel, "=="));
        MemoryHelper.pushToHeap(u, character);
        u.getCuartetas().add(new Operation3D(ptr, ptr, new Literal3D(1), "+"));
        u.getCuartetas().add(new Goto3D(loopLabel));
        u.getCuartetas().add(new LabelDefinition3D(endLabel));
    }
}
