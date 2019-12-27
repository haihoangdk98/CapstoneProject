package winter.findGuider.web.api;

import entities.Post;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import services.Post.PostServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

public class PostControllerUnitTest {
    @InjectMocks
    PostController postController;

    @Mock
    PostServiceImpl postService;

    @Rule
    public ExpectedException thrown= ExpectedException.none();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindAllPostOfOneGuider(){
        ResponseEntity<List<Post>> result = postController.findAllPostOfOneGuider(1,1);
        Assert.assertEquals(200,result.getStatusCodeValue());
    }

    @Test
    public void testFindAllPostOFOneGuiderWithException() throws Exception{

        when(postService.findAllPostOfOneGuider(1,1)).thenThrow(Exception.class);
        ResponseEntity<List<Post>> result = postController.findAllPostOfOneGuider(1,1);
        Assert.assertEquals(404,result.getStatusCodeValue());
    }

    @Test
    public void testFindAllPostOfOneCategory(){
        ResponseEntity<List<Post>> result = postController.findAllPostOfOneCategory(1,1);
        Assert.assertEquals(200,result.getStatusCodeValue());
    }

    @Test(expected = AssertionError.class)
    public void testFindAllPostOFOneCategoryWithException() throws Exception{
        thrown.expect(AssertionError.class);
        when(postService.findAllPostByCategoryId(1,1)).thenThrow(Exception.class);
        ResponseEntity<List<Post>> result = postController.findAllPostOfOneCategory(1,1);
        Assert.assertEquals(404,result.getStatusCodeValue());
    }

    @Test
    public void testFindSpecificPost(){
        ResponseEntity<Post> result = postController.findSpecificPost(1);
        Assert.assertEquals(200,result.getStatusCodeValue());
    }

    @Test(expected = AssertionError.class)
    public void testFindSpecificPOstWithException() throws Exception{
        thrown.expect(AssertionError.class);
        when(postService.findSpecificPost(1)).thenThrow(Exception.class);
        ResponseEntity<Post> result = postController.findSpecificPost(1);
        Assert.assertEquals(404,result.getStatusCodeValue());
    }

    @Test
    public void testUpdatePost(){
        Post post = Mockito.mock(Post.class);
        ResponseEntity<Long> result = postController.updatePost(post);
        Assert.assertEquals(200,result.getStatusCodeValue());
    }

    @Test
    public void testUpdatePostWithException() throws Exception {
        Post post = Mockito.mock(Post.class);
        ReflectionTestUtils.setField(postController, "postService", null);
        ResponseEntity<Long> result = postController.updatePost(post);
        Assert.assertEquals(404,result.getStatusCodeValue());
    }

    @Test
    public void testInsertNewPost(){
        Post post = Mockito.mock(Post.class);
        ResponseEntity<Integer> result = postController.insertNewPost(1,post);
        Assert.assertEquals(200,result.getStatusCodeValue());
    }

    @Test(expected = AssertionError.class)
    public void testInsertNewPostWithException() throws Exception{
        thrown.expect(AssertionError.class);
        Post post = Mockito.mock(Post.class);
        when(postService.insertNewPost(1,post)).thenThrow(Exception.class);
        ResponseEntity<Integer> result = postController.insertNewPost(1,post);
        Assert.assertEquals(404,result.getStatusCodeValue());
    }

    @Test
    public void testGetTop5TourWithException() throws Exception{
        when(postService.getTopTour()).thenThrow(Exception.class);
        ResponseEntity<List<Post>> result = postController.getTop5Tour();
        Assert.assertEquals(404,result.getStatusCodeValue());
    }

    @Test
    public void testFindAllPostWithGuiderNameWithException() throws Exception{
        when(postService.findAllPostWithGuiderName("ha",1)).thenThrow(Exception.class);
        ResponseEntity<List<Post>> result = postController.findAllPostWithGuiderName("ha",1);
        Assert.assertEquals(404,result.getStatusCodeValue());
    }

