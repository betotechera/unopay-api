package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum CompanyActivity implements DescriptableEnum {

    AGRICULTURE("Agricultura, pecuaria, producao florestal, pesca e aquicultura"),
    EXTRACTIVE_INDUSTRY("Industrias extrativas"),
    TRANSFORMATION_INDUSTRY("Industrias de transformacao"),
    ENERGY("Eletricidade e gas"),
    WATER_COMPANY("Agua, esgoto, atividades de gestao de resíduos e descontaminacao"),
    CONSTRUCTION("Construcao"),
    AUTO_REPAIR("Comércio; reparacao de veículos automotores e motocicletas"),
    TRANSPORT("Transporte, armazenagem e correio"),
    HOUSING("Alojamento e alimentacao"),
    COMMUNICATION("Informacao e comunicacao"),
    FINANCE("Atividades financeiras, de seguros e servicos relacionados"),
    REAL_ESTATE("Atividades imobiliarias"),
    SCIENTIFIC("Atividades profissionais, científicas e técnicas"),
    ADMINISTRATIVE("Atividades administrativas e servicos complementares"),
    PUBLIC_ADMINISTRATION("Administracao publica, defesa e seguridade social"),
    EDUCATION("Educacao"),
    HEALTH("Saude humana e servicos sociais"),
    ARTS("Artes, cultura, esporte e recreacao"),
    OTHER("Outras atividades de servicos"),
    DOMESTIC("Servicos domésticos"),
    INTERNATIONAL("Organismos internacionais e outras instituicões extraterritoriais");
    private String description;

    CompanyActivity(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
