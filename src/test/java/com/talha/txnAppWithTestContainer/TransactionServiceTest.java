package com.talha.txnAppWithTestContainer;

import com.talha.txnAppWithTestContainer.dto.Transaction;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.oracle.OracleContainer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Testcontainers
class TransactionServiceTest {
    @Container
    static OracleContainer oracleContainer = new OracleContainer("gvenzl/oracle-free:slim-faststart")
            .withDatabaseName("testDB")
            .withUsername("testUser")
            .withPassword("testPassword");

    TransactionService service;

    @BeforeAll
    static void beforeAll() {
        oracleContainer.start();
    }

    @AfterAll
    static void afterAll() {
        oracleContainer.stop();
    }

    @BeforeEach
    void setUp() {
        DBConnectionProvider connectionProvider = new DBConnectionProvider(
                oracleContainer.getJdbcUrl(),
                oracleContainer.getUsername(),
                oracleContainer.getPassword()
        );
        service = new TransactionService(connectionProvider);
    }

    @Test
    void shouldReturnTransactionList() {
        service.createTransaction(new Transaction(1234567890123456L, 1));
        service.createTransaction(new Transaction(1234567890123456L, 3));
        service.createTransaction(new Transaction(1234567890123000L, 100));
        service.createTransaction(new Transaction(9999999999999999L, -1));

        List<Transaction> list = service.getTransactions();
        assertEquals(4, list.size());
    }
}