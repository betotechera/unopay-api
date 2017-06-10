package br.com.unopay.api.pamcary.model;

import br.com.unopay.api.model.CargoContract;
import br.com.unopay.api.model.TravelDocument;
import br.com.unopay.api.pamcary.translate.WithKeyFields;
import java.util.List;
import lombok.Data;

@Data
public class TravelDocumentsWrapper {

    @WithKeyFields(listType = TravelDocument.class)
    List<TravelDocument> travelDocuments;

    @WithKeyFields
    CargoContract cargoContract;
}
