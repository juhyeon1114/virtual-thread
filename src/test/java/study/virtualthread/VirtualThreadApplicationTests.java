package study.virtualthread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class VirtualThreadApplicationTests {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	@DisplayName("Virtual thread 사용법")
	void howToUse() {
		Runnable runnable = () -> log.info("Hello world");

		// 1) .startVirtualThread() 사용
		Thread.startVirtualThread(runnable);

		// 2) Builder 사용
		Thread.ofVirtual()
			.name("Virtual thread") // 생략 가능
			.start(runnable);

		// 3) ExecutorService 사용
		ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
		executorService.submit(runnable);
	}

	@Test
	@DisplayName("Virtual Thread로 100개의 sleep 쿼리 실행")
	void executeMultipleSleepQueries() throws ExecutionException, InterruptedException {
		long startTime = System.currentTimeMillis();

		// 작업 정의
		List<Runnable> tasks = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			tasks.add(() -> {
				jdbcTemplate.queryForList("select sleep(1)");
				log.info("실행");
			});
		}

		// 작업 실행
		ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
		List<? extends Future<?>> futures = tasks.stream()
			.map(executorService::submit)
			.toList();

		// 작업 취합
		for (Future<?> future : futures) {
			future.get();
		}

		// 결과 출력
		long endTime = System.currentTimeMillis();

		log.info("전체 실행 시간: {} ms", endTime - startTime);
		log.info("TPS: {}", String.format("%.2f", 100 / (endTime - startTime) / 1000.0));
	}

}
