package practica.compi2.visitors.zetariano;

import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.ParserRuleContext;
import practica.compi2.antlr4.zetariano.ZetarianoParser;
import practica.compi2.antlr4.zetariano.ZetarianoParserBaseVisitor;
import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.ArrayAccess;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.ast.expressions.FieldAccess;
import practica.compi2.ast.expressions.MethodCall;
import practica.compi2.ast.expressions.operations.IncDecOperation;
import practica.compi2.ast.expressions.operations.NegativeOperation;
import practica.compi2.ast.expressions.operations.NotOperation;
import practica.compi2.ast.expressions.operations.Operation;
import practica.compi2.ast.expressions.operations.TernaryOperation;

public class ZetarianoExpressionVisitor extends ZetarianoParserBaseVisitor<Expression> {

    private final ZetarianoAtomVisitor atomVisitor;

    public ZetarianoExpressionVisitor() {
        this.atomVisitor = new ZetarianoAtomVisitor(this);
    }

    public List<Expression> visitList(List<ZetarianoParser.ExpressionContext> contexts) {
        List<Expression> expressions = new ArrayList<>();
        for (ZetarianoParser.ExpressionContext ctx : contexts) {
            expressions.add(visit(ctx));
        }
        return expressions;
    }

    public Expression visitOptional(ZetarianoParser.ExpressionContext ctx) {
        if (ctx == null) {
            return null;
        }
        return visit(ctx);
    }

    public List<Expression> createArguments(ZetarianoParser.ArgumentsContext ctx) {
        if (ctx == null) {
            return new ArrayList<>();
        }
        return visitList(ctx.expression());
    }

    @Override
    public Expression visitMethodCallExpr(ZetarianoParser.MethodCallExprContext ctx) {
        Expression target = visit(ctx.expression());
        List<Expression> arguments = createArguments(ctx.arguments());
        return new MethodCall(Position.of(ctx.ID().getSymbol()), target, ctx.ID().getText(), arguments);
    }

    @Override
    public Expression visitFieldAccessExpr(ZetarianoParser.FieldAccessExprContext ctx) {
        Expression target = visit(ctx.expression());
        return new FieldAccess(Position.of(ctx.ID().getSymbol()), target, ctx.ID().getText());
    }

    @Override
    public Expression visitArrayAccessExpr(ZetarianoParser.ArrayAccessExprContext ctx) {
        Expression target = visit(ctx.expression(0));
        Expression index = visit(ctx.expression(1));
        return new ArrayAccess(Position.of(ctx), target, index);
    }

    @Override
    public Expression visitPostIncDecExpr(ZetarianoParser.PostIncDecExprContext ctx) {
        Expression operand = visit(ctx.expression());
        return new IncDecOperation(Position.of(ctx), operand, ctx.op.getText(), false);
    }

    @Override
    public Expression visitPreIncDecExpr(ZetarianoParser.PreIncDecExprContext ctx) {
        Expression operand = visit(ctx.expression());
        return new IncDecOperation(Position.of(ctx), operand, ctx.op.getText(), true);
    }

    @Override
    public Expression visitUnaryExpr(ZetarianoParser.UnaryExprContext ctx) {
        Expression operand = visit(ctx.expression());
        if (ctx.op.getType() == ZetarianoParser.NOT) {
            return new NotOperation(Position.of(ctx), operand);
        }
        return new NegativeOperation(Position.of(ctx), operand);
    }

    @Override
    public Expression visitMulExpr(ZetarianoParser.MulExprContext ctx) {
        return binaryOperation(ctx, ctx.expression(0), ctx.expression(1), ctx.op.getText());
    }

    @Override
    public Expression visitAddExpr(ZetarianoParser.AddExprContext ctx) {
        return binaryOperation(ctx, ctx.expression(0), ctx.expression(1), ctx.op.getText());
    }

    @Override
    public Expression visitRelExpr(ZetarianoParser.RelExprContext ctx) {
        return binaryOperation(ctx, ctx.expression(0), ctx.expression(1), ctx.op.getText());
    }

    @Override
    public Expression visitEqExpr(ZetarianoParser.EqExprContext ctx) {
        return binaryOperation(ctx, ctx.expression(0), ctx.expression(1), ctx.op.getText());
    }

    @Override
    public Expression visitAndExpr(ZetarianoParser.AndExprContext ctx) {
        return binaryOperation(ctx, ctx.expression(0), ctx.expression(1), "&&");
    }

    @Override
    public Expression visitOrExpr(ZetarianoParser.OrExprContext ctx) {
        return binaryOperation(ctx, ctx.expression(0), ctx.expression(1), "||");
    }

    @Override
    public Expression visitTernaryExpr(ZetarianoParser.TernaryExprContext ctx) {
        Expression condition = visit(ctx.expression(0));
        Expression trueValue = visit(ctx.expression(1));
        Expression falseValue = visit(ctx.expression(2));
        return new TernaryOperation(Position.of(ctx), condition, trueValue, falseValue);
    }

    @Override
    public Expression visitAtomExpr(ZetarianoParser.AtomExprContext ctx) {
        return atomVisitor.visit(ctx.atom());
    }

    private Expression binaryOperation(ParserRuleContext ctx, ZetarianoParser.ExpressionContext leftCtx,
            ZetarianoParser.ExpressionContext rightCtx, String operator) {
        Expression left = visit(leftCtx);
        Expression right = visit(rightCtx);
        return Operation.create(Position.of(ctx), left, right, operator);
    }
}
