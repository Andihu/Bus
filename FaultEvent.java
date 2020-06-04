public class FaultEvent<T> extends Event<T> {
    public final int code;
    public final String message;

    public FaultEvent(int code, String message, T data) {
        super(data);
        this.code = code;
        this.message = message;
    }
}
