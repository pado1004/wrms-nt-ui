-- 샘플 사용자 데이터
INSERT INTO user (username, password, name, email, phone, role, department, is_active, max_concurrent_cases, created_at, updated_at) VALUES
('admin', 'admin123', '관리자', 'admin@wrms.com', '010-0000-0000', 'ADMIN', '본부', TRUE, 20, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('manager1', 'manager123', '김팀장', 'manager1@wrms.com', '010-1111-1111', 'MANAGER', 'CS팀', TRUE, 15, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('counselor1', 'counsel123', '홍길동', 'counselor1@wrms.com', '010-2222-2222', 'COUNSELOR', 'CS팀', TRUE, 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('counselor2', 'counsel123', '이영희', 'counselor2@wrms.com', '010-3333-3333', 'COUNSELOR', 'CS팀', TRUE, 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('counselor3', 'counsel123', '박민수', 'counselor3@wrms.com', '010-4444-4444', 'SENIOR_COUNSELOR', '기술팀', TRUE, 12, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 샘플 고객 데이터
INSERT INTO customer (name, email, phone, address, created_at, updated_at) VALUES
('김철수', 'kim@example.com', '010-1234-5678', '서울시 강남구', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('이영희', 'lee@example.com', '010-2345-6789', '서울시 서초구', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('박민수', 'park@example.com', '010-3456-7890', '서울시 송파구', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 샘플 상담 데이터
INSERT INTO counseling (customer_name, contact, counseling_type, status, priority, counselor_id, assigned_by, original_counselor_id, content, escalation_level, transfer_count, sla_due_date, created_at, updated_at) VALUES
('김철수', '010-1234-5678', '제품문의', 'IN_PROGRESS', 'NORMAL', 3, 2, 3, '신규 제품에 대한 문의입니다.', 0, 0, DATEADD('HOUR', 72, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('이영희', '010-2345-6789', '불만접수', 'ESCALATED', 'HIGH', 2, 2, 3, '배송 지연에 대한 불만입니다.', 1, 2, DATEADD('HOUR', 24, CURRENT_TIMESTAMP), DATEADD('HOUR', -2, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
('박민수', '010-3456-7890', '기술지원', 'RESOLVED', 'NORMAL', 5, 2, 5, '제품 사용법에 대한 문의입니다.', 0, 0, DATEADD('HOUR', 72, CURRENT_TIMESTAMP), DATEADD('HOUR', -5, CURRENT_TIMESTAMP), DATEADD('HOUR', -1, CURRENT_TIMESTAMP)),
('김철수', '010-1234-5678', '환불요청', 'REGISTERED', 'URGENT', 3, 2, 3, '제품 환불 요청입니다.', 0, 0, DATEADD('HOUR', 4, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('이영희', '010-2345-6789', '제품문의', 'CLOSED', 'NORMAL', 4, 2, 4, '추가 제품 구매 문의입니다.', 0, 0, DATEADD('HOUR', 72, CURRENT_TIMESTAMP), DATEADD('DAY', -2, CURRENT_TIMESTAMP), DATEADD('DAY', -1, CURRENT_TIMESTAMP));

-- 샘플 상담 이력 데이터
INSERT INTO counseling_history (counseling_id, action_type, from_status, to_status, from_user_id, to_user_id, comment, reason, performed_by, performed_at) VALUES
(1, 'CREATED', NULL, 'REGISTERED', NULL, NULL, '상담 등록', NULL, 3, DATEADD('MINUTE', -30, CURRENT_TIMESTAMP)),
(1, 'ASSIGNED', 'REGISTERED', 'ASSIGNED', NULL, 3, '홍길동에게 할당', NULL, 2, DATEADD('MINUTE', -25, CURRENT_TIMESTAMP)),
(1, 'STATUS_CHANGED', 'ASSIGNED', 'IN_PROGRESS', NULL, NULL, '처리 시작', NULL, 3, DATEADD('MINUTE', -20, CURRENT_TIMESTAMP)),
(2, 'CREATED', NULL, 'REGISTERED', NULL, NULL, '상담 등록', NULL, 3, DATEADD('HOUR', -3, CURRENT_TIMESTAMP)),
(2, 'TRANSFERRED', 'IN_PROGRESS', 'TRANSFERRED', 3, 4, '기술팀으로 이관', 'SPECIALIST_REQUIRED', 3, DATEADD('HOUR', -2, CURRENT_TIMESTAMP)),
(2, 'ESCALATED', 'TRANSFERRED', 'ESCALATED', 4, 2, '관리자 에스컬레이션', '이관 횟수 초과', 2, DATEADD('HOUR', -1, CURRENT_TIMESTAMP)),
(3, 'CREATED', NULL, 'REGISTERED', NULL, NULL, '상담 등록', NULL, 5, DATEADD('HOUR', -5, CURRENT_TIMESTAMP)),
(3, 'RESOLVED', 'IN_PROGRESS', 'RESOLVED', NULL, NULL, '문제 해결 완료', NULL, 5, DATEADD('HOUR', -1, CURRENT_TIMESTAMP));
