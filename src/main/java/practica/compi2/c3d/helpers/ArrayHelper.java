package practica.compi2.c3d.helpers;

import java.util.List;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.HeapAccess;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.access.PointerAccess;
import practica.compi2.c3d.access.TemporalAccess;
import practica.compi2.c3d.cuartetas.Assignation3D;
import practica.compi2.c3d.cuartetas.Conditional3D;
import practica.compi2.c3d.cuartetas.Goto3D;
import practica.compi2.c3d.cuartetas.LabelDefinition3D;
import practica.compi2.c3d.cuartetas.Operation3D;
import practica.compi2.semantic.symbols.StructSymbol;

public class ArrayHelper {

    public static TemporalAccess allocate(IntermediateCodeUtils u, List<MemoryAccess> dimensions) {
        TemporalAccess total = totalSize(u, dimensions);
        TemporalAccess ptr = MemoryHelper.allocate(u, new Literal3D(dimensions.size()));
        for (int i = 0; i < dimensions.size(); i++) {
            HeapAccess header = MemoryHelper.heapSlot(u, ptr, new Literal3D(i));
            u.getCuartetas().add(new Assignation3D(header, dimensions.get(i)));
        }
        u.getCuartetas().add(new Operation3D(PointerAccess.HEAP, PointerAccess.HEAP, total, "+"));
        return ptr;
    }

    public static HeapAccess elementAddress(IntermediateCodeUtils u, MemoryAccess ptr,
            List<MemoryAccess> indexes, int dimensionCount) {
        TemporalAccess offset = MemoryHelper.load(u, indexes.get(0));
        for (int i = 1; i < indexes.size(); i++) {
            TemporalAccess size = MemoryHelper.load(u, MemoryHelper.heapSlot(u, ptr, new Literal3D(i)));
            u.getCuartetas().add(new Operation3D(offset, offset, size, "*"));
            u.getCuartetas().add(new Operation3D(offset, offset, indexes.get(i), "+"));
        }
        u.getCuartetas().add(new Operation3D(offset, offset, new Literal3D(dimensionCount), "+"));
        return MemoryHelper.heapSlot(u, ptr, offset);
    }

    public static void fillWithStructs(IntermediateCodeUtils u, TemporalAccess ptr,
            List<MemoryAccess> dimensions, StructSymbol struct) {
        TemporalAccess total = totalSize(u, dimensions);
        TemporalAccess data = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        TemporalAccess index = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        int loopLabel = u.getCounter().getAndIncrementLabelCount();
        int endLabel = u.getCounter().getAndIncrementLabelCount();
        u.getCuartetas().add(new Operation3D(data, ptr, new Literal3D(dimensions.size()), "+"));
        u.getCuartetas().add(new Assignation3D(index, new Literal3D(0)));
        u.getCuartetas().add(new LabelDefinition3D(loopLabel));
        u.getCuartetas().add(new Conditional3D(index, total, endLabel, ">="));
        TemporalAccess element = StructHelper.allocate(u, struct);
        u.getCuartetas().add(new Assignation3D(MemoryHelper.heapSlot(u, data, index), element));
        u.getCuartetas().add(new Operation3D(index, index, new Literal3D(1), "+"));
        u.getCuartetas().add(new Goto3D(loopLabel));
        u.getCuartetas().add(new LabelDefinition3D(endLabel));
    }

    private static TemporalAccess totalSize(IntermediateCodeUtils u, List<MemoryAccess> dimensions) {
        TemporalAccess total = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        u.getCuartetas().add(new Assignation3D(total, new Literal3D(1)));
        for (MemoryAccess dimension : dimensions) {
            u.getCuartetas().add(new Operation3D(total, total, dimension, "*"));
        }
        return total;
    }
}
