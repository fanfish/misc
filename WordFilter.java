package com.moe.oa.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class WordFilter {
	private HashMap wordsMap = new HashMap();
	private boolean isMaxMatch = false;// isMaxMatch true:最大长度匹配 false:最小长度匹配

	public WordFilter(String filename, boolean isMaxMatch) {
		init(filename);
		this.isMaxMatch = isMaxMatch;
	}

	private void init(String filename) {
		String original = null;
		try {
			original = readContent(filename);
			addWords(original.split(","));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addWords(String[] keywords) throws Exception {
		if (keywords == null)
			throw new Exception();
		for (String word : keywords) {
			char[] charword = word.toCharArray();
			HashMap start = wordsMap;
			for (int j = 0; j < charword.length; j++) {
				char chword = charword[j];
				if (!start.containsKey(chword)) {
					start.put(chword, new HashMap());
					if (!start.containsKey("isEnd"))
						start.put("isEnd", "0");
				}
				start = (HashMap) start.get(chword);
				if (j == word.length() - 1)
					start.put("isEnd", "1");
			}
		}
	}

	private Set<String> findWords(String text) {
		Set<String> wordset = new HashSet<String>();
		int from = 0;
		do {
			int to = segment(text, from);
			if (to != -1) {
				wordset.add(text.substring(from, to));
				from = to;
			} else {
				from += 1;
			}
		} while (from < text.length());
		return wordset;
	}

	private int segment(String text, int from) {
		int index = -1;
		HashMap start = wordsMap;
		for (int i = from; i < text.length(); i++) {
			char chword = text.charAt(i);
			if (start.containsKey(chword)) {
				if (!isMaxMatch) {
					if (start.get("isEnd").equals("1"))
						return i;
				}
				start = (HashMap) start.get(chword);
			} else {
				if (start.get("isEnd").equals("1"))
					index = i;
				break;
			}
		}
		return index;
	}

	private String readContent(String filename) throws IOException {
		FileInputStream fis = new FileInputStream(filename);
		FileChannel fc = fis.getChannel();
		// Read the contents of a file into a ByteBuffer
		ByteBuffer bb = ByteBuffer.allocate((int) fc.size());
		fc.read(bb);
		fc.close();
		// Convert ByteBuffer to one long String
		return new String(bb.array());
	}

	public static void main(String[] args) {
		WordFilter filter = new WordFilter(
				"D:/eclipse/workspace/moeOA/src/keywords_utf8.txt", true);
		String text = "中国记者无国界组织,什么好的搞暴乱呵呵.";
		Set<String> wordset = filter.findWords(text);
		Iterator<String> iter = wordset.iterator();
		while (iter.hasNext()) {
			System.out.println("keywords:" + iter.next());
		}
	}
}
