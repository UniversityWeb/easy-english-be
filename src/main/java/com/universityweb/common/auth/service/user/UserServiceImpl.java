package com.universityweb.common.auth.service.user;

import com.universityweb.common.AuthUtils;
import com.universityweb.common.Utils;
import com.universityweb.common.auth.dto.UserDTO;
import com.universityweb.common.auth.dto.UserForAdminDTO;
import com.universityweb.common.auth.entity.User;
import com.universityweb.common.auth.exception.EmailNotFoundException;
import com.universityweb.common.auth.exception.UserAlreadyExistsException;
import com.universityweb.common.auth.mapper.UserMapper;
import com.universityweb.common.auth.repos.UserRepos;
import com.universityweb.common.auth.request.GetUserFilterReq;
import com.universityweb.common.auth.request.UpdateProfileRequest;
import com.universityweb.common.infrastructure.service.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl
        extends BaseServiceImpl<User, UserDTO, String, UserRepos, UserMapper>
        implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserRepos repository, UserMapper mapper, PasswordEncoder passwordEncoder, @Qualifier("userMapper") UserMapper userMapper) {
        super(repository, mapper);
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }

    @Override
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        String msg = String.format("Could not find any user with username=%s", username);
        User user = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(msg));
        return user;
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
    public Page<UserForAdminDTO> getUsersWithoutAdmin(GetUserFilterReq filterReq) {
        List<User.ERole> excludedRoles = new ArrayList<>();
        excludedRoles.add(User.ERole.ADMIN);

        Sort sort = Sort.by(Sort.Order.asc("createdAt"));
        PageRequest pageable = PageRequest.of(filterReq.getPage(), filterReq.getSize(), sort);

        try {
            log.info("Fetching users with filter: {}", filterReq);

            User.EStatus status = Utils.safeEnumConversion(User.EStatus.class, filterReq.getStatus());
            User.EGender gender = Utils.safeEnumConversion(User.EGender.class, filterReq.getGender());
            User.ERole role = Utils.safeEnumConversion(User.ERole.class, filterReq.getRole());

            Page<User> users = repository.findAllNonRoleUsersWithFilters(
                    excludedRoles,
                    status,
                    filterReq.getFullName(),
                    filterReq.getEmail(),
                    filterReq.getPhoneNumber(),
                    gender,
                    role,
                    pageable
            );

            log.info("Successfully retrieved {} users", users.getTotalElements());
            return mapper.mapPageToPageForAdminDTO(users);
        } catch (Exception e) {
            log.error("Error occurred while fetching users", e);
            return Page.empty();
        }
    }

    @Override
    @Transactional
    public UserForAdminDTO updateUserForAdmin(String username, UserForAdminDTO req) {
        User user = loadUserByUsername(username);
        mapper.updateEntityFromDTO(req, user);

        String password = req.getPassword();
        if (AuthUtils.isValidPassword(password) == AuthUtils.PASSWORD_VALID) {
            user.setPassword(passwordEncoder.encode(password));
        }

        User savedUser = repository.save(user);
        if (!username.equals(req.getUsername())) {
            updateUsername(username, req.getUsername());
        }
        return mapper.toUserForAdminDTO(savedUser);
    }

    @Override
    public UserForAdminDTO addUserForAdmin(UserForAdminDTO req) {
        String username = req.getUsername();
        if (username == null || username.isEmpty()) {
            throw new BadCredentialsException("Username must not be empty or null");
        }

        boolean isExist = repository.existsByUsername(username);
        if (isExist) {
            throw new UserAlreadyExistsException("User with this username already exists");
        }

        User user = mapper.toEntity(req);
        User saved = repository.save(user);
        return mapper.toUserForAdminDTO(saved);
    }

    @Override
    public void softDelete(String username) {
        User user = loadUserByUsername(username);
        user.setStatus(User.EStatus.DELETED);
        repository.save(user);
    }

    private User updateUsername(String currentUsername, String newUsername) {
        User user = repository.findById(currentUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (repository.existsById(newUsername)) {
            throw new RuntimeException("Username already taken");
        }

        User newUser = User.builder()
                .username(newUsername)
                .password(user.getPassword())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .bio(user.getBio())
                .gender(user.getGender())
                .dob(user.getDob())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .status(user.getStatus())
                .avatarPath(user.getAvatarPath())
                .cart(user.getCart())
                .token(user.getToken())
                .courses(user.getCourses())
                .reviews(user.getReviews())
                .build();

        repository.save(newUser);
        repository.delete(user);

        return newUser;
    }
}
