package practica.compi2.ast;

import lombok.Getter;

@Getter
public abstract class AstNode {

    protected Position position;

    public AstNode(Position position) {
        this.position = position;
    }

    public abstract void print(AstPrinter printer);
}
