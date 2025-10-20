package study.virtualthread.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import study.virtualthread.entity.Post;

public interface PostJpaRepository extends JpaRepository<Post, Long> {
}
