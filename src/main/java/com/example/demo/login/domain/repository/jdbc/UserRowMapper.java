package com.example.demo.login.domain.repository.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.example.demo.login.domain.model.User;


/*RowMapperを使用するには、RowMapper<?>インターフェースを継承（implements）します。
*？の部分には、マッピングに使うJavaオブジェクトのクラスを指定します。RowMapperを継承して、mapRowメソッドをOverrideします。
*引数のResultSetにはSelect結果が入っています。そのため、ResultSetの値をUserクラスにセットします。
*最後にUserクラスのインスタンスをreturnすれば、RowMapperが完成します。RowMapperではSelect結果とUserクラスをあらかじめマッピング できる
*/


public class UserRowMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {

        //戻り値用のUserインスタンスを生成
        User user = new User();

        //ResultSetの取得結果をUserインスタンスにセット
        user.setUserId(rs.getString("user_id"));
        user.setPassword(rs.getString("password"));
        user.setUserName(rs.getString("user_name"));
        user.setBirthday(rs.getDate("birthday"));
        user.setAge(rs.getInt("age"));
        user.setMarriage(rs.getBoolean("marriage"));
        user.setRole(rs.getString("role"));

        return user;
    }
}