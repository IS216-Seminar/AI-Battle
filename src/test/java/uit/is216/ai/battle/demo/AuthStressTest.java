package uit.is216.ai.battle.demo;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uit.is216.ai.battle.demo.repositories.UserRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthStressTest {
    private static final Logger log = LoggerFactory.getLogger(AuthStressTest.class);
    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    private String validToken;
    @BeforeEach
    void setup() throws Exception {
        userRepository.deleteAll(); 
        // Khởi tạo user mẫu
        String signupJson = "{\"email\": \"user@gmail.com\", \"password\": \"123456\", \"confirmPassword\": \"123456\"}";
        mockMvc.perform(post("/public/auth/signup").contentType(MediaType.APPLICATION_JSON).content(signupJson));
        String loginJson = "{\"email\": \"user@gmail.com\", \"password\": \"123456\"}";
        MvcResult result = mockMvc.perform(post("/public/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJson)).andReturn();
        String response = result.getResponse().getContentAsString();
        // Trích xuất token an toàn hơn
        if (response.contains("accessToken")) {
            this.validToken = response.split("\"accessToken\":\"")[1].split("\"")[0];
        }
    }

    @Test
    void stressTestLogin() throws InterruptedException {
        int threads = 50;
        int totalRequests = 200;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        for (int i = 0; i < totalRequests; i++) {
            executor.execute(() -> {
                try {
                    String json = "{\"email\": \"user@gmail.com\", \"password\": \"123456\"}";
                    mockMvc.perform(post("/public/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                            .andExpect(status().isOk());
                    successCount.incrementAndGet();
                } catch (Throwable e) {
                    errorCount.incrementAndGet();
                    log.error("STRESS_LOGIN_ERROR: Request failed - {}", e.getMessage());
                }
            });
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);

        log.info("--- LOGIN STRESS RESULT: Success: {}, Errors: {} ---", successCount.get(), errorCount.get());
        assertThat(successCount.get()).isEqualTo(totalRequests); // Ép buộc 100% request phải thành công
    }

    @Test
    void stressTestRaceConditionSignup() throws InterruptedException {
        int threads = 20; // 20 người cùng nhấn nút đăng ký 1 lúc
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger conflictCount = new AtomicInteger(0);
        for (int i = 0; i < threads; i++) {
            executor.execute(() -> {
                try {
                    String json = "{\"email\": \"race@gmail.com\", \"password\": \"123\", \"confirmPassword\": \"123\"}";
                    MvcResult res = mockMvc.perform(post("/public/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json)).andReturn();                   
                    int status = res.getResponse().getStatus();
                    if (status == 201) successCount.incrementAndGet();
                    else if (status == 409) conflictCount.incrementAndGet();
                } catch (Exception e) {
                    log.error("STRESS_SIGNUP_ERROR: {}", e.getMessage());
                }
            });
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        log.info("--- RACE CONDITION RESULT: Created: {}, Conflicts (Expected): {} ---", successCount.get(), conflictCount.get());
        assertThat(successCount.get()).isEqualTo(1); // Chỉ DUY NHẤT 1 user được tạo thành công
    }

    @Test
    void stressTestMixedLoad() throws InterruptedException {
        int totalLoad = 100;
        ExecutorService executor = Executors.newFixedThreadPool(30);
        AtomicInteger processed = new AtomicInteger(0);
        AtomicInteger failed = new AtomicInteger(0);
        for (int i = 0; i < totalLoad; i++) {
            final int index = i;
            executor.execute(() -> {
                try {
                    if (index % 2 == 0) {
                        mockMvc.perform(post("/public/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\": \"user@gmail.com\", \"password\": \"123456\"}"))
                            .andExpect(status().isOk());
                    } else {
                        mockMvc.perform(get("/user/profile")
                            .header("Authorization", "Bearer " + validToken))
                            .andExpect(status().isOk());
                    }
                    processed.incrementAndGet();
                } catch (Throwable e) {
                    failed.incrementAndGet();
                    log.error("STRESS_MIXED_ERROR: Index {} failed - {}", index, e.getMessage());
                }
            });
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES);
        log.info("--- MIXED LOAD RESULT: Processed: {}, Failed: {} ---", processed.get(), failed.get());
        assertThat(failed.get()).isZero(); // Không được phép có request nào lỗi
    }
}