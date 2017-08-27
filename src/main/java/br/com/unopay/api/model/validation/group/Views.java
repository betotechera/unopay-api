package br.com.unopay.api.model.validation.group;

public interface Views {

    interface Person {}

    interface Address{}

    interface BankAccount {}

    interface AddressList {}

    interface Institution {
        interface Detail extends Person,Address,List {}
        interface List {}
    }

    interface Partner {
        interface Detail extends Person,Address,List,BankAccount {}
        interface List extends AddressList {}
    }

    interface User {
        interface Detail extends List{}
        interface List {}
    }

    interface Event {
        interface Detail extends List{}
        interface List {}
    }

    interface Group {
        interface Detail extends List{}
        interface List {}
    }

    interface Issuer {
        interface Detail extends Person,Address,BankAccount,List {}
        interface List extends AddressList{}
    }

    interface Establishment {
        interface Detail extends Person,Address,BankAccount,List,Contact {}
        interface List extends AddressList{}
        interface Contact {}
    }

    interface EstablishmentEvent {
        interface Detail extends List {}
        interface List {}
    }

    interface Branch {
        interface Detail extends Person,Address,BankAccount,List {}
        interface List extends AddressList{}
    }

    interface Hirer {
        interface Detail extends Person,Address,BankAccount,List {}
        interface List {}
    }

    interface HirerBranch {
        interface Detail extends Person,Address,BankAccount,List {}
        interface List {}
    }

    interface Contract {
        interface Detail extends List,Establishment,Installment {}
        interface Establishment  {}
        interface List {}
        interface Installment {}
    }

    interface Credit {
        interface Detail extends List{}
        interface List {}
    }

    interface CreditPaymentAccount {
        interface Detail extends List{}
        interface List {}
    }

    interface ContractorInstrumentCredit {
        interface Detail extends List {}
        interface List {}
    }

    interface Service {
        interface Detail extends List {}
        interface List {}
    }

    interface Product {
        interface Detail extends List {}
        interface List {}
    }

    interface Contractor {
        interface Detail extends Person,Address,BankAccount,List {}
        interface List {}
    }

    interface PaymentRuleGroup{
        interface Detail extends List{}
        interface List{}
    }

    interface PaymentInstrument{
        interface Detail extends List{}
        interface List {}
    }

    interface AccreditedNetwork {
        interface Detail extends Person,BankAccount,Address,List {}
        interface List {}
    }

    interface ServiceAuthorize {
        interface Detail extends List {}
        interface List {}
    }

    interface BatchClosing {
        interface Detail extends List{}
        interface List {}
    }

    interface Order {
        interface Detail extends Billing{}
        interface List {}
    }

    interface PaymentRemittance {
        interface Detail extends List,Payee,Item, Payer{}
        interface List {}
        interface Payee {}
        interface Payer {}
        interface Item {}
    }

    interface Billing {
        interface Detail {}
        interface List{}
    }

}
