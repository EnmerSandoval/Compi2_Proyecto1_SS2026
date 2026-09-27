package practica.compi2.ast.expressions;

import lombok.Getter;
import lombok.Setter;
import practica.compi2.ast.AstNode;
import practica.compi2.ast.Position;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.semantic.SemanticContext;
import practica.compi2.semantic.types.Type;

@Getter
@Setter
public abstract class Expression extends AstNode {

    protected Type type;

    public Expression(Position position) {
        super(position);
    }

    public abstract Type validate(SemanticContext sc);

    public abstract MemoryAccess toIntermediateCode(IntermediateCodeUtils u);

    public MemoryAccess toAddress(IntermediateCodeUtils u) {
        throw new UnsupportedOperationException("Esta expresión no tiene una dirección de memoria");
    }

    public boolean isAssignable() {
        return false;
    }

    public boolean isConstant() {
        return false;
    }

    public boolean canBeStatement() {
        return false;
    }
}
