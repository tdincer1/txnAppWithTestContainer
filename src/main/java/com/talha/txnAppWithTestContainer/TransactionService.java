package com.talha.txnAppWithTestContainer;

import com.talha.txnAppWithTestContainer.dto.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransactionService {

    private final DBConnectionProvider connectionProvider;

    public TransactionService(DBConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
        createTransactionsTableIfNotExists();
    }

    public void createTransaction(Transaction transaction) {
        try (Connection cnn = this.connectionProvider.getConnection()) {
            PreparedStatement st = cnn.prepareStatement(
                    "insert into transactions(cardNumber, amount) values(?,?)"
            );
            st.setLong(1, transaction.cardNumber());
            st.setInt(2, transaction.amount());
            st.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Transaction> getTransactions() {
        List<Transaction> transactions = new ArrayList<>();

        try (Connection cnn = this.connectionProvider.getConnection()) {
            PreparedStatement st = cnn.prepareStatement(
                    "select cardNumber, amount from transactions"
            );
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                transactions.add(new Transaction(rs.getLong("cardNumber"), rs.getInt("amount")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return transactions;
    }

    private void createTransactionsTableIfNotExists() {
        try (Connection cnn = this.connectionProvider.getConnection()) {
            PreparedStatement st = cnn.prepareStatement(
            """
                create table if not exists transactions (
                    cardNumber number(19,0) not null,
                    amount number(8,0) not null,
                )
                """
            );
            st.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
