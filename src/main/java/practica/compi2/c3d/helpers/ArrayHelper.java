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

    public static TemporalAccess allocate(IntermediateCodeUtils utils, List<MemoryAccess> dimensions) {
        TemporalAccess total = totalSize(utils, dimensions);
        TemporalAccess ptr = MemoryHelper.allocate(utils, new Literal3D(dimensions.size()));
        for (int i = 0; i < dimensions.size(); i++) {
            HeapAccess header = MemoryHelper.heapSlot(utils, ptr, new Literal3D(i));
            utils.getCuartetas().add(new Assignation3D(header, dimensions.get(i)));
        }
        utils.getCuartetas().add(new Operation3D(PointerAccess.HEAP, PointerAccess.HEAP, total, "+"));
        MemoryHelper.checkHeapLimit(utils);
        return ptr;
    }

    public static HeapAccess elementAddress(IntermediateCodeUtils utils, MemoryAccess ptr,
            List<MemoryAccess> indexes, int dimensionCount) {
        TemporalAccess offset = MemoryHelper.load(utils, indexes.get(0));
        for (int i = 1; i < indexes.size(); i++) {
            TemporalAccess size = MemoryHelper.load(utils, MemoryHelper.heapSlot(utils, ptr, new Literal3D(i)));
            utils.getCuartetas().add(new Operation3D(offset, offset, size, "*"));
            utils.getCuartetas().add(new Operation3D(offset, offset, indexes.get(i), "+"));
        }
        utils.getCuartetas().add(new Operation3D(offset, offset, new Literal3D(dimensionCount), "+"));
        return MemoryHelper.heapSlot(utils, ptr, offset);
    }

    public static void fillWithStructs(IntermediateCodeUtils utils, TemporalAccess ptr,
            List<MemoryAccess> dimensions, StructSymbol struct) {
        TemporalAccess total = totalSize(utils, dimensions);
        TemporalAccess data = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        TemporalAccess index = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        int loopLabel = utils.getCounter().getAndIncrementLabelCount();
        int endLabel = utils.getCounter().getAndIncrementLabelCount();
        utils.getCuartetas().add(new Operation3D(data, ptr, new Literal3D(dimensions.size()), "+"));
        utils.getCuartetas().add(new Assignation3D(index, new Literal3D(0)));
        utils.getCuartetas().add(new LabelDefinition3D(loopLabel));
        utils.getCuartetas().add(new Conditional3D(index, total, endLabel, ">="));
        TemporalAccess element = StructHelper.allocate(utils, struct);
        utils.getCuartetas().add(new Assignation3D(MemoryHelper.heapSlot(utils, data, index), element));
        utils.getCuartetas().add(new Operation3D(index, index, new Literal3D(1), "+"));
        utils.getCuartetas().add(new Goto3D(loopLabel));
        utils.getCuartetas().add(new LabelDefinition3D(endLabel));
    }

    private static TemporalAccess totalSize(IntermediateCodeUtils utils, List<MemoryAccess> dimensions) {
        TemporalAccess total = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        utils.getCuartetas().add(new Assignation3D(total, new Literal3D(1)));
        for (MemoryAccess dimension : dimensions) {
            utils.getCuartetas().add(new Operation3D(total, total, dimension, "*"));
        }
        return total;
    }
}
