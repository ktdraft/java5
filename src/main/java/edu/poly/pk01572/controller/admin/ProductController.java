package edu.poly.pk01572.controller.admin;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import edu.poly.pk01572.domain.Category;
import edu.poly.pk01572.domain.Product;
import edu.poly.pk01572.model.CategoryDTO;
import edu.poly.pk01572.model.ProductDTO;
import edu.poly.pk01572.service.CategoryService;
import edu.poly.pk01572.service.ProductService;
import edu.poly.pk01572.service.StorageService;

@Controller
@RequestMapping("admin/products")
public class ProductController {

	@Autowired
	CategoryService categoryService;

	@Autowired
	ProductService productService;

	@Autowired
	StorageService storageService;

	@ModelAttribute("categories")
	public List<CategoryDTO> getCategories() {
		return categoryService.findAll().stream().map(item -> {
			CategoryDTO dto = new CategoryDTO();

			BeanUtils.copyProperties(item, dto);
			return dto;
		}).toList();
	}

	@GetMapping("add")
	public String add(Model model) {

		model.addAttribute("product", new ProductDTO());

		return "admin/products/addOrEdit";
	}

	@GetMapping("edit/{productId}")
	public ModelAndView edit(ModelMap model, @PathVariable("productId") Long productId) {
		Optional<Product> otp = productService.findById(productId);
		ProductDTO dto = new ProductDTO();

		if (otp.isPresent()) {
			Product entity = otp.get();

			BeanUtils.copyProperties(entity, dto);
			dto.setCategoryId(entity.getCategory().getCategoryId());
			dto.setIsEdited(true);

			model.addAttribute("product", dto);

			return new ModelAndView("admin/products/addOrEdit", model);
		}

		model.addAttribute("message", "Product is not existed");

		return new ModelAndView("redirect:/admin/products/search", model);
	}

	@GetMapping("delete/{productId}")
	public ModelAndView delete(ModelMap model, @PathVariable("productId") Long productId) throws IOException {

		Optional<Product> product = productService.findById(productId);
		if (product.isPresent()) {
			if (!StringUtils.isEmpty(product.get().getImage())) {
				storageService.delete(product.get().getImage());
			}

			productService.deleteById(productId);
			model.addAttribute("message", "Product with id " + productId + " is delted");
		} else {
			model.addAttribute("message", "Product with id " + productId + " dosen't exist");
		}

		return new ModelAndView("forward:/admin/products", model);
	}

	@PostMapping("saveOrUpdate")
	public ModelAndView saveOrUpdate(ModelMap model, @Valid @ModelAttribute("product") ProductDTO dto,
			BindingResult result) {

		if (result.hasErrors()) {
			System.out.println(result.getAllErrors().toString());
			return new ModelAndView("admin/products/addOrEdit");
		}

		System.out.println("this is dto:" + dto.toString());

		Product entity = new Product();

		BeanUtils.copyProperties(dto, entity);

		Category category = new Category();
		category.setCategoryId(dto.getCategoryId());
		entity.setCategory(category);
		entity.setEnteredDate(new Date());

		if (!dto.getImageFile().isEmpty()) {
			UUID uuid = UUID.randomUUID();
			String uuidString = uuid.toString();

			entity.setImage(storageService.getStoragedFilename(dto.getImageFile(), uuidString));
			storageService.store(dto.getImageFile(), entity.getImage());
		}

		productService.save(entity);

		model.addAttribute("message", "Product is saved!");

		return new ModelAndView("redirect:/admin/products", model);
	}

//	@RequestMapping("")
//	public String list(ModelMap model) {
//		List<Product> list = productService.findAll();
//
//		model.addAttribute("products", list);
//
//		return "admin/products/list";
//	}

	@GetMapping("")
	public String searchString(ModelMap model, @RequestParam(name = "name", required = false) String name,
			@RequestParam(name = "page", required = false) Optional<Integer> page,
			@RequestParam(name = "pageSize", required = false) Optional<Integer> pageSize,
			@RequestParam(name = "orderBy", required = false) Optional<String> orderBy,
			@RequestParam(name = "orderType", required = false) Optional<String> orderType) {

		int currentPage = page.orElse(1);
		int currentPageSize = pageSize.orElse(10);
		String currentOrderBy = orderBy.orElse("name");
		String currentOrderType = orderType.orElse("ASC");

		Pageable pageable = PageRequest.of(currentPage - 1, currentPageSize, Sort
				.by(currentOrderType.equals("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC, currentOrderBy));

		Page<Product> list = StringUtils.hasText(name)
				? productService.findByNameContainingIgnoreCase(name.toLowerCase(), pageable)
				: productService.findAll(pageable);

		int totalPage = list.getTotalPages();
		List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
		
		model.addAttribute("products", list.getContent());
		model.addAttribute("name", name);
		model.addAttribute("page", currentPage);
		model.addAttribute("pageSize", currentPageSize);
		model.addAttribute("totalPage", totalPage);
		model.addAttribute("pageNumbers", pageNumbers);
		model.addAttribute("orderBy", currentOrderBy);
		model.addAttribute("orderType", currentOrderType);

		return "admin/products/list";
	}

	@GetMapping("images/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
		Resource file = storageService.loadAsResource(filename);

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}

}
