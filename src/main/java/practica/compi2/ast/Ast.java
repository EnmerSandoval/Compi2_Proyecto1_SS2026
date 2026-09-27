package practica.compi2.ast;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import practica.compi2.ast.programs.PigLatinProgram;
import practica.compi2.ast.programs.YProgram;
import practica.compi2.ast.programs.ZetarianoProgram;

@Getter
public class Ast {

    @Setter
    private PigLatinProgram main;
    private List<YProgram> yPrograms;
    private List<ZetarianoProgram> classes;

    public Ast() {
        this.yPrograms = new ArrayList<>();
        this.classes = new ArrayList<>();
    }

    public void addYProgram(YProgram program) {
        yPrograms.add(program);
    }

    public void addClass(ZetarianoProgram program) {
        classes.add(program);
    }
}
