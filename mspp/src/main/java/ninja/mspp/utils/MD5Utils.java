/**
 * BSD 3-Clause License
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * @author Mass++ Users Group
 * @author Masaki Murase
 * @since 2018-05-30 07:59:57+09:00
 *
 * Copyright (c) 2018, Mass++ Users Group
 * All rights reserved.
 */
package ninja.mspp.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This code is copied from PRIDE's ms-data-core-api (uk.ac.ebi.pride.utilities.data.utils) as is,
 *     since we couldn't import ms-data-core-api because of inconsistency of 
 *     version of spring framework.
 */

/**
 * MD5Utils provides static method
 * <p/>
 * @author Rui Wang, Yasset Perez-Riverol
 * Date: 24/06/11
 * Time: 10:01
 */
public final class MD5Utils {

    /**
     * Private Constructor
     */
    private MD5Utils() {

    }

    /**
     * Generate md5 hash from a given string
     *
     * @param msg input string
     * @return String  md5 hash
     * @throws java.security.NoSuchAlgorithmException
     *          java.security.NoSuchAlgorithmException
     */
    public static String generateHash(String msg) throws NoSuchAlgorithmException {
        if (msg == null) {
            throw new IllegalArgumentException("Input string can not be null");
        }

        MessageDigest m = MessageDigest.getInstance("MD5");

        m.reset();
        m.update(msg.getBytes());

        byte[] digest = m.digest();
        BigInteger bigInt = new BigInteger(1, digest);
        String hashText = bigInt.toString(16);

        // zero pad to 32 chars
        while (hashText.length() < 32) {
            hashText = "0" + hashText;
        }

        return hashText;
    }
}

