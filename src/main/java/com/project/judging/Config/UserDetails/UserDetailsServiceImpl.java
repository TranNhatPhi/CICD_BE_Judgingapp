package com.project.judging.Config.UserDetails;

import com.project.judging.Entities.Judge;
import com.project.judging.Repositories.JudgeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.logging.Logger;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private JudgeRepository judgeRepository;

    private final Logger log = Logger.getLogger(UserDetailsServiceImpl.class.getName());

    @Override
    @Transactional
    public UserDetailsImpl loadUserByUsername(String username) throws UsernameNotFoundException {
        long start = System.currentTimeMillis();
        Judge judge = judgeRepository.findByAccount(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        long end = System.currentTimeMillis();
        log.info("Database query time with ms: " +  (end - start));
        return UserDetailsImpl.build(judge);
    }

}