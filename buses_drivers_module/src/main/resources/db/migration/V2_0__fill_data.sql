INSERT INTO departments
VALUES ('aa240b42-9e9f-4c22-a040-96d0eb13325c', 'Департамент №1', 'Москва, ул. Садовая, 1', false),
       ('4006780e-ab6d-423a-9d68-d78cf76b0d87', 'Департамент №2', 'Москва, Красногвардейский проезд, 21', false);

INSERT INTO drivers
VALUES ('9773a7ca-4413-4b12-9819-b280df8736b1', 'Mo:9-17; Tu:9-17; We:9-17; Th:9-17; Fr:9-16; Sa:OFF; Su:OFF', 'Никитников Ринат', 41, '+7 (915) 264-85-93', 'driver1@transit.com', 'LIC-7845-2023', 'ACTIVE', false),
       ('9a21f5fc-d1ae-420d-a2ad-355c0a5c62b9', 'Mo:10-14,15-19; Tu:8-12; We:OFF; Th:11-15,16-20; Fr:9-13; Sa:8-12; Su:OFF', 'Хусид Глеб', 36, '+7 (925) 731-49-06', 'driver2@transit.com', 'LIC-9012-2023', 'ACTIVE', false),
       ('64a398b9-1473-46a1-9659-41bb5ec03780', 'Mo:14-22; Tu:15-23; We:14-22; Th:OFF; Fr:16-24; Sa:18-23; Su:OFF', 'Зарубин Тимофей', 53, '+7 (916) 408-27-15', 'driver3@transit.com', 'LIC-3567-2023', 'ACTIVE', false),
       ('1755fc36-14d9-4b1f-a34a-818c09b078eb', 'Mo:9-12; Tu:OFF; We:9-12; Th:OFF; Fr:9-12; Sa:OFF; Su:OFF', 'Гилев Юрий', 29, '+7 (903) 156-84-72', 'driver4@transit.com', 'LIC-6823-2023', 'ACTIVE', false),
       ('10d829a6-ee7a-4470-96c9-0ee50623587c', 'Mo:7-10,16-19; Tu:7-10,16-19; We:7-10; Th:7-10,14-17; Fr:7-9; Sa:OFF; Su:9-12', 'Волков Артем', 28, '+7 (977) 342-91-58', 'driver5@transit.com', 'LIC-4591-2023', 'ACTIVE', false),
       ('ff740762-33fa-40f2-a1c0-bd2724a1009b', 'Mo:22-6; Tu:OFF; We:22-6; Th:OFF; Fr:22-8; Sa:20-4; Su:OFF', 'Белов Анатолий', 34, '+7 (999) 675-23-40', 'driver6@transit.com', 'LIC-2378-2023', 'ACTIVE', false),
       ('09d9cb4d-e5e6-4fbd-a093-bcdbb0386f7a', 'Mo:8-20; Tu:OFF; We:8-20; Th:OFF; Fr:8-20; Sa:OFF; Su:10-15', 'Сухарев Игорь', 37, '+7 (495) 782-61-34', 'driver7@transit.com', 'LIC-5642-2023', 'ACTIVE', false),
       ('cbf7a472-0c5d-42db-9efa-abd5f606225f', 'Mo:7-9,11-13,15-17; Tu:9-11; We:OFF; Th:14-18; Fr:8-10,12-14; Sa:10-13; Su:OFF', 'Николаевский Павел', 29, '+7 (985) 219-47-63', 'driver8@transit.com', 'LIC-1289-2023', 'ACTIVE', false);

INSERT INTO buses
VALUES ('f939d709-6053-4350-9091-71703dfdda67', '54837291045628374651', 'af2c84df-7278-4085-9101-dff3863b7726', '4006780e-ab6d-423a-9d68-d78cf76b0d87', 36, 'бензин', 'ACTIVE', false),
       ('32560fb4-dc74-4a5a-852b-5c5ee97ae34f', '69012384572301985624', '414a381f-b3a7-4bfd-ae3b-77f903ee5df9', 'aa240b42-9e9f-4c22-a040-96d0eb13325c', 28, 'электрический', 'ACTIVE', false),
       ('4cb4c28c-2eec-4aea-9a7d-c61497cdbec0', '31247586903214567890', 'ccc87081-a177-4aaa-9046-002d1a9b44c7', 'aa240b42-9e9f-4c22-a040-96d0eb13325c', 30, 'бензин', 'ACTIVE', false),
       ('266b1ecb-6ce2-4220-a36b-3cf27477c895', '98765432100123456789', 'a2942598-50ae-48f2-8e04-39a7fa223d80', '4006780e-ab6d-423a-9d68-d78cf76b0d87', 31, 'электрический', 'ACTIVE', false);