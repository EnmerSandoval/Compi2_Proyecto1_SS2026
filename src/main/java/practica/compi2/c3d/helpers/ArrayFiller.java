package practica.compi2.c3d.helpers;

import java.util.ArrayList;
import java.util.List;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.ast.expressions.InitializerList;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.access.TemporalAccess;
import practica.compi2.c3d.cuartetas.Assignation3D;
import practica.compi2.semantic.types.Type;

public class ArrayFiller {

    private final IntermediateCodeUtils u;
    private final TemporalAccess ptr;
    private final int dimensionCount;
    private final Type elementType;

    public ArrayFiller(IntermediateCodeUtils u, TemporalAccess ptr, int dimensionCount, Type elementType) {
        this.u = u;
        this.ptr = ptr;
        this.dimensionCount = dimensionCount;
        this.elementType = elementType;
    }

    public void fill(InitializerList list, List<MemoryAccess> path) {
        List<Expression> elements = list.getElements();
        for (int i = 0; i < elements.size(); i++) {
            List<MemoryAccess> elementPath = new ArrayList<>(path);
            elementPath.add(new Literal3D(i));
            Expression element = elements.get(i);
            if (elementPath.size() < dimensionCount && element instanceof InitializerList) {
                fill((InitializerList) element, elementPath);
            } else if (elementPath.size() == dimensionCount) {
                store(element, elementPath);
            }
        }
    }

    private void store(Expression element, List<MemoryAccess> elementPath) {
        MemoryAccess value = InitializerHelper.valueOf(u, element, elementType);
        MemoryAccess address = ArrayHelper.elementAddress(u, ptr, elementPath, dimensionCount);
        u.getCuartetas().add(new Assignation3D(address, value));
    }
}
