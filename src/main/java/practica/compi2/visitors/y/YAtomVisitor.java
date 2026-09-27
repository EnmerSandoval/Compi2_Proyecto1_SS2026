package practica.compi2.visitors.y;

import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.Token;
import practica.compi2.antlr4.y.YParser;
import practica.compi2.antlr4.y.YParserBaseVisitor;
import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.ast.expressions.FunctionCall;
import practica.compi2.ast.expressions.InitializerList;
import practica.compi2.ast.expressions.ReadExpression;
import practica.compi2.ast.expressions.VariableAccess;
import practica.compi2.visitors.LiteralFactory;

public class YAtomVisitor extends YParserBaseVisitor<Expression> {

    private final YExpressionVisitor expressionVisitor;

    public YAtomVisitor(YExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
    }

    @Override
    public Expression visitGroupAtom(YParser.GroupAtomContext ctx) {
        return expressionVisitor.visit(ctx.expression());
    }

    @Override
    public Expression visitLiteralAtom(YParser.LiteralAtomContext ctx) {
        return visit(ctx.literal());
    }

    @Override
    public Expression visitCallAtom(YParser.CallAtomContext ctx) {
        List<Expression> arguments = arguments(ctx.arguments());
        return new FunctionCall(Position.of(ctx), ctx.ID().getText(), arguments);
    }

    @Override
    public Expression visitIdAtom(YParser.IdAtomContext ctx) {
        return new VariableAccess(Position.of(ctx), ctx.ID().getText());
    }

    @Override
    public Expression visitReadAtom(YParser.ReadAtomContext ctx) {
        return new ReadExpression(Position.of(ctx));
    }

    @Override
    public Expression visitListAtom(YParser.ListAtomContext ctx) {
        List<Expression> elements = expressionVisitor.visitList(ctx.expression());
        return new InitializerList(Position.of(ctx), elements);
    }

    @Override
    public Expression visitLiteral(YParser.LiteralContext ctx) {
        Position position = Position.of(ctx);
        Token token = ctx.getStart();
        switch (token.getType()) {
            case YParser.INT_LIT -> {
                return LiteralFactory.integer(position, token.getText());
            }
            case YParser.DECIMAL_LIT -> {
                return LiteralFactory.decimal(position, token.getText());
            }
            case YParser.STRING_LIT -> {
                return LiteralFactory.string(position, token.getText());
            }
            case YParser.CHAR_LIT -> {
                return LiteralFactory.character(position, token.getText());
            }
            default -> {
                return LiteralFactory.bool(position, token.getType() == YParser.VERDADERO);
            }
        }
    }

    private List<Expression> arguments(YParser.ArgumentsContext ctx) {
        if (ctx == null) {
            return new ArrayList<>();
        }
        return expressionVisitor.visitList(ctx.expression());
    }
}
