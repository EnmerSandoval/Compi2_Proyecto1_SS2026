package practica.compi2.ast.types;

import lombok.Getter;
import lombok.Setter;
import practica.compi2.ast.AstNode;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;

@Getter
public class TypeReference extends AstNode {

    private PrimitiveType primitive;
    private String name;
    @Setter
    private int dimensions;

    public TypeReference(Position position, PrimitiveType primitive) {
        super(position);
        this.primitive = primitive;
    }

    public TypeReference(Position position, String name) {
        super(position);
        this.name = name;
    }

    public boolean isPrimitive() {
        return primitive != null;
    }

    public boolean isArray() {
        return dimensions > 0;
    }

    @Override
    public void print(AstPrinter printer) {
        printer.value("tipo", toString());
    }

    @Override
    public String toString() {
        String base = isPrimitive() ? primitive.name() : name;
        return base + "[]".repeat(dimensions);
    }
}
