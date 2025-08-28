CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS study (
                                     id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name TEXT NOT NULL,
    description TEXT,
    question_set_version INT NOT NULL,
    settings JSONB DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    archived_at TIMESTAMPTZ
    );

CREATE TABLE IF NOT EXISTS image (
                                     id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    study_id UUID NOT NULL REFERENCES study(id) ON DELETE CASCADE,
    storage_path TEXT NOT NULL,
    order_index INT NOT NULL,
    label TEXT,
    metadata JSONB DEFAULT '{}'::jsonb
    );

CREATE TABLE IF NOT EXISTS question_set (
                                            version INT PRIMARY KEY,
                                            title TEXT NOT NULL,
                                            created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    is_locked BOOLEAN NOT NULL DEFAULT FALSE
    );

CREATE TABLE IF NOT EXISTS question (
                                        id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    question_set_version INT NOT NULL REFERENCES question_set(version) ON DELETE RESTRICT,
    index_in_set INT NOT NULL CHECK (index_in_set BETWEEN 1 AND 15),
    text TEXT NOT NULL,
    min_label TEXT,
    max_label TEXT,
    UNIQUE (question_set_version, index_in_set)
    );

CREATE TABLE IF NOT EXISTS invitation (
                                          id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    study_id UUID NOT NULL REFERENCES study(id) ON DELETE CASCADE,
    email TEXT NOT NULL,
    code_hash TEXT NOT NULL,
    status TEXT NOT NULL CHECK (status IN ('created','sent','started','completed','expired','bounced')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    sent_at TIMESTAMPTZ,
    first_login_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    expires_at TIMESTAMPTZ,
    last_send_result TEXT
    );

CREATE TABLE IF NOT EXISTS participant_profile (
                                                   id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    invitation_id UUID NOT NULL UNIQUE REFERENCES invitation(id) ON DELETE CASCADE,
    attributes JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
    );

CREATE TABLE IF NOT EXISTS response (
                                        id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    invitation_id UUID NOT NULL REFERENCES invitation(id) ON DELETE CASCADE,
    study_id UUID NOT NULL REFERENCES study(id) ON DELETE CASCADE,
    image_id UUID NOT NULL REFERENCES image(id) ON DELETE CASCADE,
    question_id UUID NOT NULL REFERENCES question(id) ON DELETE RESTRICT,
    question_index INT NOT NULL CHECK (question_index BETWEEN 1 AND 15),
    value INT NOT NULL CHECK (value BETWEEN 1 AND 8),
    answered_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    time_ms INT
    );

CREATE INDEX IF NOT EXISTS idx_resp_inv_img_q ON response (invitation_id, image_id, question_index);
