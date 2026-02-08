ALTER TABLE public.shopping_cart
    ADD CONSTRAINT uk_shopping_cart_customer UNIQUE (customer_id);