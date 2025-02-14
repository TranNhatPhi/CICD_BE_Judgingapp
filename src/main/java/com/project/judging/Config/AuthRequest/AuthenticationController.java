package com.project.judging.Config.AuthRequest;

import com.project.judging.Config.Jwt.JwtUtils;
import com.project.judging.Config.UserDetails.UserDetailsImpl;
import com.project.judging.Config.UserDetails.UserDetailsServiceImpl;
import com.project.judging.Repositories.JudgeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import com.project.judging.DTOs.JudgeDTO;
import com.project.judging.Mapper.Impl.JudgeMapper;
import com.project.judging.Entities.Judge;
import com.project.judging.Services.Impl.JudgeServiceImpl;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@Tag(name="Authentication Management", description = "Authentication Management APIs")
public class AuthenticationController {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    private JudgeServiceImpl judgeService;

    @Autowired
    private JudgeMapper judgeMapper;

//    @PostMapping("/login")
//    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
//        try {
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
//            );
//        } catch (BadCredentialsException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password");
//        }
//        final UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
//        final String jwt = jwtUtils.generateToken(userDetails);
//        final String username = userDetails.getUsername();
//        final String role = userDetails.getAuthorities().toString();
//        final UserDetailsImpl userDetailsImpl = userDetails;
//        final Integer userId = userDetailsImpl.getId();
//        final Integer semesterId = ((UserDetailsImpl) userDetails).getSemesterId();
//        log.info("User {} logged in successfully", username);
//        log.info("User {} has role {}", username, role);
//        log.info("User has SemesterId {}", semesterId);
//        return ResponseEntity.ok(new AuthenticationResponse(jwt, userId, username,semesterId, role));
//    }
@PostMapping("/login")
public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
    long startTime = System.currentTimeMillis();

    try {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
        );
    } catch (BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password");
    }

    long authEndTime = System.currentTimeMillis();
    final UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
    final String jwt = jwtUtils.generateToken(userDetails);

    long tokenGenerationEndTime = System.currentTimeMillis();
    final String username = userDetails.getUsername();
    final String role = userDetails.getAuthorities().toString();
    final Integer userId = userDetails.getId();
    final Integer semesterId =  userDetails.getSemesterId();

    log.info("User {} logged in successfully", username);
    log.info("User {} has role {}", username, role);
    log.info("User has SemesterId {}", semesterId);
    log.info("Authentication time: {} ms", (authEndTime - startTime));
    log.info("Token generation time: {} ms", (tokenGenerationEndTime - authEndTime));

    return ResponseEntity.ok(new AuthenticationResponse(jwt, userId, username, semesterId, role));
}


    @PostMapping("/create/{semesterId}")
    @Operation(summary="User Register", description = "Register the user in the system", tags={"post"})
    public ResponseEntity<JudgeDTO> registerUser(@PathVariable Integer semesterId, @RequestBody JudgeDTO judgeDTO, HttpServletResponse httpResponse) {
        Judge newJudge = this.judgeMapper.toEntity(judgeDTO);
        Judge savedJudge = this.judgeService.setInitWithExcelExports(newJudge, semesterId);
        JudgeDTO saveJudgeDTO = this.judgeMapper.toDto(savedJudge);
        return new ResponseEntity<>(saveJudgeDTO, HttpStatus.CREATED);
    }

}
