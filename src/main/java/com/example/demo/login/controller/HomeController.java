package com.example.demo.login.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.demo.login.domain.model.SignupForm;
import com.example.demo.login.domain.model.User;
import com.example.demo.login.domain.service.UserService;

//ホーム画面用コントローラークラス
//ホーム画面から各情報を受け取り、それによって各処理受け渡し
@Controller
public class HomeController {

	@Autowired
	UserService userService;

	// 結婚ステータスのラジオボタン用変数
	private Map<String, String> radioMarriage;


	/**
	 * ラジオボタンの初期化メソッド（ユーザー登録画面と同じ.////////////////////////////////////////////////////////////////////////////
	 */
	private Map<String, String> initRadioMarrige() {

		Map<String, String> radio = new LinkedHashMap<>();

		// 既婚、未婚をMapに格納
		radio.put("既婚", "true");
		radio.put("未婚", "false");

		return radio;
	}

	//ユーザー一覧画面のGET用メソッド.
	@GetMapping("/home")
	public String getHome(Model model) {

		//コンテンツ部分にユーザー詳細を表示するための文字列を登録
		//Modelクラスの"contents"というキーに値として"login/home::home_contents"セット
		//この 値 が、htmlのth: include 属性に入る
		model.addAttribute("contents", "login/home::home_contents");

		return "login/homeLayout";
	}


	/**
	 * ユーザー一覧画面のGETメソッド用処理.
	 *Modelから値を取得して表示するためカウント結果と複数検索結果をModelクラスに登録（addAttribute）。///////////////////////////////
	 */
	@GetMapping("/userList")
	public String getUserList(Model model) {

		//コンテンツ部分にユーザー一覧を表示するための文字列を登録
		model.addAttribute("contents", "login/userList :: userList_contents");

		//ユーザー一覧の生成
		List<User> userList = userService.selectMany();

		//Modelにユーザーリストを登録
		model.addAttribute("userList", userList);

		//データ件数を取得
		int count = userService.count();
		model.addAttribute("userListCount", count);

		return "login/homeLayout";
	}


	/**
	 * ユーザー詳細画面のGETメソッド用処理//////////////////////////////////////////////////////////////////////////////////////////////
	 *動的なURLに対応したメソッドを作るためには、@GetMappingや@PostMappingの値に/{<変数名>}を付けます。
	 *ユーザーIDを受け取る場合は、@GetMapping(/userDetail/{id})とします。通常は/userDetail/{id}とすればいい
	 *しかしメール形式は特殊で例えばyamada@xxx.co.jpというユーザーIDが渡されてくるとyamada@xxx.coしか受け取れません。
	 *そのため、正規表現で@GetMapping("/userDetail/{id:.+}")としています。
	 *＠PathVariableを付けると渡されてきたパス(URL)の値を引数の変数に入れることができます。
	 *~userDetail/yamada@xxx.co.jpというリクエストが来た場合yamada@xxx.co.jpという値が引数のuserIdという変数に入る
	 */

	@GetMapping("/userDetail/{id:.+}")
	public String getUserDetail(@ModelAttribute SignupForm form,
			Model model,
			@PathVariable("id") String userId) {

		// ユーザーID確認（デバッグ）
		System.out.println("userId = " + userId);

		// コンテンツ部分にユーザー詳細を表示するための文字列を登録
		model.addAttribute("contents", "login/userDetail :: userDetail_contents");

		// 結婚ステータス用ラジオボタンの初期化
		radioMarriage = initRadioMarrige();

		// ラジオボタン用のMapをModelに登録
		model.addAttribute("radioMarriage", radioMarriage);

		// ユーザーIDのチェック
		if (userId != null && userId.length() > 0) {

			// ユーザー情報を取得
			User user = userService.selectOne(userId);

			// Userクラスをフォームクラスに変換
			form.setUserId(user.getUserId()); //ユーザーID
			form.setUserName(user.getUserName()); //ユーザー名
			form.setBirthday(user.getBirthday()); //誕生日
			form.setAge(user.getAge()); //年齢
			form.setMarriage(user.isMarriage()); //結婚ステータス

			// Modelに登録
			model.addAttribute("signupForm", form);
		}

		return "login/homeLayout";
	}


