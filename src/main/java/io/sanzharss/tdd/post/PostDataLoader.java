package io.sanzharss.tdd.post;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.asm.TypeReference;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class PostDataLoader implements CommandLineRunner {

  private static final Logger log = LoggerFactory.getLogger(PostDataLoader.class);
  private final ObjectMapper objectMapper;
  private final PostRepository postRepository;

  public PostDataLoader(final ObjectMapper objectMapper, final PostRepository postRepository) {
    this.objectMapper = objectMapper;
    this.postRepository = postRepository;
  }

  @Override
  public void run(String... args) throws Exception {
    if (postRepository.count() == 0) {
      String POSTS_JSON = "/data/posts.json";
      log.info("Loading posts into database from JSON: {}", POSTS_JSON);
      try (InputStream inputStream = TypeReference.class.getResourceAsStream(POSTS_JSON)) {
        Posts response = objectMapper.readValue(inputStream, Posts.class);
        postRepository.saveAll(response.posts());
      } catch (IOException e) {
        throw new RuntimeException("Failed to read JSON data", e);
      }
    }
  }
}
