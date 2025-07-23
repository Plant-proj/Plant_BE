package dev.plant.backend.domain.transaction.controller;

import dev.plant.backend.domain.transaction.dto.*;
import dev.plant.backend.domain.transaction.service.TransactionService;
import dev.plant.backend.domain.user.domain.User;
import dev.plant.backend.domain.user.repository.UserRepository;
import dev.plant.backend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/records", produces = "application/json; charset=UTF8")
public class TransactionController {

    private final TransactionService transactionService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<MonthlyRecordResponse>> getMonthlyRecords(
            @RequestParam(required = false) String date,
            HttpSession session
    ) {
        Long kakaoId = (Long) session.getAttribute("kakaoId");
        if (kakaoId == null) {
            return ResponseEntity.status(401).body(ApiResponse.of(401, "로그인이 필요합니다.", null));
        }

        User user = userRepository.findByKakaoId(kakaoId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(ApiResponse.of(404, "사용자를 찾을 수 없습니다.", null));
        }

        Long userId = user.getId();

        YearMonth targetMonth;
        try {
            targetMonth = (date == null || date.isEmpty())
                    ? YearMonth.now()
                    : YearMonth.parse(date, DateTimeFormatter.ofPattern("yyyy-MM"));
        } catch (Exception e) {
            targetMonth = YearMonth.now();
        }

        MonthlyRecordResponse response = transactionService.getMonthlyRecords(userId, targetMonth);
        return ResponseEntity.ok(ApiResponse.of(200, "수입/지출 조회가 완료되었습니다", response));
    }

    @GetMapping(params = "date")
    public ResponseEntity<ApiResponse<?>> getRecordsByDate(
            @RequestParam String date,
            HttpSession session
    ) {
        Long kakaoId = (Long) session.getAttribute("kakaoId");
        if (kakaoId == null) {
            return ResponseEntity.status(401).body(ApiResponse.of(401, "로그인이 필요합니다.", null));
        }

        User user = userRepository.findByKakaoId(kakaoId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(404).body(ApiResponse.of(404, "사용자를 찾을 수 없습니다.", null));
        }

        Long userId = user.getId();

        try {
            if (date.matches("^\\d{4}-\\d{2}$")) {
                // yyyy-MM → 월별 조회
                YearMonth yearMonth = YearMonth.parse(date, DateTimeFormatter.ofPattern("yyyy-MM"));
                MonthlyRecordResponse response = transactionService.getMonthlyRecords(userId, yearMonth);
                return ResponseEntity.ok(ApiResponse.of(200, "월별 수입/지출 조회가 완료되었습니다", response));
            } else if (date.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                // yyyy-MM-dd → 일별 조회
                LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                DailyRecordResponse response = transactionService.getDailyRecords(userId, localDate);
                return ResponseEntity.ok(ApiResponse.of(200, "일별 수입/지출 조회가 완료되었습니다", response));
            } else {
                return ResponseEntity.badRequest().body(ApiResponse.of(400, "날짜 형식이 잘못되었습니다 (yyyy-MM 또는 yyyy-MM-dd)", null));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(ApiResponse.of(500, "서버 에러가 발생했습니다", null));
        }
    }

    @Operation(summary="거래 내역 입력",description = "사용자로부터 수입/지출 내역을 입력받을 수 있음")
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> addRecord(HttpSession session, @RequestBody AddRecordRequest addRecordRequest) {
        transactionService.addRecord(session, addRecordRequest);
        return ResponseEntity.ok(ApiResponse.ok("수입/지출 등록이 완료되었습니다",null));
    }

    @Operation(summary="거래 내역 수정",description = "사용자가 수입/지출 내역을 수정할 수 있습니다")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UpdateRecordResponse>> updateRecord(
            @PathVariable("id") Long updateRecordId,
            @RequestBody UpdateRecordRequest updateRecordRequest,
            HttpSession session)
    {
        UpdateRecordResponse updateRecordResponse = transactionService.updateRecord(updateRecordId, session, updateRecordRequest);
        return ResponseEntity.ok(ApiResponse.ok("수입/지출 수정이 완료되었습니다",updateRecordResponse));
    }

    @Operation(summary = "거래 내역 삭제",description = "사용자가 수입/지출 내역을 삭제할 수 있습니다")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Long>> deleteRecord(
            @PathVariable("id") Long deleteRecordId,
            HttpSession session) {
        transactionService.deleteRecord(deleteRecordId, session);
        return ResponseEntity.ok(ApiResponse.ok("수입/지출 기록이 삭제되었습니다",null));
    }

}
