package edu.poly.pk01572.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import edu.poly.pk01572.domain.CartItem;
import edu.poly.pk01572.service.CartService;

@Service
@SessionScope
public class CartServiceImpl implements CartService {
	private Map<Long, CartItem> map = new HashMap<Long, CartItem>();

	@Override
	public void add(CartItem item) {
		CartItem existedItem = map.get(item.getProductId());

		if (existedItem != null) {
			if (existedItem.getQuantity() < 5) {
				existedItem.setQuantity(existedItem.getQuantity() + 1);
			}
		} else {
			map.put(item.getProductId(), item);
		}
	}

	@Override
	public void remove(Long productId) {
		map.remove(productId);
	}

	@Override
	public Collection<CartItem> getCartItems() {
		return map.values();
	}

	@Override
	public void clear() {
		map.clear();
	}

	@Override
	public void update(Long productId, int quantity) {
		CartItem cartItem = map.get(productId);
		
		cartItem.setQuantity(quantity);
		if (cartItem.getQuantity() <= 0) {
			map.remove(productId);
		}
	}

	@Override
	public double getAmount() {
		if (map.isEmpty())
			return 0;

		Double amount = map.values().stream().mapToDouble(item -> item.getQuantity()
				* (item.getProduct().getUnitPrice() * (1 - item.getProduct().getDiscount() / 100))).sum();
		return Math.round(amount * 100) / 100;
	}

	@Override
	public int getCount() {
		if (map.isEmpty()) {
			return 0;
		}

		return map.values().size();
	}

}
