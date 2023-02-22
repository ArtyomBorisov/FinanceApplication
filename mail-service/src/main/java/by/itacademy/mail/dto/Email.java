package by.itacademy.mail.dto;

import javax.validation.constraints.NotBlank;

public class Email {
    @javax.validation.constraints.Email(regexp = ".+@.+\\..+")
    @NotBlank
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
