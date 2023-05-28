package com.ap.spotify.shared;

import java.io.Serializable;

public class Request implements Serializable {
    private String command;
    private String json = null;

    public Request(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
