package com.github.allisson95.algashop.ordering.domain.entity;

import com.github.allisson95.algashop.ordering.domain.exception.CustomerArchivedException;
import com.github.allisson95.algashop.ordering.domain.validator.Validators;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class Customer {

    private UUID id;

    private String fullName;

    private LocalDate birthDate;

    private String email;

    private String phone;

    private String document;

    private Boolean promotionNotificationsAllowed;

    private Boolean archived;

    private Instant registeredAt;

    private Instant archivedAt;

    private Integer loyaltyPoints;

    public Customer(final UUID id, final String fullName, final LocalDate birthDate, final String email, final String phone, final String document, final Boolean promotionNotificationsAllowed, final Instant registeredAt) {
        this.setId(id);
        this.setFullName(fullName);
        this.setBirthDate(birthDate);
        this.setEmail(email);
        this.setPhone(phone);
        this.setDocument(document);
        this.setPromotionNotificationsAllowed(promotionNotificationsAllowed);
        this.setArchived(false);
        this.setRegisteredAt(registeredAt);
        this.setArchivedAt(null);
        this.setLoyaltyPoints(0);
    }

    public Customer(final UUID id, final String fullName, final LocalDate birthDate, final String email, final String phone, final String document, final Boolean promotionNotificationsAllowed, final Boolean archived, final Instant registeredAt, final Instant archivedAt, final Integer loyaltyPoints) {
        this.id = id;
        this.fullName = fullName;
        this.birthDate = birthDate;
        this.email = email;
        this.phone = phone;
        this.document = document;
        this.promotionNotificationsAllowed = promotionNotificationsAllowed;
        this.archived = archived;
        this.registeredAt = registeredAt;
        this.archivedAt = archivedAt;
        this.loyaltyPoints = loyaltyPoints;
    }

    public void addLoyaltyPoints(final Integer loyaltyPointsToAdd) {
        this.verifyIfChangeable();
        if (loyaltyPointsToAdd <= 0) {
            throw new IllegalArgumentException("loyaltyPointsToAdd cannot be negative or zero");
        }
        this.setLoyaltyPoints(this.loyaltyPoints() + loyaltyPointsToAdd);
    }

    public void archive() {
        this.verifyIfChangeable();
        this.setArchived(true);
        this.setArchivedAt(Instant.now());
        this.setFullName("Anonymous");
        this.setBirthDate(null);
        this.setEmail("anonymous@email.com");
        this.setPhone("000-000-0000");
        this.setDocument("000-00-0000");
        this.setPromotionNotificationsAllowed(false);
    }

    public void enablePromotionNotifications() {
        this.verifyIfChangeable();
        this.setPromotionNotificationsAllowed(true);
    }

    public void disablePromotionNotifications() {
        this.verifyIfChangeable();
        this.setPromotionNotificationsAllowed(false);
    }

    public void changeName(final String newName) {
        this.verifyIfChangeable();
        this.setFullName(newName);
    }

    public void changeEmail(final String newEmail) {
        this.verifyIfChangeable();
        this.setEmail(newEmail);
    }

    public void changePhone(final String newPhone) {
        this.verifyIfChangeable();
        this.setPhone(newPhone);
    }

    public UUID id() {
        return id;
    }

    private void setId(final UUID id) {
        Objects.requireNonNull(id, "id cannot be null");
        this.id = id;
    }

    public String fullName() {
        return fullName;
    }

    private void setFullName(final String fullName) {
        Objects.requireNonNull(fullName, "fullName cannot be null");
        if (fullName.isBlank()) {
            throw new IllegalArgumentException("fullName cannot be blank");
        }
        this.fullName = fullName;
    }

    public LocalDate birthDate() {
        return birthDate;
    }

    private void setBirthDate(final LocalDate birthDate) {
        if (birthDate == null) {
            this.birthDate = null;
            return;
        }
        if (birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("birthDate cannot be in the future");
        }
        this.birthDate = birthDate;
    }

    public String email() {
        return email;
    }

    private void setEmail(final String email) {
        Objects.requireNonNull(email, "email cannot be null");
        if (email.isBlank()) {
            throw new IllegalArgumentException("email cannot be blank");
        }
        if (!Validators.isValidEmail(email)) {
            throw new IllegalArgumentException("%s is not a well-formed email address".formatted(email));
        }
        this.email = email;
    }

    public String phone() {
        return phone;
    }

    private void setPhone(final String phone) {
        Objects.requireNonNull(phone, "phone cannot be null");
        this.phone = phone;
    }

    public String document() {
        return document;
    }

    private void setDocument(final String document) {
        Objects.requireNonNull(document, "document cannot be null");
        this.document = document;
    }

    public Boolean isPromotionNotificationsAllowed() {
        return promotionNotificationsAllowed;
    }

    private void setPromotionNotificationsAllowed(final Boolean promotionNotificationsAllowed) {
        Objects.requireNonNull(promotionNotificationsAllowed, "promotionNotificationsAllowed cannot be null");
        this.promotionNotificationsAllowed = promotionNotificationsAllowed;
    }

    public Boolean isArchived() {
        return archived;
    }

    private void setArchived(final Boolean archived) {
        Objects.requireNonNull(archived, "archived cannot be null");
        this.archived = archived;
    }

    public Instant registeredAt() {
        return registeredAt;
    }

    private void setRegisteredAt(final Instant registeredAt) {
        Objects.requireNonNull(registeredAt, "registeredAt cannot be null");
        this.registeredAt = registeredAt;
    }

    public Instant archivedAt() {
        return archivedAt;
    }

    private void setArchivedAt(final Instant archivedAt) {
        this.archivedAt = archivedAt;
    }

    public Integer loyaltyPoints() {
        return loyaltyPoints;
    }

    private void setLoyaltyPoints(final Integer loyaltyPoints) {
        Objects.requireNonNull(loyaltyPoints, "loyaltyPoints cannot be null");
        if (loyaltyPoints < 0) {
            throw new IllegalArgumentException("loyaltyPoints cannot be negative");
        }
        this.loyaltyPoints = loyaltyPoints;
    }

    private void verifyIfChangeable() {
        if (this.isArchived()) {
            throw new CustomerArchivedException();
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final Customer customer = (Customer) o;
        return Objects.equals(id(), customer.id());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id());
    }

}
