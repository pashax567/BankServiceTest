package ru.aston.bankservicetest.api;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.aston.bankservicetest.model.dto.TransactionHistoryDto;
import ru.aston.bankservicetest.service.TransactionHistoryService;

import java.util.List;

@Tag(name = "Transaction History", description = "Transaction History API")
@RestController
@RequestMapping("/api/v1/history")
@RequiredArgsConstructor
public class TransactionHistoryController {

    private final TransactionHistoryService transactionHistoryService;

    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema(implementation = TransactionHistoryDto.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema())})
    })
    @GetMapping("/{accNumber}")
    public ResponseEntity<?> getHistoryByAccountNumber(@PathVariable Long accNumber) {
        final List<TransactionHistoryDto> history = transactionHistoryService
                .getAllTransactionHistoryForAccount(accNumber);

        if (CollectionUtils.isEmpty(history))
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(history);
    }
}
