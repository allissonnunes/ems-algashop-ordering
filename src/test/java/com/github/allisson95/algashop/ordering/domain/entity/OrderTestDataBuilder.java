package com.github.allisson95.algashop.ordering.domain.entity;

import com.github.allisson95.algashop.ordering.domain.valueobject.*;
import com.github.allisson95.algashop.ordering.domain.valueobject.id.CustomerId;
import net.datafaker.Faker;

import java.time.LocalDate;

public class OrderTestDataBuilder {

    private static final Faker faker = new Faker();

    private CustomerId customerId = new CustomerId();

    private PaymentMethod paymentMethod = PaymentMethod.GATEWAY_BALANCE;

    private Money shippingCost = new Money(faker.commerce().price());

    private LocalDate expectedDeliveryDate = LocalDate.now().plusDays(faker.number().numberBetween(1, 10));

    private BillingInfo billing = aBillingInfo();

    private ShippingInfo shipping = aShippingInfo();

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

    public OrderTestDataBuilder shippingCost(final Money shippingCost) {
        this.shippingCost = shippingCost;
        return this;
    }

    public OrderTestDataBuilder expectedDeliveryDate(final LocalDate expectedDeliveryDate) {
        this.expectedDeliveryDate = expectedDeliveryDate;
        return this;
    }

    public OrderTestDataBuilder billing(final BillingInfo billing) {
        this.billing = billing;
        return this;
    }

    public OrderTestDataBuilder shipping(final ShippingInfo shipping) {
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
        order.changeBillingInfo(this.billing);
        order.changeShippingInfo(this.shipping, this.shippingCost, this.expectedDeliveryDate);

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

    public static BillingInfo aBillingInfo() {
        final FullName fullName = new FullName(faker.name().firstName(), faker.name().lastName());
        final Document document = new Document(faker.idNumber().valid());
        final Phone phone = new Phone(faker.phoneNumber().cellPhone());
        final Address billingAddress = Address.builder()
                .street(faker.address().streetAddress())
                .number(faker.address().buildingNumber())
                .neighborhood(faker.address().secondaryAddress())
                .city(faker.address().city())
                .state(faker.address().state())
                .zipCode(new ZipCode(faker.address().zipCode()))
                .build();

        return BillingInfo.builder()
                .fullName(fullName)
                .document(document)
                .phone(phone)
                .address(billingAddress)
                .build();
    }

    public static ShippingInfo aShippingInfo() {
        final FullName fullName = new FullName(faker.name().firstName(), faker.name().lastName());
        final Document document = new Document(faker.idNumber().valid());
        final Phone phone = new Phone(faker.phoneNumber().cellPhone());
        final Address shippingAddress = Address.builder()
                .street(faker.address().streetAddress())
                .number(faker.address().buildingNumber())
                .neighborhood(faker.address().secondaryAddress())
                .city(faker.address().city())
                .state(faker.address().state())
                .zipCode(new ZipCode(faker.address().zipCode()))
                .build();

        return ShippingInfo.builder()
                .fullName(fullName)
                .document(document)
                .phone(phone)
                .address(shippingAddress)
                .build();
    }

}
