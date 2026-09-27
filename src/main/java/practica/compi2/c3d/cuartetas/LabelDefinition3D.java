package practica.compi2.c3d.cuartetas;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class LabelDefinition3D extends Cuarteta {

    private Integer label;

    @Override
    public void toCCode(StringBuilder sb) {
        sb.append("et").append(label).append(":\n");
    }

    @Override
    public String[] toRow() {
        return new String[]{"etiqueta", "", "", "et" + label};
    }
}
