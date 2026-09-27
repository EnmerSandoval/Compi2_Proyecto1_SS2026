package practica.compi2.console;

import java.util.List;
import practica.compi2.c3d.cuartetas.Cuarteta;

public class CuartetaPrinter {

    private static final String FORMAT = "%-6s | %-10s | %-24s | %-24s | %s%n";

    public String print(List<Cuarteta> cuartetas) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(FORMAT, "#", "Operador", "Argumento 1", "Argumento 2", "Resultado"));
        for (int i = 0; i < cuartetas.size(); i++) {
            String[] row = cuartetas.get(i).toRow();
            sb.append(String.format(FORMAT, i, row[0], row[1], row[2], row[3]));
        }
        return sb.toString();
    }
}
