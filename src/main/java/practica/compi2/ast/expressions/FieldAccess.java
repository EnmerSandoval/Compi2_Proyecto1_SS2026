package practica.compi2.ast.expressions;

import lombok.Getter;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.Literal3D;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.helpers.MemoryHelper;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.symbols.Symbol;
import practica.compi2.semantic.types.Type;

@Getter
public class FieldAccess extends Expression {

    private Expression target;
    private String field;
    private Symbol fieldSymbol;

    public FieldAccess(Position position, Expression target, String field) {
        super(position);
        this.target = target;
        this.field = field;
    }

    @Override
    public Type validate(SemanticContext sc) {
        Type targetType = target.validate(sc);
        type = Type.ERROR;
        if (targetType.isError()) {
            return type;
        }
        if (!targetType.isStruct() && !targetType.isClass()) {
            sc.error(position, field, "Un valor de tipo " + targetType + " no tiene campos");
            return type;
        }
        fieldSymbol = sc.findMember(targetType, field);
        if (fieldSymbol == null) {
            sc.error(position, field, targetType + " no tiene un campo llamado '" + field + "'");
            return type;
        }
        type = fieldSymbol.getType();
        return type;
    }

    @Override
    public MemoryAccess toIntermediateCode(IntermediateCodeUtils u) {
        return MemoryHelper.load(u, toAddress(u));
    }

    @Override
    public MemoryAccess toAddress(IntermediateCodeUtils u) {
        MemoryAccess ptr = target.toIntermediateCode(u);
        return MemoryHelper.heapSlot(u, ptr, new Literal3D(fieldSymbol.getOffset()));
    }

    @Override
    public boolean isAssignable() {
        return true;
    }

    @Override
    public void print(AstPrinter printer) {
        printer.child("objeto", target);
        printer.value("campo", field);
    }
}
