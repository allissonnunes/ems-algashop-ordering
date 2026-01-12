package br.dev.allissonnunes.algashop.ordering.infrastructure.shipping.client.rapidex;

public record DeliveryCostRequest(String originZipCode, String destinationZipCode) {

}
