package practica.compi2.ast.statements;

import lombok.Getter;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.cuartetas.Goto3D;
import practica.compi2.semantic.SemanticContext;

@Getter
public class ContinueStmt extends Statement {

    private String keyword;

    public ContinueStmt(Position position, String keyword) {
        super(position);
        this.keyword = keyword;
    }

    @Override
    public void validate(SemanticContext context) {
        if (!context.isInsideLoop()) {
            context.error(position, keyword, "'" + keyword + "' solo puede usarse dentro de un ciclo");
        }
    }

    @Override
    public void toIntermediateCode(IntermediateCodeUtils utils) {
        utils.getCuartetas().add(new Goto3D(utils.getContinueLabel()));
    }

    @Override
    public void print(AstPrinter printer) {
    }
}
