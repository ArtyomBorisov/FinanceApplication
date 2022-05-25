package by.itacademy.classifier.controller.web.controllers.rest;

import by.itacademy.classifier.model.Category;
import by.itacademy.classifier.service.api.IClassifierService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = {"/classifier/operation/category", "/classifier/operation/category/"},
        consumes = {MediaType.APPLICATION_JSON_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE})
public class CategoryController {

    private final IClassifierService<Category, UUID> categoryService;

    public CategoryController(IClassifierService<Category, UUID> categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody Category category) {
        this.categoryService.create(category);
    }

    @GetMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Page<Category> index(@RequestParam int page,
                                @RequestParam int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return this.categoryService.get(pageable);
    }
}