	/**
	 * ユーザー更新用処理.////////////////////////////////////////////////////////////////////////////////////////////////////////////
	 * ユーザー詳細画面では更新と削除のどちらを押しても/userDetailにPOSTするので更新か削除を判別する必要があります
	 * params属性を使えばURLとボタンのname属性の両方を見て判断できる
	 * 更新ボタンのhtmlは<buttonclass="btnbtn-primarybtn-lgpull-right"type="submit"name="update">更新</button>
	 * このname属性の値をparams属性に入れることでメソッドを分ける。※例外処理のためあえてバリデーション入れてない。
	 */

	@PostMapping(value = "/userDetail", params = "update")
	public String postUserDetailUpdate(@ModelAttribute SignupForm form,
			Model model) {

		System.out.println("更新ボタンの処理");

		//Userインスタンスの生成
		User user = new User();

		//フォームクラスをUserクラスに変換
		user.setUserId(form.getUserId());
		user.setPassword(form.getPassword());
		user.setUserName(form.getUserName());
		user.setBirthday(form.getBirthday());
		user.setAge(form.getAge());
		user.setMarriage(form.isMarriage());

		//更新実行
		try {

            //更新実行
            boolean result = userService.updateOne(user);

            if (result == true) {
                model.addAttribute("result", "更新成功");
            } else {
                model.addAttribute("result", "更新失敗");
            }

        } catch (DataAccessException e) {

            model.addAttribute("result", "更新失敗(トランザクションテスト)");

        }

		//ユーザー一覧画面を表示
		return getUserList(model);
	}



	/**
	 * ユーザー削除用処理./////////////////////////////////////////////////////////////////////////////////////////////////////////////
	 *更新ボタンと同様
	 */

	@PostMapping(value = "/userDetail", params = "delete")
	public String postUserDetailDelete(@ModelAttribute SignupForm form,
			Model model) {

		System.out.println("削除ボタンの処理");

		//削除実行
		boolean result = userService.deleteOne(form.getUserId());

		if (result == true) {
			model.addAttribute("result", "削除成功");
		} else {
			model.addAttribute("result", "削除失敗");
		}

		//ユーザー一覧画面を表示
		return getUserList(model);
	}

	/**
	 * ユーザー一覧のCSV出力用処理./////////////////////////////////////////////////////////////////////////////////////
	 */
	@GetMapping("/userList/csv")
	public ResponseEntity<byte[]> getUserListCsv(Model model) {

		//ユーザーを全件取得して、CSVをサーバーに保存する
		userService.userCsvOut();

		byte[] bytes = null;

		try {

			//サーバーに保存されているsample.csvファイルをbyteで取得する
			bytes = userService.getFile("sample.csv");

		} catch (IOException e) {
			e.printStackTrace();
		}

		//HTTPヘッダーの設定
		/*CSVをダウンロードするには、レスポンスにダウンロード用のHTTPヘッダを設定し、ファイルを出力します。テキストや画像も同様.
		HTTPヘッダとはサーバへのリクエスト時にどんなブラウザがどういった情報をリクエストしているのか、
		サーバからのレスポンス時にはどのようなコンテンツを返すのかといった、データ本体とは別に「ブラウザやデータに関する情報」を付加するもの。
		Content-Typeファイル形式  setContentDispositionFormDatファイル名設定
		*/

		HttpHeaders header = new HttpHeaders();
		header.add("Content-Type", "text/csv; charset=UTF-8");
		header.setContentDispositionFormData("filename", "sample.csv");

		//sample.csvを戻すをResponseEntity型にするとタイムリーフのテンプレート(html)ではなく、ファイル（byte型の配列）を返却できる
		return new ResponseEntity<>(bytes, header, HttpStatus.OK);
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	//ログアウト用メソッド.
	@PostMapping("/logout")
	public String postLogout() {

		//ログイン画面にリダイレクト
		return "redirect:/login";
	}

	 /**
     * アドミン権限専用画面のGET用メソッド.
     * @param model Modelクラス
     * @return 画面のテンプレート名
     */
    @GetMapping("/admin")
    public String getAdmin(Model model) {

        //コンテンツ部分にユーザー詳細を表示するための文字列を登録
        model.addAttribute("contents", "login/admin :: admin_contents");

        //レイアウト用テンプレート
        return "login/homeLayout";
    }


}
