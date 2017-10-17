package br.com.unopay.api.model.validation.group;

public interface Views {

    interface Address{}

    interface AddressList {}

    interface BankAccount {}

    interface Person {
        interface Detail extends Address, List {}
        interface List {}
    }


    interface AccreditedNetwork {
        interface Detail extends Person,BankAccount,Address,List {}
        interface List {}
    }

    interface BatchClosing {
        interface Detail extends List{}
        interface List {}
    }

    interface Branch {
        interface Detail extends Person,Address,BankAccount,List {}
        interface List extends AddressList{}
    }

    interface Contract {
        interface Detail extends List,Establishment,Installment {}
        interface Establishment  {}
        interface List extends Product.List {}
        interface Installment {}
    }

    interface Contractor {
        interface Detail extends Person.Detail,Address,BankAccount,List {}
        interface List {}
    }

    interface ContractorInstrumentCredit {
        interface Detail extends List {}
        interface List {}
    }

    interface InstrumentBalance {
        interface Detail extends List {}
        interface List {}
    }

    interface Credit {
        interface Detail extends List{}
        interface List {}
    }

    interface CreditPaymentAccount {
        interface Detail extends List{}
        interface List {}
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

    interface Event {
        interface Detail extends List{}
        interface List {}
    }

    interface Group {
        interface Detail extends List{}
        interface List {}
    }

    interface Hirer {
        interface Detail extends Person,Address,BankAccount,List {}
        interface List {}
    }

    interface HirerBranch {
        interface Detail extends Person,Address,BankAccount,List {}
        interface List {}
    }

    interface Institution {
        interface Detail extends Person,Address,List {}
        interface List {}
    }

    interface Issuer {
        interface Detail extends Person,Address,BankAccount,List {}
        interface List extends AddressList{}
    }

    interface Partner {
        interface Detail extends Person,Address,List,BankAccount {}
        interface List extends AddressList {}
    }

    interface PaymentInstrument{
        interface Detail extends List{}
        interface List {}
        interface Private extends Detail {}
    }

    interface PaymentRemittance {
        interface Detail extends List,Payee,Item, Payer{}
        interface List {}
        interface Payee {}
        interface Payer {}
        interface Item {}
    }

    interface PaymentRuleGroup{
        interface Detail extends List{}
        interface List{}
    }

    interface Product {
        interface Detail extends List {}
        interface List {}
        interface Partner {}
    }

    interface Service {
        interface Detail extends List {}
        interface List {}
    }

    interface ServiceAuthorize {
        interface Detail extends List {}
        interface List {}
    }

    interface User {
        interface Detail extends List, Person.Detail{}
        interface List {}
        interface Private extends Detail {}
    }

    interface Order {
        interface Detail extends Billing, List, Person.Detail {}
        interface List {}
        interface Private extends Detail {}
    }

    interface Billing {
        interface Detail {}
        interface List{}
    }

    interface Boleto {
        interface Detail {}
        interface List{}
    }

}
