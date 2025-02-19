package com.lb.book_worm_api.repository;

import com.lb.book_worm_api.model.Book;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class BookSpecification {

    public static Specification<Book> filterByCriteria(Integer year, String language) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (year != null) {
                predicates.add(cb.equal(root.get("publicationYear"), year));
            }
            if (language != null) {
                predicates.add(cb.equal(root.get("lingo"), language));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
