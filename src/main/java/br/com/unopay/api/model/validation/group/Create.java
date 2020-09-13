package br.com.unopay.api.model.validation.group;

public interface Create {

    interface Order extends Create {
        interface Adhesion extends Order{}
    }

    interface PersonCreditCard extends Create { }
}
