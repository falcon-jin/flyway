package org.flywaydb.commandline.command.version;

import lombok.CustomLog;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.output.OperationResultBase;
import org.flywaydb.core.extensibility.CommandExtension;
import org.flywaydb.core.internal.license.VersionPrinter;
import org.flywaydb.core.internal.util.Pair;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@CustomLog
public class VersionCommandExtension implements CommandExtension {
    public static final String VERSION = "version";
    public static final List<String> FLAGS = Arrays.asList("-v", "--version");

    @Override
    public boolean handlesCommand(String command) {
        return command.equals(VERSION);
    }

    public String getCommandForFlag(String flag) {
        if (FLAGS.contains(flag.toLowerCase())) {
            return VERSION;
        }
        return CommandExtension.super.getCommandForFlag(flag);
    }

    @Override
    public boolean handlesParameter(String parameter) {
        return false;
    }

    @Override
    public OperationResultBase handle(String command, Map<String, String> config, List<String> flags) throws FlywayException {
        VersionPrinter.printVersionOnly();
        LOG.info("");

        LOG.debug("Java " + System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ")");
        LOG.debug(System.getProperty("os.name") + " " + System.getProperty("os.version") + " " + System.getProperty("os.arch") + "\n");

        return null;
    }

    @Override
    public List<Pair<String, String>> getUsage() {
        return Collections.singletonList(Pair.of(VERSION + ", " + String.join(", ", FLAGS), "Print the Flyway version and edition"));
    }
}