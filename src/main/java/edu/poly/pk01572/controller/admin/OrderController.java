package edu.poly.pk01572.controller.admin;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONStringer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import edu.poly.pk01572.domain.CartItem;
import edu.poly.pk01572.domain.Customer;
import edu.poly.pk01572.domain.Order;
import edu.poly.pk01572.domain.OrderDetail;
import edu.poly.pk01572.model.CustomerDTO;
import edu.poly.pk01572.repository.CustomerRepository;
import edu.poly.pk01572.repository.OrderDetailRepository;
import edu.poly.pk01572.repository.OrderRepository;
import edu.poly.pk01572.service.CartService;
import edu.poly.pk01572.service.MailService;
import edu.poly.pk01572.service.OrderService;

@Controller
@RequestMapping("order")
public class OrderController {
	@Autowired
	CartService cartService;

	@Autowired
	CustomerRepository customerRepository;

	@Autowired
	OrderRepository ordeRepository;

	@Autowired
	OrderDetailRepository orderDetailRepository;

	@Autowired
	MailService mailService;

	@Autowired
	OrderService orderService;

	@GetMapping("list")
	public String searchString(ModelMap model, @RequestParam(name = "page", required = false) Optional<Integer> page) {

		int currentPage = page.orElse(1);

		Pageable pageable = PageRequest.of(currentPage - 1, 10, Sort.by(Sort.Direction.DESC, "orderDate"));

		Page<Order> list = orderService.findAll(pageable);

		int totalPage = list.getTotalPages();
		List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());

		model.addAttribute("orders", list.getContent());
		model.addAttribute("page", currentPage);
		model.addAttribute("totalPage", totalPage);
		model.addAttribute("pageNumbers", pageNumbers);

		System.out.println("Ra den day roi ne");

		return "admin/orders/list";
	}

	@PostMapping("")
	public ModelAndView get(ModelMap model, @Valid @ModelAttribute("customer") CustomerDTO customer,
			BindingResult result) {

		if (result.hasErrors()) {
			System.out.println(result.getAllErrors().toString());
			Collection<CartItem> list = cartService.getCartItems();

			model.addAttribute("cartItems", list);
			model.addAttribute("total", cartService.getAmount());
			model.addAttribute("cartCount", cartService.getCount());

			return new ModelAndView("/site/view-cart");
		}

		if (cartService.getCount() <= 0) {
			System.out.println("Cart is empty");
			model.addAttribute("message", "Cart is empty");
			return new ModelAndView("forward:/home/carts", model);
		}

		Customer newCustomer = customerRepository.findByEmail(customer.getEmail());

		if (newCustomer == null) {
			Customer tempt = new Customer();
			BeanUtils.copyProperties(customer, tempt);
			newCustomer = customerRepository.save(tempt);
		}

		Order order = new Order();
		order.setCustomer(newCustomer);
		order.setAmount(cartService.getAmount());
		order.setOrderDate(new Date());

		Order newOrder = ordeRepository.save(order);

		cartService.getCartItems().stream().forEach(item -> {
			OrderDetail orderDetail = new OrderDetail();

			orderDetail.setOrder(newOrder);
			orderDetail.setQuantity(item.getQuantity());
			Double orderDetailPrice = (item.getProduct().getUnitPrice()
					- item.getProduct().getUnitPrice() * item.getProduct().getDiscount() / 100);
			orderDetail.setUnitPrice(Math.round(orderDetailPrice * 100) / 100);
			orderDetail.setProduct(item.getProduct());

			orderDetailRepository.save(orderDetail);
		});

		String mess = "You have ordered " + cartService.getCartItems().size() + " items with a total value is "
				+ cartService.getAmount() + "$\n" + "Order has been recorded\n\n" + "EKShop thanks "
				+ order.getCustomer().getName();
		mailService.sendMail(order.getCustomer().getEmail(), "Thanks you for order our product", mess);

		cartService.clear();

		return new ModelAndView("redirect:/home/products", model);
	}

}
