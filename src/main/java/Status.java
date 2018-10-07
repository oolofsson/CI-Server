class Status {
    private String state;
    private String status_url;
    private String description;
    private String context;

    Status(){

    }
    Status(State state, String status_url, String description, String context) {
        this.state = state.toString();
        this.status_url = status_url;
        this.description = description;
        this.context = context;
    }

    /**
     *
     * @param state S
     * @param description
     */
    Status(State state, String description){
        this.state = state.toString();
        this.description = description;
    }

    /**
     * Gets the variable state
     * @return value of State
     */
    public String getState() {
        return state;
    }

    /**
     * Sets state
     * @param state
     */
    public void setState(State state) {
        this.state = state.toString();
    }

    /**
     * Gets status url
     * @return String
     */
    public String getStatus_url() {
        return status_url;
    }

    /**
     * Sets status url
     * @param status_url
     */
    public void setStatus_url(String status_url) {
        this.status_url = status_url;
    }

    /**
     * Gets description
     * @return String
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets context
     * @return String
     */
    public String getContext() {
        return context;
    }

    /**
     * Sets context
     * @param context
     */
    public void setContext(String context) {
        this.context = context;
    }

    /**
     * Enum containing the valid states for a status message
     */
    public enum State {
        success, failure;
    }
}
