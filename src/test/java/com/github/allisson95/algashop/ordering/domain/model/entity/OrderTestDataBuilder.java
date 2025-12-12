package com.github.allisson95.algashop.ordering.domain.model.entity;

import com.github.allisson95.algashop.ordering.domain.model.valueobject.*;
import com.github.allisson95.algashop.ordering.domain.model.valueobject.id.CustomerId;
import net.datafaker.Faker;

import java.time.LocalDate;

public class OrderTestDataBuilder {

    private static final Faker faker = new Faker();

    private CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;

    private PaymentMethod paymentMethod = PaymentMethod.GATEWAY_BALANCE;

    private Billing billing = aBilling();

    private Shipping shipping = aShipping();

    private boolean withItems = true;

    private OrderStatus status = OrderStatus.DRAFT;

    private OrderTestDataBuilder() {
    }

    public static OrderTestDataBuilder anOrder() {
        return new OrderTestDataBuilder();
    }

    public OrderTestDataBuilder customerId(final CustomerId customerId) {
        this.customerId = customerId;
        return this;
    }

    public OrderTestDataBuilder paymentMethod(final PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
        return this;
    }

    public OrderTestDataBuilder billing(final Billing billing) {
        this.billing = billing;
        return this;
    }

    public OrderTestDataBuilder shipping(final Shipping shipping) {
        this.shipping = shipping;
        return this;
    }

    public OrderTestDataBuilder withItems(final boolean withItems) {
        this.withItems = withItems;
        return this;
    }

    public OrderTestDataBuilder status(final OrderStatus status) {
        this.status = status;
        return this;
    }

    public Order build() {
        final Order order = Order.draft(this.customerId);
        order.changePaymentMethod(this.paymentMethod);
        order.changeBilling(this.billing);
        order.changeShipping(this.shipping);

        if (this.withItems) {
            order.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(faker.number().numberBetween(1, 10)));
            order.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(faker.number().numberBetween(1, 10)));
            order.addItem(ProductTestDataBuilder.aProduct().build(), new Quantity(faker.number().numberBetween(1, 10)));
        }

        switch (this.status) {
            case DRAFT -> {
            }
            case PLACED -> order.place();
            case PAID -> {
                order.place();
                order.markAsPaid();
            }
            case READY -> {
                order.place();
                order.markAsPaid();
                order.markAsReady();
            }
            case CANCELED -> order.cancel();
            case null, default -> throw new IllegalStateException("Unexpected value: " + this.status);
        }

        return order;
    }

    public static Billing aBilling() {
        final FullName fullName = new FullName(faker.name().firstName(), faker.name().lastName());
        final Document document = new Document(faker.idNumber().valid());
        final Phone phone = new Phone(faker.phoneNumber().cellPhone());
        final Email email = new Email(faker.internet().emailAddress());
        final Address billingAddress = Address.builder()
                .street(faker.address().streetAddress())
                .number(faker.address().buildingNumber())
                .neighborhood(faker.address().secondaryAddress())
                .city(faker.address().city())
                .state(faker.address().state())
                .zipCode(new ZipCode(faker.address().zipCode()))
                .build();

        return Billing.builder()
                .fullName(fullName)
                .document(document)
                .phone(phone)
                .email(email)
                .address(billingAddress)
                .build();
    }

    public static Shipping aShipping() {
        final FullName fullName = new FullName(faker.name().firstName(), faker.name().lastName());
        final Document document = new Document(faker.idNumber().valid());
        final Phone phone = new Phone(faker.phoneNumber().cellPhone());
        final Recipient recipient = Recipient.builder()
                .fullName(fullName)
                .document(document)
                .phone(phone)
                .build();
        final Address shippingAddress = Address.builder()
                .street(faker.address().streetAddress())
                .number(faker.address().buildingNumber())
                .neighborhood(faker.address().secondaryAddress())
                .city(faker.address().city())
                .state(faker.address().state())
                .zipCode(new ZipCode(faker.address().zipCode()))
                .build();
        final Money shippingCost = new Money(faker.commerce().price());
        final LocalDate expectedDeliveryDate = LocalDate.now().plusDays(faker.number().numberBetween(1, 10));

        return Shipping.builder()
                .recipient(recipient)
                .address(shippingAddress)
                .cost(shippingCost)
                .expectedDeliveryDate(expectedDeliveryDate)
                .build();
    }

}
