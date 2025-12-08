package com.tlu.hrm.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.tlu.hrm.entities.User;
import com.tlu.hrm.enums.UserStatus;
import com.tlu.hrm.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;


	public CustomUserDetailsService(UserRepository userRepository) {
		super();
		this.userRepository = userRepository;
	}

	@Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + username));

        if (user.getStatus() == UserStatus.INACTIVE)
            throw new UsernameNotFoundException("User inactive");

        if (user.getStatus() == UserStatus.LOCKED)
            throw new UsernameNotFoundException("User locked");

        return new CustomUserDetails(user);
    }
}
