package com.xeppaka.lentareader.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;

public class ParseIterator implements Iterator<List<String>> {
	private Matcher matcher;
	private int groups;
	private boolean next;
	
	private void tryFind() {
		next = matcher.find();
	}
	
	public ParseIterator(Matcher matcher, int groups) {
		if (matcher == null)
			throw new IllegalArgumentException("Argument matcher must not be null.");

		if (groups <= 0)
			throw new IllegalArgumentException("Argument groups must be greather than 0.");
		
		this.matcher = matcher;
		this.groups = groups;
		tryFind();
	}
	
	@Override
	public boolean hasNext() {
		return next;
	}

	@Override
	public List<String> next() {
		if (!next)
			throw new NoSuchElementException();

		try {
			List<String> res = new ArrayList<String>(groups);
			
			for (int i = 0; i <= groups; ++i)
				res.add(matcher.group(i));
			
			return res;
		} finally {
			tryFind();
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("This method is not impemented.");
	}
}
