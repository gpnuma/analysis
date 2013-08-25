package com.centaurean.analysis;

import java.util.Arrays;

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
 * 16/08/13 23:24
 */
public class ByteArray {
    private static final long SHARC_XOR_MASK = 0x2AE2752F;
    private static final long SHARC_HASH_BITS = 16;
    private static final long SHARC_HASH_OFFSET_BASIS = 2166115717l;
    private static final long SHARC_HASH_PRIME = 16777619;

    Byte[] array;

    public ByteArray(byte[] values) {
        array = new Byte[4];
        array[0] = values[0];
        array[1] = values[1];
        array[2] = values[2];
        array[3] = values[3];
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(array);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ByteArray))
            return false;
        if (array.length != ((ByteArray) o).getArray().length)
            return false;
        for (int i = 0; i < array.length; i++)
            if (array[i].byteValue() != ((ByteArray) o).getArray()[i].byteValue())
                return false;
        return true;
    }

    public Byte[] getArray() {
        return array;
    }

    public long sharcHash() {
        long hash = SHARC_HASH_OFFSET_BASIS;
        hash ^= (toLong() ^ SHARC_XOR_MASK);
        hash *= SHARC_HASH_PRIME;
        hash = hash % 4294967296l;
        hash = (hash >> (32 - SHARC_HASH_BITS)) ^ (hash & 0xFFFF);
        return hash;
    }

    public long toLong() {
        return Byte.toUnsignedInt(array[0]) * 16777216l + Byte.toUnsignedInt(array[1]) * 65536l + Byte.toUnsignedInt(array[2]) * 256l + Byte.toUnsignedInt(array[3]);
    }

    @Override
    public String toString() {
        return Arrays.toString(array) + " = " + toLong() + ", hash = " + sharcHash();
    }
}