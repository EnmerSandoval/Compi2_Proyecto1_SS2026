package practica.compi2.ast.expressions;

import lombok.Getter;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.helpers.MemoryHelper;
import practica.compi2.c3d.helpers.NullCheckHelper;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.symbols.Symbol;
import practica.compi2.semantic.types.Type;

@Getter
public class FieldAccess extends Expression {

    private Expression target;
    private String field;
    private Symbol fieldSymbol;
    private boolean arrayLength;

    public FieldAccess(Position position, Expression target, String field) {
        super(position);
        this.target = target;
        this.field = field;
    }

    @Override
    public Type validate(SemanticContext context) {
        Type targetType = target.validate(context);
        type = Type.ERROR;
        if (targetType.isError()) {
            return type;
        }
        if (targetType.isArray() && field.equals("length")) {
            arrayLength = true;
            type = Type.INTEGER;
            return type;
        }
        if (!targetType.isStruct() && !targetType.isClass()) {
            context.error(position, field, "Un valor de tipo " + targetType + " no tiene campos");
            return type;
        }
        fieldSymbol = context.findMember(targetType, field);
        if (fieldSymbol == null) {
            context.error(position, field, targetType + " no tiene un campo llamado '" + field + "'");
            return type;
        }
        if (fieldSymbol.getOwnerClass() != null) {
            context.checkAccess(position, field, fieldSymbol.getAccess(), fieldSymbol.getOwnerClass(),
                    "El atributo '" + field + "'");
        }
        type = fieldSymbol.getType();
        return type;
    }

    @Override
    public MemoryAccess toIntermediateCode(IntermediateCodeUtils utils) {
        return MemoryHelper.load(utils, toAddress(utils));
    }

    @Override
    public MemoryAccess toAddress(IntermediateCodeUtils utils) {
        MemoryAccess ptr = target.toIntermediateCode(utils);
        NullCheckHelper.check(utils, target, ptr, position);
        int offset = arrayLength ? 0 : fieldSymbol.getOffset();
        return MemoryHelper.heapSlot(utils, ptr, new Literal3D(offset));
    }

    @Override
    public boolean isAssignable() {
        return !arrayLength;
    }

    @Override
    public void print(AstPrinter printer) {
        printer.child("objeto", target);
        printer.value("campo", field);
    }
}
