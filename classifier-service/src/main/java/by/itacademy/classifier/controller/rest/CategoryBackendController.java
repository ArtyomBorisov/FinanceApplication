package by.itacademy.classifier.controller.rest;

import by.itacademy.classifier.constant.MessageError;
import by.itacademy.classifier.dto.Category;
import by.itacademy.classifier.service.ClassifierService;
import by.itacademy.classifier.validation.annotation.CategoryUuidValid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping("/backend/classifier/operation/category")
@Validated
public class CategoryBackendController {

    private final ClassifierService<Category, UUID> categoryService;

    public CategoryBackendController(ClassifierService<Category, UUID> categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public Page<Category> get(@RequestBody @CategoryUuidValid Collection<UUID> categories,
                              @RequestParam @Min(value = 0, message = MessageError.PAGE_NUMBER) int page,
                              @RequestParam @Min(value = 1, message = MessageError.PAGE_SIZE) int size) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return categoryService.get(categories, pageable);
    }

    @GetMapping("/{uuid}")
    public Category get(@PathVariable(name = "uuid") UUID id) {
        return categoryService.get(id);
    }
}
