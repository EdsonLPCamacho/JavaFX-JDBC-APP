package model.service;

import java.util.ArrayList;
import java.util.List;

import model.entities.Department;

public class DepartmentService {
	public List<Department> findAll(){		
		//Mock List
		List<Department> list = new ArrayList<>();
		list.add(new Department(1, "IT"));
		list.add(new Department(2, "AI"));
		list.add(new Department(3, "Support"));
		
		return list;
	}
}
