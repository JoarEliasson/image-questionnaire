ALTER TABLE response
    ADD CONSTRAINT uq_response_inv_img_q UNIQUE (invitation_id, image_id, question_index);
