package flab.gumipayments.domain.apikey.condition.issue;

import flab.gumipayments.domain.apikey.IssueFactor;
import flab.gumipayments.support.specification.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static flab.gumipayments.domain.apikey.IssueFactor.*;
import static flab.gumipayments.domain.apikey.condition.issue.ApiKeyIssueConditions.*;
import static flab.gumipayments.domain.apikey.ApiKeyType.*;
import static org.assertj.core.api.Assertions.assertThat;

class ApiKeyTypeConditionTest {
    private IssueFactorBuilder issueFactorBuilder;
    private Condition<IssueFactor> sut;

    @BeforeEach
    void setup() {
        issueFactorBuilder = builder();
    }

    @Test
    @DisplayName("조건: API 키 타입 = TEST 이면 발급 조건을 만족한다.")
    void apiKeyTest01() {
        IssueFactor issueFactor = issueFactorBuilder.apiKeyType(TEST).build();
        sut = IS_TEST_API_KEY;

        boolean result = sut.isSatisfiedBy(issueFactor);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("조건: API 키 타입 = TEST 가 아니면 발급 조건을 만족하지 않는다.")
    void apiKeyTest02() {
        IssueFactor issueFactor = issueFactorBuilder.apiKeyType(PROD).build();
        sut = IS_TEST_API_KEY;

        boolean result = sut.isSatisfiedBy(issueFactor);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("조건: API 키 타입 = PROD 이면 발급 조건을 만족한다.")
    void apiKeyPROD01() {
        IssueFactor issueFactor = issueFactorBuilder.apiKeyType(PROD).build();
        sut = IS_PROD_API_KEY;

        boolean result = sut.isSatisfiedBy(issueFactor);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("조건: API 키 타입 = PROD 가 아니면 발급 조건을 만족하지 않는다.")
    void apiKeyPROD02() {
        IssueFactor issueFactor = issueFactorBuilder.apiKeyType(TEST).build();
        sut = IS_PROD_API_KEY;

        boolean result = sut.isSatisfiedBy(issueFactor);

        assertThat(result).isFalse();
    }


}