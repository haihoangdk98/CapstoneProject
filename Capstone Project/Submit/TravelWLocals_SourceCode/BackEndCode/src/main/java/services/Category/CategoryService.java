package services.Category;

import entities.Category;

import java.util.List;

public interface CategoryService {

    List<Category> findAllCategory() throws Exception;

    void createCategory(Category category) throws Exception;
}
