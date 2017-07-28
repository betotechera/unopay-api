package br.com.unopay.api.model.validation.group;

public interface Views {
    interface Public {}

    interface Internal extends Public {}

    interface List {}

    interface GroupUserType {}

    interface ServiceAuthorize {
        interface Detail {}
        interface List {}
    }

    interface BatchClosing {
        interface Detail {}
        interface List {}
    }

    interface PaymentRemittance {
        interface Detail {}
        interface List {}
    }

}
