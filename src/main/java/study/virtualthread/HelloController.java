package study.virtualthread;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/hello")
@RequiredArgsConstructor
public class HelloController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/sleep")
    public String sleep() throws InterruptedException {
        Thread.sleep(1000);
        return "sleep";
    }

    @GetMapping("/query")
    public String query() {
//        String string = jdbcTemplate.queryForList("select pg_sleep(1)").toString();
        String string = jdbcTemplate.queryForList("select sleep(1)").toString();
        log.info(string);
        return string;
    }

}
