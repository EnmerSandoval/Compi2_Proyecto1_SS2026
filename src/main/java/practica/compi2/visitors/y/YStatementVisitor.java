package practica.compi2.visitors.y;

import java.util.ArrayList;
import java.util.List;
import practica.compi2.antlr4.y.YParser;
import practica.compi2.antlr4.y.YParserBaseVisitor;
import practica.compi2.ast.Position;
import practica.compi2.ast.definitions.StructDefinition;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.ast.statements.Assignation;
import practica.compi2.ast.statements.Block;
import practica.compi2.ast.statements.BreakStmt;
import practica.compi2.ast.statements.ContinueStmt;
import practica.compi2.ast.statements.Declaration;
import practica.compi2.ast.statements.ExpressionStmt;
import practica.compi2.ast.statements.PrintStmt;
import practica.compi2.ast.statements.ReturnStmt;
import practica.compi2.ast.statements.Statement;
import practica.compi2.ast.types.TypeReference;

public class YStatementVisitor extends YParserBaseVisitor<Statement> {

    private final YExpressionVisitor expressionVisitor;
    private final YControlFlowVisitor controlFlowVisitor;
    private final YTypeMapper typeMapper;

    public YStatementVisitor() {
        this.expressionVisitor = new YExpressionVisitor();
        this.controlFlowVisitor = new YControlFlowVisitor(this, expressionVisitor);
        this.typeMapper = new YTypeMapper();
    }

    @Override
    public Block visitBlock(YParser.BlockContext ctx) {
        Block block = new Block(Position.of(ctx));
        for (YParser.StatementContext stmtCtx : ctx.statement()) {
            block.addStatement(visit(stmtCtx));
        }
        return block;
    }

    @Override
    public Statement visitStatement(YParser.StatementContext ctx) {
        if (ctx.controlStatement() != null) {
            return controlFlowVisitor.visit(ctx.controlStatement());
        }
        return visit(ctx.getChild(0));
    }

    @Override
    public StructDefinition visitStructDefinition(YParser.StructDefinitionContext ctx) {
        StructDefinition struct = new StructDefinition(Position.of(ctx), ctx.ID().getText());
        for (YParser.FieldDefinitionContext fieldCtx : ctx.fieldDefinition()) {
            List<Expression> sizes = sizesOf(fieldCtx.dimensions());
            TypeReference type = typeOf(fieldCtx.type(), sizes);
            struct.addField(new Declaration(Position.of(fieldCtx), type, fieldCtx.ID().getText(), sizes, null));
        }
        return struct;
    }

    @Override
    public Statement visitDeclarationStatement(YParser.DeclarationStatementContext ctx) {
        return visit(ctx.declaration());
    }

    @Override
    public Declaration visitDeclaration(YParser.DeclarationContext ctx) {
        Expression value = expressionVisitor.visitOptional(ctx.expression());
        List<Expression> sizes = sizesOf(ctx.dimensions());
        TypeReference type = typeOf(ctx.type(), sizes);
        return new Declaration(Position.of(ctx), type, ctx.ID().getText(), sizes, value);
    }

    @Override
    public Statement visitAssignmentStatement(YParser.AssignmentStatementContext ctx) {
        return visit(ctx.assignment());
    }

    @Override
    public Assignation visitAssignment(YParser.AssignmentContext ctx) {
        Expression target = expressionVisitor.visit(ctx.expression(0));
        Expression value = expressionVisitor.visit(ctx.expression(1));
        return new Assignation(Position.of(ctx), target, "=", value);
    }

    @Override
    public PrintStmt visitPrintStatement(YParser.PrintStatementContext ctx) {
        List<Expression> values = new ArrayList<>();
        values.add(expressionVisitor.visit(ctx.expression()));
        return new PrintStmt(Position.of(ctx), values, true);
    }

    @Override
    public ReturnStmt visitReturnStatement(YParser.ReturnStatementContext ctx) {
        Expression value = expressionVisitor.visitOptional(ctx.expression());
        return new ReturnStmt(Position.of(ctx), value);
    }

    @Override
    public BreakStmt visitBreakStatement(YParser.BreakStatementContext ctx) {
        return new BreakStmt(Position.of(ctx), ctx.getStart().getText());
    }

    @Override
    public ContinueStmt visitContinueStatement(YParser.ContinueStatementContext ctx) {
        return new ContinueStmt(Position.of(ctx), ctx.getStart().getText());
    }

    @Override
    public ExpressionStmt visitExpressionStatement(YParser.ExpressionStatementContext ctx) {
        Expression expression = expressionVisitor.visit(ctx.expression());
        return new ExpressionStmt(Position.of(ctx), expression);
    }

    private List<Expression> sizesOf(YParser.DimensionsContext dimensionsCtx) {
        if (dimensionsCtx == null) {
            return new ArrayList<>();
        }
        return expressionVisitor.visitList(dimensionsCtx.expression());
    }

    private TypeReference typeOf(YParser.TypeContext typeCtx, List<Expression> sizes) {
        TypeReference type = typeMapper.map(typeCtx);
        type.setDimensions(sizes.size());
        return type;
    }
}
