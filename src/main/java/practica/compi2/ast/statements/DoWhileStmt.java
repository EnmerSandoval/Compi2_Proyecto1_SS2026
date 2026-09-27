package practica.compi2.ast.statements;

import lombok.Getter;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.c3d.IntermediateCodeUtils;
import practica.compi2.c3d.access.MemoryAccess;
import practica.compi2.c3d.cuartetas.LabelDefinition3D;
import practica.compi2.c3d.helpers.ConditionHelper;
import practica.compi2.semantic.SemanticContext;

@Getter
public class DoWhileStmt extends Statement {

    private Statement body;
    private Expression condition;

    public DoWhileStmt(Position position, Statement body, Expression condition) {
        super(position);
        this.body = body;
        this.condition = condition;
    }

    @Override
    public void validate(SemanticContext sc) {
        sc.enterLoop();
        body.validate(sc);
        sc.exitLoop();
        sc.checkCondition(condition, "hacer-mientras");
    }

    @Override
    public void toIntermediateCode(IntermediateCodeUtils u) {
        int bodyLabel = u.getCounter().getAndIncrementLabelCount();
        int conditionLabel = u.getCounter().getAndIncrementLabelCount();
        int endLabel = u.getCounter().getAndIncrementLabelCount();
        u.getCuartetas().add(new LabelDefinition3D(bodyLabel));
        u.enterLoop(conditionLabel, endLabel);
        body.toIntermediateCode(u);
        u.exitLoop();
        u.getCuartetas().add(new LabelDefinition3D(conditionLabel));
        MemoryAccess conditionAccess = condition.toIntermediateCode(u);
        ConditionHelper.jumpIfTrue(u, conditionAccess, bodyLabel, endLabel);
        u.getCuartetas().add(new LabelDefinition3D(endLabel));
    }

    @Override
    public void print(AstPrinter printer) {
        printer.child("cuerpo", body);
        printer.child("condicion", condition);
    }
}
