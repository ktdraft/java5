package edu.poly.pk01572.model;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO implements Serializable {

	private int orderId;

	@NotEmpty
	private Date orderDate;

	@NotEmpty
	private int customerId;

	@NotEmpty
	private int amount;

	@NotEmpty
	private short status;
}
