package com.project.judging.Config.Security;

import com.project.judging.Config.Jwt.JwtAuthenticationEntryPoint;
import com.project.judging.Config.Jwt.JwtRequestFilter;
import com.project.judging.Config.UserDetails.UserDetailsServiceImpl;
import com.project.judging.Constant.RoleValiddator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${frontend.domain.url}")
    private String URL;

    private static final String[] ADMIN_PATH = {
            //AdminController
            "/api/admin/projects/{projectId}",
            "/api/admin/highestRound1",
            "/api/admin/highestRound2",

            //CriteriaController
            "/api/criteria/showAllCriteria",
            "/api/criteria/findCriteriaById",
            "/api/criteria/editCriteria",
            "/api/criteria/createCriteria",
            "/api/criteria/deleteCriteria",

            //JudgeController
            "/api/users/showAll",
            "/api/users/find",
            "/api/users/edit",
            "/api/users/create",
            "/api/users/viewAccount",
            "/api/users/deleteAll",
            "/api/users/resetAll",
            "/api/users/assignProject",
            "/api/users/unassignProject",
            "/api/users/showAssignedProject",
            "/api/users/toRound2",
            "api/users/generate",
            "api/users/changePassword/{id}",

            //MarkingController
            "/api/marking/markRound1",
            "/api/marking/markRound2",
            "/api/marking/{projectId}/totalMark",
            "/api/marking/totalMarkByJudge",
            "/api/marking/assigned/{judgeId}",
            "/api/marking/markingRound1Status",
            "/api/marking/markingRound2Status",

            //ProjectController
            "/api/projects/showAll",
            "/api/projects/search",
            "/api/projects/create",
            "/api/projects/createProjects",
            "/api/projects/update",
            "/api/projects/{id}",
            "/api/projects/round2List/{semesterId}",
            "/api/projects/top5ProjectsRound1",
            "/api/projects/top5ProjectsRound2",
            "/api/projects/judge-marks",
            "/api/projects/judge-marks-round2",
            "/api/projects//import",


            //StudentController
            "/api/students/showAll",
            "/api/students/showByProjectId",
            "/api/students/search",
            "/api/students/create",
            "/api/students/assign"
    };

    private static final String[] JUDGE_PATH = {
            //CriteriaController
            "/api/criteria/showAllCriteria",
            "/api/criteria/findCriteriaById",
            "/api/criteria/deleteCriteria",

            //JudgeController
            "/api/users/showAll",
            "/api/users/showAssignedProject",

            //MarkingController
            "/api/marking/markRound1",
            "/api/marking/markRound2",
            "/api/marking/{projectId}/totalMark",
            "/api/marking/totalMarkByJudge",
            "/api/marking/assigned/{judgeId}",

            //ProjectController
            "/api/projects/showAll",
            "/api/projects/search",
            "/api/projects/top5ProjectsRound1",
            "/api/projects/top5ProjectsRound2",
            "/api/projects/round2List",

    };

    private static final String[] SWAGGER_WHITELIST = {
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "/webjars/**",
    };

    private static final String[] NON_REQUIRED_AUTHENTICATED = {
            // Authentication
            "/api/auth/**",
            "/api/admin/config/**",
            "/judging-websocket",
            "/notification",


            //Project
            "/api/projects/**",
            "/api/projects/round2List",

            //Judge
            "/api/users/**",

            //Criteria
            "/api/criteria/showAllCriteria",
            "/api/criteria/editCriteria",
            "/api/criteria/deleteCriteria",

            //Marking
            "/api/marking/markRound1",
            "/api/marking/markRound2",
            "/api/marking/totalMarkByJudge",
            "/api/marking/assigned/{judgeId}",
    };

    private final UserDetailsServiceImpl userDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    @Bean
    public JwtRequestFilter jwtRequestFilter() {
        return new JwtRequestFilter();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }


//    @Bean
//    public static UrlBasedCorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(List.of("*"));
//        configuration.setAllowedMethods(List.of("*"));
//        configuration.setAllowedHeaders(List.of("*"));
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.csrf(AbstractHttpConfigurer::disable)
//                .cors(c -> c.configurationSource(this.corsConfigurationSource()))
//                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
//                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .authorizeHttpRequests(auth ->
//                        auth.requestMatchers(NON_REQUIRED_AUTHENTICATED).permitAll()
//                                .requestMatchers(SWAGGER_WHITELIST).permitAll()
//                                .requestMatchers("/ws/**").permitAll()
//                                .requestMatchers(ADMIN_PATH).hasRole(RoleValiddator.roleString("admin"))
//                                .requestMatchers(JUDGE_PATH).hasRole(RoleValiddator.roleString("judge"))
//                                .anyRequest().permitAll()
//                );
//        http.addFilterBefore(jwtRequestFilter(), UsernamePasswordAuthenticationFilter.class);
//        return http.build();
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(c -> c.configurationSource(corsConfigurationSource()))
                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(NON_REQUIRED_AUTHENTICATED).permitAll()
                                .requestMatchers(SWAGGER_WHITELIST).permitAll()
                                .requestMatchers("/ws/**").permitAll()
                                .requestMatchers(ADMIN_PATH).hasRole(RoleValiddator.roleString("admin"))
                                .requestMatchers(JUDGE_PATH).hasRole(RoleValiddator.roleString("judge"))
                                .anyRequest().permitAll()
                );
        http.addFilterBefore(jwtRequestFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedOrigins(List.of(URL)); // Chỉ định rõ ràng origin từ frontend
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

@Bean
WebMvcConfigurer corsConfig() {
    return new WebMvcConfigurer() {
        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/api/ws/**")
                    .allowedMethods("GET", "POST", "PUT", "DELETE")
                    .allowedHeaders("*")
                    .allowedOrigins(URL)
                    .allowCredentials(true); // Allow credentials
        }
    };
}
}