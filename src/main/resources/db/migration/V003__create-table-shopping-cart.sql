CREATE TABLE public.shopping_cart
(
    id               UUID NOT NULL,
    customer_id      UUID NOT NULL,
    total_amount     NUMERIC(38, 2),
    total_items      INTEGER,
    created_by       UUID,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by UUID,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    version          BIGINT,
    CONSTRAINT pk_shopping_cart PRIMARY KEY (id)
);

CREATE TABLE public.shopping_cart_item
(
    id               UUID NOT NULL,
    shopping_cart_id UUID NOT NULL,
    product_id       UUID,
    product_name     VARCHAR(255),
    price            NUMERIC(38, 2),
    quantity         INTEGER,
    total_amount     NUMERIC(38, 2),
    available        BOOLEAN,
    created_by       UUID,
    created_at       TIMESTAMP WITHOUT TIME ZONE,
    last_modified_by UUID,
    last_modified_at TIMESTAMP WITHOUT TIME ZONE,
    version          BIGINT,
    CONSTRAINT pk_shopping_cart_item PRIMARY KEY (id)
);

CREATE INDEX idx_shopping_cart_customer_id ON public.shopping_cart (customer_id);
ALTER TABLE public.shopping_cart
    ADD CONSTRAINT fk_shopping_cart_on_customer FOREIGN KEY (customer_id) REFERENCES public.customer (id);

CREATE INDEX idx_shopping_cart_item_shopping_cart_id ON public.shopping_cart_item (shopping_cart_id);
ALTER TABLE public.shopping_cart_item
    ADD CONSTRAINT fk_shopping_cart_item_on_shopping_cart FOREIGN KEY (shopping_cart_id) REFERENCES public.shopping_cart (id);