    @Test
    public void testFindAllPostWithLocationNameWithException() throws Exception{
        when(postService.findAllPostWithLocationName("ha",1)).thenThrow(Exception.class);
        ResponseEntity<List<Post>> result = postController.findAllPostWithLocationName("ha",1);
        Assert.assertEquals(404,result.getStatusCodeValue());
    }

    @Test
    public void testActivatePost(){
        ResponseEntity<Boolean> result = postController.activeDeactivePost(1);
        Assert.assertEquals(200,result.getStatusCodeValue());
    }
    @Test
    public void testActivatePostWithException() throws Exception {
        ReflectionTestUtils.setField(postController, "postService", null);

        ResponseEntity<Boolean> result = postController.activeDeactivePost(1);
        Assert.assertEquals(404,result.getStatusCodeValue());
    }

    @Test
    public void testAuthorizePost(){
        ResponseEntity<Boolean> result = postController.authorizePost(1);
        Assert.assertEquals(200,result.getStatusCodeValue());
    }
    @Test
    public void testAuthorizePostWithException() throws Exception {
        ReflectionTestUtils.setField(postController, "postService", null);

        ResponseEntity<Boolean> result = postController.authorizePost(1);
        Assert.assertEquals(404,result.getStatusCodeValue());
    }

    @Test
    public void testPostOfOneGuiderPageCount(){
        ResponseEntity<Integer> result = postController.postOfOneGuiderPageCount(1);
        Assert.assertEquals(200,result.getStatusCodeValue());
    }
    @Test
    public void testPostOfOneGuiderPageCountWithException() throws Exception {
        ReflectionTestUtils.setField(postController, "postService", null);

        ResponseEntity<Integer> result = postController.postOfOneGuiderPageCount(1);
        Assert.assertEquals(404,result.getStatusCodeValue());
    }

    @Test
    public void testFindAllPostGuiderAdmin(){
        ResponseEntity<List<Post>> result = postController.findAllPostOfOneGuiderAdmin(1);
        Assert.assertEquals(200,result.getStatusCodeValue());
    }
    @Test
    public void testFindAllPostGuiderAdminWithException() throws Exception {
        ReflectionTestUtils.setField(postController, "postService", null);

        ResponseEntity<List<Post>> result = postController.findAllPostOfOneGuiderAdmin(1);
        Assert.assertEquals(404,result.getStatusCodeValue());
    }

    @Test
    public void testAllPostOfOnecategoryPageCountn(){
        ResponseEntity<Integer> result = postController.allPostOfOneCategoryPageCount(1);
        Assert.assertEquals(200,result.getStatusCodeValue());
    }
    @Test
    public void testAllPostOfOnecategoryPageCountnWithException() throws Exception {
        ReflectionTestUtils.setField(postController, "postService", null);

        ResponseEntity<Integer> result = postController.allPostOfOneCategoryPageCount(1);
        Assert.assertEquals(404,result.getStatusCodeValue());
    }

    @Test
    public void testFindAllPostWithGuiderNamePageCount(){
        ResponseEntity<Integer> result = postController.findAllPostWithGuiderNamePageCount("as");
        Assert.assertEquals(200,result.getStatusCodeValue());
    }
    @Test
    public void testFindAllPostWithGuiderNamePageCountWithException() throws Exception {
        ReflectionTestUtils.setField(postController, "postService", null);

        ResponseEntity<Integer> result = postController.findAllPostWithGuiderNamePageCount("as");
        Assert.assertEquals(404,result.getStatusCodeValue());
    }

    @Test
    public void testFindAllPostWitLocationNamePageCount(){
        ResponseEntity<Integer> result = postController.findAllPostWithLocationNamePageCount("as");
        Assert.assertEquals(200,result.getStatusCodeValue());
    }
    @Test
    public void testFindAllPostWithLocationNamePageCountWithException() throws Exception {
        ReflectionTestUtils.setField(postController, "postService", null);

        ResponseEntity<Integer> result = postController.findAllPostWithLocationNamePageCount("as");
        Assert.assertEquals(404,result.getStatusCodeValue());
    }
}
