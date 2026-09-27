package com.sental.bank.model;

public enum TransferStatus {
    DONE("готово"),
    FAILED("завершилось с ошибкой");

    private final String label;

    TransferStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
