package by.itacademy.classifier.service;

import by.itacademy.classifier.model.Category;
import by.itacademy.classifier.repository.api.ICategoryRepository;
import by.itacademy.classifier.repository.entity.CategoryEntity;
import by.itacademy.classifier.service.api.IClassifierService;
import by.itacademy.classifier.service.api.ValidationException;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class CategoryService implements IClassifierService<Category, UUID> {

    private final ICategoryRepository categoryRepository;
    private final ConversionService conversionService;

    public CategoryService(ICategoryRepository categoryRepository,
                           ConversionService conversionService) {
        this.categoryRepository = categoryRepository;
        this.conversionService = conversionService;
    }

    @Transactional
    @Override
    public Category create(Category category) {
        if (category == null || category.getTitle() == null || category.getTitle().isEmpty()) {
            throw new ValidationException("Не передано название категории");
        } else if (this.categoryRepository.findByTitle(category.getTitle()).isPresent()) {
            throw new ValidationException("Передано не уникальное название категории");
        }

        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        category.setId(id);
        category.setDtCreate(now);
        category.setDtUpdate(now);

        CategoryEntity saveEntity;

        try {
            saveEntity = this.categoryRepository.save(
                    this.conversionService.convert(category, CategoryEntity.class));
        } catch (Exception e) {
            throw new RuntimeException("Ошибка выполнения SQL", e);
        }

        return this.conversionService.convert(saveEntity, Category.class);
    }

    @Override
    public Page<Category> get(Pageable pageable) {
        Collection<CategoryEntity> entities;

        try {
            entities = this.categoryRepository.findByOrderByTitle();
        } catch (Exception e) {
            throw new RuntimeException("Ошибка выполнения SQL", e);
        }

        List<Category> categoryList = entities.stream()
                .map(entity -> this.conversionService.convert(entity, Category.class))
                .collect(Collectors.toList());

        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), categoryList.size());
        return new PageImpl<>(categoryList.subList(start, end), pageable, categoryList.size());
    }

    @Override
    public Category get(UUID uuid) {
        CategoryEntity entity;

        try {
            entity = this.categoryRepository.findById(uuid).orElse(null);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка выполнения SQL", e);
        }

        if (entity == null) {
            throw new ValidationException("Категория с id " + uuid + " не найдена");
        }

        return this.conversionService.convert(entity, Category.class);
    }

    @Override
    public Page<Category> get(Collection<UUID> collectionId, Pageable pageable) {
        List<Category> data;

        if (collectionId == null || collectionId.isEmpty()) {
            data = this.categoryRepository.findByOrderByTitle()
                    .stream()
                    .map(entity -> this.conversionService.convert(entity, Category.class))
                    .collect(Collectors.toList());
        } else {
            data = this.categoryRepository.findByIdInOrderByTitle(collectionId)
                    .stream()
                    .map(entity -> this.conversionService.convert(entity, Category.class))
                    .collect(Collectors.toList());
        }

        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), data.size());
        return new PageImpl<>(data.subList(start, end), pageable, data.size());
    }
}
