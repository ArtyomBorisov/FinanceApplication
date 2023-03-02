package by.itacademy.classifier.service.impl;

import by.itacademy.classifier.dto.Category;
import by.itacademy.classifier.dao.CategoryRepository;
import by.itacademy.classifier.dao.entity.CategoryEntity;
import by.itacademy.classifier.service.ClassifierService;
import by.itacademy.classifier.utils.Generator;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;

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
        generateIdAndTimeAndAddToCategory(category);
        CategoryEntity entityForSaving = conversionService.convert(category, CategoryEntity.class);
        CategoryEntity savedEntity = categoryRepository.save(entityForSaving);
        return conversionService.convert(savedEntity, Category.class);
    }

    @Override
    public Page<Category> get(Pageable pageable) {
        Page<CategoryEntity> entities = categoryRepository.findByOrderByTitle(pageable);
        return convertToDtoPage(entities);
    }

    @Override
    public Category get(UUID uuid) {
        return categoryRepository.findById(uuid)
                .map(entity -> conversionService.convert(entity, Category.class))
                .orElse(null);
    }

    @Override
    public Page<Category> get(Collection<UUID> collectionId, Pageable pageable) {
        if (CollectionUtils.isEmpty(collectionId)) {
            return get(pageable);
        }

        Page<CategoryEntity> entities = categoryRepository.findByIdInOrderByTitle(collectionId, pageable);
        return convertToDtoPage(entities);
    }

    private void generateIdAndTimeAndAddToCategory(Category category) {
        UUID id = generator.generateUUID();
        LocalDateTime now = generator.now();
        category.setId(id);
        category.setDtCreate(now);
        category.setDtUpdate(now);
    }

    private Page<Category> convertToDtoPage(Page<CategoryEntity> entities) {
        return entities.map(entity -> conversionService.convert(entity, Category.class));
    }
}
