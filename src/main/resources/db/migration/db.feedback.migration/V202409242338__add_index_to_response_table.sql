CREATE unique INDEX idx_identified_feedback_response_on_responder_id_feedback_id
    ON identified_feedback_response (feedback_id, responder_id);