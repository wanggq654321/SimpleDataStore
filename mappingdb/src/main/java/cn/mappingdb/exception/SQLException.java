package cn.mappingdb.exception;

public class SQLException extends RuntimeException {
    public SQLException() {
    }

    public SQLException(String error) {
        super(error);
    }
}
