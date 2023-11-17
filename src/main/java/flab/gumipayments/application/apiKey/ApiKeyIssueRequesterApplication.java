package flab.gumipayments.application.apiKey;

import flab.gumipayments.domain.apiKey.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.function.Predicate;


@Service
@RequiredArgsConstructor
public class ApiKeyIssueRequesterApplication {

    private final ApiKeyPairFactory apiKeyPairFactory;
    private final ApiKeyRepository apiKeyRepository;
    private final ApiKeyFactory apiKeyFactory;

    private final ApiKeyIssueConditionService issueConditionService;

    //api 키 발급
    public ApiKeyPair issueApiKey(ApiKeyIssueCommand issueCommand){

        // 발급 조건 확인
        if(issueConditionService.getIssueCondition(issueCommand.getKeyType()).isSatisfiedBy(convert(issueCommand))){
            throw new ApiKeyIssueException("api 키 발급을 실패했습니다.");
        }

        // api 키 쌍 생성
        ApiKeyPair apiKeyPair = apiKeyPairFactory.generateApiKeyPair();

        // api 키 생성
        ApiKey apiKey = apiKeyFactory.create(convertApiKeyCreateCommand(issueCommand, apiKeyPair));

        //api 키 저장
        apiKeyRepository.save(apiKey);

        return apiKeyPair;
    }

    //api 키 재발급
    public ApiKeyPair reIssueApiKey(ApiKeyReIssueCommand reIssueCommand){
        //재발급 조건
        if(issueConditionService.getReIssueCondition(reIssueCommand.getKeyType()).isSatisfiedBy(convert(reIssueCommand))){
            throw new ApiKeyIssueException("api 키 발급을 실패했습니다.");
        }

        // 기존 api 조회
        ApiKey apiKey = apiKeyRepository.findByAccountIdAndType(reIssueCommand.getAccountId(), reIssueCommand.getKeyType())
                .orElseThrow(() -> new NoSuchElementException("api키가 존재하지 않습니다."));

        //기존 api 키 삭제
        apiKeyRepository.delete(apiKey);

        // api 키 쌍 생성
        ApiKeyPair apiKeyPair = apiKeyPairFactory.generateApiKeyPair();

        // api 키 생성
        ApiKey reIssueapiKey = apiKeyFactory.create(convertApiKeyCreateCommand(reIssueCommand, apiKeyPair));

        //api 키 저장
        apiKeyRepository.save(reIssueapiKey);

        return apiKeyPair;
    }

    private ApiKeyCreateCommand convertApiKeyCreateCommand(ApiKeyIssueCommand issueCommand,ApiKeyPair keyPair) {
        return ApiKeyCreateCommand.builder()
                .apiKeyType(issueCommand.getKeyType())
                .secretKey(keyPair.getSecretKey())
                .expireDate(issueCommand.getExpireDate())
                .accountId(issueCommand.getAccountId())
                .build();
    }

    private ApiKeyCreateCommand convertApiKeyCreateCommand(ApiKeyReIssueCommand issueCommand,ApiKeyPair keyPair) {
        return ApiKeyCreateCommand.builder()
                .apiKeyType(issueCommand.getKeyType())
                .secretKey(keyPair.getSecretKey())
                .expireDate(issueCommand.getExpireDate())
                .accountId(issueCommand.getAccountId())
                .build();
    }

    private ApiKeyIssueCandidate convert(ApiKeyIssueCommand issueCommand) {
        return ApiKeyIssueCandidate.builder()
                .accountId(issueCommand.getAccountId())
                .build();
    }

    private ApiKeyIssueCandidate convert(ApiKeyReIssueCommand reIssueCommand) {
        return ApiKeyIssueCandidate.builder()
                .accountId(reIssueCommand.getAccountId())
                .build();
    }
}