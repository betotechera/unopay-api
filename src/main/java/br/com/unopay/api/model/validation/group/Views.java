package br.com.unopay.api.model.validation.group;

public interface Views {
    interface Public {}

    interface Internal extends Public {}

    interface List {}

    interface GroupUserType {}

    interface BatchClosing {
        interface Detail {}
        interface List {}
    }
}
