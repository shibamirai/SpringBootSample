package com.example.domain.user.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name="m_department")
public class Department {
	@Id
	private Integer departmentId;
	private String departmentName;
}
