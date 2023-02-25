package by.itacademy.mail.scheduler.dto;

public class LoginReportNameEmailDto {
    private String login;
    private String reportName;
    private String email;

    private LoginReportNameEmailDto() {}

    public String getLogin() {
        return login;
    }

    public String getReportName() {
        return reportName;
    }

    public String getEmail() {
        return email;
    }

    public static class Builder {
        private final LoginReportNameEmailDto dto;

        private Builder() {
            dto = new LoginReportNameEmailDto();
        }

        public Builder setLogin(String login) {
            dto.login = login;
            return this;
        }

        public Builder setReportName(String reportName) {
            dto.reportName = reportName;
            return this;
        }

        public Builder setEmail(String email) {
            dto.email = email;
            return this;
        }

        public LoginReportNameEmailDto build() {
            return dto;
        }

        public static Builder createBuilder() {
            return new Builder();
        }
    }
}
