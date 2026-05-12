package com.petstore.matcher;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Custom Hamcrest matchers for testing Petstore entities.
 * 
 * Provides fluent, readable assertions for common test scenarios.
 * 
 * @since 1.0.0
 */
public class PetstoreMatchers {

    /**
     * Matcher for checking if a pet has a specific name.
     * 
     * Usage: assertThat(pet, hasPetName("Fluffy"))
     * 
     * @param expectedName Expected pet name
     * @return Matcher
     */
    public static Matcher<Object> hasPetName(String expectedName) {
        return new TypeSafeMatcher<Object>() {
            @Override
            protected boolean matchesSafely(Object item) {
                try {
                    Object name = item.getClass().getMethod("getName").invoke(item);
                    return expectedName.equals(name);
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a pet with name ").appendValue(expectedName);
            }
        };
    }

    /**
     * Matcher for checking if a pet has a specific species.
     * 
     * Usage: assertThat(pet, hasPetSpecies("Dog"))
     * 
     * @param expectedSpecies Expected species
     * @return Matcher
     */
    public static Matcher<Object> hasPetSpecies(String expectedSpecies) {
        return new TypeSafeMatcher<Object>() {
            @Override
            protected boolean matchesSafely(Object item) {
                try {
                    Object species = item.getClass().getMethod("getSpecies").invoke(item);
                    return expectedSpecies.equals(species);
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a pet with species ").appendValue(expectedSpecies);
            }
        };
    }

    /**
     * Matcher for checking if a pet is within a price range.
     * 
     * Usage: assertThat(pet, priceInRange(100.0, 1000.0))
     * 
     * @param minPrice Minimum price (inclusive)
     * @param maxPrice Maximum price (inclusive)
     * @return Matcher
     */
    public static Matcher<Object> priceInRange(double minPrice, double maxPrice) {
        return new TypeSafeMatcher<Object>() {
            @Override
            protected boolean matchesSafely(Object item) {
                try {
                    Object price = item.getClass().getMethod("getPrice").invoke(item);
                    double petPrice = ((Number) price).doubleValue();
                    return petPrice >= minPrice && petPrice <= maxPrice;
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a pet with price between ")
                        .appendValue(minPrice)
                        .appendText(" and ")
                        .appendValue(maxPrice);
            }
        };
    }

    /**
     * Matcher for checking if an order is in a specific status.
     * 
     * Usage: assertThat(order, hasOrderStatus("completed"))
     * 
     * @param expectedStatus Expected order status
     * @return Matcher
     */
    public static Matcher<Object> hasOrderStatus(String expectedStatus) {
        return new TypeSafeMatcher<Object>() {
            @Override
            protected boolean matchesSafely(Object item) {
                try {
                    Object status = item.getClass().getMethod("getStatus").invoke(item);
                    return expectedStatus.equals(status);
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("an order with status ").appendValue(expectedStatus);
            }
        };
    }

    /**
     * Matcher for checking if a seller is verified.
     * 
     * Usage: assertThat(seller, isVerifiedSeller())
     * 
     * @return Matcher
     */
    public static Matcher<Object> isVerifiedSeller() {
        return new TypeSafeMatcher<Object>() {
            @Override
            protected boolean matchesSafely(Object item) {
                try {
                    Object verified = item.getClass().getMethod("isVerified").invoke(item);
                    return (Boolean) verified;
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a verified seller");
            }
        };
    }
}
