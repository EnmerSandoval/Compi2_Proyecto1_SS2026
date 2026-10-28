package practica.compi2.c3d.helpers;

import java.util.ArrayList;
import java.util.List;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.access.TemporalAccess;
import practica.compi2.semantic.types.Type;

public class DefaultValueHelper {

    public static MemoryAccess create(IntermediateCodeUtils utils, Type type, List<Expression> sizes) {
        if (!sizes.isEmpty()) {
            List<MemoryAccess> dimensions = new ArrayList<>();
            for (Expression size : sizes) {
                dimensions.add(size.toIntermediateCode(utils));
            }
            TemporalAccess ptr = ArrayHelper.allocate(utils, dimensions);
            Type elementType = type.withDimensions(0);
            if (elementType.isStruct()) {
                ArrayHelper.fillWithStructs(utils, ptr, dimensions, utils.getStruct(elementType.getName()));
            }
            return ptr;
        }
        if (type.isStruct()) {
            return StructHelper.allocate(utils, utils.getStruct(type.getName()));
        }
        return new Literal3D(0);
    }
}
