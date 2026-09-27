package practica.compi2.console;

import java.nio.file.Files;
import java.nio.file.Path;
import practica.compi2.ast.Ast;
import practica.compi2.ast.AstPrinter;
import practica.compi2.ast.programs.Program;
import practica.compi2.ast.programs.YProgram;
import practica.compi2.ast.programs.ZetarianoProgram;
import practica.compi2.c3d.CCodeGenerator;
import practica.compi2.compiler.CompilationResult;
import practica.compi2.compiler.Compiler;

public class ConsoleRunner {

    private final ConsoleOptions options;
    private final AstPrinter astPrinter;
    private final ErrorPrinter errorPrinter;
    private final SymbolPrinter symbolPrinter;
    private final CuartetaPrinter cuartetaPrinter;
    private final TypeReportPrinter typeReportPrinter;

    public ConsoleRunner(ConsoleOptions options) {
        this.options = options;
        this.astPrinter = new AstPrinter();
        this.errorPrinter = new ErrorPrinter();
        this.symbolPrinter = new SymbolPrinter();
        this.cuartetaPrinter = new CuartetaPrinter();
        this.typeReportPrinter = new TypeReportPrinter();
    }

    public void run() {
        Path file = Path.of(options.getPath()).toAbsolutePath().normalize();
        if (!Files.exists(file)) {
            System.out.println("No se encontró el archivo: " + file);
            return;
        }

        CompilationResult compilation = new Compiler().compile(file);
        if (options.isShowAst()) {
            printAst(compilation.getAst());
        }
        if (compilation.getTypeTable() != null) {
            System.out.println("================ TABLA DE SÍMBOLOS ================");
            System.out.println(symbolPrinter.print(compilation.getSymbols()));
            if (options.isShowTypes()) {
                System.out.println(typeReportPrinter.print(compilation.getTypeTable()));
            }
        }
        System.out.println("================ ERRORES ================");
        System.out.println(errorPrinter.print(compilation.getErrors()));
        if (compilation.hasErrors()) {
            return;
        }
        if (!compilation.hasCode()) {
            System.out.println("Para generar código se necesita compilar un archivo .pig");
            return;
        }
        printCode(compilation);
    }

    private void printCode(CompilationResult compilation) {
        if (options.isShowCuartetas()) {
            System.out.println("================ CUARTETAS ================");
            System.out.println(cuartetaPrinter.print(compilation.getCuartetas()));
        }
        if (options.isShowCode()) {
            System.out.println("================ CÓDIGO DE TRES DIRECCIONES ================");
            System.out.println(compilation.getCode());
        }
        CCodeGenerator generator = new CCodeGenerator();
        System.out.println(generator.generateFile(compilation.getCode(), Path.of(System.getProperty("user.dir"))));
    }

    private void printAst(Ast ast) {
        if (ast.getMain() != null) {
            printProgram(ast.getMain());
        }
        for (YProgram program : ast.getYPrograms()) {
            printProgram(program);
        }
        for (ZetarianoProgram program : ast.getClasses()) {
            printProgram(program);
        }
    }

    private void printProgram(Program program) {
        System.out.println("================ AST: " + program.getFileName() + " ================");
        System.out.println(astPrinter.print(program));
    }
}
