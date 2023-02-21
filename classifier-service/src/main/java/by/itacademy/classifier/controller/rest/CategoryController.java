package by.itacademy.classifier.controller.rest;

import by.itacademy.classifier.constant.MessageError;
import by.itacademy.classifier.dto.Category;
import by.itacademy.classifier.service.ClassifierService;
import by.itacademy.classifier.validation.annotation.CustomValid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.UUID;

@RestController
@RequestMapping("/classifier/operation/category")
@Validated
public class CategoryController {

    private final ClassifierService<Category, UUID> categoryService;

    public CategoryController(ClassifierService<Category, UUID> categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody @CustomValid Category category) {
        categoryService.create(category);
    }

    @GetMapping
    public Page<Category> get(@RequestParam @Min(value = 0, message = MessageError.PAGE_NUMBER) int page,
                              @RequestParam @Min(value = 1, message = MessageError.PAGE_SIZE) int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return categoryService.get(pageable);
    }
}
