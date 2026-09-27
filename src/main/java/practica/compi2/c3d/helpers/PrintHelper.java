package practica.compi2.c3d.helpers;

import java.util.List;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.cuartetas.Conditional3D;
import practica.compi2.c3d.cuartetas.Goto3D;
import practica.compi2.c3d.cuartetas.LabelDefinition3D;
import practica.compi2.c3d.cuartetas.Print3D;
import practica.compi2.c3d.cuartetas.PrintText3D;
import practica.compi2.c3d.natives.PrintStringNative;
import practica.compi2.semantic.types.Type;
import practica.compi2.semantic.types.TypeKind;

public class PrintHelper {

    public static void print(IntermediateCodeUtils u, MemoryAccess value, Type type) {
        if (type.isString()) {
            CallHelper.call(u, PrintStringNative.NAME, null, List.of(value));
        } else if (type.is(TypeKind.FLOAT)) {
            u.getCuartetas().add(new Print3D(value, "%.2f"));
        } else if (type.is(TypeKind.CHAR)) {
            u.getCuartetas().add(new Print3D(value, "%c"));
        } else if (type.isBoolean()) {
            printBoolean(u, value);
        } else {
            u.getCuartetas().add(new Print3D(value, "%d"));
        }
    }

    public static void printNewLine(IntermediateCodeUtils u) {
        u.getCuartetas().add(new PrintText3D("\\n"));
    }

    private static void printBoolean(IntermediateCodeUtils u, MemoryAccess value) {
        int trueLabel = u.getCounter().getAndIncrementLabelCount();
        int endLabel = u.getCounter().getAndIncrementLabelCount();
        u.getCuartetas().add(new Conditional3D(value, new Literal3D(1), trueLabel, "=="));
        u.getCuartetas().add(new PrintText3D(u.getFalseText()));
        u.getCuartetas().add(new Goto3D(endLabel));
        u.getCuartetas().add(new LabelDefinition3D(trueLabel));
        u.getCuartetas().add(new PrintText3D(u.getTrueText()));
        u.getCuartetas().add(new LabelDefinition3D(endLabel));
    }
}
