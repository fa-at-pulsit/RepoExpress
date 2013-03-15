/*
    Copyright 2013, Strategic Gains, Inc.

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package com.strategicgains.repoexpress.util;


/**
 * Implements a very efficient URL-safe base64 encoding/decoding algorithm to encode/decode.
 * Instead of using '+' and '/' like the normal base 64 encoder uses, which require URL encoding,
 * this algorithm uses '-' and '_' which are URL safe.
 * <p/>
 * NOTE: this Base64 implementor does NOT tolerate line separators or illegal characters and will throw an exception.
 * 
 * @author toddf
 * @since Mar 15, 2013
 */
public class UrlSafeBase64
{
	private static final char[] B64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_".toCharArray();
	private static final char PADDING = '=';

	public String encode(byte[] bytes)
    {
	    return null;
    }

	public byte[] decode(String s)
    {
		if (s == null) throw new NullPointerException("Cannot decode null string");
		
		if (s.isEmpty()) return new byte[0];
		
	    // TODO Auto-generated method stub
	    return null;
    }
}
