package com.example.demo.trySpring;

import lombok.Data;

// ポイント：@ Data
@Data
public class Employee {

	private int employeeId; // 従業員 ID
	private String employeeName; // 従業員 名
	private int age; // 年齢
}
