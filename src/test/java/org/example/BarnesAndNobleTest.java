package org.example;
import org.example.Barnes.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;


import static org.assertj.core.api.Assertions.assertThat;


import static org.junit.jupiter.api.Assertions.*;


/* Specification Tests:
    retrieveBook(String ISBN, int quantity, PurchaseSummary purchaseSummary):
        Empty string --> returns null or error
        incorrect length string --> returns null or error
        book's quantity < quantity --> should add unavailable books to db and update quantity
        book's quantity >= quantity --> should update purchaseSummary

    getPriceForCart(Map<String, Integer> order)
        null order --> return null

*/

public class BarnesAndNobleTest {
    private BarnesAndNoble bnn;
    private BookDatabase mockBookDatabase;
    private BuyBookProcess mockBookProcess;

    @BeforeEach
    public void setup() {
        // mock the databases
        mockBookDatabase = mock(BookDatabase.class);
        mockBookProcess = mock(BuyBookProcess.class);

        // initialize BarnesAndNoble with mocked databases
        bnn = new BarnesAndNoble(mockBookDatabase, mockBookProcess);
    }

    @Test
    @DisplayName("Specification test")
    public void testNullOnGetPriceForCart() {
        assertThat(bnn.getPriceForCart(null)).isNull();
    }

    @Test
    @DisplayName("Stuctural based test")
    public void testCalculateTotalPrice() {
        // Create book test data
        Book b1 = new Book("12345678", 1200, 5);
        Book b2 = new Book("87654321", 1500, 3);

        // Mock database behavior
        when(mockBookDatabase.findByISBN("12345678")).thenReturn(b1);
        when(mockBookDatabase.findByISBN("87654321")).thenReturn(b2);

        // Create an order (hashmap)
        Map<String, Integer> order = new HashMap<>();
        order.put("12345678", 2);
        order.put("87654321", 3);

        // execute getPriceForCart()
        PurchaseSummary summary = bnn.getPriceForCart(order);
        assertThat(summary.getTotalPrice()).isEqualTo(1200 * 2 + 1500 * 3);
        verify(mockBookProcess).buyBook(b1, 2);
        verify(mockBookProcess).buyBook(b2, 3);
    }

    @Test
    @DisplayName("Structural based test")
    public void testUnavailableBooks() {
        Book b1 = new Book("12345678", 1200, 5);
        Book b2 = new Book("87654321", 1500, 0);

        when(mockBookDatabase.findByISBN("12345678")).thenReturn(b1);
        when(mockBookDatabase.findByISBN("87654321")).thenReturn(b2);

        Map<String, Integer> order = new HashMap<>();
        order.put("12345678", 7); // this requests 7, but only 5
        order.put("87654321", 1);  // this requests 1, but there none

        PurchaseSummary summary = bnn.getPriceForCart(order);
        assertThat(summary.getTotalPrice()).isEqualTo(1200 * 5);
        assertThat(summary.getUnavailable()).containsEntry(b2, 1);
        assertThat(summary.getUnavailable()).containsEntry(b1, 2);

        verify(mockBookProcess).buyBook(b1, 5);
        verify(mockBookProcess, never()).buyBook(b2, 1);
    }




}
