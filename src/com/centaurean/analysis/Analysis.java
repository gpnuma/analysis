package com.centaurean.analysis;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.TreeMap;

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
 * Analysis
 *
 * 17/05/13 02:16
 */
public class Analysis {
    enum MODES {RARE_SEARCH, DICTIONARY_PRELOAD}

    private static final boolean LOG = false;
    private static final MODES mode = MODES.DICTIONARY_PRELOAD;

    public static boolean isPrime(long n) {
        if (n % 2 == 0)
            return false;
        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0)
                return false;
        }
        return true;
    }

    public static void main(String... args) throws IOException {
        HashMap<ByteArray, Long> dictionary = new HashMap<>();
        ValueComparator bvc = new ValueComparator(dictionary);
        TreeMap<ByteArray, Long> sorted_dictionary = new TreeMap<>(bvc);

        File directory = new File(args[0]);
        File[] list = directory.listFiles();
        if (list == null)
            throw new IOException();

        for (File file : list) {

            System.out.println(file.getAbsolutePath());
            BufferedInputStream bis;
            try {
                bis = new BufferedInputStream(new FileInputStream(file));

                byte[] cache = new byte[16384];
                int read;

                byte[] n = new byte[4];

                while ((read = bis.read(cache)) >= 0) {
                    for (int i = 0; i < read; i++) {
                        n[3] = n[2];
                        n[2] = n[1];
                        n[1] = n[0];
                        n[0] = cache[i];

                        ByteArray key = new ByteArray(n);
                        Long found = dictionary.get(key);
                        if (found == null)
                            found = 0l;
                        dictionary.put(key, found + 1);
                    }
                }
            } catch (IOException ignored) {
                ignored.printStackTrace();
            }
        }

        System.out.print("Sorting dictionary ... ");
        sorted_dictionary.putAll(dictionary);
        System.out.println("OK");

        if (LOG) {
            System.out.print("Creating log ... ");
            File out = new File("preload.log");
            boolean created = out.createNewFile();
            if (out.exists()) {
                BufferedWriter logWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(out)));

                for (ByteArray key : sorted_dictionary.keySet()) {
                    String text = "[" + key + "] = " + dictionary.get(key);
                    logWriter.write(text);
                    logWriter.newLine();
                }

                logWriter.close();
            }
            System.out.println("OK");
        }

        switch (mode) {
            case RARE_SEARCH:
                Random r = new Random();
                int limit = 256;
                while (limit >= 0) {
                    byte[] newKey = new byte[4];
                    r.nextBytes(newKey);
                    ByteArray key = new ByteArray(newKey);
                    if (isPrime(key.toLong())) {
                        if (dictionary.get(key) == null) {
                            byte[] reverse = new byte[]{key.getArray()[3], key.getArray()[2], key.getArray()[1], key.getArray()[0]};
                            System.out.println("Not found : " + key.toString() + " (" + new String(reverse) + ")");
                            limit--;
                        }
                    }
                }
                break;
            case DICTIONARY_PRELOAD:
                long[] dictionaryImage = new long[65536];

                System.out.print("Preparing hash dictionary ... ");
                HashSet<Long> uniqueHashes = new HashSet<>();
                for (ByteArray key : sorted_dictionary.keySet()) {
                    if (!uniqueHashes.contains(key.sharcHash())) {
                        uniqueHashes.add(key.sharcHash());
                        dictionaryImage[(int) key.sharcHash()] = key.toLong();
                    }
                }
                System.out.println("OK");

                File preload = new File("preload.data");
                boolean created = preload.createNewFile();
                if (preload.exists()) {
                    BufferedWriter preloadWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(preload)));
                    preloadWriter.write("#define SHARC_DICTIONARY {");
                    for (int i = 0; i < dictionaryImage.length; i++) {
                        long data = dictionaryImage[i];
                        if (i > 0)
                            preloadWriter.write(", ");
                        preloadWriter.write("{0x" + Long.toHexString(0x100000000l | data).substring(1).toUpperCase() + "}");
                    }
                    preloadWriter.write("}");
                    preloadWriter.close();

                    System.out.println(sorted_dictionary.size() + " -> " + uniqueHashes.size());
                }
                break;
        }
    }
}
