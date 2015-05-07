/*******************************************************************************
 * Copyright 2015 Tobias Brandt - SoftConEx GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tobibrandt.custombase64;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * This CustomBase64 class provides the functionality to encode/decode a byte array to/from a Base64 String.<br>
 * This CustomBase64 class does NOT follow the RFC 3548 or RFC 4648 specification of Base64 as the used charset can be specified 
 * and no padding characters are used.<br>
 * So only the characters provided by the user by calling the {@link #setBase64Charset(String)} will appear in the encoded String and no other characters 
 * like equal signs '=' (sometimes used for padding in Base64) will appear.
 * 
 * 
 * @author Tobias Brandt
 *
 */
public class CustomBase64 {
	public static final int BASE = 64;

	public static final String DEFAULT_CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+-";

	private String base64Charset;

	private char[] base64Chars;

	private HashMap<Character, Integer> charPositionMap;

	public CustomBase64() {
		setBase64Charset(DEFAULT_CHARSET);
	}

	public String encode(byte[] byteArr) throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();

		int bitsToNext = 2;
		byte rem = 0;

		for (int i = 0; i < byteArr.length; i++) {
			byte b = byteArr[i];
			if (bitsToNext == 2) {
				b = (byte) ((byteArr[i] >> 2) & 0x3f);
				rem = (byte) ((byteArr[i] << (8 - 4)) & 0x3f);
			} else if (bitsToNext == 4) {
				b = (byte) ((byteArr[i] >> 4) & 0x0f);
				b = (byte) (b | rem);
				rem = (byte) ((byteArr[i] << (8 - 6)) & 0x3f);
			} else if (bitsToNext == 6) {
				b = (byte) ((byteArr[i] >> 6) & 0x03);
				b = (byte) (b | rem);
				rem = (byte) (byteArr[i] & 0x3f);
			} else if (bitsToNext == 0) {
				b = rem;
				rem = 0;
			}
			bitsToNext = (bitsToNext + 2) % 8;
			if (bitsToNext == 0)
				i--;

			sb.append(base64Chars[b]);
		}

		if (rem != 0) {
			sb.append(base64Chars[rem]);
		} else if (sb.length() < (int) Math.ceil(byteArr.length * 8d / 6d)) {
			sb.append(base64Chars[0]);
		}

		return sb.toString();
	}

	public byte[] decode(String base64String) {
		byte[] decodedBytes = new byte[(int) Math.floor(base64String.length() * 6d / 8d)];

		int missingBits = 2;
		int stringPos = 0;
		for (int i = 0; i < decodedBytes.length; i++) {

			decodedBytes[i] = (byte) (int) charPositionMap.get(base64String.charAt(stringPos));
			decodedBytes[i] = (byte) (decodedBytes[i] << missingBits);
			byte b = (byte) (int) charPositionMap.get(base64String.charAt(stringPos + 1));
			b = (byte) (b >> (6 - missingBits));
			decodedBytes[i] = (byte) (decodedBytes[i] | b);

			missingBits = (missingBits + 2) % 8;
			if (missingBits == 0) {
				stringPos++;
				missingBits += 2;
			}
			stringPos++;
		}

		return decodedBytes;
	}

	public String getBase64Charset() {
		return base64Charset;
	}

	/**
	 * The base64Charset must be a String containing exactly 64 distinct characters.<br>
	 * If it does not a {@link CustomBase64Exception} is thrown.
	 * @param base64Charset
	 */
	public void setBase64Charset(String base64Charset) {
		if (base64Charset.length() != BASE) {
			throw new CustomBase64Exception("The charset must be 64 characters long.");
		}
		this.base64Charset = base64Charset;
		this.base64Chars = this.base64Charset.toCharArray();
		this.charPositionMap = new HashMap();
		for (int i = 0; i < this.base64Chars.length; i++) {
			this.charPositionMap.put(this.base64Chars[i], i);
		}
		if (this.charPositionMap.size() != BASE) {
			throw new CustomBase64Exception("The charset must contain 64 unique characters.");
		}
	}

}
