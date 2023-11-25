package flab.gumipayments.application.apikey.condition;

import flab.gumipayments.application.apikey.ApiKeyIssueCommand;
import flab.gumipayments.application.apikey.condition.specification.ApiKeyIssueCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static flab.gumipayments.application.apikey.ApiKeyIssueCommand.*;
import static flab.gumipayments.application.apikey.condition.ApiKeyIssueConditions.*;
import static flab.gumipayments.application.apikey.condition.specification.CompositeApiKeyIssueCondition.*;
import static flab.gumipayments.domain.apikey.ApiKeyType.*;
import static org.assertj.core.api.Assertions.*;

class ApiKeyIssueConditionsTest {

    private ApiKeyIssueCondition sut =
            or(
                    and(IS_TEST_API_KEY, EXIST_ACCOUNT, not(EXIST_API_KEY)),
                    and(IS_PROD_API_KEY, EXIST_ACCOUNT, IS_CONTRACT_COMPLETE, not(EXIST_API_KEY))
            );

    private ApiKeyIssueCommandBuilder apiKeyIssueCommandBuilder;

    @BeforeEach
    void setup() {
        apiKeyIssueCommandBuilder = ApiKeyIssueCommand.builder();
    }

    @Test
    @DisplayName("조건: 테스트 api키 조건을 만족한다.")
    void testIssueCondition() {
        ApiKeyIssueCommand issueCommand = apiKeyIssueCommandBuilder.apiKeyType(TEST)
                .accountExist(true)
                .apiKeyExist(false).build();

        boolean result = sut.isSatisfiedBy(issueCommand);

        assertThat(result).isTrue();
    }


    @Test
    @DisplayName("조건: 실서비스용 api키 조건을 만족한다.")
    void prodIssueCondition() {
        ApiKeyIssueCommand issueCommand = apiKeyIssueCommandBuilder.apiKeyType(PRODUCTION)
                .accountExist(true)
                .apiKeyExist(false)
                .contractCompleteExist(true).build();

        boolean result = sut.isSatisfiedBy(issueCommand);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("조건: 실서비스용 또는 테스트용 api키 조건을 만족하지 않는다.")
    void IssueCondition() {
        ApiKeyIssueCommand issueCommand = apiKeyIssueCommandBuilder.build();

        boolean result = sut.isSatisfiedBy(issueCommand);

        assertThat(result).isFalse();
    }
}