CREATE unique INDEX idx_identified_feedback_response_on_feedback_id_responder_id
    ON identified_feedback_response (feedback_id, responder_id);