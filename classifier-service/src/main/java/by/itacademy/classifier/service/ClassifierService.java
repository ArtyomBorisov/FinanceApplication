package by.itacademy.classifier.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;

public interface ClassifierService<T, ID> {
    T create(T item);
    Page<T> get(Pageable pageable);
    T get(ID id);
    Page<T> get(Collection<ID> collectionId, Pageable pageable);
}
