package practica.compi2.ast.programs;

import lombok.Getter;
import practica.compi2.ast.AstNode;
import practica.compi2.ast.Position;

@Getter
public abstract class Program extends AstNode {

    protected String fileName;

    public Program(Position position, String fileName) {
        super(position);
        this.fileName = fileName;
    }
}
