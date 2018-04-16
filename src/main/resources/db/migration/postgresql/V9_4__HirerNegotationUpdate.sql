UPDATE ONLY hirer_negotiation 
  SET issuer_document_number =
    (SELECT 
      (SELECT (SELECT document_number FROM person WHERE id = issuer.person_id) 
       FROM issuer WHERE id = product.issuer_id)
     FROM product WHERE id = hirer_negotiation.product_id);

UPDATE ONLY hirer_negotiation
  SET hirer_document_number =
    (SELECT (SELECT document_number FROM person WHERE id = hirer.person_id)
     FROM hirer WHERE id = hirer_negotiation.hirer_id);