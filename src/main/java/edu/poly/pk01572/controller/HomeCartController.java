package edu.poly.pk01572.controller;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.poly.pk01572.domain.CartItem;
import edu.poly.pk01572.domain.Order;
import edu.poly.pk01572.domain.Product;
import edu.poly.pk01572.model.CustomerDTO;
import edu.poly.pk01572.service.CartService;
import edu.poly.pk01572.service.CategoryService;
import edu.poly.pk01572.service.ProductService;

@Controller
@RequestMapping("home/carts")
public class HomeCartController {
	@Autowired
	CategoryService categoryService;

	@Autowired
	CartService cartService;

	@Autowired
	ProductService productService;

	@RequestMapping("")
	public String list(ModelMap model) {
		Collection<CartItem> list = cartService.getCartItems();

		model.addAttribute("cartItems", list);
		model.addAttribute("total", cartService.getAmount());
		model.addAttribute("cartCount", cartService.getCount());
		
		CustomerDTO customer = new CustomerDTO();
		model.addAttribute("customer", customer);

		return "site/view-cart";
	}

	@GetMapping("add/{productId}")
	public String add(@PathVariable("productId") Long productId) {
		Product product = productService.getById(productId);

		if (product != null) {
			CartItem item = new CartItem();

			item.setProductId(product.getProductId());
			item.setProduct(product);
			item.setQuantity(1);

			System.out.print("cc gi day: " + item.toString());

			cartService.add(item);
		}
		return "redirect:/home/products";
	}

	@GetMapping("remove/{productId}")
	public String remove(@PathVariable("productId") Long productId) {
		cartService.remove(productId);

		return "redirect:/home/carts";
	}

	@PostMapping("update")
	public String update(@RequestParam("productId") Long productId, @RequestParam("quantity") Integer quantity) {
		
		cartService.update(productId, quantity);
		return "redirect:/home/carts";
	}

	@GetMapping("clear")
	public String clear() {
		cartService.clear();
		return "redirect:/home/carts";
	}
}
