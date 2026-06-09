package com.fox.taskmanager.initializer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fox.taskmanager.config.DefaultUserProperties;
import com.fox.taskmanager.model.UserProfile;
import com.fox.taskmanager.repository.UserProfileRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

class DefaultUserInitializerTest {

    @Test
    void runCreatesDefaultUserWithEncodedPassword() {
        DefaultUserProperties properties = new DefaultUserProperties();
        properties.setEnabled(true);
        properties.setLogin(" User ");
        properties.setPassword("jdbcDefault");
        properties.setDisplayName("user");
        properties.setUpdatePassword(true);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        UserProfileRepository userProfileRepository = mock(UserProfileRepository.class);

        when(userProfileRepository.findByLogin("user")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("jdbcDefault")).thenReturn("encoded-password");

        new DefaultUserInitializer(properties, passwordEncoder, userProfileRepository).run();

        ArgumentCaptor<UserProfile> userProfileCaptor = ArgumentCaptor.forClass(UserProfile.class);

        verify(userProfileRepository).save(userProfileCaptor.capture());
        verify(passwordEncoder).encode("jdbcDefault");
        assertThat(userProfileCaptor.getValue().getLogin()).isEqualTo("user");
        assertThat(userProfileCaptor.getValue().getPasswordHash()).isEqualTo("encoded-password");
    }

    @Test
    void runDoesNothingWhenDefaultUserIsDisabled() {
        DefaultUserProperties properties = new DefaultUserProperties();
        properties.setEnabled(false);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        UserProfileRepository userProfileRepository = mock(UserProfileRepository.class);

        new DefaultUserInitializer(properties, passwordEncoder, userProfileRepository).run();

        verify(userProfileRepository, org.mockito.Mockito.never()).save(any());
    }
}
