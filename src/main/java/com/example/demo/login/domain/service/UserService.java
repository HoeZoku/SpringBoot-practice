package com.example.demo.login.domain.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.example.demo.login.domain.model.User;
import com.example.demo.login.domain.repository.UserDao;

@Service
public class UserService {

    @Autowired
    //＠Autowiredと一緒に@Qualifierを使用する と二つ以上ある場合のBeanを指定できる
    @Qualifier("UserDaoJdbcImpl2")
    UserDao dao;

    /**
     * insert用メソッド.///////////////////////////////////////////////////////////////////////////////////////////////////////////
     */
    public boolean insert(User user) {

        // insert実行
    	//リポジトリークラスのinsertOneメソッド 呼出し
    	//呼び出し先はinserfaceだけど勝手に実装クラスのUserDaoJdbcImplになるの？？
        int rowNumber = dao.insertOne(user);

        // 判定用変数
        boolean result = false;

        if (rowNumber > 0) {
            // insert成功
            result = true;
        }
        return result;
    }

    /**
     * カウント用メソッド.////////////////////////////////////////////////////////////////////////////////////////////////////////
     */
    public int count() {
        return dao.count();
    }

    /**
     * 全件取得用メソッド./////////////////////////////////////////////////////////////////////////////////////////////////////
     */
    public List<User> selectMany() {
        // 全件取得
        return dao.selectMany();
    }

    /**
     * １件取得用メソッド.//////////////////////////////////////////////////////////////////////////////////////////////////////
     */
    public User selectOne(String userId) {
        // selectOne実行
        return dao.selectOne(userId);
    }

    /**
    * １件更新用メソッド.////////////////////////////////////////////////////////////////////////////////////////////////////////
    */
   public boolean updateOne(User user) {

       // 判定用変数
       boolean result = false;

       // １件更新
       int rowNumber = dao.updateOne(user);

       if (rowNumber > 0) {
           // update成功
           result = true;
       }

       return result;
   }

    /**
     * １件削除用メソッド.////////////////////////////////////////////////////////////////////////////////////////////////////////
     */
    public boolean deleteOne(String userId) {
        // １件削除
        int rowNumber = dao.deleteOne(userId);
        // 判定用変数
        boolean result = false;

        if (rowNumber > 0) {
            // delete成功
            result = true;
        }
        return result;
    }



}