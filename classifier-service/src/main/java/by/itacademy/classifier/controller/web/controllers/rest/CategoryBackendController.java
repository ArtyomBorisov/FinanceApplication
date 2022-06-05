package by.itacademy.classifier.controller.web.controllers.rest;

import by.itacademy.classifier.model.Category;
import by.itacademy.classifier.service.api.MessageError;
import by.itacademy.classifier.service.api.IClassifierService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping(value = "/backend/classifier/operation/category", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public class CategoryBackendController {

    private final IClassifierService<Category, UUID> categoryService;

    public CategoryBackendController(IClassifierService<Category, UUID> categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Page<Category> index(@RequestBody Collection<UUID> categories,
                                @RequestParam @Min(value = 0, message = MessageError.PAGE_NUMBER) int page,
                                @RequestParam @Min(value = 1, message = MessageError.PAGE_SIZE) int size) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return this.categoryService.get(categories, pageable);
    }

    @GetMapping(value = "/{uuid}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Category index(@PathVariable(name = "uuid") UUID id) {
        return this.categoryService.get(id);
    }
}
