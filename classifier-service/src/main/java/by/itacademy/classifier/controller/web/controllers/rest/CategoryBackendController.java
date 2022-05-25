package by.itacademy.classifier.controller.web.controllers.rest;

import by.itacademy.classifier.model.Category;
import by.itacademy.classifier.service.api.IClassifierService;
import by.itacademy.classifier.service.api.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = {"/backend/classifier/operation/category", "/backend/classifier/operation/category/"},
        consumes = {MediaType.APPLICATION_JSON_VALUE},
        produces = {MediaType.APPLICATION_JSON_VALUE})
public class CategoryBackendController {

    private final IClassifierService<Category, UUID> categoryService;

    public CategoryBackendController(IClassifierService<Category, UUID> categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Page<Category> index(@RequestBody Collection<String> categoriesUuid,
                                @RequestParam int page,
                                @RequestParam int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        try {
            List<UUID> uuidList = categoriesUuid.stream().map(UUID::fromString).collect(Collectors.toList());
            return this.categoryService.get(uuidList, pageable);
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Передан неверный(ые) uuid");
        }
    }

    @GetMapping(value = {"/{uuid}/", "/{uuid}"})
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public Category index(@PathVariable(name = "uuid") UUID id) {
        return this.categoryService.get(id);
    }
}
