package dev.plant.backend.domain.transaction.service;

import dev.plant.backend.domain.transaction.domain.Transaction;
import dev.plant.backend.domain.transaction.dto.*;
import dev.plant.backend.domain.transaction.exception.NotFoundTransaction;
import dev.plant.backend.domain.transaction.exception.UnauthorizedTransactionAccessException;
import dev.plant.backend.domain.transaction.model.Category;
import dev.plant.backend.domain.transaction.model.Type;
import dev.plant.backend.domain.transaction.repository.TransactionRepository;
import dev.plant.backend.domain.user.domain.User;
import dev.plant.backend.domain.user.exception.NotFoundUserException;
import dev.plant.backend.domain.user.repository.UserRepository;
import dev.plant.backend.global.ErrorCode;
import dev.plant.backend.global.exception.InvalidArgumentException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public MonthlyRecordResponse getMonthlyRecords(Long userId, YearMonth yearMonth) {
        List<Transaction> transactions = transactionRepository.findByMonth(userId, yearMonth);

        int totalBalance = transactions.stream()
                .mapToInt(t -> t.getType() == Type.INCOME ? t.getAmount() : -t.getAmount())
                .sum();

        List<RecordDetailDto> recordDtos = transactions.stream()
                .map(t -> RecordDetailDto.builder()
                        .id(t.getId())
                        .type(t.getType().name())
                        .amount(t.getAmount())
                        .category(t.getCategory().name())
                        .memo(t.getMemo())
                        .date(t.getDate())
                        .build())
                .toList();

        return MonthlyRecordResponse.builder()
                .yearMonth(
                        yearMonth.atDay(1)
                                .atStartOfDay()
                                .toEpochSecond(ZoneOffset.UTC)
                )
                .totalBalance(totalBalance)
                .monthlyRecords(recordDtos)
                .build();
    }

    @Transactional(readOnly = true)
    public DailyRecordResponse getDailyRecords(Long userId, LocalDate date) {
        List<Transaction> transactions = transactionRepository.findByDate(userId, date);

        int totalBalance = transactions.stream()
                .mapToInt(t -> t.getType() == Type.INCOME ? t.getAmount() : -t.getAmount())
                .sum();

        List<RecordDetailDto> recordDtos = transactions.stream()
                .map(t -> RecordDetailDto.builder()
                        .id(t.getId())
                        .type(t.getType().name())
                        .amount(t.getAmount())
                        .category(t.getCategory().name())
                        .memo(t.getMemo())
                        .date(t.getDate())
                        .build())
                .toList();

        return DailyRecordResponse.builder()
                .date(date.atStartOfDay().toEpochSecond(ZoneOffset.UTC)) // 유닉스 타임스탬프
                .totalBalance(totalBalance)
                .dailyRecords(recordDtos)
                .build();
    }
    @Transactional
    public void addRecord(HttpSession session, AddRecordRequest addRecordRequest) {

        Long kakoId = getKakaoId(session);
        User user = getUser(kakoId);
        //Unix timestamp
        LocalDateTime dateTime = Instant.ofEpochSecond(addRecordRequest.getDate())
                .atZone(ZoneId.of("Asia/Seoul"))
                .toLocalDateTime();

        if(addRecordRequest != null) {
            try {
                Transaction transaction = Transaction.builder()
                        .type(Type.valueOf(addRecordRequest.getType().toUpperCase()))
                        .category(Category.valueOf(addRecordRequest.getCategory().toUpperCase()))
                        .amount(addRecordRequest.getAmount())
                        .title(addRecordRequest.getTitle())
                        .memo(addRecordRequest.getMemo())
                        .date(addRecordRequest.getDate())
                        .build();
                transaction.setUser(user);
                transactionRepository.save(transaction);
            }catch (InvalidArgumentException e) {
                throw new InvalidArgumentException(ErrorCode.INVALID_INPUT);
            }
        }
    }
    @Transactional
    public UpdateRecordResponse updateRecord(Long transactionId, HttpSession session, UpdateRecordRequest updateRecordRequest) {

        Long kakoId = getKakaoId(session);
        User user = getUser(kakoId);

        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NotFoundTransaction(ErrorCode.NOT_FOUND_TRANSACTION));
        //현재 로그인한 사용자의 거래내역인지 확인
        if (!transaction.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedTransactionAccessException(ErrorCode.UNAUTHORIZED_TRANSACTION_EDIT);
        }
        transaction.updateTransaction(updateRecordRequest);
        transactionRepository.save(transaction);

        return UpdateRecordResponse.of(transaction);
    }

    @Transactional
    public void deleteRecord(Long transactionId, HttpSession session){

        Long kakoId = getKakaoId(session);
        User user = getUser(kakoId);
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new NotFoundTransaction(ErrorCode.NOT_FOUND_TRANSACTION));

        if(!transaction.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedTransactionAccessException(ErrorCode.UNAUTHORIZED_TRANSACTION_EDIT);
        }
        transactionRepository.delete(transaction);
    }

    //Kakao ID Get
    public long getKakaoId(HttpSession session){
        Long kakaoId = (Long) session.getAttribute("kakaoId");
        return kakaoId;
    }
    //user Get
    public User getUser(Long kakaoId){
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new NotFoundUserException(ErrorCode.NOT_FOUND_USER));
        return user;
    }
}
