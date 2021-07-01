package edu.poly.pk01572.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Customer implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int customerId;
	
//	@NotEmpty
	@Column(length = 50, nullable = false)
	private String name;
	
//	@NotEmpty
	@Column(length = 200, nullable = false)
	private String address;
	
//	@Email
	@Column(length = 100, nullable = false)
	private String email;
	
//	@Column(length = 30, nullable = false)
//	private String password;
	
//	@NotEmpty
	@Column(length = 11, nullable = false)
	private String phone;
	
//	@Column(nullable = false)
//	@Temporal(TemporalType.DATE)
//	private Date registerDate;
	
//	@Column(nullable = false)
//	private short status;
	
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public Set<Order> getOrders() {
		return orders;
	}

	public void setOrders(Set<Order> orders) {
		this.orders = orders;
	}

	@OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
	Set<Order> orders;

	@Override
	public String toString() {
		return "Customer [customerId=" + customerId + ", name=" + name + ", address=" + address + ", email=" + email
				+ ", phone=" + phone + ", orders=" + orders + "]";
	}
	
	
	
}
