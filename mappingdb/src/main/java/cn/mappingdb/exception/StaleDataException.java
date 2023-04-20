package cn.mappingdb.exception;

public class StaleDataException extends RuntimeException {
    public StaleDataException() {
    }

    public StaleDataException(String description) {
        super(description);
    }
}
