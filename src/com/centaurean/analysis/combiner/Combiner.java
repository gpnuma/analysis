package com.centaurean.analysis.combiner;

import java.io.*;

/*
 * Copyright (c) 2013, Guillaume Voirin
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Centaurean nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Centaurean BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * analysis
 *
 * 01/10/13 16:53
 */
public class Combiner {
    public static void main(String... args) throws IOException {
        FileInputStream is = new FileInputStream(args[0]);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        FileOutputStream os = new FileOutputStream(args[1]);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));

        String[] lines = new String[256];
        for (int i = 0; i < 256; i++) {
            lines[i] = reader.readLine();
        }

        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 256; j++) {
                String combined = lines[i] + lines[j];
                writer.write("{BINARY_TO_UINT(" + combined + "), " + combined.length() + "},\\");
                writer.newLine();
            }
        }
        reader.close();
        writer.close();

        is.close();
        os.close();
    }
}
