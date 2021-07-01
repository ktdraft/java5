package edu.poly.pk01572.model;

import java.io.Serializable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerDTO implements Serializable {

	private int customerId;

	@NotBlank
	private String name;

	@NotBlank
	@Email
	private String email;

//	@NotEmpty
//	private String password;

	@NotBlank
	@Pattern(regexp = "\\d{10}")
	private String phone;
	
	@NotBlank
	private String address;

	public int getCustomerId() {
		return customerId;
	}

	public void setCustomerId(int customerId) {
		this.customerId = customerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

//	@NotEmpty
//	private Date registerDate;

//	@NotEmpty
//	private short status;
}
