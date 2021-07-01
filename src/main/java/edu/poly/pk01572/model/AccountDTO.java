package edu.poly.pk01572.model;

import java.io.Serializable;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO implements Serializable {
	private String usename;

	// @NotEmpty
	// @Min(value = 5)
	// private String password;
}
