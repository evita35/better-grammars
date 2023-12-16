package compression.parser;

import compression.grammar.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GrammarReaderNWriter {
	private final File grammarFile;

	public GrammarReaderNWriter(File folderPath, SecondaryStructureGrammar theGrammar) {
		grammarFile = new File(folderPath, theGrammar.name + ".txt");
	}

	public GrammarReaderNWriter(String grammarFilePath) {
		grammarFile = new File(grammarFilePath);
	}

	private enum ReadState {
		GRAMMARNAME, STARTSYMBOL, NONTERMINALS, RULES, TERMINALS
	}

	public SecondaryStructureGrammar getGrammarFromFile() throws FileNotFoundException {
		//Grammar<PairOfChar> grammar;
		String grammarName = "";
		String startSymbol = "";
		List<NonTerminal> nonTerminals = new ArrayList<>();
		List<CharTerminal> terminals = new ArrayList<>();
		List<Rule> arrayListOfRules = new ArrayList<>();

		try (BufferedReader in = new BufferedReader(
				new InputStreamReader(new FileInputStream(grammarFile), StandardCharsets.UTF_8))) {
			String line;
			ReadState readState = null;

			while ((line = in.readLine()) != null) {
				switch (line.toLowerCase()) {
					case ("grammarname"):
						readState = ReadState.GRAMMARNAME;
						break;
					case ("start symbol"):
						readState = ReadState.STARTSYMBOL;
						break;
					case ("non-terminals"):
						readState = ReadState.NONTERMINALS;
						break;
					case ("rules"):
						readState = ReadState.RULES;
						break;
					case ("terminals"):
						readState = ReadState.TERMINALS;
						break;
					default:
						break;
				}
				if (readState == ReadState.GRAMMARNAME && !line.equalsIgnoreCase("grammarname") && !line.isBlank()) {
					grammarName = line;
				} else if (readState == ReadState.STARTSYMBOL && !line.equalsIgnoreCase("start symbol") && !line.isBlank()) {
					startSymbol = line;
				} else if (readState == ReadState.NONTERMINALS && !line.equalsIgnoreCase("non-terminals") && !line.isBlank()) {
					nonTerminals.add(new NonTerminal("" + line));
				} else if (readState == ReadState.TERMINALS && !line.equalsIgnoreCase("terminals") && !line.isBlank()) {
					terminals.add(new CharTerminal(line.charAt(0)));
				} else if (readState == ReadState.RULES && !line.equalsIgnoreCase("rules") && !line.isBlank()) {
					arrayListOfRules.add(Rule.create(getLHS(line, nonTerminals),
							getRHS(line, nonTerminals, terminals)));
				}
			}
			//
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		Grammar.Builder<Character> grammarFromFile = new Grammar.Builder<>(grammarName, new NonTerminal(startSymbol));
		for (Rule rule : arrayListOfRules) {
			grammarFromFile.addRule(rule);
		}


		SecondaryStructureGrammar GrammarFromFile = SecondaryStructureGrammar.fromCheap(grammarFromFile.build());
		return GrammarFromFile;
	}

	public File getFileName() {
		return grammarFile;
	}

	public void writeGrammarToFile(SecondaryStructureGrammar grammar) throws IOException {
		//File grammarFile = new File(LocalConfig.GIT_ROOT+"/src/GrammarGenerator/ParsableGrammars/"+grammar.name+".txt");
		try (BufferedWriter bf = new BufferedWriter(new FileWriter(grammarFile))) {
			bf.write("GrammarName");
			bf.newLine();
			bf.write(grammar.name);
			bf.newLine();
			bf.newLine();
			bf.write("Start Symbol");
			bf.newLine();
			bf.write(grammar.startSymbol.name);
			bf.newLine();
			bf.newLine();
			bf.write("Non-Terminals");
			bf.newLine();
			for (Object nt : grammar.getNonTerminals()) {
				bf.write(nt.toString());
				bf.newLine();
			}
			bf.newLine();
			bf.write("Terminals");
			bf.newLine();
			for (Terminal<Character> t : grammar.terminals) {
//                System.out.println("value of t is "+ t.toString());
				bf.write(t.getChars());
				bf.newLine();
			}
			bf.newLine();
			bf.write("Rules");
			bf.newLine();
			for (Rule rule : grammar.getAllRules()) {
				bf.write(rule.left + " " + "->" + " ");
				for (Category c : rule.right) {
					bf.write(c.toString());
					bf.write(' ');
				}
				bf.newLine();
			}
		}
	}


	public NonTerminal getLHS(String line, List<NonTerminal> nonTerminals) {
		String buildTheString = "";
		for (char i : line.toCharArray()) {
			if (i != '-' && i != ' ' && i != '→') {
				buildTheString += i; // only short names, hence fine to append to string
			} else
				for (NonTerminal nt : nonTerminals) {
					if (nt.toString().equals(buildTheString)) {
						return nt;
					}
				}

		}
		return null;
	}

	public Category[] getRHS(String line, List<NonTerminal> nonTerminals, List<CharTerminal> terminals) {
		List<Category> categoryList = new ArrayList<>();
		boolean rhsBegins = false;
		String buildTheString = "";
		for (char i : line.toCharArray()) {
			if (i == '>') {
				rhsBegins = true;
			}
			if (rhsBegins) {
				if (i != ' ' && i != '>') {
					buildTheString += i;
					for (NonTerminal nt : nonTerminals) {//checks if it is a nonterminal
						if (nt.toString().equals(buildTheString)) {
							categoryList.add(nt);
						}
					}
					for (CharTerminal t : terminals) {//checks if it is a terminal
						if (t.getChars() == buildTheString.charAt(0)) {
							categoryList.add(t);
						}
					}
				} else {
					buildTheString = ""; // reset to read next category
				}

			}//end if
		}
		return categoryList.toArray(new Category[0]);
	}

}
