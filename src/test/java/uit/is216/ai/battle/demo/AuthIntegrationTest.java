
package uit.is216.ai.battle.demo;
// Các Annotation và Class thiếu
import uit.is216.ai.battle.demo.entities.User;
import uit.is216.ai.battle.demo.repositories.UserRepository;
import uit.is216.ai.battle.demo.repositories.RefreshTokenRepository;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

// Import các Repository của chính bạn (Cần đổi 'demo' thành đường dẫn folder repository của bạn)

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Đảm bảo mỗi lần test xong dữ liệu sẽ được xóa sạch
public class AuthIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private RefreshTokenRepository refreshTokenRepository;
    @Autowired private ObjectMapper objectMapper;

    // Helper method để tạo nhanh 1 user hợp lệ phục vụ các test case login/profile
    private void createDefaultUser() {
        // Bạn nên gọi API signup để tạo user đúng quy trình
        String signupJson = "{\"email\": \"user@gmail.com\", \"password\": \"123456\", \"confirmPassword\": \"123456\"}";
        try {
            mockMvc.perform(post("/public/auth/signup").contentType(MediaType.APPLICATION_JSON).content(signupJson));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testFullAuthFlow() throws Exception {
        // 1. Signup Success
        String signupJson = "{\"email\": \"newuser@gmail.com\", \"password\": \"123456\", \"confirmPassword\": \"123456\"}";
        mockMvc.perform(post("/public/auth/signup").contentType(MediaType.APPLICATION_JSON).content(signupJson))
                .andExpect(status().isCreated());

        // 2. Login & Check Both Tokens + Refresh Token in DB
        String loginJson = "{\"email\": \"newuser@gmail.com\", \"password\": \"123456\"}";
        MvcResult loginResult = mockMvc.perform(post("/public/auth/login").contentType(MediaType.APPLICATION_JSON).content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andReturn();

        // 3. Check Profile Data
        String token = objectMapper.readTree(loginResult.getResponse().getContentAsString()).get("accessToken").asText();
        mockMvc.perform(get("/user/profile").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newuser@gmail.com")); // Thêm check "correct user data"

        User user = userRepository.findByEmail("newuser@gmail.com").orElseThrow(); 
//assertThat(user.getPassword()).startsWith("$2a$");
//assertThat(refreshTokenRepository.findAll()).isNotEmpty();
        //User user = (User) userRepository.findByEmail("newuser@gmail.com").orElseThrow();
        assertThat(user.getPassword()).startsWith("$2a$");
        assertThat(refreshTokenRepository.findAll()).isNotEmpty();
    }

    @Test
    void testLoginFailures() throws Exception {
        createDefaultUser(); // Đảm bảo có user để test sai pass

        // Sai password -> 401
        String wrongPass = "{\"email\": \"user@gmail.com\", \"password\": \"wrong\"}";
        mockMvc.perform(post("/public/auth/login").contentType(MediaType.APPLICATION_JSON).content(wrongPass))
                .andExpect(status().isUnauthorized());

        // Email không tồn tại -> 401
        String unknownEmail = "{\"email\": \"nobody@gmail.com\", \"password\": \"123456\"}";
        mockMvc.perform(post("/public/auth/login").contentType(MediaType.APPLICATION_JSON).content(unknownEmail))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testSecurityConstraints() throws Exception {
        // Without Token -> 401
        mockMvc.perform(get("/user/profile")).andExpect(status().isUnauthorized());

        // Invalid Token -> 401
        mockMvc.perform(get("/user/profile").header("Authorization", "Bearer invalid.token.here"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testSignupValidation() throws Exception {
        // Mismatched Pass -> 400
        String mismatchJson = "{\"email\": \"u2@gmail.com\", \"password\": \"123\", \"confirmPassword\": \"456\"}";
        mockMvc.perform(post("/public/auth/signup").contentType(MediaType.APPLICATION_JSON).content(mismatchJson))
                .andExpect(status().isBadRequest());

        // Duplicate Email -> 409
        createDefaultUser();
        String dupJson = "{\"email\": \"user@gmail.com\", \"password\": \"123456\", \"confirmPassword\": \"123456\"}";
        mockMvc.perform(post("/public/auth/signup").contentType(MediaType.APPLICATION_JSON).content(dupJson))
                .andExpect(status().isConflict());
    }
}