package hello.jdbc.exception.translator;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static hello.jdbc.connection.ConnectionConst.*;

@Slf4j
public class SpringExceptionTranslatorTest {
    DataSource dataSource;

    @BeforeEach
    void init() {
        dataSource = new DriverManagerDataSource(URL,USERNAME,PASSWORD);
    }

    @Test
    void sqlExceptionErrorCode() {
        String sql = "select bad grammer";

        try {
            Connection con = dataSource.getConnection();
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.executeQuery();
        } catch (SQLException e) {
            //이전에 살펴봤던 SQL ErrorCode를 직접 확인하는 방법이다. 이렇게 직접 예외를 확인하고 하나하나
            //스프링이 만들어준 예외로 변환하는 것은 현실성이 없다. 이렇게 하려면 해당 오류 코드를 확인하고
            //스프링의 예외 체계에 맞추어 예외를 직접 변환해야 할 것이다. 그리고 데이터베이스마다 오류 코드가
            //다르다는 점도 해결해야 한다.
            //그래서 스프링은 예외 변환기를 제공한다.
            Assertions.assertThat(e.getErrorCode()).isEqualTo(42122);  //h2에서
            int errorCode = e.getErrorCode();
            log.info("errorCode = {}",errorCode);
            log.info("error",e);
        }
    }

    @Test
    void exceptionTranslator() {
        String sql = "select bad grammer";

        try {
            Connection con = dataSource.getConnection();
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.executeQuery();
        } catch (SQLException e) {
            Assertions.assertThat(e.getErrorCode()).isEqualTo(42122);
            //org.springframework.jdbc.support.sql-error-codes.xml
            //해당 파일에 데이터베이스 별로 코드값이 다 들어있어서 이걸 보고 찾는다.

            SQLErrorCodeSQLExceptionTranslator exTranslator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
            DataAccessException resultEx = exTranslator.translate("selet", sql, e);
            log.info("resultEx + {}",resultEx);
            Assertions.assertThat(resultEx.getClass()).isEqualTo(BadSqlGrammarException.class);
        }

    }
}
