/*
 * implementation based on http://rails-oceania.googlecode.com/svn/lachiecox/qs_score/trunk/qs_score.js
 * The MIT License Copyright (c) 2008 Lachie Cox
 *
 * original objective-c can be found on http://docs.blacktree.com/quicksilver/development/string_ranking
 * javascript is more understandable than objective-c to me ^^
 */

//
// The MIT License
//
// Copyright (c) 2010 Ka-shing Chan
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in
// all copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
//

public class QSString {

	private String str;

	public QSString(String s) {
		str = s.toLowerCase();
	}

	public int length() {
		return str.length();
	}

	public QSString substring(int begin) {
		return new QSString(str.substring(begin));
	}

	public QSString substring(int begin, int end) {
		return new QSString(str.substring(begin, end));
	}

	public int indexOf(QSString other) {
		return str.indexOf(other.str);
	}

	public char charAt(int index) {
		return str.charAt(index);
	}



	public float score(QSString abbr) {
		return this.score(abbr, 0);
	}

	public float score(QSString abbr, int offset) {

		if(abbr.length() == 0) return 0.9f;
		if(abbr.length() > this.length()) return 0.0f;

		for (int i = abbr.length(); i > 0; i--) {
			QSString sub_abbr = abbr.substring(0, i);
			int index = this.indexOf(sub_abbr);

			if (index < 0) continue;
			if (index + abbr.length() > this.length() + offset) continue;

			QSString next_string = this.substring(index+sub_abbr.length());
			QSString next_abbr = null;

			if (i >= abbr.length()) next_abbr = new QSString("");
			else next_abbr = abbr.substring(i);

			float remaining_score = next_string.score(next_abbr, offset+index);

			if (remaining_score > 0.0f) {
				float score = this.length()-next_string.length();

				if (index != 0) {
					char c = this.charAt(index-1);
					if (c == (char) 32 || c == (char) 9) {
						for (int j = (index-2); j >= 0; j--) {
							c = this.charAt(j);
							score -= (c == (char) 32 || c == (char) 9) ? 1.0f : 0.15f;
						}
						// XXX maybe not port this heuristic
						//
						//          } else if ([[NSCharacterSet uppercaseLetterCharacterSet] characterIsMember:[self characterAtIndex:matchedRange.location]]) {
						//            for (j = matchedRange.location-1; j >= (int) searchRange.location; j--) {
						//              if ([[NSCharacterSet uppercaseLetterCharacterSet] characterIsMember:[self characterAtIndex:j]])
						//                score--;
						//              else
						//                score -= 0.15;
						//            }
					} else {
						score -= index;
					}
				}

				score += remaining_score * next_string.length();
				score /= this.length();
				return score;
			}
		}
		return 0.0f;
	}

	public static void main(String[] argv) {

		System.out.println(
			new QSString("My Great Test String").score(new QSString("mgstr"))
		);


		System.out.println(
			new QSString("hello world").score(new QSString("axl"))
		);


		System.out.println(
			new QSString("hello world").score(new QSString("ow"))
		);


		System.out.println(
			new QSString("hello world").score(new QSString("hello world"))
		);

	}
}
