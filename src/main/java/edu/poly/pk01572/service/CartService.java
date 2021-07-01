package edu.poly.pk01572.service;

import java.util.Collection;

import edu.poly.pk01572.domain.CartItem;

public interface CartService {

	int getCount();

	double getAmount();

	void update(Long productId, int quantity);

	void clear();

	Collection<CartItem> getCartItems();

	void remove(Long productId);

	void add(CartItem item);

}
