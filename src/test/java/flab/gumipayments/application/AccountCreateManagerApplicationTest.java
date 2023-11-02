package flab.gumipayments.application;

import flab.gumipayments.domain.KeyFactory;
import flab.gumipayments.domain.account.AccountCreateCommand;
import flab.gumipayments.domain.account.AccountFactory;
import flab.gumipayments.domain.account.AccountRepository;
import flab.gumipayments.domain.signup.Signup;
import flab.gumipayments.domain.signup.SignupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static flab.gumipayments.domain.signup.SignupStatus.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountCreateManagerApplicationTest {

    @Mock
    private AccountFactory accountFactory;
    @InjectMocks
    private AccountCreateManagerApplication accountCreateManagerApplication;
    @Mock
    private SignupRepository signupRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AccountCreateCommand accountCreateCommand;

    private Signup signup;

    @BeforeEach
    void setUp() {
        String email = "love4@naver.com";
        String signupKey= KeyFactory.generateSignupKey();
        signup = Signup.builder().signupKey(signupKey)
                .email(email)
                .expireDate(LocalDateTime.now().plusDays(1)).build();
    }

    @Test
    @DisplayName("계정 생성 성공")
    void accountCreate() {
        signup.accept();
        when(signupRepository.findById(any())).thenReturn(Optional.of(signup));

        accountCreateManagerApplication.create(accountCreateCommand, signup.getId());

        verify(signupRepository).findById(any());
        verify(accountFactory).create(any(), any());
        verify(accountRepository).save(any());
        assertThat(signup.getStatus()).isEqualTo(ACCOUNT_CREATED);
    }

    @Test
    @DisplayName("계정 생성 실패(이미 계정을 생성한 가입요청인 경우)")
    void accountCreatedIsTrue() {
        signup.accept();
        signup.accountCreated();
        when(signupRepository.findById(any())).thenReturn(Optional.of(signup));

        assertThatThrownBy(() -> accountCreateManagerApplication.create(accountCreateCommand, signup.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("올바르지 않은 가입 요청 status 변경입니다.");
    }
}