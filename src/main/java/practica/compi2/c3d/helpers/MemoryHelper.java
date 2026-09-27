package practica.compi2.c3d.helpers;

import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.HeapAccess;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.access.PointerAccess;
import practica.compi2.c3d.access.StackAccess;
import practica.compi2.c3d.access.TemporalAccess;
import practica.compi2.c3d.cuartetas.Assignation3D;
import practica.compi2.c3d.cuartetas.Operation3D;

public class MemoryHelper {

    public static StackAccess stackSlot(IntermediateCodeUtils u, int offset) {
        TemporalAccess address = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        u.getCuartetas().add(new Operation3D(address, PointerAccess.STACK, new Literal3D(offset), "+"));
        return new StackAccess(address);
    }

    public static HeapAccess heapSlot(IntermediateCodeUtils u, MemoryAccess base, MemoryAccess offset) {
        TemporalAccess address = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        u.getCuartetas().add(new Operation3D(address, base, offset, "+"));
        return new HeapAccess(address);
    }

    public static TemporalAccess load(IntermediateCodeUtils u, MemoryAccess location) {
        TemporalAccess value = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        u.getCuartetas().add(new Assignation3D(value, location));
        return value;
    }

    public static TemporalAccess allocate(IntermediateCodeUtils u, MemoryAccess size) {
        TemporalAccess ptr = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        u.getCuartetas().add(new Assignation3D(ptr, PointerAccess.HEAP));
        u.getCuartetas().add(new Operation3D(PointerAccess.HEAP, PointerAccess.HEAP, size, "+"));
        return ptr;
    }

    public static void pushToHeap(IntermediateCodeUtils u, MemoryAccess value) {
        u.getCuartetas().add(new Assignation3D(new HeapAccess(PointerAccess.HEAP), value));
        u.getCuartetas().add(new Operation3D(PointerAccess.HEAP, PointerAccess.HEAP, new Literal3D(1), "+"));
    }

    public static TemporalAccess thisPointer(IntermediateCodeUtils u) {
        return load(u, stackSlot(u, 1));
    }
}
