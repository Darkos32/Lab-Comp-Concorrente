package SensorAtuador;

public class Leitura {
    private final Integer idLeitura;
    private final Integer idSensor;
    private final Integer valor;

    public Leitura(int idLeitura, int idSensor, int valor) {
        this.idLeitura = idLeitura;
        this.idSensor = idSensor;
        this.valor = valor;
    }

    public Integer getIdLeitura() {
        return idLeitura;
    }

    public Integer getIdSensor() {
        return idSensor;
    }

    public Integer getValor() {
        return valor;
    }
    @Override
    public String toString() {
        String temp = "(Leitura do sensor: " +Integer.toString(idSensor) +" " + "Valor: "+Integer.toString(valor)+")";
        return temp;
    }
}
