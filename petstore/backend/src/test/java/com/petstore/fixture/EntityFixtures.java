package com.petstore.fixture;

import java.util.UUID;

/**
 * Factory methods for creating test entities.
 * 
 * Provides builder-like methods for creating entities with realistic test data.
 * Useful for reducing boilerplate in test classes.
 * 
 * @since 1.0.0
 */
public class EntityFixtures {

    /**
     * Create a test User entity with default values.
     * 
     * @return User fixture
     */
    public static UserFixture aUser() {
        return new UserFixture();
    }

    /**
     * Create a test Seller entity with default values.
     * 
     * @return Seller fixture
     */
    public static SellerFixture aSeller() {
        return new SellerFixture();
    }

    /**
     * Create a test Pet entity with default values.
     * 
     * @return Pet fixture
     */
    public static PetFixture aPet() {
        return new PetFixture();
    }

    /**
     * Create a test Order entity with default values.
     * 
     * @return Order fixture
     */
    public static OrderFixture anOrder() {
        return new OrderFixture();
    }

    /**
     * Builder fixture for User entity.
     */
    public static class UserFixture {
        private String id = UUID.randomUUID().toString();
        private String email = "test@petstore.local";
        private String firstName = "Test";
        private String lastName = "User";
        private boolean emailVerified = false;

        public UserFixture withId(String id) {
            this.id = id;
            return this;
        }

        public UserFixture withEmail(String email) {
            this.email = email;
            return this;
        }

        public UserFixture withFirstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public UserFixture withEmailVerified(boolean verified) {
            this.emailVerified = verified;
            return this;
        }

        public UserFixture build() {
            return this;
        }

        public String getId() {
            return id;
        }

        public String getEmail() {
            return email;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public boolean isEmailVerified() {
            return emailVerified;
        }
    }

    /**
     * Builder fixture for Seller entity.
     */
    public static class SellerFixture {
        private String id = UUID.randomUUID().toString();
        private String businessName = "Test Pet Shop";
        private String userId = UUID.randomUUID().toString();
        private boolean verified = false;
        private double commissionRate = 10.0;

        public SellerFixture withId(String id) {
            this.id = id;
            return this;
        }

        public SellerFixture withBusinessName(String businessName) {
            this.businessName = businessName;
            return this;
        }

        public SellerFixture withUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public SellerFixture withVerified(boolean verified) {
            this.verified = verified;
            return this;
        }

        public SellerFixture withCommissionRate(double rate) {
            this.commissionRate = rate;
            return this;
        }

        public SellerFixture build() {
            return this;
        }

        public String getId() {
            return id;
        }

        public String getBusinessName() {
            return businessName;
        }

        public String getUserId() {
            return userId;
        }

        public boolean isVerified() {
            return verified;
        }

        public double getCommissionRate() {
            return commissionRate;
        }
    }

    /**
     * Builder fixture for Pet entity.
     */
    public static class PetFixture {
        private String id = UUID.randomUUID().toString();
        private String name = "Fluffy";
        private String species = "Cat";
        private String breed = "Persian";
        private double price = 500.00;
        private String status = "active";

        public PetFixture withId(String id) {
            this.id = id;
            return this;
        }

        public PetFixture withName(String name) {
            this.name = name;
            return this;
        }

        public PetFixture withSpecies(String species) {
            this.species = species;
            return this;
        }

        public PetFixture withBreed(String breed) {
            this.breed = breed;
            return this;
        }

        public PetFixture withPrice(double price) {
            this.price = price;
            return this;
        }

        public PetFixture withStatus(String status) {
            this.status = status;
            return this;
        }

        public PetFixture build() {
            return this;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getSpecies() {
            return species;
        }

        public String getBreed() {
            return breed;
        }

        public double getPrice() {
            return price;
        }

        public String getStatus() {
            return status;
        }
    }

    /**
     * Builder fixture for Order entity.
     */
    public static class OrderFixture {
        private String id = UUID.randomUUID().toString();
        private String userId = UUID.randomUUID().toString();
        private String sellerId = UUID.randomUUID().toString();
        private double totalAmount = 500.00;
        private String status = "pending";

        public OrderFixture withId(String id) {
            this.id = id;
            return this;
        }

        public OrderFixture withUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public OrderFixture withSellerId(String sellerId) {
            this.sellerId = sellerId;
            return this;
        }

        public OrderFixture withTotalAmount(double amount) {
            this.totalAmount = amount;
            return this;
        }

        public OrderFixture withStatus(String status) {
            this.status = status;
            return this;
        }

        public OrderFixture build() {
            return this;
        }

        public String getId() {
            return id;
        }

        public String getUserId() {
            return userId;
        }

        public String getSellerId() {
            return sellerId;
        }

        public double getTotalAmount() {
            return totalAmount;
        }

        public String getStatus() {
            return status;
        }
    }
}
