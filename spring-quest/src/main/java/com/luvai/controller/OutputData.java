package com.luvai.controller;

public class OutputData {

    private String from;
    private String data;
    private String time;

    public OutputData(final String from, final String data, final String time) {

        this.from = from;
        this.data = data;
        this.time = time;
    }

    public String getData() {
        return data;
    }

    public String getTime() {
        return time;
    }

    public String getFrom() {
        return from;
    }
}
