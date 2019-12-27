package winter.findGuider.web.api;

import entities.Category;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import services.Category.CategoryServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

public class CategoryControllerUnitTest {
    @InjectMocks
    CategoryController categoryController;

    @Mock
    CategoryServiceImpl categoryService;

    @Rule
    public ExpectedException thrown= ExpectedException.none();
    @Before
    public void init() {

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindAllcategory(){
        List<Category> listLocation = new ArrayList<>();
        //when(locationService.showAllLocation()).then()

        ResponseEntity<Category> result =categoryController.findAllCategory();
        Assert.assertEquals(200, result.getStatusCodeValue());
        //Assert.assertEquals(true, result.equals());
    }

    @Test
    public void testCategoryWithException() throws Exception{

        when(categoryService.findAllCategory()).thenThrow(Exception.class);
        ResponseEntity<Category> result =categoryController.findAllCategory();


        Assert.assertEquals(404, result.getStatusCodeValue());
    }
    @Test
    public void testCreateCategory(){
        Category category = new Category();
        category.setCategory("test");
        category.setImage("Test.jpg");
        ResponseEntity<Boolean> result = categoryController.createCategory(category);
        Assert.assertEquals(200,result.getStatusCodeValue());
    }
    @Test
    public void testActivatePostWithException() throws Exception {
        ReflectionTestUtils.setField(categoryController, "categoryService", null);
        Category category = new Category();
        ResponseEntity<Boolean> result = categoryController.createCategory(category);
        Assert.assertEquals(404,result.getStatusCodeValue());
    }
}
