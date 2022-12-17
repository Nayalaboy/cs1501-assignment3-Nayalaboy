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

    public static void compress(CompressionCodeBookInterface flushOption) {
        flushIfFull = "flush".equals(flushOption);
        CompressionCodeBookInterface codebook =
                new DLBCodeBook(9, 16);

        BinaryStdOut.write(flushIfFull);
        BinaryStdOut.write(1, 1);

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
        flushIfFull = BinaryStdIn.readBoolean();
        ExpansionCodeBookInterface codebook = new ArrayCodeBook(9,16);

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
            if (args.length > 1) {
                // parse the second argument
                // you can use Integer.parseInt() to parse the argument as an integer
                int maxCodeLength = Integer.parseInt(args[1]);
                // create a codebook with the specified maximum code length
                CompressionCodeBookInterface codebook = new DLBCodeBook(maxCodeLength, 16);
                compress(codebook);
            } else {
                // create a default codebook if no second argument is provided
                CompressionCodeBookInterface codebook = new DLBCodeBook(9, 16);
                compress(codebook);
            }
        } else if (args[0].equals("+")) {
            // create a default codebook for expansion
            ExpansionCodeBookInterface codebook = new ArrayCodeBook(9, 16);
            expand();
        } else {
            throw new RuntimeException("Illegal command line argument");
        }
    }

}
