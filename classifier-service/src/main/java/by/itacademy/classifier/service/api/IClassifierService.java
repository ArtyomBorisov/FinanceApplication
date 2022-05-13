package by.itacademy.classifier.service.api;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IClassifierService<T, ID> {
    T create(T item);
    Page<T> get(Pageable pageable);
    T get(ID id);
}
