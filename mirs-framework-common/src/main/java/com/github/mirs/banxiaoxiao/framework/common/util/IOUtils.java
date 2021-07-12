package com.github.mirs.banxiaoxiao.framework.common.util;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * IO utils.
 *
 * @author fjli
 * @since 1.0.1
 */
public class IOUtils {

    private static final int DEFAULT_BUF_SIZE = 4096;

    private IOUtils() {
    }

    /**
     * Close object quietly.
     *
     * @param closeable - the object to close
     */
    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) { // NOSONAR
            }
        }
    }

    /**
     * Close connection quietly.
     *
     * @param connection - the connection to close
     */
    public static void closeQuietly(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) { // NOSONAR
            }
        }
    }

    /**
     * Close statement quietly.
     *
     * @param statement - the statement to close
     */
    public static void closeQuietly(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (Exception e) { // NOSONAR
            }
        }
    }

    /**
     * Close resultSet quietly.
     *
     * @param resultSet - the resultSet to close
     */
    public static void closeQuietly(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (Exception e) { // NOSONAR
            }
        }
    }

    /**
     * Read all bytes from input stream.
     *
     * @param in - the input stream
     * @return the bytes read from input stream.
     * @throws IOException if read failed.
     * @since 1.0.6
     */
    public static byte[] readAllBytes(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            copy(in, out);
            return out.toByteArray();
        } finally {
            closeQuietly(out);
        }
    }

    public static byte[] readAllBytes(FileChannel inChannel, boolean autoClose) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            int len = 1;
            while (len != -1 && len != 0) {
                ByteBuffer buf = ByteBuffer.allocate(DEFAULT_BUF_SIZE);
                len = inChannel.read(buf);
                if (len > 0) {
                    out.write(buf.array(), 0, len);
                }
            }
            return out.toByteArray();
        } finally {
            closeQuietly(out);
            if (autoClose) {
                inChannel.close();
            }
        }
    }

    public static <T> T readBody(InputStream input, Class<T> clazz) throws IOException {
        byte[] b = IOUtils.readAllBytes(input);
        String jsonBody = new String(b);
        T data = JsonUtils.fromJson(clazz, jsonBody);
        return data;
    }

    public static <T> T readBody(FileChannel channel, Class<T> clazz, boolean autoClose) throws IOException {
        byte[] b = IOUtils.readAllBytes(channel, autoClose);
        String jsonBody = new String(b);
        T data = JsonUtils.fromJson(clazz, jsonBody);
        return data;
    }

    /**
     * Read all bytes from the specified file.
     *
     * @param file - the specified file
     * @return the bytes read from the specified file.
     * @throws IOException if read failed.
     * @since 1.0.6
     */
    public static byte[] readAllBytes(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    /**
     * Read all bytes from the specified file path.
     *
     * @param path - the path of the file
     * @return the bytes read from the specified file path.
     * @throws IOException if read failed.
     * @since 1.0.6
     */
    public static byte[] readAllBytes(Path path) throws IOException {
        return Files.readAllBytes(path);
    }

    /**
     * To a BufferedReader with the specified path and charset.
     *
     * @param path - the path to the file
     * @param cs   - the charset to use for decoding
     * @return a new buffered reader.
     * @throws IOException if an I/O error occurs opening the file.
     * @since 1.0.6
     */
    public static BufferedReader toBufferedReader(Path path, Charset cs) throws IOException {
        return Files.newBufferedReader(path, cs);
    }

    /**
     * To a BufferedReader with the specified file and charset.
     *
     * @param file - the path to the file
     * @param cs   - the charset to use for decoding
     * @return a new buffered reader.
     * @throws IOException if an I/O error occurs opening the file.
     * @since 1.0.6
     */
    public static BufferedReader toBufferedReader(File file, Charset cs) throws IOException {
        return Files.newBufferedReader(file.toPath(), cs);
    }

    /**
     * To a BufferedReader with the specified input stream and charset.
     *
     * @param in - the input stream
     * @param cs - the charset to use for decoding
     * @return a new buffered reader.
     * @since 1.0.6
     */
    public static BufferedReader toBufferedReader(InputStream in, Charset cs) {
        return new BufferedReader(new InputStreamReader(in, cs));
    }

    /**
     * To a BufferedReader with the specified reader.
     *
     * @param reader - the reader
     * @return a new buffered reader if the reader is not a buffered reader.
     * @since 1.0.6
     */
    public static BufferedReader toBufferedReader(Reader reader) {
        return (reader instanceof BufferedReader) ? (BufferedReader) reader : new BufferedReader(reader);
    }

    /**
     * Read all lines from the specified buffered reader.
     *
     * @param reader - the buffered reader
     * @return the lines from the reader as a List.
     * @throws IOException If an I/O error occurs.
     * @since 1.0.6
     */
    public static List<String> readAllLines(BufferedReader reader) throws IOException {
        try {
            List<String> result = new ArrayList<>();
            for (; ; ) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                result.add(line);
            }
            return result;
        } finally {
            closeQuietly(reader);
        }
    }

    /**
     * Read all lines from the specified input stream.
     *
     * @param in - the input stream
     * @param cs - the charset to use for decoding
     * @return the lines from the reader as a List.
     * @throws IOException If an I/O error occurs.
     * @since 1.0.6
     */
    public static List<String> readAllLines(InputStream in, Charset cs) throws IOException {
        return readAllLines(toBufferedReader(in, cs));
    }

    /**
     * Read all lines from the specified reader.
     *
     * @param reader - the reader
     * @return the lines from the reader as a List.
     * @throws IOException If an I/O error occurs.
     * @since 1.0.6
     */
    public static List<String> readAllLines(Reader reader) throws IOException {
        return readAllLines(toBufferedReader(reader));
    }

    /**
     * Read all lines from the specified file.
     *
     * @param file - the path to the file
     * @param cs   - the charset to use for decoding
     * @return the lines from the file as a List.
     * @throws IOException If an I/O error occurs.
     * @since 1.0.6
     */
    public static List<String> readAllLines(File file, Charset cs) throws IOException {
        return readAllLines(toBufferedReader(file, cs));
    }

    /**
     * Read all lines from the specified file.
     *
     * @param path - the path to the file
     * @param cs   - the charset to use for decoding
     * @return the lines from the file as a List.
     * @throws IOException If an I/O error occurs.
     * @since 1.0.6
     */
    public static List<String> readAllLines(Path path, Charset cs) throws IOException {
        return readAllLines(toBufferedReader(path, cs));
    }

    /**
     * Read the first line in the specified file.
     *
     * @param path - the path to the file
     * @param cs   - the charset to use for decoding
     * @return the first line in the file.
     * @throws IOException If an I/O error occurs.
     * @since 1.0.6
     */
    public static String readFirstLine(Path path, Charset cs) throws IOException {
        BufferedReader reader = toBufferedReader(path, cs);
        try {
            return reader.readLine();
        } finally {
            closeQuietly(reader);
        }
    }

    /**
     * Read string from the specified file. The file may only contain a string value.
     *
     * @param path - the path to the file
     * @return the string value from the specified file.
     * @throws IOException If an I/O error occurs.
     * @since 1.0.6
     */
    public static String readString(Path path) throws IOException {
        return readFirstLine(path, Charset.defaultCharset());
    }

    /**
     * Read byte from the specified file. The file may only contain a byte value.
     *
     * @param path - the path to the file
     * @return the string value from the specified file.
     * @throws IOException           If an I/O error occurs.
     * @throws NumberFormatException if the string does not contain a parsable byte.
     * @since 1.0.6
     */
    public static Byte readByte(Path path) throws IOException {
        String value = readString(path);
        if (value != null) {
            return Byte.decode(value);
        }
        return null;
    }

    /**
     * Read short from the specified file. The file may only contain a short value.
     *
     * @param path - the path to the file
     * @return the string value from the specified file.
     * @throws IOException           If an I/O error occurs.
     * @throws NumberFormatException if the string does not contain a parsable short.
     * @since 1.0.6
     */
    public static Short readShort(Path path) throws IOException {
        String value = readString(path);
        if (value != null) {
            return Short.decode(value);
        }
        return null;
    }

    /**
     * Read int from the specified file. The file may only contain a int value.
     *
     * @param path - the path to the file
     * @return the string value from the specified file.
     * @throws IOException           If an I/O error occurs.
     * @throws NumberFormatException if the string does not contain a parsable int.
     * @since 1.0.6
     */
    public static Integer readInt(Path path) throws IOException {
        String value = readString(path);
        if (value != null) {
            return Integer.decode(value);
        }
        return null;
    }

    /**
     * Read long from the specified file. The file may only contain a long value.
     *
     * @param path - the path to the file
     * @return the string value from the specified file.
     * @throws IOException           If an I/O error occurs.
     * @throws NumberFormatException if the string does not contain a parsable long.
     * @since 1.0.6
     */
    public static Long readLong(Path path) throws IOException {
        String value = readString(path);
        if (value != null) {
            return Long.decode(value);
        }
        return null;
    }

    /**
     * Read float from the specified file. The file may only contain a float value.
     *
     * @param path - the path to the file
     * @return the string value from the specified file.
     * @throws IOException           If an I/O error occurs.
     * @throws NumberFormatException if the string does not contain a parsable float.
     * @since 1.0.6
     */
    public static Float readFloat(Path path) throws IOException {
        String value = readString(path);
        if (value != null) {
            return Float.parseFloat(value);
        }
        return null;
    }

    /**
     * Read double from the specified file. The file may only contain a double value.
     *
     * @param path - the path to the file
     * @return the string value from the specified file.
     * @throws IOException           If an I/O error occurs.
     * @throws NumberFormatException if the string does not contain a parsable double.
     * @since 1.0.6
     */
    public static Double readDouble(Path path) throws IOException {
        String value = readString(path);
        if (value != null) {
            return Double.parseDouble(value);
        }
        return null;
    }

    /**
     * To a BufferedWriter with the specified file and charset.
     *
     * @param file    - the specified file
     * @param cs      - the charset to use for decoding
     * @param options - the write option
     * @return a new buffered writer.
     * @throws IOException                   if an I/O error occurs opening or creating the file.
     * @throws UnsupportedOperationException if an unsupported option is specified.
     * @since 1.0.6
     */
    public static BufferedWriter toBufferedWriter(File file, Charset cs, OpenOption... options) throws IOException {
        return Files.newBufferedWriter(file.toPath(), cs, options);
    }

    /**
     * To a BufferedWriter with the specified path and charset.
     *
     * @param path    - the path to the file
     * @param cs      - the charset to use for decoding
     * @param options - the write option
     * @return a new buffered writer.
     * @throws IOException                   if an I/O error occurs opening or creating the file.
     * @throws UnsupportedOperationException if an unsupported option is specified.
     * @since 1.0.6
     */
    public static BufferedWriter toBufferedWriter(Path path, Charset cs, OpenOption... options) throws IOException {
        return Files.newBufferedWriter(path, cs, options);
    }

    /**
     * To a BufferedWriter with the specified output stream and charset.
     *
     * @param out - the output stream
     * @param cs  - the charset to use for decoding
     * @return a new buffered writer.
     * @since 1.0.6
     */
    public static BufferedWriter toBufferedWriter(OutputStream out, Charset cs) {
        return new BufferedWriter(new OutputStreamWriter(out, cs.newEncoder()));
    }

    /**
     * To a BufferedWriter with the specified writer.
     *
     * @param writer - the writer
     * @return a new buffered writer if the writer is not a buffered writer.
     * @since 1.0.6
     */
    public static BufferedWriter toBufferedWriter(Writer writer) {
        return (writer instanceof BufferedWriter) ? (BufferedWriter) writer : new BufferedWriter(writer);
    }

    /**
     * Write all lines to the specified buffered writer.
     *
     * @param writer - the specified buffered writer
     * @param lines  - the lines to writer
     * @throws IOException if write failed.
     * @since 1.0.6
     */
    public static void writeAllLines(BufferedWriter writer, Iterable<String> lines) throws IOException {
        try {
            if (lines != null) {
                for (CharSequence line : lines) {
                    writer.append(line);
                    writer.newLine();
                }
            }
        } finally {
            closeQuietly(writer);
        }
    }

    /**
     * @param output
     * @param data
     * @throws IOException
     */
    public static void writeBytes(Path path, byte[] data, boolean append) throws IOException {
        writeBytes(Files.newOutputStream(path, getDefaultOpenOptions(append)), data);
    }

    /**
     * @param output
     * @param data
     * @throws IOException
     */
    public static void writeBytes(Path path, byte[] data) throws IOException {
        writeBytes(Files.newOutputStream(path), data);
    }

    /**
     * @param output
     * @param data
     * @throws IOException
     */
    public static void writeBytes(OutputStream output, byte[] data) throws IOException {
        try {
            if (data != null) {
                output.write(data);
            }
        } finally {
            closeQuietly(output);
        }
    }

    public static void writeBytes(FileChannel outChannel, byte[] data) throws IOException {
        writeBytes(outChannel, data, true);
    }

    public static void writeBytes(FileChannel outChannel, byte[] data, boolean autoClose) throws IOException {
        try {
            if (data != null) {
                ByteBuffer buf = ByteBuffer.allocate(data.length);
//              ByteBuffer buf = ByteBuffer.allocate(DEFAULT_BUF_SIZE);
                buf.clear();
                buf.put(data);
                buf.flip();
                while (buf.hasRemaining()) {
                    outChannel.write(buf);
                }
            }
        } finally {
            if (autoClose) {
                outChannel.close();
            }
        }
    }

    /**
     * Write all lines to the specified writer.
     *
     * @param writer - the specified writer
     * @param lines  - the lines to writer
     * @throws IOException if write failed.
     * @since 1.0.6
     */
    public static void writeAllLines(Writer writer, Iterable<String> lines) throws IOException {
        writeAllLines(toBufferedWriter(writer), lines);
    }

    /**
     * Write all lines to the specified output stream.
     *
     * @param out   - the specified output stream
     * @param cs    - the charset to use for encoding
     * @param lines - the lines to writer
     * @throws IOException if write failed.
     * @since 1.0.6
     */
    public static void writeAllLines(OutputStream out, Charset cs, Iterable<String> lines) throws IOException {
        writeAllLines(toBufferedWriter(out, cs), lines);
    }

    /**
     * Write all lines to the specified file.
     *
     * @param file   - the specified file
     * @param cs     - the charset to use for encoding
     * @param lines  - the lines to write
     * @param append - append or overwrite
     * @throws IOException if write failed.
     * @since 1.0.6
     */
    public static void writeAllLines(File file, Charset cs, Iterable<String> lines, boolean append) throws IOException {
        writeAllLines(file.toPath(), cs, lines, getDefaultOpenOptions(append));
    }

    /**
     * Write all lines to the specified file.
     *
     * @param file    - the specified file
     * @param cs      - the charset to use for encoding
     * @param lines   - the lines to write
     * @param options - the open options
     * @throws IOException if write failed.
     * @since 1.0.6
     */
    public static void writeAllLines(File file, Charset cs, Iterable<String> lines, OpenOption... options) throws IOException {
        writeAllLines(toBufferedWriter(file, cs, options), lines);
    }

    /**
     * Write all lines to the specified file.
     *
     * @param path   - the path to file
     * @param cs     - the charset to use for encoding
     * @param lines  - the lines to write
     * @param append - append or overwrite
     * @throws IOException if write failed.
     * @since 1.0.6
     */
    public static void writeAllLines(Path path, Charset cs, Iterable<String> lines, boolean append) throws IOException {
        writeAllLines(path, cs, lines, getDefaultOpenOptions(append));
    }

    /**
     * Write all lines to the specified file.
     *
     * @param path    - the path to file
     * @param cs      - the charset to use for encoding
     * @param lines   - the lines to write
     * @param options - the open options
     * @throws IOException if write failed.
     * @since 1.0.6
     */
    public static void writeAllLines(Path path, Charset cs, Iterable<String> lines, OpenOption... options) throws IOException {
        writeAllLines(toBufferedWriter(path, cs, options), lines);
    }

    /**
     * Get the default open options for appen or override.
     *
     * @param append - append or override
     * @return an array of open options.
     * @since 1.0.6
     */
    public static OpenOption[] getDefaultOpenOptions(boolean append) {
        Set<OpenOption> opts = new HashSet<OpenOption>();
        opts.add(StandardOpenOption.WRITE);
        if (append) {
            opts.add(StandardOpenOption.APPEND);
        } else {
            opts.add(StandardOpenOption.CREATE);
            opts.add(StandardOpenOption.TRUNCATE_EXISTING);
        }
        return opts.toArray(new OpenOption[0]);
    }

    /**
     * Copy file from one to another.
     *
     * @param src      - the source file
     * @param dst      - the destination file
     * @param override - override the source or not.
     * @throws FileAlreadyExistsException if override is set to false and the target file exists but cannot be replaced.
     * @throws IOException                if an I/O error occurs.
     * @since 1.0.6
     */
    public static void copy(File src, File dst, boolean override) throws IOException {
        if (override) {
            Files.copy(src.toPath(), dst.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
        } else {
            Files.copy(src.toPath(), dst.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
        }
    }

    /**
     * Copy file from one to another.
     *
     * @param src      - the source path
     * @param dst      - the destination path
     * @param override - override the source or not.
     * @throws FileAlreadyExistsException if override is set to false and the target file exists but cannot be replaced.
     * @throws IOException                if an I/O error occurs.
     * @since 1.0.6
     */
    public static void copy(Path src, Path dst, boolean override) throws IOException {
        if (override) {
            Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
        } else {
            Files.copy(src, dst, StandardCopyOption.COPY_ATTRIBUTES);
        }
    }

    /**
     * Copy data form input stream to output stream.
     *
     * @param input  - the input stream
     * @param output - the output stream
     * @throws IOException if an I/O error occurs.
     * @since 1.0.6
     */
    public static int copy(InputStream input, OutputStream output) throws IOException {
        int total = 0;
        int len = 0;
        byte[] buffer = new byte[DEFAULT_BUF_SIZE];
        while ((len = input.read(buffer)) != -1) {
            output.write(buffer, 0, len);
            total += len;
        }
        return total;
    }

    /**
     * Copy data form reader to writer.
     *
     * @param reader - the reader
     * @param writer - the writer
     * @throws IOException if an I/O error occurs.
     * @since 1.0.6
     */
    public static int copy(Reader reader, Writer writer) throws IOException {
        int total = 0;
        int len = 0;
        char[] buffer = new char[DEFAULT_BUF_SIZE];
        while ((len = reader.read(buffer)) != -1) {
            writer.write(buffer, 0, len);
            total += len;
        }
        return total;
    }

    /**
     * Copy data from reader to output stream.
     *
     * @param reader - the reader
     * @param output - the output stream
     * @throws IOException if an I/O error occurs.
     * @since 1.0.6
     */
    public static void copy(Reader reader, OutputStream output) throws IOException {
        copy(reader, output, null);
    }

    /**
     * Copy data from reader to output stream.
     *
     * @param reader - the reader
     * @param output - the output stream
     * @param cs     - the charset to encoding
     * @throws IOException if an I/O error occurs.
     * @since 1.0.6
     */
    public static void copy(Reader reader, OutputStream output, String cs) throws IOException {
        OutputStreamWriter writer = null;
        if (cs == null) {
            writer = new OutputStreamWriter(output);
        } else {
            writer = new OutputStreamWriter(output, cs);
        }
        copy(reader, writer);
        writer.flush();
    }

    /**
     * Copy data from input to writer.
     *
     * @param input  - input stream
     * @param writer - the writer
     * @throws IOException if an I/O error occurs.
     * @since 1.0.6
     */
    public static void copy(InputStream input, Writer writer) throws IOException {
        copy(input, writer, null);
    }

    /**
     * Copy data from input to writer.
     *
     * @param input  - input stream
     * @param writer - the writer
     * @param cs     - the charset to encoding
     * @throws IOException if an I/O error occurs.
     * @since 1.0.6
     */
    public static void copy(InputStream input, Writer writer, String cs) throws IOException {
        InputStreamReader reader = null;
        if (cs == null) {
            reader = new InputStreamReader(input);
        } else {
            reader = new InputStreamReader(input, cs);
        }
        copy(reader, writer);
        writer.flush();
    }
}
