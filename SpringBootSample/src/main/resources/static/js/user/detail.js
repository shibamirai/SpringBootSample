'use strict';

/** 画面ロード時の処理. */
jQuery(function($) {
	/** 更新ボタンを押したときの処理 */
	$('#btn-update').click(function (event) {
		updateUser();
	});
	
	/** 削除ボタンを押したときの処理 */
	$('#btn-delete').click(function (event) {
		deleteUser();
	});
});

/** ユーザー更新処理. */
function updateUser() {

	var formData = $('#user-detail-form').serializeArray();
	
	// ajax 通信
	$.ajax({
		type: "PUT",
		cache: false,
		url: '/user/update',
		data: formData,
		dataType: 'json',
	}).done(function (data) {
		// ajax 成功時の処理
		alert('ユーザーを更新しました');
		window.location.href = '/user/list';
	}).fail(function (jqXHR, textStatus, errorThrown) {
		// ajux 失敗時の処理
		alert('ユーザーの更新に失敗しました')
	}).always(function () {
		// 常に実行する処理
	});
}

/** ユーザー削除処理. */
function deleteUser() {

	var formData = $('#user-detail-form').serializeArray();
	
	// ajax 通信
	$.ajax({
		type: "DELETE",
		cache: false,
		url: '/user/delete',
		data: formData,
		dataType: 'json',
	}).done(function (data) {
		// ajax 成功時の処理
		alert('ユーザーを削除しました');
		window.location.href = '/user/list';
	}).fail(function (jqXHR, textStatus, errorThrown) {
		// ajux 失敗時の処理
		alert('ユーザーの削除に失敗しました')
	}).always(function () {
		// 常に実行する処理
	});
	
}
