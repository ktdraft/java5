package edu.poly.pk01572.controller.admin;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import org.springframework.web.servlet.ModelAndView;

import edu.poly.pk01572.domain.Category;
import edu.poly.pk01572.model.CategoryDTO;
import edu.poly.pk01572.service.CategoryService;

@Controller
@RequestMapping("admin/categories")
public class CategoryController {

	@Autowired
	CategoryService categoryService;

	@GetMapping("add")
	public String add(Model model) {
		CategoryDTO category = new CategoryDTO();

		model.addAttribute("category", category);

		return "admin/categories/addOrEdit";
	}

	@GetMapping("edit/{categoryId}")
	public ModelAndView edit(ModelMap model, @PathVariable("categoryId") Long categoryId) {
		Optional<Category> otp = categoryService.findById(categoryId);
		CategoryDTO dto = new CategoryDTO();

		if (otp.isPresent()) {
			Category entity = otp.get();

			BeanUtils.copyProperties(entity, dto);
			dto.setIsEdited(true);

			model.addAttribute("category", dto);

			return new ModelAndView("admin/categories/addOrEdit", model);
		}

		model.addAttribute("message", "Category is not existed");

		return new ModelAndView("redirect:/admin/categories/search", model);
	}

	@GetMapping("delete/{categoryId}")
	public ModelAndView delete(ModelMap model, @PathVariable("categoryId") Long categoryId) {

		categoryService.deleteById(categoryId);
		model.addAttribute("message", "Category is delted");

		return new ModelAndView("forward:/admin/categories", model);
	}

	@PostMapping("saveOrUpdate")
	public ModelAndView saveOrUpdate(ModelMap model, @Valid @ModelAttribute("category") CategoryDTO dto,
			BindingResult result) {

		if (result.hasErrors()) {
			return new ModelAndView("admin/categories/addOrEdit");
		}

		Category entity = new Category();

		System.out.println("dto:" + dto);

		BeanUtils.copyProperties(dto, entity);

		categoryService.save(entity);

		model.addAttribute("message", "Category is saved!");

		return new ModelAndView("forward:/admin/categories", model);
	}

	@RequestMapping("")
	public String list(ModelMap model, @RequestParam(name = "name", required = false) String name,
			@RequestParam(name = "page", required = false) Optional<Integer> page,
			@RequestParam(name = "pageSize", required = false) Optional<Integer> pageSize) {
		
		int currentPage = page.orElse(1);
		int currentPageSize = pageSize.orElse(10);
		
		Pageable pageable = PageRequest.of(currentPage - 1, currentPageSize, Sort.by("name"));
		
		Page<Category> list = StringUtils.hasText(name)
				? categoryService.findByNameContainingIgnoreCase(name.toLowerCase(), pageable)
				: categoryService.findAll(pageable);
		
		int totalPage = list.getTotalPages();
		List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());
		
		model.addAttribute("categories", list.getContent());
		model.addAttribute("name", name);
		model.addAttribute("page", currentPage);
		model.addAttribute("pageSize", currentPageSize);
		model.addAttribute("totalPage", totalPage);
		model.addAttribute("pageNumbers", pageNumbers);

		return "admin/categories/list";
	}

	@GetMapping("search")
	public String search(ModelMap model, @RequestParam(name = "name", required = false) String name) {
		List<Category> list = null;

		if (StringUtils.hasText(name)) {
			list = categoryService.findByNameContaining(name);
		} else {
			list = categoryService.findAll();
		}

		model.addAttribute("categories", list);

		return "admin/categories/search";
	}

	@GetMapping("search-paginated")
	public String search(ModelMap model, @RequestParam(name = "name", required = false) String name,
			@RequestParam(name = "page") Optional<Integer> page,
			@RequestParam(name = "pageSize") Optional<Integer> pageSize) {

		int currentPage = page.orElse(1);
		int currentPageSize = pageSize.orElse(5);

		Pageable pageable = PageRequest.of(currentPage - 1, currentPageSize, Sort.by("name"));

		Page<Category> resultPage = null;

		if (StringUtils.hasText(name)) {
			resultPage = categoryService.findByNameContaining(name, pageable);
			model.addAttribute("name", name);
		} else {
			resultPage = categoryService.findAll(pageable);
		}

		int totalPage = resultPage.getTotalPages();
		List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPage).boxed().collect(Collectors.toList());

		model.addAttribute("pageNumbers", pageNumbers);
		model.addAttribute("categoryPage", resultPage);

		return "admin/categories/search-paginated";
	}

}
