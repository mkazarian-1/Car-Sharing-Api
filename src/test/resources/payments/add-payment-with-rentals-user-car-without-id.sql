INSERT INTO cars (id, model, brand, type, inventory, daily_fee)
VALUES (1, 'LanotOnete', 'Blank', 'SUV', 10, 100);

INSERT INTO users (id, email, first_name, second_name, password)
VALUES (2, 'Lanot@gmail.com-MANAGER', 'Jhon1', 'JoJhon1', 'some password'),
       (3, 'Lanot@gmail.com-CUSTOMER', 'Jhon2', 'JoJhon2', 'some password');

INSERT INTO user_roles (user_id, roles) VALUES (3, 'CUSTOMER'),(2, 'CUSTOMER'), (2,'MANAGER');

INSERT INTO rentals (id, rental_date, return_date, actual_return_date, car_id, user_id)
VALUES (1, '2025-06-01', '2025-08-01', NULL, 1, 3),
       (2, '2025-08-01', '2025-10-02', NULL, 1, 2),
       (3, '2025-06-01', '2025-08-01', NULL, 1, 3),
       (4, '2026-06-01', '2026-08-01', '2026-08-01', 1, 3),
       (5, '2025-06-01', '2025-08-01', '2025-09-01', 1, 2),
       (6, '2025-06-01', '2025-08-01', '2025-09-01', 1, 2);

INSERT INTO payments (status, type, rental_id, amount_to_pay,session_url,session_id)
VALUES ('PAID', 'PAYMENT', 1, 1000,'https://checkout.stripe.com/1','cs_test_a1iKI1EwbZMgWe1'),
       ('PENDING', 'FINE', 2, 1100,'https://checkout.stripe.com/2','cs_test_a1iKI1EwbZMgWe2');