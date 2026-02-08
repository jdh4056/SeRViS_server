-- MSA 멀티모듈용 스키마 초기화
-- 각 서비스가 독립적인 논리 스키마를 사용합니다.

CREATE DATABASE IF NOT EXISTS serve_auth_db;
CREATE DATABASE IF NOT EXISTS serve_team_db;
CREATE DATABASE IF NOT EXISTS serve_core_db;

-- serve_user 에게 모든 스키마 접근 권한 부여
GRANT ALL PRIVILEGES ON serve_auth_db.* TO 'serve_user'@'%';
GRANT ALL PRIVILEGES ON serve_team_db.* TO 'serve_user'@'%';
GRANT ALL PRIVILEGES ON serve_core_db.* TO 'serve_user'@'%';

FLUSH PRIVILEGES;
