package practica.compi2.semantic.symbols;

import lombok.Getter;

@Getter
public class Frame {

    private String name;
    private int size;

    public Frame(String name, int reservedSlots) {
        this.name = name;
        this.size = reservedSlots;
    }

    public int allocate() {
        size++;
        return size - 1;
    }
}
