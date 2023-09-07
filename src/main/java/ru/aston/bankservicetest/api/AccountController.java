package ru.aston.bankservicetest.api;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.aston.bankservicetest.exception.AccountNotFoundException;
import ru.aston.bankservicetest.exception.BankServiceException;
import ru.aston.bankservicetest.model.dto.AccountDto;
import ru.aston.bankservicetest.model.dto.BankOperationRequest;
import ru.aston.bankservicetest.model.dto.CreateAccountRequest;
import ru.aston.bankservicetest.service.AccountService;

@Tag(name = "Account", description = "Account API")
@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @ApiResponses({
            @ApiResponse(responseCode = "201", content = {@Content(schema = @Schema(implementation = AccountDto.class))}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema(implementation = String.class))})
    })
    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody CreateAccountRequest request) {
        try {
            return new ResponseEntity<>(accountService.createAccount(request.beneficiaryName(), request.pinCode()), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(implementation = String.class))})
    })
    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(@RequestBody BankOperationRequest request) {
        try {
            accountService.depositAndSaveHistory(request.accNumberTo(), request.amount());
            return ResponseEntity.ok().build();
        } catch (AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (TransactionException e) {
            return ResponseEntity.internalServerError().body("Transaction timeout expired or other problems with transaction");
        } catch (BankServiceException | RuntimeException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(implementation = String.class))})
    })
    @PostMapping("/withDraw")
    public ResponseEntity<?> withdraw(@RequestBody BankOperationRequest request) {
        try {
            accountService.withdrawAndSaveHistory(request.accNumberFrom(), request.amount(), request.pinCode());
            return ResponseEntity.ok().build();
        } catch (AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (TransactionException e) {
            return ResponseEntity.internalServerError().body("Transaction timeout expired or other problems with transaction");
        } catch (BankServiceException | RuntimeException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "500", content = {@Content(schema = @Schema(implementation = String.class))}),
            @ApiResponse(responseCode = "404", content = {@Content(schema = @Schema(implementation = String.class))})
    })
    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@RequestBody BankOperationRequest request) {
        try {
            accountService.transferAndSaveHistory(request.accNumberFrom(), request.accNumberTo(), request.amount(), request.pinCode());
            return ResponseEntity.ok().build();
        } catch (AccountNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (TransactionException e) {
            return ResponseEntity.internalServerError().body("Transaction timeout expired or other problems with transaction");
        } catch (BankServiceException | RuntimeException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}
