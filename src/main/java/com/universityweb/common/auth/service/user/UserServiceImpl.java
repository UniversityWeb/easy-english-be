package com.universityweb.common.auth.service.user;

import com.universityweb.common.auth.dto.UserDTO;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.exception.EmailNotFoundException;
import com.universityweb.common.auth.mapper.UserMapper;
import com.universityweb.common.auth.repos.UserRepos;
import com.universityweb.common.auth.request.GetUserFilterReq;
import com.universityweb.common.auth.request.UpdateProfileRequest;
import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl
        extends BaseServiceImpl<User, UserDTO, String, UserRepos, UserMapper>
        implements UserService {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepos repository, UserMapper mapper, PasswordEncoder passwordEncoder) {
        super(repository, mapper);
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        String msg = String.format("Could not find any user with username=%s", username);
        User user = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(msg));
        return user;
    }

    @Override
    public UserDTO getUserByUsername(String username) throws UsernameNotFoundException {
        User user = loadUserByUsername(username);
        return mapper.toDTO(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return repository.existsByUsername(username);
    }

    @Override
    public UserDTO update(String username, UserDTO dto) {
        return null;
    }

    @Override
    protected void throwNotFoundException(String username) {
        String msg = String.format("Could not find any user with username=%s", username);
        throw new UsernameNotFoundException(msg);
    }

    @Override
    public List<User> saveAll(List<User> users) {
        return repository.saveAll(users);
    }

    @Override
    public UserDTO update(UpdateProfileRequest updateProfileRequest) {
        String usernameToUpdate = updateProfileRequest.getUsername();

        User user = loadUserByUsername(usernameToUpdate);

        user.setFullName(updateProfileRequest.getFullName());
        user.setEmail(updateProfileRequest.getEmail());
        user.setPhoneNumber(updateProfileRequest.getPhoneNumber());
        user.setBio(updateProfileRequest.getBio());
        user.setGender(updateProfileRequest.getGender());
        user.setDob(updateProfileRequest.getDob());

        User savedUser = repository.save(user);
        return mapper.toDTO(savedUser);
    }

    @Override
    public String getEmailByUsername(String username) {
        return repository.getEmailByUsername(username);
    }

    @Override
    public User getUserByEmail(String email) {
        return repository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException("Could not find your email: " + email));
    }

    @Override
    public Page<UserDTO> getUsersWithoutAdmin(GetUserFilterReq filterReq) {
        List<User.ERole> excludedRoles = new ArrayList<>();
        excludedRoles.add(User.ERole.ADMIN);

        Sort sort = Sort.by(Sort.Order.asc("createdAt"));
        PageRequest pageable = PageRequest.of(filterReq.getPage(), filterReq.getSize(), sort);

        Page<User> users = repository.findAllNonAdminUsersWithFilters(
                excludedRoles,
                filterReq.getStatus(),
                filterReq.getStartDate(),
                filterReq.getEndDate(),
                filterReq.getFullName(),
                filterReq.getEmail(),
                pageable
        );

        return mapper.mapPageToPageDTO(users);
    }
}
