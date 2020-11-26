package com.example.demo.trySpring;

import java.util.Map;

import org.springframework.beans.factory. annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HelloService {
	@Autowired
	private HelloRepository helloRepository;

	public Employee findOne(int id) {
	// １ 件 検索 実行
	Map<String,Object>map=helloRepository.findOne(id);

	// Map から 値 を 取得     
	int employeeId=(Integer)map.get("employee_id");
	String employeeName=(String)map.get("employee_name");
	int age =(Integer)map.get("age");

	// Employee クラス に 値 を セット     
	Employee employee =new Employee();
	employee.setEmployeeId(employeeId);
	employee.setEmployeeName(employeeName);
	employee.setAge(age);
	return employee;
	}
}
