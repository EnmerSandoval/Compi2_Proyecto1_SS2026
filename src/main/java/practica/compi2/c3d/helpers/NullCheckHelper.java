package practica.compi2.c3d.helpers;

import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.ast.expressions.SuperAccess;
import practica.compi2.ast.expressions.ThisAccess;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.cuartetas.Conditional3D;
import practica.compi2.c3d.cuartetas.Halt3D;
import practica.compi2.c3d.cuartetas.LabelDefinition3D;
import practica.compi2.c3d.cuartetas.PrintText3D;

public class NullCheckHelper {

    public static void check(IntermediateCodeUtils u, Expression target, MemoryAccess object, Position position) {
        if (!target.getType().isClass() || target instanceof ThisAccess || target instanceof SuperAccess) {
            return;
        }
        int validLabel = u.getCounter().getAndIncrementLabelCount();
        u.getCuartetas().add(new Conditional3D(object, new Literal3D(0), validLabel, "!="));
        u.getCuartetas().add(new PrintText3D("\\nError en tiempo de ejecución: se usó un objeto nulo en la línea "
                + position.getLine() + ", columna " + position.getColumn() + "\\n"));
        u.getCuartetas().add(new Halt3D());
        u.getCuartetas().add(new LabelDefinition3D(validLabel));
    }
}
