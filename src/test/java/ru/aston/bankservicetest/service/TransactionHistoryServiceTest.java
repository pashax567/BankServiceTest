package ru.aston.bankservicetest.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;
import ru.aston.bankservicetest.component.mapper.TransactionHistoryMapper;
import ru.aston.bankservicetest.model.dto.TransactionHistoryDto;
import ru.aston.bankservicetest.model.entity.Account;
import ru.aston.bankservicetest.model.entity.Beneficiary;
import ru.aston.bankservicetest.model.entity.TransactionHistory;
import ru.aston.bankservicetest.model.type.EOperationType;
import ru.aston.bankservicetest.repo.TransactionHistoryRepo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@SpringBootTest
class TransactionHistoryServiceTest {

    @MockBean
    private TransactionHistoryRepo transactionHistoryRepo;

    @MockBean
    private TransactionHistoryMapper transactionHistoryMapper;

    @Autowired
    private TransactionHistoryService transactionHistoryService;

    private final List<TransactionHistory> TRANSACTION_HISTORY_LIST = new ArrayList<>();
    private final String SIMPLE_PASS = "1234";
    private final String SIMPLE_PASS_HASH = SIMPLE_PASS + "_hash";
    private final Long AMOUNT = 10L;
    private final Long FIRST_ACC_NUMBER = 1L;
    private final Long SECOND_ACC_NUMBER = 2L;
    private final String FIRST_BENEFICIARY_NAME = "first_ben";
    private final String SECOND_BENEFICIARY_NAME = "second_ben";
    private final Beneficiary FIRST_BENEFICIARY = new Beneficiary(FIRST_BENEFICIARY_NAME, null);
    private final Beneficiary SECOND_BENEFICIARY = new Beneficiary(SECOND_BENEFICIARY_NAME, null);
    private final Account FIRST_ACCOUNT = new Account(FIRST_ACC_NUMBER, FIRST_BENEFICIARY, SIMPLE_PASS_HASH, 30L);
    private final Account SECOND_ACCOUNT = new Account(SECOND_ACC_NUMBER, SECOND_BENEFICIARY, SIMPLE_PASS_HASH, 30L);

    private void fillTransactionHistoryList() {
        final TransactionHistory transactionHistory1 = new TransactionHistory(FIRST_ACCOUNT, null, EOperationType.WITHDRAW, 10L);
        final TransactionHistory transactionHistory2 = new TransactionHistory(null, FIRST_ACCOUNT, EOperationType.DEPOSIT, 20L);
        final TransactionHistory transactionHistory3 = new TransactionHistory(FIRST_ACCOUNT, SECOND_ACCOUNT, EOperationType.TRANSFER, 30L);

        TRANSACTION_HISTORY_LIST.addAll(Arrays.asList(transactionHistory1, transactionHistory2, transactionHistory3));
    }

    @BeforeEach
    void setUp() {
        FIRST_ACCOUNT.setId(1L);
        SECOND_ACCOUNT.setId(2L);
        fillTransactionHistoryList();

        Mockito
                .when(transactionHistoryRepo.findByAccountFrom_NumberOrAccountTo_Number(FIRST_ACC_NUMBER, FIRST_ACC_NUMBER))
                .thenReturn(TRANSACTION_HISTORY_LIST
                        .stream()
                        .filter(x -> FIRST_ACCOUNT.equals(x.getAccountTo()) || FIRST_ACCOUNT.equals(x.getAccountFrom()))
                        .collect(Collectors.toList())
                );
        Mockito
                .when(transactionHistoryRepo.findByAccountFrom_NumberOrAccountTo_Number(SECOND_ACC_NUMBER, SECOND_ACC_NUMBER))
                .thenReturn(TRANSACTION_HISTORY_LIST
                        .stream()
                        .filter(x -> SECOND_ACCOUNT.equals(x.getAccountTo()) || SECOND_ACCOUNT.equals(x.getAccountFrom()))
                        .collect(Collectors.toList())
                );
        Mockito
                .when(transactionHistoryMapper.map(any(TransactionHistory.class)))
                .thenReturn(new TransactionHistoryDto(
                        null, null, null, null, null
                        )
                );

        doAnswer((Answer<Void>) invocation -> {
            Object[] arguments = invocation.getArguments();
            if (arguments != null && arguments.length > 0 && arguments[0] != null) {
                TransactionHistory entity = (TransactionHistory) arguments[0];
                TRANSACTION_HISTORY_LIST.add(entity);
            }
            return null;
        }).when(transactionHistoryRepo).save(any(TransactionHistory.class));
    }

    @Test
    @Transactional
    void addTransactionHistory() {

        int oldSize = TRANSACTION_HISTORY_LIST.size();

        transactionHistoryService.addTransactionHistory(null, FIRST_ACCOUNT, EOperationType.DEPOSIT, AMOUNT);
        assertEquals(++oldSize, TRANSACTION_HISTORY_LIST.size());
        transactionHistoryService.addTransactionHistory(FIRST_ACCOUNT, null, EOperationType.WITHDRAW, AMOUNT);
        assertEquals(++oldSize, TRANSACTION_HISTORY_LIST.size());
        transactionHistoryService.addTransactionHistory(FIRST_ACCOUNT, SECOND_ACCOUNT, EOperationType.TRANSFER, AMOUNT);
        assertEquals(++oldSize, TRANSACTION_HISTORY_LIST.size());

        assertThrows(IllegalArgumentException.class,
                () -> transactionHistoryService.addTransactionHistory(null, FIRST_ACCOUNT, EOperationType.DEPOSIT, 0L)
        );
        assertThrows(IllegalArgumentException.class,
                () -> transactionHistoryService.addTransactionHistory(null, FIRST_ACCOUNT, EOperationType.DEPOSIT, null)
        );
        assertThrows(IllegalArgumentException.class,
                () -> transactionHistoryService.addTransactionHistory(null, null, EOperationType.DEPOSIT, AMOUNT)
        );
        assertThrows(IllegalArgumentException.class,
                () -> transactionHistoryService.addTransactionHistory(null, null, EOperationType.WITHDRAW, AMOUNT)
        );
        assertThrows(IllegalArgumentException.class,
                () -> transactionHistoryService.addTransactionHistory(FIRST_ACCOUNT, null, EOperationType.TRANSFER, AMOUNT)
        );
        assertThrows(IllegalArgumentException.class,
                () -> transactionHistoryService.addTransactionHistory(null, SECOND_ACCOUNT, EOperationType.TRANSFER, AMOUNT)
        );
        assertThrows(IllegalArgumentException.class,
                () -> transactionHistoryService.addTransactionHistory(null, null, EOperationType.TRANSFER, AMOUNT)
        );
    }

    @Test
    void getAllTransactionHistoryForAccount() {
        final List<TransactionHistoryDto> historyFirstAcc =
                transactionHistoryService.getAllTransactionHistoryForAccount(FIRST_ACC_NUMBER);
        final List<TransactionHistoryDto> historySecondAcc =
                transactionHistoryService.getAllTransactionHistoryForAccount(SECOND_ACC_NUMBER);
        assertEquals(historyFirstAcc.size(), 3);
        assertEquals(historySecondAcc.size(), 1);
    }
}