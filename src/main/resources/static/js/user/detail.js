'use strict';

/** 画面ロード時の処理 */
document.addEventListener('DOMContentLoaded', function() {

  /** 更新ボタンを押したときの処理 */
  document.querySelector('#btn-update')
      .addEventListener('click', updateUser);

  /** 削除ボタンを押したときの処理 */
  document.querySelector('#btn-delete')
      .addEventListener('click', deleteUser);

});

/** ユーザー更新処理 */
function updateUser() {

  const form = document.querySelector('#user-detail-form');
  const formData = new FormData();
  formData.append("_csrf", form.elements['_csrf'].value);
  formData.append("userId", form.elements['userId'].value);
  formData.append("password", form.elements['password'].value);
  formData.append("userName", form.elements['userName'].value);

  fetch('/user/update', {
	headers: {
		'Accept': 'application/json, */*',
	},
    method: 'PUT',
    body: formData
  }).then(response => {
    if (!response.ok) {
      alert('ユーザーの更新に失敗しました');
      exit();
    }
    return response.json();
  }).then(response => {
    // ユーザー一覧画面にリダイレクト
    window.location.href = '/user/list';
  }).catch(error => {
    alert('ユーザーの更新に失敗しました');
  });
}

/** ユーザー削除処理 */
function deleteUser() {

  const form = document.querySelector('#user-detail-form');
  const formData = new FormData();
  formData.append("_csrf", form.elements['_csrf'].value);
  formData.append("userId", form.elements['userId'].value);

  fetch('/user/delete', {
	headers: {
		'Accept': 'application/json, */*',
	},
    method: 'DELETE',
    body: formData
  }).then(response => {
    if (!response.ok) {
      alert('ユーザーの削除に失敗しました');
      exit();
    }
    return response.json();
  }).then(response => {
    // ユーザー一覧画面にリダイレクト
    window.location.href = '/user/list';
  }).catch(error => {
    alert('ユーザーの削除に失敗しました');
  });
}