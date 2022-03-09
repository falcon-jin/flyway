package org.flywaydb.core.api.output;

import java.util.LinkedList;

public class CompositeResult extends OperationResultBase {
    public LinkedList<OperationResultBase> individualResults = new LinkedList<>();
}