package study.virtualthread.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import study.virtualthread.entity.Post;
import study.virtualthread.repository.PostJpaRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl {

	private final PostJpaRepository postJpaRepository;

	public Post writePost(String title, String content) {
		return postJpaRepository.saveAndFlush(Post.create(title, content));
	}

	public Post writeRandomPost() {
		return writePost(UUID.randomUUID().toString(), UUID.randomUUID().toString());
	}

}
