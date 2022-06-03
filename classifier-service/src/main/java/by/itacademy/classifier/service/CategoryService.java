package by.itacademy.classifier.service;

import by.itacademy.classifier.model.Category;
import by.itacademy.classifier.repository.api.ICategoryRepository;
import by.itacademy.classifier.repository.entity.CategoryEntity;
import by.itacademy.classifier.service.api.IClassifierService;
import by.itacademy.classifier.service.api.MessageError;
import by.itacademy.classifier.service.api.ValidationError;
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
            throw new ValidationException(
                    new ValidationError("title (название категории)", MessageError.MISSING_FIELD));

        } else if (this.categoryRepository.findByTitle(category.getTitle()).isPresent()) {
            throw new ValidationException(
                    new ValidationError("title (название категории)", MessageError.NO_UNIQUE_FIELD));
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
            throw new RuntimeException(MessageError.SQL_ERROR, e);
        }

        return this.conversionService.convert(saveEntity, Category.class);
    }

    @Override
    public Page<Category> get(Pageable pageable) {
        Page<CategoryEntity> entities;

        try {
            entities = this.categoryRepository.findByOrderByTitle(pageable);
        } catch (Exception e) {
            throw new RuntimeException(MessageError.SQL_ERROR, e);
        }

        return new PageImpl<>(entities.stream()
                .map(entity -> this.conversionService.convert(entity, Category.class))
                .collect(Collectors.toList()), pageable, entities.getTotalElements());
    }

    @Override
    public Category get(UUID uuid) {
        CategoryEntity entity;

        try {
            entity = this.categoryRepository.findById(uuid).orElse(null);
        } catch (Exception e) {
            throw new RuntimeException(MessageError.SQL_ERROR, e);
        }

        if (entity == null) {
            throw new ValidationException(MessageError.ID_NOT_EXIST);
        }

        return this.conversionService.convert(entity, Category.class);
    }

    @Override
    public Page<Category> get(Collection<UUID> collectionId, Pageable pageable) {
        Page<CategoryEntity> entities;

        if (collectionId == null || collectionId.isEmpty()) {
            entities = this.categoryRepository.findByOrderByTitle(pageable);
        } else {
            List<ValidationError> errors = new ArrayList<>();

            for (UUID id : collectionId) {
                if (!this.categoryRepository.existsCategoryEntityById(id)) {
                    errors.add(new ValidationError(id.toString(), MessageError.ID_NOT_EXIST));
                }
            }

            if (!errors.isEmpty()) {
                throw new ValidationException(errors);
            }

            entities = this.categoryRepository.findByIdInOrderByTitle(collectionId, pageable);
        }

        return new PageImpl<>(entities.stream()
                .map(entity -> this.conversionService.convert(entity, Category.class))
                .collect(Collectors.toList()), pageable, entities.getTotalElements());
    }
}
