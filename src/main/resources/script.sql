-- Ensure the UUID extension is installed (PostgreSQL)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Insert sellers with UUID codes
INSERT INTO sellers (code, name) VALUES ('7fcdb0f5-8467-4965-b62e-00f2a3f2f4e5', 'Seller One');
INSERT INTO sellers (code, name) VALUES (uuid_generate_v4(), 'Seller Two');
INSERT INTO sellers (code, name) VALUES (uuid_generate_v4(), 'Seller Three');

INSERT INTO payments (billing_code, amount, amount_paid, status, seller_id) VALUES
                                                                                ('BILL001', 100.00, 0.00, 'PENDING', 1),
                                                                                ('BILL002', 200.00, 50.00, 'PENDING', 1),
                                                                                ('BILL003', 150.00, 150.00, 'PENDING', 2),
                                                                                ('BILL004', 250.00, 300.00, 'PENDING', 3);