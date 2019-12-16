
/**
 * 最长公共子字符串普通实现,没有基于动态编程算法实现
 *
 */
public class LongestCommonSubString {
	private int step;
	private int minStep;
	private int maxStep;
	private int step_mode;
	private String s1;
	private String s2;

	private void init() {
		this.minStep = this.step = this.step_mode = 1;
		this.maxStep = s2.length();
	}

	public String find(String seq_a, String seq_b) {
		this.s1 = seq_a;
		this.s2 = seq_b;
		String longestCommons = "";
		boolean b = (s1.length() < s2.length()) ? true : false;
		if (b) {
			String s = s1;
			s1 = s2;
			s2 = s;
		}
		int begin = 0;
		while (begin < s2.length()) {
			init();
			int commonLen = getCSLength(begin);
			if (commonLen > 0) {
				String commonSeq = s2.substring(begin, begin + commonLen);
				begin += commonLen;
				if (commonSeq.length() > longestCommons.length())
					longestCommons = commonSeq;
			} else {
				begin += 1;
			}
		}
		return longestCommons;
	}

	private int getCSLength(int begin) {
		int len = 0;
		int start = begin;
		boolean stopped = false;
		while (!stopped) {
			String s = null;
			int index = begin + step;
			if (index < s2.length()) {
				s = s2.substring(begin, index);
				if (s1.contains(s)) {
					start = index;
					step = (step > maxStep) ? maxStep : 2 * step;
				} else {
					int increment = step + begin - start;
					step = (increment == minStep) ? minStep : increment / 2;
					step_mode = 0;
					stopped = true;
				}
			} else {
				s = s2.substring(begin);
				if (s1.contains(s)) {
					stopped = true;
					len = s.length();
				} else {
					int increment = s2.length() - start;
					step = (increment == minStep) ? minStep : increment / 2;
					step_mode = 0;
					stopped = true;
				}
			}
		}
		if (step_mode == 0) {
			len = getCSLength(start, step, begin);
		}
		return len;
	}

	private int getCSLength(int start, int step, int begin) {
		int offset = 0;
		boolean stopped = false;
		while (!stopped) {
			String s = s2.substring(begin, start + step);
			if (s1.contains(s)) {
				start += step;
				step = (step == minStep) ? minStep : step / 2;
			} else {
				if (step == 1) {
					stopped = true;
					offset = s.length() - 1;
				}
				step = (step == minStep) ? minStep : step / 2;
			}
		}
		return offset;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String s1 = "dynamictutorialProgramming";
		String s2 = "tutorialhorizon";
		LongestCommonSubString instance = new LongestCommonSubString();
		System.out.print(instance.find(s1, s2));
	}

}
