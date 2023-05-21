package dao;

/**
 Builder для клиента, читающего данные с сервера HTTP экземпляра класса {@code KVClient}
 */
public class KVClientBuilder {
    private int port;//порт 8078

    public KVClientBuilder() {
    }

    public KVClientBuilder port(int port) {
        this.port = port;
        return this;
    }

    public KVClient create() {
        return new KVClient(port);
    }
}