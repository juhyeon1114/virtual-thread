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

	// Pinning 관찰용: synchronized로 모니터를 잡은 상태에서 블로킹 호출 실행
	public void runQuery(int count) {
		Object lock = new Object();
		synchronized (lock) {
			jdbcTemplate.queryForList("select sleep(1)");
		}
		log.info("실행(pinned {}/100)", count);
	}

	@Test
	@DisplayName("Virtual Thread - pinning 이슈")
	void test() throws ExecutionException, InterruptedException {
		long startTime = System.currentTimeMillis();

		// 작업 정의
		List<Runnable> tasks = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			int count = i + 1;
			tasks.add(() -> runQuery(count));
		}

		// 작업 실행 및 종료 보장
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
		long elapsedMs = endTime - startTime;
		double tps = 100.0 / (elapsedMs / 1000.0);

		log.info("전체 실행 시간: {} ms", elapsedMs);
		log.info("TPS: {}", String.format("%.2f", tps));
	}

}
