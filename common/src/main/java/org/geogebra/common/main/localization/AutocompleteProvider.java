package org.geogebra.common.main.localization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.geogebra.common.kernel.Macro;
import org.geogebra.common.kernel.commands.AlgebraProcessor;
import org.geogebra.common.main.App;
import org.geogebra.common.main.GuiManagerInterface;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.debug.Log;

public class AutocompleteProvider {
	private final App app;
	private final boolean forCAS;

	/**
	 * @param app application
	 * @param forCAS whether this is for the classic CAS
	 */
	public AutocompleteProvider(App app, boolean forCAS) {
		this.app = app;
		this.forCAS = forCAS;
	}

	/**
	 * @param command localized command
	 * @return syntaxes of a single command
	 */
	public List<String> getSyntaxes(String command) {
		ArrayList<String> syntaxes = new ArrayList<>();
		addSyntaxes(command, syntaxes);
		return syntaxes;
	}

	/**
	 * Take a list of commands and return all possible syntaxes for these
	 * commands
	 *
	 * @param commands
	 *            commands
	 * @return syntaxes
	 */
	public List<String> getSyntaxes(List<String> commands) {
		if (commands == null) {
			return null;
		}
		ArrayList<String> syntaxes = new ArrayList<>();
		for (String cmd : commands) {
			addSyntaxes(cmd, syntaxes);
		}
		return syntaxes;
	}

	private void addSyntaxes(String cmd, ArrayList<String> syntaxes) {
		String cmdInt = app.getInternalCommand(cmd);
		boolean englishOnly = cmdInt == null
				&& isFallbackCompletionAllowed();

		if (englishOnly) {
			cmdInt = app.englishToInternal(cmd);
		}
		String syntaxString;
		if (forCAS) {
			syntaxString = app.getLocalization()
					.getCommandSyntaxCAS(cmdInt);
		} else {
			AlgebraProcessor ap = app.getKernel().getAlgebraProcessor();
			syntaxString = englishOnly
					? ap.getEnglishSyntax(cmdInt, app.getSettings())
					: ap.getSyntax(cmdInt, app.getSettings());
		}

		if (syntaxString == null) {
			return;
		}

		if (syntaxString.endsWith(Localization.syntaxCAS)
				|| syntaxString.endsWith(Localization.syntaxStr)) {
			// command not found, check for macros
			Macro macro = forCAS ? null
					: app.getKernel().getMacro(cmd);
			if (macro != null) {
				syntaxes.add(macro.toString());
			} else {
				// syntaxes.add(cmdInt + "[]");
				Log.debug("Can't find syntax for: " + cmd);
			}

			return;
		}
		for (String syntax : syntaxString.split("\\n")) {
			syntaxes.add(syntax);
		}
	}

	/**
	 * @return whether to allow English commands as well
	 */
	public boolean isFallbackCompletionAllowed() {
		return "zh".equals(app.getLocalization().getLanguage());
	}

	/**
	 * @param curWord word to be completed
	 * @return stream of suggestions
	 */
	public Stream<Completion> getCompletions(String curWord) {
		Stream str = app.getParserFunctions().getCompletions(curWord).stream()
				.map(function -> new Completion(function.split("\\(")[0],
						Collections.singletonList(function), App.WIKI_OPERATORS,
						GuiManagerInterface.Help.GENERIC));
		List<String> cmdDict = app.getCommandDictionary()
				.getCompletions(curWord.toLowerCase());
		if (cmdDict != null) {
			Stream<Completion> cmdstr = cmdDict.stream()
					.map(command -> new Completion(command, getSyntaxes(command),
							app.getInternalCommand(command), GuiManagerInterface.Help.COMMAND));
			return Stream.concat(str, cmdstr);
		}
		return str;
	}

	public static class Completion {
		public final String command;
		public final List<String> syntaxes;
		public final String helpPage;
		public final GuiManagerInterface.Help helpType;

		private Completion(String command, List<String> syntaxes, String helpPage,
				GuiManagerInterface.Help helpType) {
			this.command = command;
			this.syntaxes = syntaxes;
			this.helpPage = helpPage;
			this.helpType = helpType;
		}
	}
}