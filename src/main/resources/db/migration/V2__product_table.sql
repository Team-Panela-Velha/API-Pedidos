-- V2__create_full_schema.sql

---------------------------------------------------------
-- 1. REESTRUTURAÇÃO DA TABELA PRODUCT (V1)
---------------------------------------------------------
ALTER TABLE product DROP CONSTRAINT IF EXISTS product_pkey;
ALTER TABLE product DROP COLUMN id;

ALTER TABLE product 
    ADD COLUMN product_id UUID PRIMARY KEY,
    ALTER COLUMN name TYPE VARCHAR(255),
    ADD COLUMN price FLOAT(53) NOT NULL,
    ADD COLUMN description TEXT NOT NULL,
    ADD COLUMN category_id UUID NOT NULL;

---------------------------------------------------------
-- 2. CRIAÇÃO DAS NOVAS TABELAS
---------------------------------------------------------

-- Tabela de Categorias
CREATE TABLE "category" (
    "id" UUID PRIMARY KEY,
    "name" VARCHAR(255) NOT NULL,
    "description" TEXT NOT NULL
);

-- Tabela de Mesas (Palavra reservada, requer "")
CREATE TABLE "table" (
    "table_id" UUID PRIMARY KEY,
    "code" VARCHAR(255) NOT NULL
);

-- Tabela de Comandas/Contas
CREATE TABLE "tab" (
    "tab_id" UUID PRIMARY KEY,
    "total_value" FLOAT(53) NOT NULL,
    "table_id" UUID NULL
);

-- Tabela de Pedidos (Palavra reservada, requer "")
CREATE TABLE "order" (
    "order_id" UUID PRIMARY KEY,
    "tab_id" UUID NOT NULL
);

-- Tabela de Itens do Pedido
CREATE TABLE "order_item" (
    "order_item_id" UUID PRIMARY KEY,
    "product_id" UUID NULL,
    "order_id" UUID NULL,
    "quantity" SMALLINT NOT NULL,
    "observation" TEXT NOT NULL
);

-- Tabela de Adicionais/Extras
CREATE TABLE "extra" (
    "extra_id" UUID PRIMARY KEY,
    "name" VARCHAR(255) NOT NULL
);

-- Tabela Intermediária: Produtos e seus Extras possíveis
CREATE TABLE "product_extra" (
    "adicional_id" UUID NULL,
    "produto_id" UUID NULL
);

-- Tabela Intermediária: Extras selecionados em um Item de Pedido
CREATE TABLE "item_extra" (
    "order_item_id" UUID NOT NULL,
    "extra_id" UUID NOT NULL,
    PRIMARY KEY ("order_item_id", "extra_id")
);

---------------------------------------------------------
-- 3. DEFINIÇÃO DE CONSTRAINTS (FOREIGN KEYS)
---------------------------------------------------------

-- FK de Produto para Categoria
ALTER TABLE "product" 
    ADD CONSTRAINT "product_category_id_foreign" 
    FOREIGN KEY ("category_id") REFERENCES "category" ("id");

-- FK de Tab para Table
ALTER TABLE "tab" 
    ADD CONSTRAINT "tab_table_id_foreign" 
    FOREIGN KEY ("table_id") REFERENCES "table" ("table_id");

-- FK de Order para Tab
ALTER TABLE "order" 
    ADD CONSTRAINT "order_tab_id_foreign" 
    FOREIGN KEY ("tab_id") REFERENCES "tab" ("tab_id");

-- FKs de Order_Item
ALTER TABLE "order_item" 
    ADD CONSTRAINT "order_item_order_id_foreign" 
    FOREIGN KEY ("order_id") REFERENCES "order" ("order_id");

ALTER TABLE "order_item" 
    ADD CONSTRAINT "order_item_product_id_foreign" 
    FOREIGN KEY ("product_id") REFERENCES "product" ("product_id");

-- FKs de Product_Extra (Relacionamento N:N entre Produto e Extra)
ALTER TABLE "product_extra" 
    ADD CONSTRAINT "product_extra_produto_id_foreign" 
    FOREIGN KEY ("produto_id") REFERENCES "product" ("product_id");

ALTER TABLE "product_extra" 
    ADD CONSTRAINT "product_extra_adicional_id_foreign" 
    FOREIGN KEY ("adicional_id") REFERENCES "extra" ("extra_id");

-- FKs de Item_Extra
-- Corrigi a referência: o extra_id aponta para a tabela extra
ALTER TABLE "item_extra" 
    ADD CONSTRAINT "item_extra_extra_id_foreign" 
    FOREIGN KEY ("extra_id") REFERENCES "extra" ("extra_id");

-- O order_item_id aponta para a tabela order_item
ALTER TABLE "item_extra" 
    ADD CONSTRAINT "item_extra_order_item_id_foreign" 
    FOREIGN KEY ("order_item_id") REFERENCES "order_item" ("order_item_id");