package org.flywaydb.core.api.output;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UndoOutput {
    public String version;
    public String description;
    public String filepath;
    public int executionTime;
}