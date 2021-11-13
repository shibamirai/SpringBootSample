'use strint';

/** 画面ロード時の処理 */
document.addEventListener('DOMContentLoaded', function() {

  /** 登録ボタンを押したときの処理 */
  document.querySelector('#btn-signup')
      .addEventListener('click', signupUser);
});

/** ユーザー登録処理 */
function signupUser() {

  // バリデーション結果をクリア
  removeValidResult();

  // フォームの値を取得
  const form = document.querySelector('#signup-form');
  const formData = new FormData();
  formData.append("_csrf", form.elements['_csrf'].value);
  formData.append("userId", form.elements['userId'].value);
  formData.append("password", form.elements['password'].value);
  formData.append("userName", form.elements['userName'].value);
  formData.append("birthday", form.elements['birthday'].value);
  formData.append("age", form.elements['age'].value);
  formData.append("gender", form.elements['gender'].value);

  fetch('/user/signup/rest', {
	headers: {
		'Accept': 'application/json, */*',
	},
    method: 'POST',
    body: formData

  }).then(response => {
    if (!response.ok) {
      alert('ユーザー登録に失敗しました');
      exit();
    }

    return response.json();

  }).then(data => {
    console.log(data);

    if (data.result === 90) {
      // validationエラー時の処理
      for (const key in data.errors) {
        reflectValidResult(key, data.errors[key]);
      }

    } else if (data.result === 0) {
      alert('ユーザーを登録しました');
      // ログイン画面にリダイレクト
      window.location.href = '/login';
    }

  }).catch(error => {
    alert('ユーザーの更新に失敗しました');

  });
}

/** バリデーション結果をクリア */
function removeValidResult() {
  let elms = document.querySelectorAll('.is-invalid');
  if (elms) {
    for (let i = 0; i < elms.length; i++) {
      elms[i].classList.remove('is-invalid');
    }
  }
  elms = document.querySelectorAll('.invalid-feedback');
  if (elms) {
    for (let i = 0; i < elms.length; i++) {
      elms[i].remove();
    }
  }
  elms = document.querySelectorAll('.text-danger');
  if (elms) {
    for (let i = 0; i < elms.length; i++) {
      elms[i].remove();
    }
  }
}

/** バリデーション結果の反映 */
function reflectValidResult(key, value) {

  // エラーメッセージのテキストノード作成
  const textNode = document.createTextNode(value);

  // エラーメッセージ追加
  if (key === 'gender') { // 性別の場合
    const elms = document.querySelectorAll('input[name=' + key + ']');
    for (let i = 0; i < elms.length; i++) {
    // CSS適用
      elms[i].classList.add('is-invalid');
      // エラーメッセージ追加
      const mesElm = document.createElement('div');
      mesElm.classList.add('text-danger');
      mesElm.appendChild(textNode);
      elms[i].parentElement.parentElement.appendChild(mesElm);
    }

  } else { // 性別以外の場合
    const elm = document.querySelector('input[id=' + key + ']');
    // CSS適用
    elm.classList.add('is-invalid');
    // エラーメッセージ追加
    const mesElm = document.createElement('div');
    mesElm.classList.add('invalid-feedback');
	mesElm.appendChild(textNode);
    elm.after(mesElm);

  }
}