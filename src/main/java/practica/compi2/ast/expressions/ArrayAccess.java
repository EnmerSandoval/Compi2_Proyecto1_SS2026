package practica.compi2.ast.expressions;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.helpers.ArrayHelper;
import practica.compi2.c3d.helpers.MemoryHelper;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.types.Type;
import practica.compi2.semantic.types.TypeKind;

@Getter
public class ArrayAccess extends Expression {

    private Expression target;
    private Expression index;

    public ArrayAccess(Position position, Expression target, Expression index) {
        super(position);
        this.target = target;
        this.index = index;
    }

    @Override
    public Type validate(SemanticContext sc) {
        Type targetType = target.validate(sc);
        Type indexType = index.validate(sc);
        type = Type.ERROR;
        if (!indexType.isError() && !indexType.is(TypeKind.INTEGER)) {
            sc.error(index.getPosition(), "", "El índice de un arreglo debe ser entero y es " + indexType);
        }
        if (targetType.isError()) {
            return type;
        }
        if (!targetType.isArray()) {
            sc.error(position, "", "Un valor de tipo " + targetType + " no es un arreglo");
            return type;
        }
        type = targetType.elementType();
        return type;
    }

    @Override
    public MemoryAccess toIntermediateCode(IntermediateCodeUtils u) {
        return MemoryHelper.load(u, toAddress(u));
    }

    @Override
    public MemoryAccess toAddress(IntermediateCodeUtils u) {
        List<Expression> indexExpressions = new ArrayList<>();
        Expression array = this;
        while (array instanceof ArrayAccess) {
            ArrayAccess access = (ArrayAccess) array;
            indexExpressions.add(0, access.getIndex());
            array = access.getTarget();
        }
        MemoryAccess ptr = array.toIntermediateCode(u);
        List<MemoryAccess> indexes = new ArrayList<>();
        for (Expression indexExpression : indexExpressions) {
            indexes.add(indexExpression.toIntermediateCode(u));
        }
        return ArrayHelper.elementAddress(u, ptr, indexes, array.getType().getDimensions());
    }

    @Override
    public boolean isAssignable() {
        return true;
    }

    @Override
    public void print(AstPrinter printer) {
        printer.child("arreglo", target);
        printer.child("indice", index);
    }
}
