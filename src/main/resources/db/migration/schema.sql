-- 사용자 테이블
CREATE TABLE IF NOT EXISTS user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(50),
    role VARCHAR(50) NOT NULL,  -- COUNSELOR, SENIOR_COUNSELOR, MANAGER, ADMIN
    department VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    max_concurrent_cases INT DEFAULT 10,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 고객 테이블
CREATE TABLE IF NOT EXISTS customer (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(50),
    address VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 상담 테이블 (확장)
CREATE TABLE IF NOT EXISTS counseling (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,

    -- 기본 정보
    customer_name VARCHAR(100) NOT NULL,
    contact VARCHAR(50),
    counseling_type VARCHAR(50),
    content TEXT,

    -- 상태 및 우선순위
    status VARCHAR(20) DEFAULT 'REGISTERED',  -- REGISTERED, ASSIGNED, IN_PROGRESS, PENDING_INFO, TRANSFERRED, ESCALATED, RESOLVED, CLOSED, CANCELLED
    priority VARCHAR(20) DEFAULT 'NORMAL',  -- URGENT, HIGH, NORMAL, LOW

    -- 담당자 정보
    counselor_id BIGINT,
    assigned_by BIGINT,
    original_counselor_id BIGINT,

    -- 에스컬레이션 정보
    escalation_level INT DEFAULT 0,
    escalated_at TIMESTAMP,
    escalated_by BIGINT,
    escalation_reason TEXT,

    -- 이관 정보
    transfer_count INT DEFAULT 0,
    last_transferred_at TIMESTAMP,

    -- SLA 정보
    sla_due_date TIMESTAMP,
    first_response_at TIMESTAMP,
    resolved_at TIMESTAMP,

    -- 해결 내용
    resolution TEXT,

    -- 메타데이터
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- 외래 키
    FOREIGN KEY (counselor_id) REFERENCES user(id),
    FOREIGN KEY (assigned_by) REFERENCES user(id),
    FOREIGN KEY (original_counselor_id) REFERENCES user(id),
    FOREIGN KEY (escalated_by) REFERENCES user(id)
);

-- 상담 이력 테이블
CREATE TABLE IF NOT EXISTS counseling_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    counseling_id BIGINT NOT NULL,
    action_type VARCHAR(50) NOT NULL,  -- CREATED, ASSIGNED, STATUS_CHANGED, TRANSFERRED, ESCALATED, COMMENTED, RESOLVED, CLOSED

    -- 액션 정보
    from_status VARCHAR(20),
    to_status VARCHAR(20),
    from_user_id BIGINT,
    to_user_id BIGINT,

    -- 상세 정보
    comment TEXT,
    reason VARCHAR(50),

    -- 메타데이터
    performed_by BIGINT NOT NULL,
    performed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- 외래 키
    FOREIGN KEY (counseling_id) REFERENCES counseling(id),
    FOREIGN KEY (from_user_id) REFERENCES user(id),
    FOREIGN KEY (to_user_id) REFERENCES user(id),
    FOREIGN KEY (performed_by) REFERENCES user(id)
);

-- 인덱스 생성
CREATE INDEX IF NOT EXISTS idx_counseling_status ON counseling(status);
CREATE INDEX IF NOT EXISTS idx_counseling_priority ON counseling(priority);
CREATE INDEX IF NOT EXISTS idx_counseling_counselor_id ON counseling(counselor_id);
CREATE INDEX IF NOT EXISTS idx_counseling_created_at ON counseling(created_at);
CREATE INDEX IF NOT EXISTS idx_counseling_sla_due_date ON counseling(sla_due_date);
CREATE INDEX IF NOT EXISTS idx_counseling_escalation_level ON counseling(escalation_level);

CREATE INDEX IF NOT EXISTS idx_customer_email ON customer(email);
CREATE INDEX IF NOT EXISTS idx_customer_phone ON customer(phone);

CREATE INDEX IF NOT EXISTS idx_user_username ON user(username);
CREATE INDEX IF NOT EXISTS idx_user_role ON user(role);
CREATE INDEX IF NOT EXISTS idx_user_is_active ON user(is_active);

CREATE INDEX IF NOT EXISTS idx_counseling_history_counseling_id ON counseling_history(counseling_id);
CREATE INDEX IF NOT EXISTS idx_counseling_history_performed_by ON counseling_history(performed_by);
CREATE INDEX IF NOT EXISTS idx_counseling_history_action_type ON counseling_history(action_type);
