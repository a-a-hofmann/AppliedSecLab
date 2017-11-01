package ch.ethz.asl.ca;

//@Configuration
//public class SecurityConfig extends WebSecurityConfigurerAdapter {
//
//    private static final Logger logger = Logger.getLogger(SecurityConfig.class);
//
//    private final AuthenticationUserDetailsService userDetailsService;
//
//    public SecurityConfig(AuthenticationUserDetailsService authenticationUserDetailsService) {
//        this.userDetailsService = authenticationUserDetailsService;
//        logger.info("asdfasdfasdfasdfasdf");
//    }
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth)
//            throws Exception {
//        auth.authenticationProvider(authenticationProvider());
//    }
//
//    @Override
//    public void configure(HttpSecurity http) throws Exception {
//        http
//                .csrf().disable()
//                .authorizeRequests().anyRequest().authenticated()
//                .and()
//                .formLogin();
//    }
//
//    @Bean
//    public DaoAuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider
//                = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(userDetailsService);
//        authProvider.setPasswordEncoder(sha1());
//        return authProvider;
//    }
//
//    @Bean
//    public PasswordEncoder sha1() {
//        return new ShaPasswordEncoder();
//    }
//}
