package practica.compi2.visitors.piglatin;

import practica.compi2.antlr4.piglatin.PigLatinParser;
import practica.compi2.antlr4.piglatin.PigLatinParserBaseVisitor;
import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.ast.statements.Block;
import practica.compi2.ast.statements.DoWhileStmt;
import practica.compi2.ast.statements.ExpressionStmt;
import practica.compi2.ast.statements.ForStmt;
import practica.compi2.ast.statements.IfStmt;
import practica.compi2.ast.statements.Statement;
import practica.compi2.ast.statements.WhileStmt;

public class PigLatinControlFlowVisitor extends PigLatinParserBaseVisitor<Statement> {

    private final PigLatinStatementVisitor statementVisitor;
    private final PigLatinExpressionVisitor expressionVisitor;

    public PigLatinControlFlowVisitor(PigLatinStatementVisitor statementVisitor,
            PigLatinExpressionVisitor expressionVisitor) {
        this.statementVisitor = statementVisitor;
        this.expressionVisitor = expressionVisitor;
    }

    @Override
    public IfStmt visitIfStatement(PigLatinParser.IfStatementContext ctx) {
        Expression condition = expressionVisitor.visit(ctx.expression());
        Block body = statementVisitor.visitBlock(ctx.block());
        Statement elseBranch = createElseBranch(ctx, 0);
        return new IfStmt(Position.of(ctx), condition, body, elseBranch);
    }

    @Override
    public WhileStmt visitWhileStatement(PigLatinParser.WhileStatementContext ctx) {
        Expression condition = expressionVisitor.visit(ctx.expression());
        Block body = statementVisitor.visitBlock(ctx.block());
        return new WhileStmt(Position.of(ctx), condition, body);
    }

    @Override
    public DoWhileStmt visitDoWhileStatement(PigLatinParser.DoWhileStatementContext ctx) {
        Block body = statementVisitor.visitBlock(ctx.block());
        Expression condition = expressionVisitor.visit(ctx.expression());
        return new DoWhileStmt(Position.of(ctx), body, condition);
    }

    @Override
    public ForStmt visitForStatement(PigLatinParser.ForStatementContext ctx) {
        Statement init = null;
        if (ctx.forInit() != null) {
            init = statementVisitor.visit(ctx.forInit());
        }
        Expression condition = expressionVisitor.visitOptional(ctx.expression());
        Statement update = createForUpdate(ctx.forUpdate());
        Block body = statementVisitor.visitBlock(ctx.block());
        return new ForStmt(Position.of(ctx), init, condition, update, body);
    }

    private Statement createElseBranch(PigLatinParser.IfStatementContext ctx, int index) {
        if (index < ctx.elseIfClause().size()) {
            PigLatinParser.ElseIfClauseContext elseIfCtx = ctx.elseIfClause(index);
            Expression condition = expressionVisitor.visit(elseIfCtx.expression());
            Block body = statementVisitor.visitBlock(elseIfCtx.block());
            return new IfStmt(Position.of(elseIfCtx), condition, body, createElseBranch(ctx, index + 1));
        }
        if (ctx.elseClause() != null) {
            return statementVisitor.visitBlock(ctx.elseClause().block());
        }
        return null;
    }

    private Statement createForUpdate(PigLatinParser.ForUpdateContext ctx) {
        if (ctx == null) {
            return null;
        }
        if (ctx.assignment() != null) {
            return statementVisitor.visit(ctx.assignment());
        }
        Expression expression = expressionVisitor.visit(ctx.expression());
        return new ExpressionStmt(Position.of(ctx), expression);
    }
}
