package com.hugokindel.common;

import com.hugokindel.common.cli.option.Runnable;
import com.hugokindel.common.cli.option.annotation.Command;
import com.hugokindel.common.cli.print.Out;
import com.hugokindel.common.utility.Resources;

public abstract class BaseProgram extends Runnable {
    /** The name of the game instance. */
    protected static String instanceName = "program";

    /** The game instance. */
    private static BaseProgram instance;

    /** @return the instance's name. */
    public static String getInstanceName() {
        return instanceName;
    }

    @Override
    public int run(String[] args) {
        BaseProgram.instance = this;

        if (getClass().getAnnotation(Command.class) != null) {
            instanceName = getClass().getAnnotation(Command.class).name();
        }

        Out.start(args, false, true);

        if (!readArguments(args, getClass())) {
            return 1;
        }

        initialize();

        int returnValue = programMain(args);

        destroy();

        Out.end();

        return returnValue;
    }

    protected abstract int programMain(String[] args);

    private void initialize() {
        Resources.loadConfig();
    }

    private void destroy() {
        Resources.saveConfig(
                "// This file contains the bot configuration, feel free to edit what you need.\n" +
                "//\n" +
                "// Have fun!\n\n");
    }

    /** @return the game. */
    public static BaseProgram get() {
        return instance;
    }
}
