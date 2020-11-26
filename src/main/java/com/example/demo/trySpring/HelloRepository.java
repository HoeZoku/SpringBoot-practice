package com.example.demo.trySpring;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org. springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


//ポイント １：@ Repository
@Repository
public class HelloRepository {

	// ポイント ２： JdbcTemplate   
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public Map<String,Object>findOne(int id){

		// SELECT 文     
		String query ="SELECT "
		+ "employee_id,"
		+ "employee_name,"
		+ "age "
		+ "FROM employee "
		+ "WHERE employee_id=?";

		// 検索 実行     
		Map<String,Object>employee=jdbcTemplate.queryForMap(query,id);

		return employee;
	}
}
