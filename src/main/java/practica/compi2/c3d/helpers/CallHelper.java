package practica.compi2.c3d.helpers;

import java.util.List;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.access.PointerAccess;
import practica.compi2.c3d.access.StackAccess;
import practica.compi2.c3d.access.TemporalAccess;
import practica.compi2.c3d.cuartetas.Assignation3D;
import practica.compi2.c3d.cuartetas.Call3D;
import practica.compi2.c3d.cuartetas.Operation3D;

public class CallHelper {

    public static TemporalAccess call(IntermediateCodeUtils u, String name, MemoryAccess thisPointer,
            List<MemoryAccess> arguments) {
        Literal3D frameSize = new Literal3D(u.getFrameSize());
        TemporalAccess newFrame = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        u.getCuartetas().add(new Operation3D(newFrame, PointerAccess.STACK, frameSize, "+"));
        int position = 1;
        if (thisPointer != null) {
            storeInFrame(u, newFrame, position, thisPointer);
            position++;
        }
        for (MemoryAccess argument : arguments) {
            storeInFrame(u, newFrame, position, argument);
            position++;
        }
        u.getCuartetas().add(new Operation3D(PointerAccess.STACK, PointerAccess.STACK, frameSize, "+"));
        u.getCuartetas().add(new Call3D(name));
        TemporalAccess temp = MemoryHelper.load(u, new StackAccess(PointerAccess.STACK));
        u.getCuartetas().add(new Operation3D(PointerAccess.STACK, PointerAccess.STACK, frameSize, "-"));
        return temp;
    }

    private static void storeInFrame(IntermediateCodeUtils u, TemporalAccess frame, int position,
            MemoryAccess value) {
        TemporalAccess address = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        u.getCuartetas().add(new Operation3D(address, frame, new Literal3D(position), "+"));
        u.getCuartetas().add(new Assignation3D(new StackAccess(address), value));
    }
}
