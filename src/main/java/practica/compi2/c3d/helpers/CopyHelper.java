package practica.compi2.c3d.helpers;

import java.util.ArrayList;
import java.util.List;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.HeapAccess;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.access.TemporalAccess;
import practica.compi2.c3d.cuartetas.Assignation3D;
import practica.compi2.c3d.cuartetas.Conditional3D;
import practica.compi2.c3d.cuartetas.Goto3D;
import practica.compi2.c3d.cuartetas.LabelDefinition3D;
import practica.compi2.c3d.cuartetas.Operation3D;
import practica.compi2.semantic.symbols.StructSymbol;
import practica.compi2.semantic.types.Type;

public class CopyHelper {

    public static TemporalAccess cloneStruct(IntermediateCodeUtils utils, MemoryAccess source, StructSymbol struct) {
        TemporalAccess copy = StructHelper.allocate(utils, struct);
        copyStruct(utils, copy, source, struct, new ArrayList<>());
        return copy;
    }

    public static TemporalAccess cloneArray(IntermediateCodeUtils utils, MemoryAccess source, Type type) {
        List<MemoryAccess> dimensions = new ArrayList<>();
        for (int i = 0; i < type.getDimensions(); i++) {
            dimensions.add(MemoryHelper.load(utils, MemoryHelper.heapSlot(utils, source, new Literal3D(i))));
        }
        TemporalAccess copy = ArrayHelper.allocate(utils, dimensions);
        copyArray(utils, copy, source, type, new ArrayList<>());
        return copy;
    }

    public static void copyStruct(IntermediateCodeUtils utils, MemoryAccess target, MemoryAccess source,
            StructSymbol struct) {
        copyStruct(utils, target, source, struct, new ArrayList<>());
    }

    private static void copyStruct(IntermediateCodeUtils utils, MemoryAccess target, MemoryAccess source,
            StructSymbol struct, List<String> chain) {
        chain.add(struct.getName());
        for (int i = 0; i < struct.getFields().size(); i++) {
            HeapAccess targetSlot = MemoryHelper.heapSlot(utils, target, new Literal3D(i));
            HeapAccess sourceSlot = MemoryHelper.heapSlot(utils, source, new Literal3D(i));
            copyValue(utils, targetSlot, sourceSlot, struct.getFields().get(i).getType(), chain);
        }
        chain.remove(chain.size() - 1);
    }

    private static void copyValue(IntermediateCodeUtils utils, HeapAccess targetSlot, HeapAccess sourceSlot, Type type,
            List<String> chain) {
        if (type.isArray()) {
            copyArray(utils, MemoryHelper.load(utils, targetSlot), MemoryHelper.load(utils, sourceSlot), type, chain);
        } else if (type.isStruct() && !chain.contains(type.getName())) {
            copyStruct(utils, MemoryHelper.load(utils, targetSlot), MemoryHelper.load(utils, sourceSlot),
                    utils.getStruct(type.getName()), chain);
        } else {
            utils.getCuartetas().add(new Assignation3D(targetSlot, MemoryHelper.load(utils, sourceSlot)));
        }
    }

    private static void copyArray(IntermediateCodeUtils utils, MemoryAccess target, MemoryAccess source, Type type,
            List<String> chain) {
        int dimensionCount = type.getDimensions();
        TemporalAccess total = smallestTotal(utils, target, source, dimensionCount);
        TemporalAccess index = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        TemporalAccess offset = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        int loopLabel = utils.getCounter().getAndIncrementLabelCount();
        int endLabel = utils.getCounter().getAndIncrementLabelCount();
        utils.getCuartetas().add(new Assignation3D(index, new Literal3D(0)));
        utils.getCuartetas().add(new LabelDefinition3D(loopLabel));
        utils.getCuartetas().add(new Conditional3D(index, total, endLabel, ">="));
        utils.getCuartetas().add(new Operation3D(offset, index, new Literal3D(dimensionCount), "+"));
        copyValue(utils, MemoryHelper.heapSlot(utils, target, offset), MemoryHelper.heapSlot(utils, source, offset),
                type.withDimensions(0), chain);
        utils.getCuartetas().add(new Operation3D(index, index, new Literal3D(1), "+"));
        utils.getCuartetas().add(new Goto3D(loopLabel));
        utils.getCuartetas().add(new LabelDefinition3D(endLabel));
    }

    private static TemporalAccess smallestTotal(IntermediateCodeUtils utils, MemoryAccess target, MemoryAccess source,
            int dimensionCount) {
        TemporalAccess targetTotal = totalOf(utils, target, dimensionCount);
        TemporalAccess sourceTotal = totalOf(utils, source, dimensionCount);
        int keepLabel = utils.getCounter().getAndIncrementLabelCount();
        utils.getCuartetas().add(new Conditional3D(targetTotal, sourceTotal, keepLabel, "<="));
        utils.getCuartetas().add(new Assignation3D(targetTotal, sourceTotal));
        utils.getCuartetas().add(new LabelDefinition3D(keepLabel));
        return targetTotal;
    }

    private static TemporalAccess totalOf(IntermediateCodeUtils utils, MemoryAccess array, int dimensionCount) {
        TemporalAccess total = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        utils.getCuartetas().add(new Assignation3D(total, new Literal3D(1)));
        for (int i = 0; i < dimensionCount; i++) {
            TemporalAccess size = MemoryHelper.load(utils, MemoryHelper.heapSlot(utils, array, new Literal3D(i)));
            utils.getCuartetas().add(new Operation3D(total, total, size, "*"));
        }
        return total;
    }
}
