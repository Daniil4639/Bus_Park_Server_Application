INSERT INTO departments
VALUES ('aa240b42-9e9f-4c22-a040-96d0eb13325c', 'Департамент №1', 'Москва, ул. Садовая, 1', false),
       ('4006780e-ab6d-423a-9d68-d78cf76b0d87', 'Департамент №2', 'Москва, Красногвардейский проезд, 21', false);

INSERT INTO drivers
VALUES ('9773a7ca-4413-4b12-9819-b280df8736b1', 'Никитников Ринат', 41, '+7 (915) 264-85-93', 'driver1@transit.com', 'LIC-7845-2023', 'ACTIVE', false),
       ('9a21f5fc-d1ae-420d-a2ad-355c0a5c62b9', 'Хусид Глеб', 36, '+7 (925) 731-49-06', 'driver2@transit.com', 'LIC-9012-2023', 'ACTIVE', false),
       ('64a398b9-1473-46a1-9659-41bb5ec03780', 'Зарубин Тимофей', 53, '+7 (916) 408-27-15', 'driver3@transit.com', 'LIC-3567-2023', 'ACTIVE', false),
       ('1755fc36-14d9-4b1f-a34a-818c09b078eb', 'Гилев Юрий', 29, '+7 (903) 156-84-72', 'driver4@transit.com', 'LIC-6823-2023', 'ACTIVE', false),
       ('10d829a6-ee7a-4470-96c9-0ee50623587c', 'Волков Артем', 28, '+7 (977) 342-91-58', 'driver5@transit.com', 'LIC-4591-2023', 'ACTIVE', false),
       ('ff740762-33fa-40f2-a1c0-bd2724a1009b', 'Белов Анатолий', 34, '+7 (999) 675-23-40', 'driver6@transit.com', 'LIC-2378-2023', 'ACTIVE', false),
       ('09d9cb4d-e5e6-4fbd-a093-bcdbb0386f7a', 'Сухарев Игорь', 37, '+7 (495) 782-61-34', 'driver7@transit.com', 'LIC-5642-2023', 'ACTIVE', false),
       ('cbf7a472-0c5d-42db-9efa-abd5f606225f', 'Николаевский Павел', 29, '+7 (985) 219-47-63', 'driver8@transit.com', 'LIC-1289-2023', 'ACTIVE', false);

INSERT INTO buses
VALUES ('f939d709-6053-4350-9091-71703dfdda67', '54837291045628374651', 'af2c84df-7278-4085-9101-dff3863b7726', '4006780e-ab6d-423a-9d68-d78cf76b0d87', 36, 'бензин', 'ACTIVE', false),
       ('32560fb4-dc74-4a5a-852b-5c5ee97ae34f', '69012384572301985624', '414a381f-b3a7-4bfd-ae3b-77f903ee5df9', 'aa240b42-9e9f-4c22-a040-96d0eb13325c', 28, 'электрический', 'ACTIVE', false),
       ('4cb4c28c-2eec-4aea-9a7d-c61497cdbec0', '31247586903214567890', 'ccc87081-a177-4aaa-9046-002d1a9b44c7', 'aa240b42-9e9f-4c22-a040-96d0eb13325c', 30, 'бензин', 'ACTIVE', false),
       ('266b1ecb-6ce2-4220-a36b-3cf27477c895', '98765432100123456789', 'a2942598-50ae-48f2-8e04-39a7fa223d80', '4006780e-ab6d-423a-9d68-d78cf76b0d87', 31, 'электрический', 'ACTIVE', false);