package practica.compi2.ast.statements;

import lombok.Getter;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.cuartetas.Goto3D;
import practica.compi2.c3d.cuartetas.LabelDefinition3D;
import practica.compi2.c3d.helpers.ConditionHelper;
import practica.compi2.semantic.SemanticContext;

@Getter
public class WhileStmt extends Statement {

    private Expression condition;
    private Statement body;

    public WhileStmt(Position position, Expression condition, Statement body) {
        super(position);
        this.condition = condition;
        this.body = body;
    }

    @Override
    public void validate(SemanticContext sc) {
        sc.checkCondition(condition, "mientras");
        sc.enterLoop();
        body.validate(sc);
        sc.exitLoop();
    }

    @Override
    public void toIntermediateCode(IntermediateCodeUtils u) {
        int startLabel = u.getCounter().getAndIncrementLabelCount();
        int bodyLabel = u.getCounter().getAndIncrementLabelCount();
        int endLabel = u.getCounter().getAndIncrementLabelCount();
        u.getCuartetas().add(new LabelDefinition3D(startLabel));
        MemoryAccess conditionAccess = condition.toIntermediateCode(u);
        ConditionHelper.jumpIfTrue(u, conditionAccess, bodyLabel, endLabel);
        u.getCuartetas().add(new LabelDefinition3D(bodyLabel));
        u.enterLoop(startLabel, endLabel);
        body.toIntermediateCode(u);
        u.exitLoop();
        u.getCuartetas().add(new Goto3D(startLabel));
        u.getCuartetas().add(new LabelDefinition3D(endLabel));
    }

    @Override
    public void print(AstPrinter printer) {
        printer.child("condicion", condition);
        printer.child("cuerpo", body);
    }
}
