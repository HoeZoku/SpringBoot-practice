package com.example.demo;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


//セキュリティ設定用クラスには＠EnableWebSecurityを付ける。更にWebSecurityConfigurerAdapterクラスを継承。
//このクラスの各種メソッドをオーバーライドすることでセキュリティ設定ができる。セキュリティ用にBean定義を行うこともあるため@Configuration

@EnableWebSecurity
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	//パスワードエンコーダー
	/*パスワードを暗号化したり復号するインターフェースとしてPasswordEncoderというインターフェースがSpringで用意されている。
	そのPasswordEncoderを実装したBCryptPasswordEncoderのインスタンスを返すBean定義。
	Bean定義しているのはユーザー登録のリポジトリークラスなどで使う(Autowiredする)から。
	他にも実装クラスは暗号化アルゴリズムによっていくつかある*/
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		//静的リソースへのアクセスには、セキュリティを適用しない
		web.ignoring().antMatchers("/webjars/∗∗", "/css/∗∗");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// ログイン不要ページの設定
		/* ∗２つで正規表現でいずれかの意。直リンクを禁止するためにhttp.authorizeRequests()にメソッドチェーンでリンク禁止先の条件を追加
    	antMatchersメソッドの引数にリンク先をセットすることで個別設定anyRequestメソッドで全てのリンク先が対象。
    	authenticatedメソッドで認証しないとアクセスできないように設定。下記のこのように設定するとpermitAllしたリンク先以外の直リンクを禁止。
    	メソッドチェーンでは上から順番に設定がされるので順番に注意。※逆だとすべて認証がいるようになる */
		http
		.authorizeRequests()
		.antMatchers("/webjars/**").permitAll() //webjarsへアクセス許可
		.antMatchers("/css/**").permitAll() //cssへアクセス許可
		.antMatchers("/login").permitAll() //ログインページは直リンクOK
		.antMatchers("/signup").permitAll() //ユーザー登録画面は直リンクOK
		.antMatchers("/admin").hasAuthority("ROLE_ADMIN") //アドミンに許可(SpringではDB含めロール名先頭にはROLE_ を付けるのがルール)
		.anyRequest().authenticated(); //それ以外は直リンク禁止


		//ログイン処理
		/*ログイン処理を追加するには、http.formLogin()にメソッドチェーンで条件を追加
loginProcessingUrlはログイン画面のフォームタグのaction="/login"の部分と一致させる
loginPageを設定しておかないとSpringセキュリティのログインページが表示されてしまう。コントローラークラスの@GetMapping("/login")に一致させる。
userNameParameter("<パラメーター名>")　ログインページのユーザーID入力エリアのパラメーター名を指定。
指定するパラメータ名はログイン用Html該当項目入力箇所のname属性(パスワードも同じ）*/
		http
		.formLogin()
		.loginProcessingUrl("/login") //ログイン処理のパス
		.loginPage("/login") //ログインページの指定
		.failureUrl("/login") //ログイン失敗時の遷移先
		.usernameParameter("userId") //ログインページのユーザーID
		.passwordParameter("password") //ログインページのパスワード
		.defaultSuccessUrl("/home", true); //ログイン成功後の遷移先

		//ログアウト処理
		/*GETメソッドでリクエストを送る場合logoutRequestMatcher()メソッドを使う。参考程度。
		logoutUrl(<パス>)はPOSTメソッドでログアウトする場合の設定。
		*/
		http
		.logout()
		.logoutRequestMatcher(new AntPathRequestMatcher("/logout")) //
		.logoutUrl("/logout") //ログアウトのURL
		.logoutSuccessUrl("/login"); //ログアウト成功後のURL


		//CSRF対策設定を一時に敵に無効（学習用）
		//http.csrf().disable();
	}

	//ユーザデータ取得
	/*まずはSQLを実行できるようにするためDataSourceをAutowired。これはSpringがBeanを自動で用意しています。　
	次にユーザーデータを取得するためにSQL文を２つ用意。
	１つ目ではユーザーIDパスワード使用可否(Enabled)を検索(このプログラムでは全てture)２つ目ではユーザーIDと権限を検索。
	これらのSQL文をusersByUsernameQueryメソッドとauthoritiesByUsernameQueryメソッドの引数に入れる。
	これで入力されたユーザーIDとパスワードを使って認証処理をSpringが行う。※ただしIDとパスワードの入力エリアをログイン処理で定義済であること。
	 */
	// データソース
	@Autowired
	private DataSource dataSource;

	// ユーザーIDとパスワードを取得するSQL文
	private static final String USER_SQL = "SELECT"
			+ "    user_id,"
			+ "    password,"
			+ "    true"
			+ " FROM"
			+ "    m_user"
			+ " WHERE"
			+ "    user_id = ?";

	// ユーザーのロールを取得するSQL文
	private static final String ROLE_SQL = "SELECT"
			+ "    user_id,"
			+ "    role"
			+ " FROM"
			+ "    m_user"
			+ " WHERE"
			+ "    user_id = ?";

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// ログイン処理時のユーザー情報を、DBから取得する
		//ログイン時にパスワード復号するためpasswordEncoderメソッドにBean定義したPasswordEncoderをセット
		auth.jdbcAuthentication()
		.dataSource(dataSource)
		.usersByUsernameQuery(USER_SQL)
		.authoritiesByUsernameQuery(ROLE_SQL)
		.passwordEncoder(passwordEncoder());
	}
}