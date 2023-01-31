package by.itacademy.classifier.service.impl;

import by.itacademy.classifier.dto.Category;
import by.itacademy.classifier.repository.CategoryRepository;
import by.itacademy.classifier.repository.entity.CategoryEntity;
import by.itacademy.classifier.service.ClassifierService;
import by.itacademy.classifier.utils.Generator;
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
public class CategoryService implements ClassifierService<Category, UUID> {

    private final CategoryRepository categoryRepository;
    private final ConversionService conversionService;
    private final Generator generator;

    public CategoryService(CategoryRepository categoryRepository,
                           ConversionService conversionService,
                           Generator generator) {
        this.categoryRepository = categoryRepository;
        this.conversionService = conversionService;
        this.generator = generator;
    }

    @Transactional
    @Override
    public Category create(Category category) {
        UUID id = generator.generateUUID();
        LocalDateTime now = generator.now();

        category.setId(id);
        category.setDtCreate(now);
        category.setDtUpdate(now);

        CategoryEntity entity = conversionService.convert(category, CategoryEntity.class);
        CategoryEntity savedEntity = categoryRepository.save(entity);
        return conversionService.convert(savedEntity, Category.class);
    }

    @Override
    public Page<Category> get(Pageable pageable) {
        Page<CategoryEntity> entities = categoryRepository.findByOrderByTitle(pageable);

        List<Category> categories = entities.stream()
                .map(entity -> conversionService.convert(entity, Category.class))
                .collect(Collectors.toList());

        return new PageImpl<>(categories, pageable, entities.getTotalElements());
    }

    @Override
    public Category get(UUID uuid) {
        CategoryEntity entity = categoryRepository.findById(uuid).orElse(null);

        return entity != null ? conversionService.convert(entity, Category.class) : null;
    }

    @Override
    public Page<Category> get(Collection<UUID> collectionId, Pageable pageable) {
        if (collectionId == null || collectionId.isEmpty()) {
            return get(pageable);
        }

        Page<CategoryEntity> entities = categoryRepository.findByIdInOrderByTitle(collectionId, pageable);

        List<Category> categories = entities.stream()
                .map(entity -> conversionService.convert(entity, Category.class))
                .collect(Collectors.toList());

        return new PageImpl<>(categories, pageable, entities.getTotalElements());
    }
}
