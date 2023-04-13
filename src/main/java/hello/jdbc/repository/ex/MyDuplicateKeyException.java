package hello.jdbc.repository.ex;

public class MyDuplicateKeyException extends MyDbException { //MyDbException을 상속받아 의미있는 계층 형성. 데이터베이스 관련 예외라는 계층.
    public MyDuplicateKeyException() {
    }

    public MyDuplicateKeyException(String message) {
        super(message);
    }

    public MyDuplicateKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    public MyDuplicateKeyException(Throwable cause) {
        super(cause);
    }
}
