package org.example;
import org.example.Amazon.*;
import org.example.Amazon.Cost.DeliveryPrice;
import org.example.Amazon.Cost.ExtraCostForElectronics;
import org.example.Amazon.Cost.ItemType;
import org.example.Amazon.Cost.PriceRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import org.example.Amazon.Item;


import java.util.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/*

*/

public class AmazonUnitTest {
    private Amazon amazon;
    private ShoppingCart mockCart;
    private PriceRule deliveryPriceRule;
    private PriceRule extraCostForElectronics;

    @BeforeEach
    void setUp() {
        mockCart = mock(ShoppingCart.class);
        deliveryPriceRule = new DeliveryPrice();
        extraCostForElectronics = new ExtraCostForElectronics();
        amazon = new Amazon(mockCart, List.of(deliveryPriceRule, extraCostForElectronics));
    }

    @Test
    @DisplayName("Specification test")
    void testCartInitialization() {
        when(mockCart.getItems()).thenReturn(List.of());
        double totalPrice = amazon.calculate();
        assertThat(totalPrice).isEqualTo(0);
    }

    @Test
    @DisplayName("Specification test")
    void testAddItemToCart() {
        Item item = new Item(ItemType.OTHER, "Notebook", 2, 3.50);
        amazon.addToCart(item);
        verify(mockCart, times(1)).add(item);
    }

    @Test
    @DisplayName("Specification test")
    void testDeliveryPricingForThreeItems() {
        List<Item> cartItems = List.of(
                new Item(ItemType.OTHER, "Notebook", 1, 3.50),
                new Item(ItemType.OTHER, "Pencil", 1, 1.50),
                new Item(ItemType.OTHER, "Eraser", 1, 1.00)
        );
        when(mockCart.getItems()).thenReturn(cartItems);
        double totalPrice = amazon.calculate();
        assertThat(totalPrice).isEqualTo(5.0);
    }

    @Test
    @DisplayName("Specification test")
    void testDeliveryPricingForFiveItems() {
        List<Item> cartItems = List.of(
                new Item(ItemType.OTHER, "Notebook", 1, 3.50),
                new Item(ItemType.OTHER, "Pencil", 1, 1.50),
                new Item(ItemType.OTHER, "Eraser", 1, 1.00),
                new Item(ItemType.OTHER, "Highlighter", 1, 1.75),
                new Item(ItemType.OTHER, "Pen", 1, 1.50)
        );
        when(mockCart.getItems()).thenReturn(cartItems);
        double totalPrice = amazon.calculate();
        assertThat(totalPrice).isEqualTo(12.5);
    }

    @Test
    @DisplayName("Specification test")
    void testDeliveryPricingForManyItems() {
        List<Item> cartItems = List.of(
                new Item(ItemType.OTHER, "Notebook", 1, 3.50),
                new Item(ItemType.OTHER, "Pencil", 1, 1.50),
                new Item(ItemType.OTHER, "Eraser", 1, 1.00),
                new Item(ItemType.OTHER, "Highlighter", 1, 1.75),
                new Item(ItemType.OTHER, "Pen", 1, 1.50),
                new Item(ItemType.OTHER, "Ribbon", 1, 2.00),
                new Item(ItemType.OTHER, "Portait", 1, 7.00),
                new Item(ItemType.OTHER, "Sanitizer", 1, 3.00),
                new Item(ItemType.OTHER, "Cologne", 1, 60.00),
                new Item(ItemType.OTHER, "Post-its", 1, 2.00),
                new Item(ItemType.OTHER, "Stickers", 1, 1.50)
        );
        when(mockCart.getItems()).thenReturn(cartItems);
        double totalPrice = amazon.calculate();
        assertThat(totalPrice).isEqualTo(20.0);
    }

    @Test
    @DisplayName("Structural based test")
    void testExtraCostForElectronics() {
        List<Item> cartItems = List.of(
                new Item(ItemType.ELECTRONIC, "Calculator", 1, 80.00)
        );
        when(mockCart.getItems()).thenReturn(cartItems);
        double totalPrice = amazon.calculate();
        assertThat(totalPrice).isEqualTo(12.50); // adds 7.50 to delivery price
    }

    @Test
    @DisplayName("Structural based test")
    void testExtraCostForMixedItems() {
        List<Item> cartItems = List.of(
                new Item(ItemType.ELECTRONIC, "Calculator", 1, 80.00),
                new Item(ItemType.OTHER, "Notebook", 1, 3.50),
                new Item(ItemType.OTHER, "Pencil", 1, 1.50)
        );
        when(mockCart.getItems()).thenReturn(cartItems);
        double totalPrice = amazon.calculate();
        assertThat(totalPrice).isEqualTo(12.50); // adds 7.50 to delivery price (3 items)
    }





}
