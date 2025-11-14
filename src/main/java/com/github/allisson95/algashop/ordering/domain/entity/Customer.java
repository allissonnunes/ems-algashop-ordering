package com.github.allisson95.algashop.ordering.domain.entity;

import com.github.allisson95.algashop.ordering.domain.exception.CustomerArchivedException;
import com.github.allisson95.algashop.ordering.domain.validator.Validators;
import com.github.allisson95.algashop.ordering.domain.valueobject.CustomerId;
import com.github.allisson95.algashop.ordering.domain.valueobject.FullName;
import com.github.allisson95.algashop.ordering.domain.valueobject.LoyaltyPoints;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

public class Customer {

    private CustomerId id;

    private FullName fullName;

    private LocalDate birthDate;

    private String email;

    private String phone;

    private String document;

    private Boolean promotionNotificationsAllowed;

    private Boolean archived;

    private Instant registeredAt;

    private Instant archivedAt;

    private LoyaltyPoints loyaltyPoints;

    public Customer(final FullName fullName, final LocalDate birthDate, final String email, final String phone, final String document, final Boolean promotionNotificationsAllowed, final Instant registeredAt) {
        this.setId(new CustomerId());
        this.setFullName(fullName);
        this.setBirthDate(birthDate);
        this.setEmail(email);
        this.setPhone(phone);
        this.setDocument(document);
        this.setPromotionNotificationsAllowed(promotionNotificationsAllowed);
        this.setArchived(false);
        this.setRegisteredAt(registeredAt);
        this.setArchivedAt(null);
        this.setLoyaltyPoints(LoyaltyPoints.ZERO);
    }

    public Customer(final CustomerId id, final FullName fullName, final LocalDate birthDate, final String email, final String phone, final String document, final Boolean promotionNotificationsAllowed, final Boolean archived, final Instant registeredAt, final Instant archivedAt, final LoyaltyPoints loyaltyPoints) {
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

    public void addLoyaltyPoints(final LoyaltyPoints loyaltyPointsToAdd) {
        this.verifyIfChangeable();
        this.setLoyaltyPoints(this.loyaltyPoints().add(loyaltyPointsToAdd));
    }

    public void archive() {
        this.verifyIfChangeable();
        this.setArchived(true);
        this.setArchivedAt(Instant.now());
        this.setFullName(new FullName("Anonymous", "Anonymous"));
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

    public void changeName(final FullName newName) {
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

    public CustomerId id() {
        return id;
    }

    private void setId(final CustomerId id) {
        Objects.requireNonNull(id, "id cannot be null");
        this.id = id;
    }

    public FullName fullName() {
        return fullName;
    }

    private void setFullName(final FullName fullName) {
        Objects.requireNonNull(fullName, "fullName cannot be null");
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

    public LoyaltyPoints loyaltyPoints() {
        return loyaltyPoints;
    }

    private void setLoyaltyPoints(final LoyaltyPoints loyaltyPoints) {
        Objects.requireNonNull(loyaltyPoints, "loyaltyPoints cannot be null");
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
