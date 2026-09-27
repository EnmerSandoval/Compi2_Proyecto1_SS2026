package practica.compi2.visitors.piglatin;

import java.util.List;
import org.antlr.v4.runtime.Token;
import practica.compi2.antlr4.piglatin.PigLatinParser;
import practica.compi2.antlr4.piglatin.PigLatinParserBaseVisitor;
import practica.compi2.ast.Position;
import practica.compi2.ast.expressions.Expression;
import practica.compi2.ast.expressions.FunctionCall;
import practica.compi2.ast.expressions.InitializerList;
import practica.compi2.ast.expressions.NewObject;
import practica.compi2.ast.expressions.VariableAccess;
import practica.compi2.visitors.LiteralFactory;

public class PigLatinAtomVisitor extends PigLatinParserBaseVisitor<Expression> {

    private final PigLatinExpressionVisitor expressionVisitor;

    public PigLatinAtomVisitor(PigLatinExpressionVisitor expressionVisitor) {
        this.expressionVisitor = expressionVisitor;
    }

    @Override
    public Expression visitGroupAtom(PigLatinParser.GroupAtomContext ctx) {
        return expressionVisitor.visit(ctx.expression());
    }

    @Override
    public Expression visitLiteralAtom(PigLatinParser.LiteralAtomContext ctx) {
        return visit(ctx.literal());
    }

    @Override
    public Expression visitCallAtom(PigLatinParser.CallAtomContext ctx) {
        List<Expression> arguments = expressionVisitor.createArguments(ctx.arguments());
        return new FunctionCall(Position.of(ctx), ctx.ID().getText(), arguments);
    }

    @Override
    public Expression visitIdAtom(PigLatinParser.IdAtomContext ctx) {
        return new VariableAccess(Position.of(ctx), ctx.ID().getText());
    }

    @Override
    public Expression visitNewObjectAtom(PigLatinParser.NewObjectAtomContext ctx) {
        List<Expression> arguments = expressionVisitor.createArguments(ctx.arguments());
        return new NewObject(Position.of(ctx), ctx.ID().getText(), arguments);
    }

    @Override
    public Expression visitListAtom(PigLatinParser.ListAtomContext ctx) {
        List<Expression> elements = expressionVisitor.visitList(ctx.expression());
        return new InitializerList(Position.of(ctx), elements);
    }

    @Override
    public Expression visitLiteral(PigLatinParser.LiteralContext ctx) {
        Position position = Position.of(ctx);
        Token token = ctx.getStart();
        switch (token.getType()) {
            case PigLatinParser.INT_LIT -> {
                return LiteralFactory.integer(position, token.getText());
            }
            case PigLatinParser.DECIMAL_LIT -> {
                return LiteralFactory.decimal(position, token.getText());
            }
            case PigLatinParser.STRING_LIT -> {
                return LiteralFactory.string(position, token.getText());
            }
            case PigLatinParser.CHAR_LIT -> {
                return LiteralFactory.character(position, token.getText());
            }
            default -> {
                return LiteralFactory.bool(position, token.getType() == PigLatinParser.VERUM);
            }
        }
    }
}
