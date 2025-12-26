package com.github.allisson95.algashop.ordering.domain.model.customer;

import com.github.allisson95.algashop.ordering.domain.model.AbstractEventSourceEntity;
import com.github.allisson95.algashop.ordering.domain.model.AggregateRoot;
import com.github.allisson95.algashop.ordering.domain.model.commons.*;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

@Getter
public class Customer
        extends AbstractEventSourceEntity
        implements AggregateRoot<CustomerId> {

    private CustomerId id;

    private FullName fullName;

    private BirthDate birthDate;

    private Email email;

    private Phone phone;

    private Document document;

    private Boolean promotionNotificationsAllowed;

    private Boolean archived;

    private Instant registeredAt;

    private Instant archivedAt;

    private LoyaltyPoints loyaltyPoints;

    private Address address;

    private Long version;

    @Builder(builderClassName = "NewCustomerBuilder", builderMethodName = "newCustomer")
    private static Customer createNew(final FullName fullName, final BirthDate birthDate, final Email email, final Phone phone, final Document document, final Boolean promotionNotificationsAllowed, final Address address) {
        final Customer newCustomer = new Customer(new CustomerId(), fullName, birthDate, email, phone, document, promotionNotificationsAllowed, false, Instant.now(), null, LoyaltyPoints.ZERO, address);

        newCustomer.registerEvent(new CustomerRegisteredEvent(newCustomer.getId(), newCustomer.getRegisteredAt()));

        return newCustomer;
    }

    @Builder(builderClassName = "ExistingCustomerBuilder", builderMethodName = "existingCustomer")
    private Customer(final CustomerId id, final FullName fullName, final BirthDate birthDate, final Email email, final Phone phone, final Document document, final Boolean promotionNotificationsAllowed, final Boolean archived, final Instant registeredAt, final Instant archivedAt, final LoyaltyPoints loyaltyPoints, final Address address) {
        this.setId(id);
        this.setFullName(fullName);
        this.setBirthDate(birthDate);
        this.setEmail(email);
        this.setPhone(phone);
        this.setDocument(document);
        this.setPromotionNotificationsAllowed(promotionNotificationsAllowed);
        this.setArchived(archived);
        this.setRegisteredAt(registeredAt);
        this.setArchivedAt(archivedAt);
        this.setLoyaltyPoints(loyaltyPoints);
        this.setAddress(address);
    }

    public void addLoyaltyPoints(final LoyaltyPoints loyaltyPointsToAdd) {
        this.verifyIfChangeable();
        this.setLoyaltyPoints(this.getLoyaltyPoints().add(loyaltyPointsToAdd));
    }

    public void archive() {
        this.verifyIfChangeable();
        this.setArchived(true);
        this.setArchivedAt(Instant.now());
        this.setFullName(new FullName("Anonymous", "Anonymous"));
        this.setBirthDate(null);
        this.setEmail(new Email("anonymous@email.com"));
        this.setPhone(new Phone("000-000-0000"));
        this.setDocument(new Document("000-00-0000"));
        this.setPromotionNotificationsAllowed(false);
        this.setAddress(this.getAddress().toBuilder()
                .number("Anonymized")
                .complement(null)
                .build());

        super.registerEvent(new CustomerArchivedEvent(this.getId(), this.getArchivedAt()));
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

    public void changeEmail(final Email newEmail) {
        this.verifyIfChangeable();
        this.setEmail(newEmail);
    }

    public void changePhone(final Phone newPhone) {
        this.verifyIfChangeable();
        this.setPhone(newPhone);
    }

    public void changeAddress(final Address newAddress) {
        this.verifyIfChangeable();
        this.setAddress(newAddress);
    }

    private void setId(final CustomerId id) {
        requireNonNull(id, "id cannot be null");
        this.id = id;
    }

    private void setFullName(final FullName fullName) {
        requireNonNull(fullName, "fullName cannot be null");
        this.fullName = fullName;
    }

    private void setBirthDate(final BirthDate birthDate) {
        if (birthDate == null) {
            this.birthDate = null;
            return;
        }
        this.birthDate = birthDate;
    }

    private void setEmail(final Email email) {
        requireNonNull(email, "email cannot be null");
        this.email = email;
    }

    private void setPhone(final Phone phone) {
        requireNonNull(phone, "phone cannot be null");
        this.phone = phone;
    }

    private void setDocument(final Document document) {
        requireNonNull(document, "document cannot be null");
        this.document = document;
    }

    private void setPromotionNotificationsAllowed(final Boolean promotionNotificationsAllowed) {
        requireNonNull(promotionNotificationsAllowed, "promotionNotificationsAllowed cannot be null");
        this.promotionNotificationsAllowed = promotionNotificationsAllowed;
    }

    private void setArchived(final Boolean archived) {
        requireNonNull(archived, "archived cannot be null");
        this.archived = archived;
    }

    private void setRegisteredAt(final Instant registeredAt) {
        requireNonNull(registeredAt, "registeredAt cannot be null");
        this.registeredAt = registeredAt;
    }

    private void setArchivedAt(final Instant archivedAt) {
        this.archivedAt = archivedAt;
    }

    private void setLoyaltyPoints(final LoyaltyPoints loyaltyPoints) {
        requireNonNull(loyaltyPoints, "loyaltyPoints cannot be null");
        this.loyaltyPoints = loyaltyPoints;
    }

    private void setAddress(final Address address) {
        requireNonNull(address, "address cannot be null");
        this.address = address;
    }

    private void verifyIfChangeable() {
        if (this.getArchived()) {
            throw new CustomerArchivedException();
        }
    }

    private Long getVersion() {
        return version;
    }

    private void setVersion(final Long version) {
        this.version = version;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        final Customer customer = (Customer) o;
        return Objects.equals(this.getId(), customer.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.getId());
    }

}
