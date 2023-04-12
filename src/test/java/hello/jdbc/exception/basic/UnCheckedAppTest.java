package hello.jdbc.exception.basic;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

public class UnCheckedAppTest {


    @Test
    void unchecked() {
        Controller controller = new Controller();
        Assertions.assertThatThrownBy(() -> controller.request())
                .isInstanceOf(Exception.class); // 이렇게 최상위인 Exception으로 예외를 처리하게되면 상세하게 예외를 처리한 의미가 없어진다.
    }

    static class Controller{
        Service service = new Service();

        //SQLException은 JDBC 관련 exception이다. 추후 JPA를 도입하면 JPA에서 발생하는 Exception으로 모두 바꿔야한다.
        //소스가 100개면 100개 모두 수정해야햔다.. 의존성을 띄게됨.
        public void request() {
            service.logic();
        }
    }

    static class Service {
        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic() {
            repository.call();
            networkClient.call();
        }

    }
    static class NetworkClient {
        public void call() {
            throw new RuntimeConnectException("연결 실패");
        }

    }
    static class Repository {
        public void call() {
            try {
                runSql();
            } catch (SQLException e) {
                throw new RuntimeSQLException(e);  //checked exception을 runtimeException으로 변경하여 처리하여 기존 의존성 문제를 해결.
            }
        }

        public void runSql() throws SQLException {
            throw new SQLException("ex");
        }
    }

    static class RuntimeConnectException extends RuntimeException {
        public RuntimeConnectException(String message) {
            super(message);
        }
    }

    static class RuntimeSQLException extends RuntimeException {
        public RuntimeSQLException(Throwable cause) {
            super(cause);
        }
    }

}
