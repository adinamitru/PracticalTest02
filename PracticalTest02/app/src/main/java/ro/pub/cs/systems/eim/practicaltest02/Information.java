package ro.pub.cs.systems.eim.practicaltest02;

public class Information {
    String code;
    String rate;
    String description;
    String rate_flow;

    public Information() {
        this.code = null;
        this.rate = null;
        this.description = null;
        this.rate_flow = null;
    }

    public Information(String code, String rate, String description, String rate_flow) {
        this.code = code;
        this.rate = rate;
        this.description = description;
        this.rate_flow = rate_flow;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRate_flow() {
        return rate_flow;
    }

    public void setRate_flow(String rate_flow) {
        this.rate_flow = rate_flow;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Information{");
        sb.append("code='").append(code).append('\'');
        sb.append(", rate='").append(rate).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", rate_flow='").append(rate_flow).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
