package dao;

public static class KVClientBuilder {
    int port;

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