package com.example.demo.login.domain.repository.jdbc;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.demo.login.domain.model.User;

//BeanPropertyRowMapperではデータベースから取得してきたカラム名と同一のフィールド名がクラスにあれば自動でマッピングをしてくれます。
//RowMapperのようにどのカラムとどのフィールドを一致させるか、いちいち用意する必要がありません。
//ただし、自動でマッピングするためには■カラム名は単語をスネーク(user_id)■フィールド名はキャメル(String userId;)

@Repository("UserDaoJdbcImpl3")
public class UserDaoJdbcImpl3 extends UserDaoJdbcImpl {

    @Autowired
    private JdbcTemplate jdbc;

    //ユーザー１件取得
    @Override
    public User selectOne(String userId) {

        //１件取得用SQL
        String sql = "SELECT * FROM m_user WHERE user_id = ?";

        //RowMapperの生成
        RowMapper<User> rowMapper = new BeanPropertyRowMapper<User>(User.class);

        //SQL実行
        return jdbc.queryForObject(sql, rowMapper, userId);
    }

    //ユーザー全件取得
    @Override
    public List<User> selectMany() {

        //M_USERテーブルのデータを全件取得するSQL
        String sql = "SELECT * FROM m_user";

        //RowMapperの生成
        RowMapper<User> rowMapper = new BeanPropertyRowMapper<User>(User.class);

        //SQL実行
        return jdbc.query(sql, rowMapper);
    }
}