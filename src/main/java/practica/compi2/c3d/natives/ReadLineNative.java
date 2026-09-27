package practica.compi2.c3d.natives;

import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.PointerAccess;
import practica.compi2.c3d.access.TemporalAccess;
import practica.compi2.c3d.cuartetas.Conditional3D;
import practica.compi2.c3d.cuartetas.Goto3D;
import practica.compi2.c3d.cuartetas.LabelDefinition3D;
import practica.compi2.c3d.cuartetas.ReadChar3D;
import practica.compi2.c3d.helpers.MemoryHelper;

public class ReadLineNative extends NativeFunction {

    public static final String NAME = "nativeLeerLinea";

    @Override
    public void generate(IntermediateCodeUtils u) {
        u.startFunction(NAME, 1);
        TemporalAccess temp = MemoryHelper.load(u, PointerAccess.HEAP);
        TemporalAccess character = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        int loopLabel = u.getCounter().getAndIncrementLabelCount();
        int endLabel = u.getCounter().getAndIncrementLabelCount();
        u.getCuartetas().add(new LabelDefinition3D(loopLabel));
        u.getCuartetas().add(new ReadChar3D(character));
        u.getCuartetas().add(new Conditional3D(character, new Literal3D(10), endLabel, "=="));
        u.getCuartetas().add(new Conditional3D(character, new Literal3D(-1), endLabel, "=="));
        u.getCuartetas().add(new Conditional3D(character, new Literal3D(13), loopLabel, "=="));
        MemoryHelper.pushToHeap(u, character);
        u.getCuartetas().add(new Goto3D(loopLabel));
        u.getCuartetas().add(new LabelDefinition3D(endLabel));
        MemoryHelper.pushToHeap(u, new Literal3D(0));
        returnValue(u, temp);
        u.endFunction();
    }
}
