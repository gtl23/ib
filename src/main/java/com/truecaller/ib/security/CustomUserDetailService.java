package com.truecaller.ib.security;

import com.truecaller.ib.entity.User;
import com.truecaller.ib.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByPhone(phone);
        user.orElseThrow(() -> new UsernameNotFoundException("User Not Found."));

        return user.map(CustomUserDetail::new).get();
    }
}
