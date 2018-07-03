package example.util;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

public final class Logger {
	private static Messager messager;

	private Logger() {
	}

	public static void init(final Messager msger) {
		messager = msger;
	}

	public static void note(final String message) {
		messager.printMessage(Diagnostic.Kind.NOTE, message);
		// System.out.println(message);
	}

	public static void error(final String message) {
		messager.printMessage(Diagnostic.Kind.ERROR, message);
		// System.out.println(message);
	}

}
