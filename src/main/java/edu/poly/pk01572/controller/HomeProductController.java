package edu.poly.pk01572.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.poly.pk01572.domain.Category;
import edu.poly.pk01572.domain.Product;
import edu.poly.pk01572.service.CategoryService;
import edu.poly.pk01572.service.ProductService;

@Controller
@RequestMapping("home/products")
public class HomeProductController {
	@Autowired
	CategoryService categoryService;

	@Autowired
	ProductService productService;

	@RequestMapping("")
	public String list(ModelMap model, @RequestParam(name = "cid", required = false) Long categoryId,
			@RequestParam(name = "name", required = false) Optional<String> name,
			@RequestParam(name = "page", required = false) Optional<Integer> page) {

		int currentPage = page.orElse(1);
		String currentName = name.orElse("");
		Pageable pageable = PageRequest.of(currentPage - 1, 9, Sort.by(Sort.Direction.ASC, "name"));

		Page<Product> list = null;
		if (categoryId != null) {
			list = productService.findByNameIgnoreCaseContainingAndCategoryId(currentName, categoryId, pageable);
		} else if (currentName != null) {
			list = productService.findByNameContainingIgnoreCase(currentName, pageable);
		} else {
			list = productService.findAll(pageable);
		}

		List<Category> listC = categoryService.findAll();
		int totalPage = list.getTotalPages();
		List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());

		model.addAttribute("products", list.getContent());
		model.addAttribute("categories", listC);
		model.addAttribute("name", currentName);
		model.addAttribute("cid", categoryId);
		model.addAttribute("page", currentPage);
		model.addAttribute("totalPage", totalPage);
		model.addAttribute("pageNumbers", pageNumbers);

		return "site/view-products";
	}
}
