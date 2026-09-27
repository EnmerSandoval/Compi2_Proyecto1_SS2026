package practica.compi2;

import practica.compi2.console.ConsoleOptions;
import practica.compi2.console.ConsoleRunner;
import practica.compi2.vista.IDERecaf;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            IDERecaf.main(args);
            return;
        }
        ConsoleOptions options = ConsoleOptions.fromArgs(args);
        ConsoleRunner runner = new ConsoleRunner(options);
        runner.run();
    }
}
