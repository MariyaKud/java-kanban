package dao;

public class KVClientBuilder {
    private int port;//порт 8078

    public KVClientBuilder() {
    }

    public KVClientBuilder port(int port) {
        this.port = port;
        return this;
    }

    public KVClient create() {
        KVClient kvClient = new KVClient(port);
        kvClient.register();
        return kvClient;
    }
}