--liquibase formatted sql

-- changeset vishnunarayananp_tcs@comfortdelgro.com:1 splitStatements:false
-- comment: Added tables and triggers

-- Create a sequence which is used as ID for the table vehicle_comm_scheduler
CREATE SEQUENCE SEQ_vehicle_comm_scheduler__vehicle_comm_scheduler_id
   START 1
   INCREMENT BY 1;

-- Create vehicle_comm_scheduler table
CREATE TABLE IF NOT EXISTS vehicle_comm_scheduler (
   id                        BIGINT PRIMARY KEY DEFAULT nextval('SEQ_vehicle_comm_scheduler__vehicle_comm_scheduler_id'),
   event_name                VARCHAR(30),
   payload                   JSONB,
   remarks                   TEXT,
   status                    VARCHAR(20) NOT NULL CHECK(status IN ('PENDING', 'SUCCESS', 'FAILED')),
   created_dt                TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
   updated_dt                TIMESTAMP
);

COMMENT ON COLUMN vehicle_comm_scheduler.id                       IS 'The id auto increment by sequence SEQ_vehicle_comm_scheduler__vehicle_comm_scheduler_id';
COMMENT ON COLUMN vehicle_comm_scheduler.event_name               IS 'Name of the failed event';
COMMENT ON COLUMN vehicle_comm_scheduler.payload                  IS 'Payload for the failed event';
COMMENT ON COLUMN vehicle_comm_scheduler.remarks                  IS 'Remarks for the scheduled information';
COMMENT ON COLUMN vehicle_comm_scheduler.status                   IS 'Status for the scheduled information';
COMMENT ON COLUMN vehicle_comm_scheduler.created_dt               IS 'Created date and time for Vehicle Comm Service Scheduler';
COMMENT ON COLUMN vehicle_comm_scheduler.updated_dt               IS 'Updated date time for Vehicle Comm Service Scheduler';

-- Create an index for event_name column
CREATE INDEX IDX_vehicle_comm_scheduler__event_name ON vehicle_comm_scheduler (event_name);

-- Create vehicle_comm_scheduler_audit table
CREATE TABLE IF NOT EXISTS vehicle_comm_scheduler_audit (
   id BIGINT PRIMARY KEY,
   event_name              VARCHAR(30),
   payload                   JSONB,
   remarks                   TEXT,
   status                    VARCHAR(20) NOT NULL CHECK(status IN ('PENDING', 'SUCCESS', 'FAILED')),
   created_dt                TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
   updated_dt                TIMESTAMP
);

COMMENT ON COLUMN vehicle_comm_scheduler_audit.id                       IS 'The id auto increment by sequence SEQ_vehicle_comm_scheduler__vehicle_comm_scheduler_id';
COMMENT ON COLUMN vehicle_comm_scheduler.event_name                     IS 'Name of the failed event';
COMMENT ON COLUMN vehicle_comm_scheduler_audit.payload                  IS 'Payload for the failed event';
COMMENT ON COLUMN vehicle_comm_scheduler_audit.remarks                  IS 'Remarks for the scheduled information';
COMMENT ON COLUMN vehicle_comm_scheduler_audit.status                   IS 'Status for the scheduled information';
COMMENT ON COLUMN vehicle_comm_scheduler_audit.created_dt               IS 'Created date and time for Vehicle Comm Service Scheduler';
COMMENT ON COLUMN vehicle_comm_scheduler_audit.updated_dt               IS 'Updated date time for Vehicle Comm Service Scheduler';


-- Create a trigger function
CREATE OR REPLACE FUNCTION audit_vehicle_comm_scheduler()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO ngp_me_vehiclecomm.vehicle_comm_scheduler_audit (
        id,
        event_name,
        payload,
        remarks,
        status,
        created_dt,
        updated_dt
    )
    VALUES (
        OLD.id,
        OLD.event_name,
        OLD.payload,
        OLD.remarks,
        OLD.status,
        OLD.created_dt,
        OLD.updated_dt
    );
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

-- Create a trigger
CREATE TRIGGER trg_audit_vehicle_comm_scheduler
AFTER DELETE ON vehicle_comm_scheduler
FOR EACH ROW
EXECUTE FUNCTION audit_vehicle_comm_scheduler();

-- rollback DROP TRIGGER IF EXISTS trg_audit_vehicle_comm_scheduler ON ngp_me_vehiclecomm.vehicle_comm_scheduler;
-- rollback DROP FUNCTION IF EXISTS ngp_me_vehiclecomm.audit_vehicle_comm_scheduler();
-- rollback DROP TABLE IF EXISTS ngp_me_vehiclecomm.vehicle_comm_scheduler_audit;
-- rollback DROP TABLE IF EXISTS ngp_me_vehiclecomm.vehicle_comm_scheduler;
-- rollback DROP SEQUENCE IF EXISTS ngp_me_vehiclecomm.SEQ_vehicle_comm_scheduler__vehicle_comm_scheduler_id;


