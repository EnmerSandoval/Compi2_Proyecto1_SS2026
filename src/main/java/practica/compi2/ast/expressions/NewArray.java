package practica.compi2.ast.expressions;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.ast.types.TypeReference;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.helpers.ArrayHelper;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.types.Type;
import practica.compi2.semantic.types.TypeKind;

@Getter
public class NewArray extends Expression {

    private TypeReference baseType;
    private List<Expression> sizes;

    public NewArray(Position position, TypeReference baseType, List<Expression> sizes) {
        super(position);
        this.baseType = baseType;
        this.sizes = sizes;
    }

    @Override
    public Type validate(SemanticContext context) {
        for (Expression size : sizes) {
            Type sizeType = size.validate(context);
            if (!sizeType.isError() && !sizeType.is(TypeKind.INTEGER)) {
                context.error(size.getPosition(), "", "El tamaño de un arreglo debe ser entero y es " + sizeType);
            }
        }
        type = context.resolveType(baseType);
        return type;
    }

    @Override
    public MemoryAccess toIntermediateCode(IntermediateCodeUtils utils) {
        List<MemoryAccess> dimensions = new ArrayList<>();
        for (Expression size : sizes) {
            dimensions.add(size.toIntermediateCode(utils));
        }
        return ArrayHelper.allocate(utils, dimensions);
    }

    @Override
    public void print(AstPrinter printer) {
        printer.value("tipo", baseType);
        printer.children("dimensiones", sizes);
    }
}
