package flab.gumipayments.application.apikey;

import flab.gumipayments.application.NotFoundSystemException;
import flab.gumipayments.domain.apikey.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static flab.gumipayments.domain.apikey.ReIssueFactor.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiKeyReIssueRequesterApplicationTest {

    @InjectMocks
    private ApiKeyReIssueRequesterApplication sut;

    @Mock
    private ApiKeyRepository apiKeyRepository;

    @Mock
    private ApiKeyCreatorRequesterApplication apiKeyCreatorRequesterApplication;

    private static ApiKeyReIssuePolicy alwaysTrue = ApiKeyReIssuePolicy.of(command -> true);
    private static ApiKeyReIssuePolicy alwaysFalse = ApiKeyReIssuePolicy.of(command -> false);

    private ApiKeyResponse.ApiKeyResponseBuilder apiKeyResponseBuilder;
    private ReIssueFactorBuilder apiKeyReIssueCommandBuilder;

    @BeforeEach
    void setup() {
        apiKeyResponseBuilder = ApiKeyResponse.builder();
        apiKeyReIssueCommandBuilder = ReIssueFactor.builder();
    }

    @Test
    @DisplayName("예외: 재발급 조건을 만족하지 못하면 API 키 재발급에 실패한다.")
    void reIssueApiKeyFailByCondition() {
        sut.setReIssuePolicy(alwaysFalse);

        assertThatThrownBy(() -> sut.reIssueApiKey(apiKeyReIssueCommandBuilder.build()))
                .isInstanceOf(ApiKeyIssueException.class)
                .hasMessage("api 키 재발급 조건이 올바르지 않습니다.");
    }

    @Test
    @DisplayName("예외: 이전에 발급받은 API키가 존재하지 않으면 API 키 재발급에 실패한다.")
    void reIssueApiKeyFail02ByApiKeyExist() {
        when(apiKeyRepository.findByAccountIdAndType(any(),any())).thenReturn(Optional.empty());
        sut.setReIssuePolicy(alwaysTrue);


        assertThatThrownBy(() -> sut.reIssueApiKey(apiKeyReIssueCommandBuilder.build()))
                .isInstanceOf(NotFoundSystemException.class)
                .hasMessage("api키가 존재하지 않습니다.");
    }

    @Test
    @DisplayName("성공: API 키 재발급을 성공한다.")
    void issueApiKey() {
        when(apiKeyCreatorRequesterApplication.create(any())).thenReturn(apiKeyResponseBuilder.build());
        when(apiKeyRepository.findByAccountIdAndType(any(),any())).thenReturn(Optional.ofNullable(ApiKey.builder().build()));
        doNothing().when(apiKeyRepository).delete(any());
        sut.setReIssuePolicy(alwaysTrue);

        sut.reIssueApiKey(apiKeyReIssueCommandBuilder.build());

        verify(apiKeyRepository).save(any());
    }
}