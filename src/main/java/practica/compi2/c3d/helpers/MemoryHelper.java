package practica.compi2.c3d.helpers;

import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.HeapAccess;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.access.PointerAccess;
import practica.compi2.c3d.access.StackAccess;
import practica.compi2.c3d.access.TemporalAccess;
import practica.compi2.c3d.cuartetas.Assignation3D;
import practica.compi2.c3d.cuartetas.Conditional3D;
import practica.compi2.c3d.cuartetas.Halt3D;
import practica.compi2.c3d.cuartetas.LabelDefinition3D;
import practica.compi2.c3d.cuartetas.Operation3D;
import practica.compi2.c3d.cuartetas.PrintText3D;
import practica.compi2.riscv.RiscVGenerator;

public class MemoryHelper {

    public static StackAccess stackSlot(IntermediateCodeUtils utils, int offset) {
        TemporalAccess address = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        utils.getCuartetas().add(new Operation3D(address, PointerAccess.STACK, new Literal3D(offset), "+"));
        return new StackAccess(address);
    }

    public static HeapAccess heapSlot(IntermediateCodeUtils utils, MemoryAccess base, MemoryAccess offset) {
        TemporalAccess address = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        utils.getCuartetas().add(new Operation3D(address, base, offset, "+"));
        return new HeapAccess(address);
    }

    public static TemporalAccess load(IntermediateCodeUtils utils, MemoryAccess location) {
        TemporalAccess value = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        utils.getCuartetas().add(new Assignation3D(value, location));
        return value;
    }

    public static TemporalAccess allocate(IntermediateCodeUtils utils, MemoryAccess size) {
        TemporalAccess ptr = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        utils.getCuartetas().add(new Assignation3D(ptr, PointerAccess.HEAP));
        utils.getCuartetas().add(new Operation3D(PointerAccess.HEAP, PointerAccess.HEAP, size, "+"));
        checkHeapLimit(utils);
        return ptr;
    }

    public static void checkHeapLimit(IntermediateCodeUtils utils) {
        int validLabel = utils.getCounter().getAndIncrementLabelCount();
        utils.getCuartetas().add(new Conditional3D(PointerAccess.HEAP, new Literal3D(RiscVGenerator.HEAP_SIZE),
                validLabel, "<"));
        utils.getCuartetas().add(new PrintText3D("Error: heap agotado\\n"));
        utils.getCuartetas().add(new Halt3D());
        utils.getCuartetas().add(new LabelDefinition3D(validLabel));
    }

    public static void pushToHeap(IntermediateCodeUtils utils, MemoryAccess value) {
        utils.getCuartetas().add(new Assignation3D(new HeapAccess(PointerAccess.HEAP), value));
        utils.getCuartetas().add(new Operation3D(PointerAccess.HEAP, PointerAccess.HEAP, new Literal3D(1), "+"));
    }

    public static TemporalAccess thisPointer(IntermediateCodeUtils utils) {
        return load(utils, stackSlot(utils, 1));
    }
}
