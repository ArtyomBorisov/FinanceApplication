package by.itacademy.classifier.converter;

import by.itacademy.classifier.dto.Category;
import by.itacademy.classifier.dao.entity.CategoryEntity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CategoryEntityToDtoConverter implements Converter<CategoryEntity, Category> {
    @Override
    public Category convert(CategoryEntity entity) {
        return Category.Builder.createBuilder()
                .setId(entity.getId())
                .setDtCreate(entity.getDtCreate())
                .setDtUpdate(entity.getDtUpdate())
                .setTitle(entity.getTitle())
                .build();
    }
}
