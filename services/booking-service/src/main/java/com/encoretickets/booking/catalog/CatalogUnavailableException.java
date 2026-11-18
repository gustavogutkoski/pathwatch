package com.encoretickets.booking.catalog;

public class CatalogUnavailableException extends RuntimeException {

    public CatalogUnavailableException(Throwable cause) {
        super("Event catalog is unavailable", cause);
    }
}