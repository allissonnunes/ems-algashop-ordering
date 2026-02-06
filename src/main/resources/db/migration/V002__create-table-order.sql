CREATE TABLE public."order"
(
    id                              BIGINT NOT NULL,
    customer_id                     UUID   NOT NULL,
    total_amount                    NUMERIC(38, 2),
    total_items                     INTEGER,
    placed_at                       TIMESTAMP WITHOUT TIME ZONE,
    paid_at                         TIMESTAMP WITHOUT TIME ZONE,
    canceled_at                     TIMESTAMP WITHOUT TIME ZONE,
    ready_at                        TIMESTAMP WITHOUT TIME ZONE,
    status                          VARCHAR(255),
    payment_method                  VARCHAR(255),
    created_by                      UUID,
    created_at                      TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by                UUID,
    last_modified_at                TIMESTAMP WITHOUT TIME ZONE,
    version                         BIGINT,
    billing_first_name              VARCHAR(255),
    billing_last_name               VARCHAR(255),
    billing_document                VARCHAR(255),
    billing_phone                   VARCHAR(255),
    billing_email                   VARCHAR(255),
    billing_address_street          VARCHAR(255),
    billing_address_number          VARCHAR(255),
    billing_address_complement      VARCHAR(255),
    billing_address_neighborhood    VARCHAR(255),
    billing_address_city            VARCHAR(255),
    billing_address_state           VARCHAR(255),
    billing_address_zip_code        VARCHAR(255),
    shipping_recipient_first_name   VARCHAR(255),
    shipping_recipient_last_name    VARCHAR(255),
    shipping_recipient_document     VARCHAR(255),
    shipping_recipient_phone        VARCHAR(255),
    shipping_address_street         VARCHAR(255),
    shipping_address_number         VARCHAR(255),
    shipping_address_complement     VARCHAR(255),
    shipping_address_neighborhood   VARCHAR(255),
    shipping_address_city           VARCHAR(255),
    shipping_address_state          VARCHAR(255),
    shipping_address_zip_code       VARCHAR(255),
    shipping_cost                   NUMERIC(38, 2),
    shipping_expected_delivery_date date,
    CONSTRAINT "pk_'order'" PRIMARY KEY (id)
);

CREATE TABLE public.order_item
(
    id           BIGINT NOT NULL,
    order_id     BIGINT NOT NULL,
    product_id   UUID,
    product_name VARCHAR(255),
    price        NUMERIC(38, 2),
    quantity     INTEGER,
    total_amount NUMERIC(38, 2),
    CONSTRAINT pk_order_item PRIMARY KEY (id)
);

CREATE INDEX idx_order_customer_id ON public."order" (customer_id);
ALTER TABLE public."order"
    ADD CONSTRAINT "fk_'order'_on_customer" FOREIGN KEY (customer_id) REFERENCES public.customer (id);

CREATE INDEX idx_order_item_order_id ON public.order_item (order_id);
ALTER TABLE public.order_item
    ADD CONSTRAINT fk_order_item_on_order FOREIGN KEY (order_id) REFERENCES public."order" (id);
