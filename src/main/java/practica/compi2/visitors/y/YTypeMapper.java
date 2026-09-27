package practica.compi2.visitors.y;

import practica.compi2.antlr4.y.YParser;
import practica.compi2.ast.Position;
import practica.compi2.ast.types.PrimitiveType;
import practica.compi2.ast.types.TypeReference;

public class YTypeMapper {

    public TypeReference map(YParser.TypeContext ctx) {
        Position position = Position.of(ctx);
        switch (ctx.getStart().getType()) {
            case YParser.ENTERO -> {
                return new TypeReference(position, PrimitiveType.INTEGER);
            }
            case YParser.FLOTANTE -> {
                return new TypeReference(position, PrimitiveType.FLOAT);
            }
            case YParser.CADENA -> {
                return new TypeReference(position, PrimitiveType.STRING);
            }
            case YParser.CARACTER -> {
                return new TypeReference(position, PrimitiveType.CHAR);
            }
            case YParser.BOOL -> {
                return new TypeReference(position, PrimitiveType.BOOLEAN);
            }
            default -> {
                return new TypeReference(position, ctx.getText());
            }
        }
    }
}
