package io.relayr.ble.service.error;

public class UndocumentedException extends Throwable {

    public UndocumentedException(String detailMessage) {
        super(detailMessage);
    }

    public UndocumentedException() {
        super("Undocumented GATT error exception.");
    }
}
