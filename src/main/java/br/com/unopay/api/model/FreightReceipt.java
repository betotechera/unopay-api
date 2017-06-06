package br.com.unopay.api.model;

import br.com.unopay.api.bacen.model.Contractor;
import br.com.unopay.api.bacen.model.Establishment;
import br.com.unopay.api.bacen.model.ServiceType;
import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class FreightReceipt implements Serializable {

    public static final long serialVersionUID = 1L;

    public FreightReceipt(){}

    private Contract contract;
    private Contractor contractor;
    private Establishment establishment;
    private ServiceType serviceType;
    private List<TravelDocument> travelDocuments;
    private CargoContract cargoContract;
}
