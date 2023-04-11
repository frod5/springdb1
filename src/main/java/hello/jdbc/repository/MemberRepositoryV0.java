package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC - DriverManager 사용
 */
@Slf4j
public class MemberRepositoryV0 {
    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values(?,?)";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();  //db 커넥션
            pstmt = con.prepareStatement(sql);  //sql 문장 생성
            pstmt.setString(1, member.getMemberId()); // 파라미터 세팅
            pstmt.setInt(2,member.getMoney());
            pstmt.executeUpdate(); // SQL 실행
            return member;
        } catch (SQLException e) {
            log.info("db error",e);
            throw e;
        } finally {
            close(con,pstmt,null);
        }
    }

    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = DBConnectionUtil.getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1,memberId);
            rs = pstmt.executeQuery(); //rs = 내부 커서를 이용하여 조회

            if(rs.next()) { // 최초의 커서는 데이터를 가르키고 있지않아 rs.next()를 한번 호출해줘야한다.
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId = " + memberId);
            }

        } catch (SQLException e) {
            log.error("db error",e);
            throw e;
        } finally {
            close(con,pstmt,rs);
        }
    }

    private void close(Connection con, Statement stmt, ResultSet rs) {
        //역순으로 close

        if(rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.error("error",e);
            }
        }

        if(stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.error("error",e);
            }
        }

        if(con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                log.error("error",e);
            }
        }
    }

    private Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }
}
