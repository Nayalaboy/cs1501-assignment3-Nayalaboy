/*************************************************************************
 *  Compilation:  javac LZWmod.java
 *  Execution:    java LZWmod - < input.txt > output.lzw  (compress input.txt 
 *                                                         into output.lzw)
 *  Execution:    java LZWmod + < output.lzw > input.rec  (expand output.lzw 
 *                                                         into input.rec)
 *  Dependencies: BinaryStdIn.java BinaryStdOut.java
 *
 *  Compress or expand binary input from standard input using LZW.
 *
 *
 *************************************************************************/

public class LZW {
    private static final int R = 256;        // alphabet size
    private static boolean flushIfFull = false;

    public static void compress() {
        CompressionCodeBookInterface codebook =
                new DLBCodeBook(9, 16);

        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            if (!codebook.advance(c)) { //found longest match
                int codeword = codebook.getCodeWord();
                BinaryStdOut.write(codeword, codebook.getCodewordWidth());
                codebook.add(flushIfFull);
                codebook.advance(c);
            }
        }
        int codeword = codebook.getCodeWord();
        BinaryStdOut.write(codeword, codebook.getCodewordWidth());

        BinaryStdOut.write(R, codebook.getCodewordWidth());
        BinaryStdOut.close();
    }


    public static void expand() {
        ExpansionCodeBookInterface codebook = new ArrayCodeBook(12, 12);

        int codeword = BinaryStdIn.readInt(codebook.getCodewordWidth(flushIfFull));
        String val = codebook.getString(codeword);

        while (true) {
            BinaryStdOut.write(val);
            codeword = BinaryStdIn.readInt(codebook.getCodewordWidth(flushIfFull));

            if (codeword == R) break;
            String s = codebook.getString(codeword);
            if (codebook.size() == codeword) s = val + val.charAt(0); // special case hack

            codebook.add(val + s.charAt(0), flushIfFull);
            val = s;

        }
        BinaryStdOut.close();
    }


    public static void main(String[] args) {
        if (args[0].equals("-")) {
            int minW = 9;
            int maxW = 16;
            CompressionCodeBookInterface codebook = new DLBCodeBook(minW, maxW);
            BinaryStdOut.write(flushIfFull);
            BinaryStdOut.write(1, 1);  // write 1 bit to indicate that the minimum and maximum codeword widths are present
            compress();
        } else if (args[0].equals("+")) {
            flushIfFull = BinaryStdIn.readBoolean();
            int firstBit = BinaryStdIn.readInt(1);
            if (firstBit == 1) {
                int minW = 9;
                int maxW =16;
                ExpansionCodeBookInterface codebook = new ArrayCodeBook(minW, maxW);
                expand();
            } else {
                ExpansionCodeBookInterface codebook = new ArrayCodeBook(9, 16);
                expand();
            }
        } else {
            throw new RuntimeException("Illegal command line argument");
        }
    }
}
