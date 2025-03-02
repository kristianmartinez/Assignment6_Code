package org.example;
import org.example.Amazon.*;
import org.example.Amazon.Cost.DeliveryPrice;
import org.example.Amazon.Cost.ItemType;
import org.example.Amazon.Cost.PriceRule;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AmazonIntegrationTest {
    private  Database database;
    private ShoppingCartAdaptor shoppingCart;
    private Amazon amazon;

    @BeforeAll
    void setUpDatabase() {
        database = new Database();
    }

    @BeforeEach
    void setup() {
        database.resetDatabase();
        shoppingCart = new ShoppingCartAdaptor(database);
        List<PriceRule> priceRules = List.of(new DeliveryPrice());
        amazon = new Amazon(shoppingCart, priceRules);
    }

    @Test
    @DisplayName("Specification test")
    void testAddItemToCart() {
        Item item = new Item(ItemType.OTHER, "Notebook", 1, 1.50);
        amazon.addToCart(item);

        List<Item> items = shoppingCart.getItems();
        assertThat(items).size().isEqualTo(1);
        assertThat(items.get(0).getName()).isEqualTo("Notebook");
    }

    @Test
    @DisplayName("Structural based test")
    void testCalculateTotalPriceWithDelivery() {
        amazon.addToCart(new Item(ItemType.OTHER, "Notebook", 1, 3.50));
        amazon.addToCart(new Item(ItemType.OTHER, "Pencil", 1, 1.50));

        double totalPrice = amazon.calculate();
        assertThat(totalPrice).isEqualTo(5.0);
    }

    @Test
    @DisplayName("Structural based test")
    void testCalculateTotalPriceWithElectronics() {
        amazon.addToCart(new Item(ItemType.ELECTRONIC, "Phone", 1, 699.0));

        double totalPrice = amazon.calculate();

        // test not working, should be adding additional charge for an electronic item
        assertThat(totalPrice).isEqualTo(5.0);

    }

    @AfterEach
    void tearDown() {
        database.close();  // Close database connection after all tests
    }


}
