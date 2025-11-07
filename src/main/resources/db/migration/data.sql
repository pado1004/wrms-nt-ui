-- 샘플 고객 데이터
INSERT INTO customer (name, email, phone, address, created_at, updated_at) VALUES
('김철수', 'kim@example.com', '010-1234-5678', '서울시 강남구', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('이영희', 'lee@example.com', '010-2345-6789', '서울시 서초구', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('박민수', 'park@example.com', '010-3456-7890', '서울시 송파구', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 샘플 상담 데이터
INSERT INTO counseling (customer_name, contact, counseling_type, status, counselor_id, content, created_at, updated_at) VALUES
('김철수', '010-1234-5678', '제품문의', 'PENDING', 1, '신규 제품에 대한 문의입니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('이영희', '010-2345-6789', '불만접수', 'IN_PROGRESS', 1, '배송 지연에 대한 불만입니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('박민수', '010-3456-7890', '기술지원', 'COMPLETED', 2, '제품 사용법에 대한 문의입니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('김철수', '010-1234-5678', '환불요청', 'PENDING', 2, '제품 환불 요청입니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('이영희', '010-2345-6789', '제품문의', 'COMPLETED', 1, '추가 제품 구매 문의입니다.', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
