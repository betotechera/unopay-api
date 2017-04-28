package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptionEnum;

public enum CompanyActivity implements DescriptionEnum {

    AGRICULTURE("Agricultura, pecuária, produção florestal, pesca e aqüicultura"),
    EXTRACTIVE_INDUSTRY("Indústrias extrativas"),
    TRANSFORMATION_INDUSTRY("Indústrias de transformação"),
    ENERGY("Eletricidade e gás"),
    WATER_COMPANY("Água, esgoto, atividades de gestão de resíduos e descontaminação"),
    CONSTRUCTION("Construção"),
    AUTO_REPAIR("Comércio; reparação de veículos automotores e motocicletas"),
    TRANSPORT("Transporte, armazenagem e correio"),
    HOUSING("Alojamento e alimentação"),
    COMMUNICATION("Informação e comunicação"),
    FINANCE("Atividades financeiras, de seguros e serviços relacionados"),
    REAL_ESTATE("Atividades imobiliárias"),
    SCIENTIFIC("Atividades profissionais, científicas e técnicas"),
    ADMINISTRATIVE("Atividades administrativas e serviços complementares"),
    PUBLIC_ADMINISTRATION("Administração pública, defesa e seguridade social"),
    EDUCATION("Educação"),
    HEALTH("Saúde humana e serviços sociais"),
    ARTS("Artes, cultura, esporte e recreação"),
    OTHER("Outras atividades de serviços"),
    DOMESTIC("Serviços domésticos"),
    INTERNATIONAL("Organismos internacionais e outras instituições extraterritoriais");
    private String description;

    CompanyActivity(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
