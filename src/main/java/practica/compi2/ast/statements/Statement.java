package practica.compi2.ast.statements;

import practica.compi2.ast.AstNode;
import practica.compi2.ast.Position;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.semantic.SemanticContext;

public abstract class Statement extends AstNode {

    public Statement(Position position) {
        super(position);
    }

    public abstract void validate(SemanticContext sc);

    public abstract void toIntermediateCode(IntermediateCodeUtils u);
}
