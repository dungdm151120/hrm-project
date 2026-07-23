ALTER TABLE labor_contracts
    ADD COLUMN union_member TINYINT(1) NOT NULL DEFAULT 0 AFTER note;

UPDATE labor_contracts lc
JOIN user_union_membership uum ON uum.user_id = lc.user_id
SET lc.union_member = uum.is_member;

DROP TABLE user_union_membership;
