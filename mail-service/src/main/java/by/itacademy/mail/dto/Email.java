package by.itacademy.mail.dto;

public class Email {
    @javax.validation.constraints.Email
    private String address;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
