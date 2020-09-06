package com.els.service;

import com.els.model.Output;

public class Computer {
    private Output output;

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public Output getOutput() {
        return output;
    }

    public void setOutput(Output output) {
        this.output = output;
    }

    public void useOut(){
        output.printlnOne();
    }
}
