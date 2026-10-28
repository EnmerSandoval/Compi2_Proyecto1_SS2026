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

    public static void print(IntermediateCodeUtils utils, MemoryAccess value, Type type) {
        if (type.isString()) {
            CallHelper.call(utils, PrintStringNative.NAME, null, List.of(value));
        } else if (type.is(TypeKind.FLOAT)) {
            utils.getCuartetas().add(new Print3D(value, "%.2f"));
        } else if (type.is(TypeKind.CHAR)) {
            utils.getCuartetas().add(new Print3D(value, "%c"));
        } else if (type.isBoolean()) {
            printBoolean(utils, value);
        } else {
            utils.getCuartetas().add(new Print3D(value, "%d"));
        }
    }

    public static void printNewLine(IntermediateCodeUtils utils) {
        utils.getCuartetas().add(new PrintText3D("\\n"));
    }

    private static void printBoolean(IntermediateCodeUtils utils, MemoryAccess value) {
        int trueLabel = utils.getCounter().getAndIncrementLabelCount();
        int endLabel = utils.getCounter().getAndIncrementLabelCount();
        utils.getCuartetas().add(new Conditional3D(value, new Literal3D(1), trueLabel, "=="));
        utils.getCuartetas().add(new PrintText3D(utils.getFalseText()));
        utils.getCuartetas().add(new Goto3D(endLabel));
        utils.getCuartetas().add(new LabelDefinition3D(trueLabel));
        utils.getCuartetas().add(new PrintText3D(utils.getTrueText()));
        utils.getCuartetas().add(new LabelDefinition3D(endLabel));
    }
}
