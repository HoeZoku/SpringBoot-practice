package com.example.demo.login.domain.repository.jdbc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import com.example.demo.login.domain.model.User;
import com.example.demo.login.domain.repository.UserDao;


//Bean名をセットすることで@Autowiredする際にどのクラスを使用するか指定できる
@Repository("UserDaoJdbcImpl")
public class UserDaoJdbcImpl implements UserDao {

/*
 * UserDaoインターフェイスを実装するクラス
 * JdbcTemplateはSpringが用意して既にBean定義済。@Autowiredするだけ。このクラスのメソッド を 使ってSQL
 */
    @Autowired
    JdbcTemplate jdbc;
    //暗号化用
   @Autowired
    PasswordEncoder passwordEncoder;


    // Userテーブルの件数を取得.///////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public int count() throws DataAccessException {
        // 全件取得してカウント
        int count = jdbc.queryForObject("SELECT COUNT(*) FROM m_user", Integer.class);

        return count;
    }

    // Userテーブルにデータを1件insert.//////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public int insertOne(User user) throws DataAccessException {
    	//１件登録
    	//JdbcTemplateクラス(=jdbc)でinsertするにはupdateメソッドを仕様。更新削除にも使う。戻り値は登録したレコード数
    	//使い方は第１引数にSQL文第２引数以降にPreparedStatement。
    	//PreparedStatementにはSQL文の？の部分に入れる変数を引数にセット。引数にセットした順番にSQL文に代入される。


    	//暗号化
    	String password = passwordEncoder.encode(user.getPassword());

        int rowNumber = jdbc.update("INSERT INTO m_user(user_id,"
                + " password,"
                + " user_name,"
                + " birthday,"
                + " age,"
                + " marriage,"
                + " role)"
                + " VALUES(?, ?, ?, ?, ?, ?, ?)",
                user.getUserId(),	//m_userテーブルのuser_idにUserServiceクラスから受け取ったuserクラスインスタンスにあるUserID(getter自動生成)をいれる
                password,
                user.getUserName(),
                user.getBirthday(),
                user.getAge(),
                user.isMarriage(),
                user.getRole());

        return rowNumber;
    }

    // Userテーブルのデータを１件取得////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public User selectOne(String userId) throws DataAccessException {
    // １件取得
	//1件のレコードを取得するには、queryForMapメソッドを使います。
    //戻り値はMap<String,Object>型です。第１引数にSQL文、第２引数以降にPreparedStatementを指定します。
    //戻り値のMapのgetメソッドにカラム名を指定することで、値を取得することができます。複数件取得する場合と、使い方はほとんど一緒です。

        Map<String, Object> map = jdbc.queryForMap("SELECT * FROM m_user"
                + " WHERE user_id = ?", userId);

        // 結果返却用の変数
        User user = new User();

        // 取得したデータを結果返却用の変数にセットしていく
        user.setUserId((String) map.get("user_id")); //ユーザーID
        user.setPassword((String) map.get("password")); //パスワード
        user.setUserName((String) map.get("user_name")); //ユーザー名
        user.setBirthday((Date) map.get("birthday")); //誕生日
        user.setAge((Integer) map.get("age")); //年齢
        user.setMarriage((Boolean) map.get("marriage")); //結婚ステータス
        user.setRole((String) map.get("role")); //ロール

        return user;
    }

    // Userテーブルの全データを取得.///////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public List<User> selectMany() throws DataAccessException {
    	// M_USERテーブルのデータを全件取得
    	//カウントの結果やカラムを１つだけ取得してくるような場合にはqueryForObjectメソッドを使う。第１引数にSQL文第２引数に戻り値のオブジェクトのclassを指定。
    	//複数件のselectをする場合は、queryForListメソッドを使います。戻り値の型にはList<Map<String,Object>>を指定します。Listが行を表していて、Mapが列を表しています。
    	//Mapのgetメソッドでテーブルのカラム名を指定することで値を取得できます。引数を追加すれば、PreparedStatementを使うこともできます。
    	//ここでは、拡張for文を使って、List<Map<String,Object>>をList<User>に変換しています。

        List<Map<String, Object>> getList = jdbc.queryForList("SELECT * FROM m_user");

        // 結果返却用の変数
        List<User> userList = new ArrayList<>();

        // 取得したデータを結果返却用のListに格納していく
        for (Map<String, Object> map : getList) {

            //Userインスタンスの生成
            User user = new User();

            // Userインスタンスに取得したデータをセットする
            user.setUserId((String) map.get("user_id")); //ユーザーID
            user.setPassword((String) map.get("password")); //パスワード
            user.setUserName((String) map.get("user_name")); //ユーザー名
            user.setBirthday((Date) map.get("birthday")); //誕生日
            user.setAge((Integer) map.get("age")); //年齢
            user.setMarriage((Boolean) map.get("marriage")); //結婚ステータス
            user.setRole((String) map.get("role")); //ロール

            //結果返却用のListに追加
            userList.add(user);
        }

        return userList;
    }

    // Userテーブルを１件更新.insert同じ///////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public int updateOne(User user) throws DataAccessException {

    	String password = passwordEncoder.encode(user.getPassword());

        //１件更新
        int rowNumber = jdbc.update("UPDATE M_USER"
                + " SET"
                + " password = ?,"
                + " user_name = ?,"
                + " birthday = ?,"
                + " age = ?,"
                + " marriage = ?"
                + " WHERE user_id = ?",
                password,
                user.getUserName(),
                user.getBirthday(),
                user.getAge(),
                user.isMarriage(),
                user.getUserId());

		/*//トランザクション確認のため、わざと例外をthrowする
		        if (rowNumber > 0) {
		            throw new DataAccessException("トランザクションテスト") {
		            };
		        }*/

        return rowNumber;
    }

    // Userテーブルを１件削除.///////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public int deleteOne(String userId) throws DataAccessException {

        //１件削除
    	//ipdate 更新削除にも使う。戻り値は登録したレコード数
        int rowNumber = jdbc.update("DELETE FROM m_user WHERE user_id = ?", userId);

        return rowNumber;
    }

    // SQL取得結果をサーバーにCSVで保存する//////////////////////////////////////////////////////////////////////////////
    @Override
    public void userCsvOut() throws DataAccessException {

    	// M_USERテーブルのデータを全件取得するSQL
         String sql = "SELECT * FROM m_user";

         // ResultSetExtractorの生成
         UserRowCallbackHandler handler = new UserRowCallbackHandler();

         //SQL実行＆CSV出力
         jdbc.query(sql, handler);

    }
}