package com.example.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.domain.user.model.MUser;

@Mapper
public interface UserMapper {
	/** ユーザー登録 */
	public int insertOne(MUser user);

	/** ユーザー取得 */
	public List<MUser> findMany();

	/** ユーザー取得(1件) */
	public MUser findOne(String userId);

	/** ユーザー更新(1件) */
	public void updateOne(@Param("userId") String userId,
			@Param("password") String password,
			@Param("userName") String userName);

	/** ユーザー削除(1件) */
	public int deleteOne(@Param("userId") String userId);
}
