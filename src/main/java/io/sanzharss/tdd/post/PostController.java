package io.sanzharss.tdd.post;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
public class PostController {

  private final PostRepository postRepository;

  public PostController(PostRepository postRepository) {
    this.postRepository = postRepository;
  }

  @GetMapping("")
  List<Post> findAll() {
    return postRepository.findAll();
  }

  @GetMapping("/{id}")
  Optional<Post> findById(@PathVariable Integer id) {
    return Optional.ofNullable(postRepository.findById(id)
        .orElseThrow(PostNotFoundException::new));
  }

  @ResponseStatus(HttpStatus.CREATED)
  @PostMapping("")
  Post create(@RequestBody @Valid Post post) {
    return postRepository.save(post);
  }

  @PutMapping("{id}")
  Post update(@PathVariable Integer id, @RequestBody @Valid Post post) {
    Optional<Post> optionalPost = postRepository.findById(id);
    if (optionalPost.isPresent()) {
      var updatedPost = new Post(
          optionalPost.get().id(),
          optionalPost.get().userId(),
          post.title(),
          post.body(),
          optionalPost.get().version()
      );

      return postRepository.save(updatedPost);
    } else {
      throw new PostNotFoundException();
    }
  }

  @ResponseStatus(HttpStatus.NO_CONTENT)
  @DeleteMapping("{id}")
  void delete(@PathVariable Integer id) {
    postRepository.deleteById(id);
  }
}
