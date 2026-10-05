package KTGK.de03.service;

public class CommerceException_24133056 extends RuntimeException {
    private final int status;
    public CommerceException_24133056(int status, String message) {
        super(message);
        this.status = status;
    }
    public int getStatus() { return status; }
}
