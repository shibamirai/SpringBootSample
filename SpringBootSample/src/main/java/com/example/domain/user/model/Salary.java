package com.example.domain.user.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "t_salary")
public class Salary {
//	private String userId;
//	private String yearMonth;
	@EmbeddedId
	private SalaryKey salaryKey;
	private Integer salary;
}
