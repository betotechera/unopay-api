package br.com.unopay.api.model.validation.group;

public interface Views {
    interface Public {}

    interface Internal extends Public {}

    interface List {}

    interface GroupUserType {}

    interface Person {}

    interface Address{}
    interface BankAccount {}

    interface Institution {
        interface Detail extends Person,Address {}
        interface List {}
    }

    interface AccreditedNetwork {
        interface Detail extends Person,BankAccount,Address {}
        interface List {}
    }

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
