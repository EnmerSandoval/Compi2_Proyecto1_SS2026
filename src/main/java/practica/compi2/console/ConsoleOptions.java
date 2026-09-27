package practica.compi2.console;

import lombok.Getter;

@Getter
public class ConsoleOptions {

    private String path;
    private boolean showAst;
    private boolean showCuartetas;
    private boolean showCode;
    private boolean showTypes;

    public ConsoleOptions() {
        this.path = "main.pig";
    }

    public static ConsoleOptions fromArgs(String[] args) {
        ConsoleOptions options = new ConsoleOptions();
        for (String arg : args) {
            switch (arg) {
                case "--ast" -> options.showAst = true;
                case "--cuartetas" -> options.showCuartetas = true;
                case "--c3d" -> options.showCode = true;
                case "--tipos" -> options.showTypes = true;
                default -> options.path = arg;
            }
        }
        return options;
    }
}
