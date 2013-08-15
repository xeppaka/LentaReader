package cz.fit.lentaruand.parser;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class ParseHelper implements Iterable<List<String>> {
	private String text;
	private Pattern pattern;
	private int groups;
	
	private ParseHelper(String text, Pattern pattern, int groups) {
		this.text = text;
		this.pattern = pattern;
		this.groups = groups;
	}
	
	public static Iterable<List<String>> createParser(String text, Pattern pattern, int groups) {
		if (text == null)
			throw new IllegalArgumentException("Argument text must not be null.");
		
		if (pattern == null)
			throw new IllegalArgumentException("Argument pattern must not be null.");
		
		if (groups <= 0)
			throw new IllegalArgumentException("Argument groups must be greather than 0.");
		
		return new ParseHelper(text, pattern, groups);
	}

	@Override
	public Iterator<List<String>> iterator() {
		if (text.length() <= 0) {
			List<List<String>> emptyList = Collections.emptyList();
			return emptyList.iterator();
		}
		
		return new ParseIterator(pattern.matcher(text), groups);
	}
}
