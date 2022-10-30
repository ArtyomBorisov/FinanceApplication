package by.itacademy.classifier.controller.web.controllers.rest;

import by.itacademy.classifier.dto.Category;
import by.itacademy.classifier.exception.MessageError;
import by.itacademy.classifier.service.api.IClassifierService;
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

    private final IClassifierService<Category, UUID> categoryService;

    public CategoryBackendController(IClassifierService<Category, UUID> categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    public Page<Category> index(@RequestBody Collection<UUID> categories,
                                @RequestParam @Min(value = 0, message = MessageError.PAGE_NUMBER) int page,
                                @RequestParam @Min(value = 1, message = MessageError.PAGE_SIZE) int size) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return categoryService.get(categories, pageable);
    }

    @GetMapping(value = "/{uuid}")
    public Category index(@PathVariable(name = "uuid") UUID id) {
        return categoryService.get(id);
    }
}
