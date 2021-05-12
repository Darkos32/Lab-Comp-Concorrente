package Trabalho_2;

import java.util.HashMap;
import java.util.Map;

public class Buffer {
    private Leitura buffer[];
    private int length;
    private int proxPos = -1;
    private Map<Integer,Integer> ultimaPosEscrita;
    public Buffer() {
        this.length = 60;
        ultimaPosEscrita = new HashMap<>();
        buffer = new Leitura[this.length];
        if (proxPos < 0) {
            proxPos = 0;
        }
    }

    public Leitura ler(int pos) {
        return this.buffer[pos];
    }

    public int getProxPos() {
        return proxPos;
    }

    public void escrever(Leitura medicao) {
        this.buffer[this.proxPos] = medicao;
    }

    public int getLength() {
        return length;
    }
    public int getUltimaPosEscrita(int id) {
        return ultimaPosEscrita.get(id);
    }
}
