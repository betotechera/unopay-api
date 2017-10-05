package br.com.unopay.api.model.validation.group;

public interface Create {

    interface Order {
        interface Adhesion extends Order, Create{}

    }
}
