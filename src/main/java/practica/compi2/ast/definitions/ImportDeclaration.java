package practica.compi2.ast.definitions;

import java.util.List;
import lombok.Getter;
import practica.compi2.ast.AstNode;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.Position;

@Getter
public class ImportDeclaration extends AstNode {

    private List<String> path;

    public ImportDeclaration(Position position, List<String> path) {
        super(position);
        this.path = path;
    }

    public String getExtension() {
        return path.get(path.size() - 1);
    }

    public String getRelativePath() {
        List<String> folders = path.subList(0, path.size() - 1);
        return String.join("/", folders) + "." + getExtension();
    }

    @Override
    public void print(AstPrinter printer) {
        printer.value("archivo", getRelativePath());
    }
}
