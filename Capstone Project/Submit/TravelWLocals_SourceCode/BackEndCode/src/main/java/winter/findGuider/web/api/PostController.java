package winter.findGuider.web.api;

import entities.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.Post.PostService;

import java.util.List;

@RestController
@RequestMapping(path = "/guiderpost", produces = "application/json")
@CrossOrigin(origins = "*")
public class PostController {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private PostService postService;

    @Autowired
    public PostController(PostService ps) {
        this.postService = ps;
    }

    @RequestMapping("/postOfOneGuider/{guider_id}/{page}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Post>> findAllPostOfOneGuider(@PathVariable("guider_id") long guider_id,
                                                             @PathVariable("page") int page) {
        try {
            return new ResponseEntity<>(postService.findAllPostOfOneGuider(guider_id, page), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/postOfOneGuiderPageCount/{guider_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Integer> postOfOneGuiderPageCount(@PathVariable("guider_id") long guider_id) {
        try {
            return new ResponseEntity<>(postService.findAllPostOfOneGuiderPageCount(guider_id), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/postOfOneGuiderAdmin")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Post>> findAllPostOfOneGuiderAdmin(@RequestParam("guider_id") long guider_id) {
        try {
            return new ResponseEntity<>(postService.findAllPostOfOneGuiderAdmin(guider_id), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/allPostOfOneCategory/{category_id}/{page}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Post>> findAllPostOfOneCategory(@PathVariable("category_id") long category_id,
                                                               @PathVariable("page") int page) {
        try {
            return new ResponseEntity<>(postService.findAllPostByCategoryId(category_id, page), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/allPostOfOneCategoryPageCount/{category_id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Integer> allPostOfOneCategoryPageCount(@PathVariable("category_id") long category_id) {
        try {
            return new ResponseEntity<>(postService.findAllPostByCategoryIdPageCount(category_id), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/findSpecificPost")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Post> findSpecificPost(@RequestParam("post_id") long id) {
        try {
            return new ResponseEntity(postService.findSpecificPost(id), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(consumes = "application/json", value = "/update/post")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Long> updatePost(@RequestBody Post post) {
        try {
            postService.updatePost(post.getPost_id(), post);
            return new ResponseEntity(post.getPost_id(), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping(consumes = "application/json", value = "/add/post")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Integer> insertNewPost(@RequestParam("guider_id") long guider_id, @RequestBody Post post) {
        try {
            System.out.println(post.getCategory());
            return new ResponseEntity(postService.insertNewPost(guider_id, post), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(post.getCategory() + e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/getTopTour")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Post>> getTop5Tour() {
        try {
            return new ResponseEntity<>(postService.getTopTour(), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/findAllPostWithGuiderName/{name}/{page}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Post>> findAllPostWithGuiderName(@PathVariable("name") String name,
                                                                @PathVariable("page") int page) {
        try {
            return new ResponseEntity<>(postService.findAllPostWithGuiderName(name, page), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/findAllPostWithGuiderNamePageCount/{name}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Integer> findAllPostWithGuiderNamePageCount(@PathVariable("name") String name) {
        try {
            return new ResponseEntity<>(postService.findAllPostWithGuiderNamePageCount(name), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/activeDeactivePost")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Boolean> activeDeactivePost(@RequestParam("post_id") long post_id) {
        try {
            postService.activeDeactivePostForGuider(post_id);
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/findAllPostWithLocationName/{name}/{page}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Post>> findAllPostWithLocationName(@PathVariable("name") String name,
                                                                  @PathVariable("page") int page) {
        try {
            return new ResponseEntity<>(postService.findAllPostWithLocationName(name, page), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/findAllPostWithLocationNamePageCount/{name}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Integer> findAllPostWithLocationNamePageCount(@PathVariable("name") String name) {
        try {
            return new ResponseEntity<>(postService.findAllPostWithLocationNamePageCount(name), HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    @RequestMapping("/authorizePost")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Boolean> authorizePost(@RequestParam("post_id") long post_id) {
        try {
            postService.authorizePost(post_id);
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
        }
    }
}
