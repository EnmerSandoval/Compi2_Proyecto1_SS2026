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
public class ForStmt extends Statement {

    private Statement init;
    private Expression condition;
    private Statement update;
    private Statement body;

    public ForStmt(Position position, Statement init, Expression condition, Statement update, Statement body) {
        super(position);
        this.init = init;
        this.condition = condition;
        this.update = update;
        this.body = body;
    }

    @Override
    public void validate(SemanticContext sc) {
        sc.enterScope();
        if (init != null) {
            init.validate(sc);
        }
        if (condition != null) {
            sc.checkCondition(condition, "para");
        }
        if (update != null) {
            update.validate(sc);
        }
        sc.enterLoop();
        body.validate(sc);
        sc.exitLoop();
        sc.exitScope();
    }

    @Override
    public void toIntermediateCode(IntermediateCodeUtils u) {
        int conditionLabel = u.getCounter().getAndIncrementLabelCount();
        int bodyLabel = u.getCounter().getAndIncrementLabelCount();
        int updateLabel = u.getCounter().getAndIncrementLabelCount();
        int endLabel = u.getCounter().getAndIncrementLabelCount();
        if (init != null) {
            init.toIntermediateCode(u);
        }
        u.getCuartetas().add(new LabelDefinition3D(conditionLabel));
        if (condition != null) {
            MemoryAccess conditionAccess = condition.toIntermediateCode(u);
            ConditionHelper.jumpIfTrue(u, conditionAccess, bodyLabel, endLabel);
        }
        u.getCuartetas().add(new LabelDefinition3D(bodyLabel));
        u.enterLoop(updateLabel, endLabel);
        body.toIntermediateCode(u);
        u.exitLoop();
        u.getCuartetas().add(new LabelDefinition3D(updateLabel));
        if (update != null) {
            update.toIntermediateCode(u);
        }
        u.getCuartetas().add(new Goto3D(conditionLabel));
        u.getCuartetas().add(new LabelDefinition3D(endLabel));
    }

    @Override
    public void print(AstPrinter printer) {
        printer.child("inicio", init);
        printer.child("condicion", condition);
        printer.child("actualizacion", update);
        printer.child("cuerpo", body);
    }
}
