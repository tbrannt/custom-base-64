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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import junit.framework.TestCase;

public class CustomBase64Test extends TestCase {

	private Random rand = new Random(5341711);

	public void testCustomBase64Charset() {
		CustomBase64 base64 = new CustomBase64();

		assertEquals(64, base64.getBase64Charset().length());

		Set<Character> set = new HashSet();

		for (char c : base64.getBase64Charset().toCharArray()) {
			set.add(c);
		}

		assertEquals(64, set.size());

		// no exception
		base64.setBase64Charset(CustomBase64.DEFAULT_CHARSET);

		try {
			base64.setBase64Charset(CustomBase64.DEFAULT_CHARSET + "Ü");
			fail("Excpeted CustomBase64Exception because provided charset is too long");
		} catch (CustomBase64Exception e) {

		}

		try {
			base64.setBase64Charset(CustomBase64.DEFAULT_CHARSET.substring(1));
			fail("Excpeted CustomBase64Exception because provided charset is too short");
		} catch (CustomBase64Exception e) {

		}

		try {
			base64.setBase64Charset(CustomBase64.DEFAULT_CHARSET.substring(1) + "X");
			fail("Excpeted CustomBase64Exception because provided charset does not contain 64 unique characters (X twice)");
		} catch (CustomBase64Exception e) {

		}

		// no exception
		base64.setBase64Charset(CustomBase64.DEFAULT_CHARSET.substring(1) + "Ü");

	}

	public void testCustomBase64EncodingDecoding() throws UnsupportedEncodingException {
		CustomBase64 base64 = new CustomBase64();

		for (int i = 0; i < 500000; i++) {
			byte[] randByteArr = getRandByteArr();

			String expectedString = new String(randByteArr, "UTF-8");
			String encoded = base64.encode(randByteArr);
			byte[] decoded = base64.decode(encoded);
			assertTrue(Arrays.equals(randByteArr, decoded));
			assertEquals(expectedString, new String(decoded, "UTF-8"));
		}

	}

	public void testCustomBase64Encoding() throws UnsupportedEncodingException {
		CustomBase64 base64 = new CustomBase64();

		for (int i = 0; i < 500000; i++) {
			byte[] randByteArr = getRandByteArr();

			String encoded = base64.encode(randByteArr);
			int expected = (int) Math.ceil(randByteArr.length * 8d / 6d);
			if (encoded.length() != expected) {
				printByteArray(randByteArr);
				fail(i + " is not correct.. expected: " + expected + " but was: " + encoded.length());
			}
		}
	}

	private byte[] getRandByteArr() {
		byte[] bytearr = new byte[1 + rand.nextInt(30)];
		rand.nextBytes(bytearr);
		return bytearr;
	}

	private void printByte(byte b) {
		String s1 = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
		System.out.print(s1 + "/" + b);
	}

	private void printByteArray(byte[] bytearr) {
		for (int i = 0; i < bytearr.length; i++) {
			if (i < (bytearr.length - 1)) {
				printByte(bytearr[i]);
				System.out.print(", ");
			} else {
				printByte(bytearr[i]);
				System.out.println();
			}
		}
	}
}
