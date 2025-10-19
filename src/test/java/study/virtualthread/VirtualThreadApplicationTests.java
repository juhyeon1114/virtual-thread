package study.virtualthread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class VirtualThreadApplicationTests {

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

}
