package study.virtualthread.controller;

import static java.util.concurrent.CompletableFuture.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import study.virtualthread.entity.Post;
import study.virtualthread.service.PostServiceImpl;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/posts")
public class PostController {

	private final PostServiceImpl postService;

	@PostMapping
	public void writeRandomPost() {
		log.info("글 생성");
		postService.writeRandomPost();
	}

	public void writeRandomPosts(int count, ExecutorService executor) {
		List<CompletableFuture<Post>> futures = new ArrayList<>();
		for (int i = 0; i < count; i++) {
			if (executor == null) {
				futures.add(CompletableFuture.supplyAsync(postService::writeRandomPost));
			} else {
				futures.add(CompletableFuture.supplyAsync(postService::writeRandomPost, executor));
			}
		}
		String result = allOf(futures.toArray(new CompletableFuture[0]))
			.thenApply(v -> {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < futures.size(); i++) {
					if (i > 0) {
						sb.append(", ");
					}
					sb.append(futures.get(i).join().getId());
				}
				return sb.toString();
			}).join();
		log.info(result);
	}

	@PostMapping("/{count}/platform")
	public void writeRandomPostByPlatformThread(
		@PathVariable int count
	) {
		writeRandomPosts(count, null);
	}

	@PostMapping("/{count}/virtual")
	public void writeRandomPostByVirtualThread(
		@PathVariable int count
	) {
		writeRandomPosts(count, Executors.newVirtualThreadPerTaskExecutor());
	}

}
