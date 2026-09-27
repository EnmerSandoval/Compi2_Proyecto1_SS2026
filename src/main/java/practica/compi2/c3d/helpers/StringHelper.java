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

    public static TemporalAccess allocateString(IntermediateCodeUtils u, String text) {
        TemporalAccess ptr = MemoryHelper.load(u, PointerAccess.HEAP);
        for (byte b : text.getBytes(StandardCharsets.UTF_8)) {
            MemoryHelper.pushToHeap(u, new Literal3D(Byte.toUnsignedInt(b)));
        }
        MemoryHelper.pushToHeap(u, new Literal3D(0));
        return ptr;
    }

    public static MemoryAccess toText(IntermediateCodeUtils u, MemoryAccess value, Type type) {
        if (type.isString()) {
            return value;
        }
        if (type.is(TypeKind.CHAR)) {
            TemporalAccess ptr = MemoryHelper.load(u, PointerAccess.HEAP);
            MemoryHelper.pushToHeap(u, value);
            MemoryHelper.pushToHeap(u, new Literal3D(0));
            return ptr;
        }
        if (type.isBoolean()) {
            return booleanToText(u, value);
        }
        if (type.is(TypeKind.FLOAT)) {
            return CallHelper.call(u, FloatToStringNative.NAME, null, List.of(value));
        }
        return CallHelper.call(u, IntToStringNative.NAME, null, List.of(value));
    }

    public static MemoryAccess fromText(IntermediateCodeUtils u, MemoryAccess text, Type type) {
        if (type.isString()) {
            return text;
        }
        if (type.is(TypeKind.CHAR)) {
            return MemoryHelper.load(u, new HeapAccess(text));
        }
        if (type.is(TypeKind.FLOAT)) {
            return CallHelper.call(u, StringToFloatNative.NAME, null, List.of(text));
        }
        return CallHelper.call(u, StringToIntNative.NAME, null, List.of(text));
    }

    public static TemporalAccess concat(IntermediateCodeUtils u, MemoryAccess left, MemoryAccess right) {
        return CallHelper.call(u, ConcatNative.NAME, null, List.of(left, right));
    }

    public static TemporalAccess equalsText(IntermediateCodeUtils u, MemoryAccess left, MemoryAccess right) {
        return CallHelper.call(u, CompareStringsNative.NAME, null, List.of(left, right));
    }

    private static TemporalAccess booleanToText(IntermediateCodeUtils u, MemoryAccess value) {
        TemporalAccess temp = new TemporalAccess(u.getCounter().getAndIncrementTemporalCount());
        int trueLabel = u.getCounter().getAndIncrementLabelCount();
        int endLabel = u.getCounter().getAndIncrementLabelCount();
        u.getCuartetas().add(new Conditional3D(value, new Literal3D(1), trueLabel, "=="));
        u.getCuartetas().add(new Assignation3D(temp, allocateString(u, u.getFalseText())));
        u.getCuartetas().add(new Goto3D(endLabel));
        u.getCuartetas().add(new LabelDefinition3D(trueLabel));
        u.getCuartetas().add(new Assignation3D(temp, allocateString(u, u.getTrueText())));
        u.getCuartetas().add(new LabelDefinition3D(endLabel));
        return temp;
    }
}
