package academicPlanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AppUserController {

    @Autowired
    private AppUserService appUserService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");

        Map<String, Object> response = new HashMap<>();

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            response.put("status", "error");
            response.put("message", "Email and password are required");
            return ResponseEntity.badRequest().body(response);
        }

        if (appUserService.findByEmail(email).isPresent()) {
            response.put("status", "error");
            response.put("message", "Email already exists");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        AppUser user = new AppUser();
        user.setUsername(email);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole("STUDENT");

        AppUser savedUser = appUserService.registerUser(user);

        response.put("status", "success");
        response.put("message", "User registered successfully");
        response.put("data", savedUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
        String loginValue = Optional.ofNullable(credentials.get("login"))
                .filter(s -> !s.isBlank())
                .orElse(Optional.ofNullable(credentials.get("email"))
                        .filter(s -> !s.isBlank())
                        .orElse(credentials.get("username")));

        String password = credentials.get("password");

        Map<String, Object> response = new HashMap<>();

        if (loginValue == null || loginValue.isBlank() || password == null || password.isBlank()) {
            response.put("status", "error");
            response.put("message", "Login value and password are required");
            return ResponseEntity.badRequest().body(response);
        }

        Optional<AppUser> userOpt = appUserService.findByEmail(loginValue);
        if (userOpt.isEmpty()) {
            userOpt = appUserService.findByUsername(loginValue);
        }

        if (userOpt.isEmpty()) {
            response.put("status", "error");
            response.put("message", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        AppUser user = userOpt.get();

        org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder encoder =
                new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();

        if (!encoder.matches(password, user.getPassword())) {
            response.put("status", "error");
            response.put("message", "Invalid username/email or password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        var userDetails = customUserDetailsService.loadUserByUsername(user.getUsername());
        String token = jwtUtil.generateToken(userDetails);

        response.put("status", "success");
        response.put("message", "Login successful");
        response.put("token", token);
        response.put("data", user);

        return ResponseEntity.ok(response);
    }
}