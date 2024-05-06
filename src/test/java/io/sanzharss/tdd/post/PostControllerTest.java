package io.sanzharss.tdd.post;


import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

// Slice test - only loads relevant parts for the web
@WebMvcTest(PostController.class)
@AutoConfigureWebMvc
// going to make mock calls to controller(rest api) and return responses, not loading tomcat and servlet container
public class PostControllerTest {

  @Autowired
  MockMvc mockMvc;

  @MockBean
  PostRepository postRepository;

  List<Post> posts = new ArrayList<Post>();

  @BeforeEach
  void setUp() {
    // create some posts
    posts = List.of(
        new Post(1, 1, "Hello, World!", "This is my first post", null),
        new Post(2, 1, "Second post", "This is my second post", null)
    );
  }

  @Test
  void whenFindAllPosts_thenReturnList() throws Exception {
    String jsonResponse = """
        [
            {
                "id": 1,
                "userId": 1,
                "title": "Hello, World!",
                "body": "This is my first post",
                "version": null
            },
            {
                "id": 2,
                "userId": 1,
                "title": "Second post",
                "body": "This is my second post",
                "version": null
            }
        ]
        """;
    when(postRepository.findAll()).thenReturn(posts);

    mockMvc.perform(get("/api/posts"))
        .andExpect(status().isOk())
        .andExpect(content().json(jsonResponse));
  }

  @Test
  void whenFindPostById_thenReturnPost() throws Exception {
    when(postRepository.findById(1)).thenReturn(Optional.of(posts.getFirst()));

    var post = posts.getFirst();
    String json = STR."""
        {
            "id": \{post.id()},
            "userId": \{post.userId()},
            "title": "\{post.title()}",
            "body": "\{post.body()}",
            "version": null
        }
        """;

    mockMvc.perform(get("/api/posts/1"))
        .andExpect(status().isOk())
        .andExpect(content().json(json));
  }

  @Test
  void whenFindPostById_thenReturnNotFound() throws Exception {
    when(postRepository.findById(999)).thenThrow(PostNotFoundException.class);

    mockMvc.perform(get("/api/posts/999"))
        .andExpect(status().isNotFound());
  }

  @Test
  void whenCreatePost_thenReturnPost() throws Exception {
    var post = new Post(3, 1, "New title", "This is my third post", null);
    when(postRepository.save(post)).thenReturn(post);
    String json = STR."""
        {
            "id": \{post.id()},
            "userId": \{post.userId()},
            "title": "\{post.title()}",
            "body": "\{post.body()}",
            "version": null
        }
        """;

    mockMvc.perform(post("/api/posts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isCreated());
  }

  @Test
  void whenCreatePost_thenReturnBadRequest() throws Exception {
    var post = new Post(3, 1, "", "", null);
    when(postRepository.save(post)).thenReturn(post);
    String json = STR."""
        {
            "id": \{post.id()},
            "userId": \{post.userId()},
            "title": "\{post.title()}",
            "body": "\{post.body()}",
            "version": null
        }
        """;

    mockMvc.perform(post("/api/posts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest());
  }

  @Test
  void whenUpdatePost_thenReturnPost() throws Exception {
    var updatedPost = new Post(1, 1, "New title", "Body post updated", null);
    when(postRepository.findById(1)).thenReturn(Optional.of(posts.getFirst()));
    when(postRepository.save(updatedPost)).thenReturn(updatedPost);
    String json = STR."""
        {
            "id": \{updatedPost.id()},
            "userId": \{updatedPost.userId()},
            "title": "\{updatedPost.title()}",
            "body": "\{updatedPost.body()}",
            "version": \{updatedPost.version()}
        }
        """;

    mockMvc.perform(put("/api/posts/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isOk());
  }

  @Test
  void whenUpdatePost_thenReturnBadRequest() throws Exception {
    var updatedPost = new Post(1, 1, "", "", null);
    String json = STR."""
        {
            "id": \{updatedPost.id()},
            "userId": \{updatedPost.userId()},
            "title": "\{updatedPost.title()}",
            "body": "\{updatedPost.body()}",
            "version": \{updatedPost.version()}
        }
        """;

    mockMvc.perform(put("/api/posts/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(json))
        .andExpect(status().isBadRequest());
  }

  @Test
  void whenDeletePost_thenIsOk() throws Exception {
    doNothing().when(postRepository).deleteById(1);

    mockMvc.perform(delete("/api/posts/1"))
        .andExpect(status().isNoContent());
  }

  @Test
  void whenDeletePost_thenNotFound() throws Exception {
    doThrow(PostNotFoundException.class).when(postRepository).deleteById(1);

    mockMvc.perform(delete("/api/posts/1"))
        .andExpect(status().isNotFound());
  }
}
