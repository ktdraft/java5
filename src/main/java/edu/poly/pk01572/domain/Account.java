package edu.poly.pk01572.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table
public class Account implements Serializable {
	@Id
	@Column(length = 30)
	private String usename;
	
	// @Column(nullable = true, length = 30)
	// private String password;
}
