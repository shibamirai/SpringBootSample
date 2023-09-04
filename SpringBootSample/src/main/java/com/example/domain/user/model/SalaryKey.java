package com.example.domain.user.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class SalaryKey {
	private String userId;
	private String yearMonth;
}
