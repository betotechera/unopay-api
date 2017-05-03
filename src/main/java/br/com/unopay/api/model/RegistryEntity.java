package br.com.unopay.api.model;

import br.com.unopay.api.http.DescriptableEnum;

public enum RegistryEntity implements DescriptableEnum {

    SSP("Secretaria de Segurança Pública"),
    MMA("Ministério da Marinha"),
    DIC("Diretoria de Identificação Civil"),
    POF("Polícia Federal"),
    IFP("Instituto Félix Pacheco"),
    POM("Polícia Militar"),
    IPF("Instituto Pereira Faustino"),
    SES("Carteira de Estrangeiro"),
    MAE("Ministério da Aeronaútica"),
    MEX("Ministério de Exército"),
    DETRAN("Departamento de Trânsito"),
    CNH("Carteira Nacional de Habilitação");

    private String description;

    RegistryEntity(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
