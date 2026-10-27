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

    public static TemporalAccess cloneStruct(IntermediateCodeUtils u, MemoryAccess source, StructSymbol struct) {
        TemporalAccess copy = StructHelper.allocate(u, struct);
        copyStruct(u, copy, source, struct, new ArrayList<>());
        return copy;
    }

    public static TemporalAccess cloneArray(IntermediateCodeUtils u, MemoryAccess source, Type type) {
        List<MemoryAccess> dimensions = new ArrayList<>();
        for (int i = 0; i < type.getDimensions(); i++) {
            dimensions.add(MemoryHelper.load(u, MemoryHelper.heapSlot(u, source, new Literal3D(i))));
        }
        TemporalAccess copy = ArrayHelper.allocate(u, dimensions);
        copyArray(u, copy, source, type, new ArrayList<>());
        return copy;
    }

    public static void copyStruct(IntermediateCodeUtils u, MemoryAccess target, MemoryAccess source,
            StructSymbol struct) {
        copyStruct(u, target, source, struct, new ArrayList<>());
    }

    private static void copyStruct(IntermediateCodeUtils u, MemoryAccess target, MemoryAccess source,
            StructSymbol struct, List<String> chain) {
        chain.add(struct.getName());
        for (int i = 0; i < struct.getFields().size(); i++) {
            HeapAccess targetSlot = MemoryHelper.heapSlot(u, target, new Literal3D(i));
            HeapAccess sourceSlot = MemoryHelper.heapSlot(u, source, new Literal3D(i));
            copyValue(u, targetSlot, sourceSlot, struct.getFields().get(i).getType(), chain);
        }
        chain.remove(chain.size() - 1);
    }

    private static void copyValue(IntermediateCodeUtils u, HeapAccess targetSlot, HeapAccess sourceSlot, Type type,
            List<String> chain) {
        if (type.isArray()) {
            copyArray(u, MemoryHelper.load(u, targetSlot), MemoryHelper.load(u, sourceSlot), type, chain);
        } else if (type.isStruct() && !chain.contains(type.getName())) {
            copyStruct(u, MemoryHelper.load(u, targetSlot), MemoryHelper.load(u, sourceSlot),
                    u.getStruct(type.getName()), chain);
        } else {
            u.getCuartetas().add(new Assignation3D(targetSlot, MemoryHelper.load(u, sourceSlot)));
        }
    }

    private static void copyArray(IntermediateCodeUtils u, MemoryAccess target, MemoryAccess source, Type type,
            List<String> chain) {
        int dimensionCount = type.getDimensions();
        TemporalAccess total = smallestTotal(u, target, source, dimensionCount);
        TemporalAccess index = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        TemporalAccess offset = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        int loopLabel = u.getCounter().getAndIncrementLabelCount();
        int endLabel = u.getCounter().getAndIncrementLabelCount();
        u.getCuartetas().add(new Assignation3D(index, new Literal3D(0)));
        u.getCuartetas().add(new LabelDefinition3D(loopLabel));
        u.getCuartetas().add(new Conditional3D(index, total, endLabel, ">="));
        u.getCuartetas().add(new Operation3D(offset, index, new Literal3D(dimensionCount), "+"));
        copyValue(u, MemoryHelper.heapSlot(u, target, offset), MemoryHelper.heapSlot(u, source, offset),
                type.withDimensions(0), chain);
        u.getCuartetas().add(new Operation3D(index, index, new Literal3D(1), "+"));
        u.getCuartetas().add(new Goto3D(loopLabel));
        u.getCuartetas().add(new LabelDefinition3D(endLabel));
    }

    private static TemporalAccess smallestTotal(IntermediateCodeUtils u, MemoryAccess target, MemoryAccess source,
            int dimensionCount) {
        TemporalAccess targetTotal = totalOf(u, target, dimensionCount);
        TemporalAccess sourceTotal = totalOf(u, source, dimensionCount);
        int keepLabel = u.getCounter().getAndIncrementLabelCount();
        u.getCuartetas().add(new Conditional3D(targetTotal, sourceTotal, keepLabel, "<="));
        u.getCuartetas().add(new Assignation3D(targetTotal, sourceTotal));
        u.getCuartetas().add(new LabelDefinition3D(keepLabel));
        return targetTotal;
    }

    private static TemporalAccess totalOf(IntermediateCodeUtils u, MemoryAccess array, int dimensionCount) {
        TemporalAccess total = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        u.getCuartetas().add(new Assignation3D(total, new Literal3D(1)));
        for (int i = 0; i < dimensionCount; i++) {
            TemporalAccess size = MemoryHelper.load(u, MemoryHelper.heapSlot(u, array, new Literal3D(i)));
            u.getCuartetas().add(new Operation3D(total, total, size, "*"));
        }
        return total;
    }
}
