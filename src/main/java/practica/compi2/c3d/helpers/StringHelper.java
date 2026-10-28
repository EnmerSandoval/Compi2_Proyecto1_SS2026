package practica.compi2.c3d.helpers;

import java.nio.charset.StandardCharsets;
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
import practica.compi2.c3d.natives.CompareStringsNative;
import practica.compi2.c3d.natives.ConcatNative;
import practica.compi2.c3d.natives.FloatToStringNative;
import practica.compi2.c3d.natives.IntToStringNative;
import practica.compi2.c3d.natives.StringToFloatNative;
import practica.compi2.c3d.natives.StringToIntNative;
import practica.compi2.semantic.types.Type;
import practica.compi2.semantic.types.TypeKind;

public class StringHelper {

    public static TemporalAccess allocateString(IntermediateCodeUtils utils, String text) {
        TemporalAccess ptr = MemoryHelper.load(utils, PointerAccess.HEAP);
        for (byte b : text.getBytes(StandardCharsets.UTF_8)) {
            MemoryHelper.pushToHeap(utils, new Literal3D(Byte.toUnsignedInt(b)));
        }
        MemoryHelper.pushToHeap(utils, new Literal3D(0));
        MemoryHelper.checkHeapLimit(utils);
        return ptr;
    }

    public static MemoryAccess toText(IntermediateCodeUtils utils, MemoryAccess value, Type type) {
        if (type.isString()) {
            return value;
        }
        if (type.is(TypeKind.CHAR)) {
            TemporalAccess ptr = MemoryHelper.load(utils, PointerAccess.HEAP);
            MemoryHelper.pushToHeap(utils, value);
            MemoryHelper.pushToHeap(utils, new Literal3D(0));
            MemoryHelper.checkHeapLimit(utils);
            return ptr;
        }
        if (type.isBoolean()) {
            return booleanToText(utils, value);
        }
        if (type.is(TypeKind.FLOAT)) {
            return CallHelper.call(utils, FloatToStringNative.NAME, null, List.of(value));
        }
        return CallHelper.call(utils, IntToStringNative.NAME, null, List.of(value));
    }

    public static MemoryAccess fromText(IntermediateCodeUtils utils, MemoryAccess text, Type type) {
        if (type.isString()) {
            return text;
        }
        if (type.is(TypeKind.CHAR)) {
            return MemoryHelper.load(utils, new HeapAccess(text));
        }
        if (type.is(TypeKind.FLOAT)) {
            return CallHelper.call(utils, StringToFloatNative.NAME, null, List.of(text));
        }
        return CallHelper.call(utils, StringToIntNative.NAME, null, List.of(text));
    }

    public static TemporalAccess concat(IntermediateCodeUtils utils, MemoryAccess left, MemoryAccess right) {
        return CallHelper.call(utils, ConcatNative.NAME, null, List.of(left, right));
    }

    public static TemporalAccess equalsText(IntermediateCodeUtils utils, MemoryAccess left, MemoryAccess right) {
        return CallHelper.call(utils, CompareStringsNative.NAME, null, List.of(left, right));
    }

    private static TemporalAccess booleanToText(IntermediateCodeUtils utils, MemoryAccess value) {
        TemporalAccess temp = new TemporalAccess(utils.getCounter().getAndIncrementTemporalCount());
        int trueLabel = utils.getCounter().getAndIncrementLabelCount();
        int endLabel = utils.getCounter().getAndIncrementLabelCount();
        utils.getCuartetas().add(new Conditional3D(value, new Literal3D(1), trueLabel, "=="));
        utils.getCuartetas().add(new Assignation3D(temp, allocateString(utils, utils.getFalseText())));
        utils.getCuartetas().add(new Goto3D(endLabel));
        utils.getCuartetas().add(new LabelDefinition3D(trueLabel));
        utils.getCuartetas().add(new Assignation3D(temp, allocateString(utils, utils.getTrueText())));
        utils.getCuartetas().add(new LabelDefinition3D(endLabel));
        return temp;
    }
}
