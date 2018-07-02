package example.util;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic.Kind;

public final class Logger {
	private static Messager messager;

	private Logger() {
	}

	public static void init(final Messager msger) {
		messager = msger;
	}

	public static void note(final String message) {
		messager.printMessage(Kind.NOTE, message);
	}

	public static void error(final String message) {
		messager.printMessage(Kind.ERROR, message);
	}

}
