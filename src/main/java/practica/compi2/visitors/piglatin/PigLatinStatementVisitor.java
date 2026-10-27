package practica.compi2.visitors.piglatin;

import java.util.ArrayList;
import java.util.List;
import practica.compi2.antlr4.piglatin.PigLatinParser;
import practica.compi2.antlr4.piglatin.PigLatinParserBaseVisitor;
import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.ast.expressions.Literal;
import practica.compi2.ast.statements.Assignation;
import practica.compi2.ast.statements.Block;
import practica.compi2.ast.statements.BreakStmt;
import practica.compi2.ast.statements.ContinueStmt;
import practica.compi2.ast.statements.Declaration;
import practica.compi2.ast.statements.ExpressionStmt;
import practica.compi2.ast.statements.PrintStmt;
import practica.compi2.ast.statements.ReadStmt;
import practica.compi2.ast.statements.Statement;
import practica.compi2.ast.types.TypeReference;

public class PigLatinStatementVisitor extends PigLatinParserBaseVisitor<Statement> {

    private final PigLatinExpressionVisitor expressionVisitor;
    private final PigLatinControlFlowVisitor controlFlowVisitor;
    private final PigLatinTypeMapper typeMapper;

    public PigLatinStatementVisitor() {
        this.expressionVisitor = new PigLatinExpressionVisitor();
        this.controlFlowVisitor = new PigLatinControlFlowVisitor(this, expressionVisitor);
        this.typeMapper = new PigLatinTypeMapper();
    }

    @Override
    public Block visitBlock(PigLatinParser.BlockContext ctx) {
        Block block = new Block(Position.of(ctx));
        for (PigLatinParser.StatementContext stmtCtx : ctx.statement()) {
            block.addStatement(visit(stmtCtx));
        }
        return block;
    }

    @Override
    public Statement visitStatement(PigLatinParser.StatementContext ctx) {
        if (ctx.controlStatement() != null) {
            return controlFlowVisitor.visit(ctx.controlStatement());
        }
        return visit(ctx.getChild(0));
    }

    @Override
    public Declaration visitTypedDeclaration(PigLatinParser.TypedDeclarationContext ctx) {
        TypeReference type = typeMapper.map(ctx.type());
        Expression value = expressionVisitor.visit(ctx.expression());
        return new Declaration(Position.of(ctx), type, ctx.ID().getText(), new ArrayList<>(), value);
    }

    @Override
    public Declaration visitVariableDeclaration(PigLatinParser.VariableDeclarationContext ctx) {
        TypeReference type = null;
        if (ctx.type() != null) {
            type = typeMapper.map(ctx.type());
        }
        Expression value = expressionVisitor.visitOptional(ctx.expression());
        return new Declaration(Position.of(ctx), type, ctx.ID().getText(), new ArrayList<>(), value);
    }

    @Override
    public Declaration visitArrayDeclaration(PigLatinParser.ArrayDeclarationContext ctx) {
        List<Expression> sizes = expressionVisitor.visitList(ctx.dimensions().expression());
        TypeReference type = typeMapper.map(ctx.type());
        type.setDimensions(sizes.size());
        Expression value = expressionVisitor.visitOptional(ctx.expression());
        return new Declaration(Position.of(ctx), type, ctx.ID().getText(), sizes, value);
    }

    @Override
    public Declaration visitForDeclaration(PigLatinParser.ForDeclarationContext ctx) {
        TypeReference type = null;
        if (ctx.type() != null) {
            type = typeMapper.map(ctx.type());
        }
        Expression value = expressionVisitor.visit(ctx.expression());
        return new Declaration(Position.of(ctx), type, ctx.ID().getText(), new ArrayList<>(), value);
    }

    @Override
    public Statement visitForAssignment(PigLatinParser.ForAssignmentContext ctx) {
        return visit(ctx.assignment());
    }

    @Override
    public Statement visitAssignmentStatement(PigLatinParser.AssignmentStatementContext ctx) {
        return visit(ctx.assignment());
    }

    @Override
    public Assignation visitAssignment(PigLatinParser.AssignmentContext ctx) {
        Expression target = expressionVisitor.visit(ctx.expression(0));
        Expression value = expressionVisitor.visit(ctx.expression(1));
        return new Assignation(Position.of(ctx), target, "=", value);
    }

    @Override
    public PrintStmt visitPrintStatement(PigLatinParser.PrintStatementContext ctx) {
        List<Expression> values = expressionVisitor.visitList(ctx.expression());
        return new PrintStmt(Position.of(ctx), values, !endsWithNewLine(values));
    }

    private boolean endsWithNewLine(List<Expression> values) {
        Expression last = values.get(values.size() - 1);
        if (last instanceof Literal) {
            return ((Literal) last).getValue().toString().endsWith("\n");
        }
        return false;
    }

    @Override
    public ReadStmt visitReadStatement(PigLatinParser.ReadStatementContext ctx) {
        Expression target = expressionVisitor.visitOptional(ctx.expression());
        return new ReadStmt(Position.of(ctx), target);
    }

    @Override
    public ContinueStmt visitContinueStatement(PigLatinParser.ContinueStatementContext ctx) {
        return new ContinueStmt(Position.of(ctx), ctx.getStart().getText());
    }

    @Override
    public BreakStmt visitBreakStatement(PigLatinParser.BreakStatementContext ctx) {
        return new BreakStmt(Position.of(ctx), ctx.getStart().getText());
    }

    @Override
    public ExpressionStmt visitExpressionStatement(PigLatinParser.ExpressionStatementContext ctx) {
        Expression expression = expressionVisitor.visit(ctx.expression());
        return new ExpressionStmt(Position.of(ctx), expression);
    }
}
