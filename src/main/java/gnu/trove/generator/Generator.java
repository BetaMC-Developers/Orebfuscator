//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package gnu.trove.generator;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class Generator {
    private static final Generator.WrapperInfo[] WRAPPERS = new Generator.WrapperInfo[]{new Generator.WrapperInfo("byte", "Byte", "MAX_VALUE", "MIN_VALUE")};
    private static final Pattern PATTERN_v = Pattern.compile("#v#");
    private static final Pattern PATTERN_V = Pattern.compile("#V#");
    private static final Pattern PATTERN_VC = Pattern.compile("#VC#");
    private static final Pattern PATTERN_VT = Pattern.compile("#VT#");
    private static final Pattern PATTERN_VMAX = Pattern.compile("#VMAX#");
    private static final Pattern PATTERN_VMIN = Pattern.compile("#VMIN#");
    private static final Pattern PATTERN_V_UNDERBAR = Pattern.compile("_V_");
    private static final Pattern PATTERN_k = Pattern.compile("#k#");
    private static final Pattern PATTERN_K = Pattern.compile("#K#");
    private static final Pattern PATTERN_KC = Pattern.compile("#KC#");
    private static final Pattern PATTERN_KT = Pattern.compile("#KT#");
    private static final Pattern PATTERN_KMAX = Pattern.compile("#KMAX#");
    private static final Pattern PATTERN_KMIN = Pattern.compile("#KMIN#");
    private static final Pattern PATTERN_K_UNDERBAR = Pattern.compile("_K_");
    private static final Pattern PATTERN_e = Pattern.compile("#e#");
    private static final Pattern PATTERN_E = Pattern.compile("#E#");
    private static final Pattern PATTERN_EC = Pattern.compile("#EC#");
    private static final Pattern PATTERN_ET = Pattern.compile("#ET#");
    private static final Pattern PATTERN_EMAX = Pattern.compile("#EMAX#");
    private static final Pattern PATTERN_EMIN = Pattern.compile("#EMIN#");
    private static final Pattern PATTERN_E_UNDERBAR = Pattern.compile("_E_");
    private static File root_output_dir;

    public Generator() {
    }

    public static void main(String[] args) throws Exception {
        File input_directory = new File("C:\\Users\\Administrator\\Desktop\\trove-3.0.0\\templates");
        File output_directory = new File("C:\\Users\\Administrator\\Desktop\\trove-3.0.0\\templates2");
        if (!input_directory.exists()) {
            System.err.println("Directory \"" + input_directory + "\" not found.");
            System.exit(-1);
        } else {
            if (!output_directory.exists()) {
                makeDirs(output_directory);
            }

            root_output_dir = output_directory;
            System.out.println("Removing contents of \"" + output_directory + "\"...");
            cleanDir(output_directory);
            scanForFiles(input_directory, output_directory);
        }
    }

    private static void makeDirs(File directory) {
        if (directory.exists() && !directory.isDirectory()) {
            throw new IllegalArgumentException(directory + " not a directory");
        } else if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IllegalStateException("Could not create directories " + directory);
            }
        }
    }

    private static void scanForFiles(File input_directory, File output_directory) throws IOException {
        File[] files = input_directory.listFiles();
        File[] array = files;
        int length = files.length;

        for(int i = 0; i < length; ++i) {
            File file = array[i];
            if (!file.isHidden()) {
                if (file.isDirectory()) {
                    if (!file.getName().equals("CVS")) {
                        scanForFiles(file, new File(output_directory, file.getName()));
                    }
                } else {
                    processFile(file, output_directory);
                }
            }
        }

    }

    private static void processFile(File input_file, File output_directory) throws IOException {
        System.out.println("Process file: " + input_file);
        String content = readFile(input_file);
        String file_name = input_file.getName();
        file_name = file_name.replaceAll("\\.template", ".java");
        File output_file = new File(output_directory, file_name);
        if (file_name.contains("_K_")) {
            processKVMarkers(content, output_directory, file_name);
        } else if (file_name.contains("_E_")) {
            processEMarkers(content, output_directory, file_name);
        } else {
            if (input_file.lastModified() < output_file.lastModified()) {
                System.out.println("File " + output_file + " up to date, not processing input");
                return;
            }

            StringBuilder processed_replication_output = new StringBuilder();
            Map<Integer, String> replicated_blocks = findReplicatedBlocks(content, processed_replication_output);
            if (replicated_blocks != null) {
                content = processReplication(processed_replication_output.toString(), replicated_blocks);
            }

            writeFile(content, output_file);
        }

    }

    private static void processKVMarkers(String content, File output_dir, String file_name) throws IOException {
        Generator.WrapperInfo[] wrappers;
        int length = (wrappers = WRAPPERS).length;

        for(int i = 0; i < length; ++i) {
            Generator.WrapperInfo info = wrappers[i];
            String k = info.primitive;
            String KT = info.class_name;
            String K = abbreviate(KT);
            String KC = K.toUpperCase();
            String KMAX = info.max_value;
            String KMIN = info.min_value;
            String out = PATTERN_k.matcher(content).replaceAll(k);
            out = PATTERN_K.matcher(out).replaceAll(K);
            out = PATTERN_KC.matcher(out).replaceAll(KC);
            out = PATTERN_KT.matcher(out).replaceAll(KT);
            out = PATTERN_KMAX.matcher(out).replaceAll(KMAX);
            out = PATTERN_KMIN.matcher(out).replaceAll(KMIN);
            String out_file_name = "T" + file_name;
            out_file_name = PATTERN_K_UNDERBAR.matcher(out_file_name).replaceAll(K);
            Generator.WrapperInfo[] wrappers2;
            int length2 = (wrappers2 = WRAPPERS).length;

            for(int j = 0; j < length2; ++j) {
                Generator.WrapperInfo jinfo = wrappers2[j];
                String v = jinfo.primitive;
                String VT = jinfo.class_name;
                String V = abbreviate(VT);
                String VC = V.toUpperCase();
                String VMAX = jinfo.max_value;
                String VMIN = jinfo.min_value;
                String vout = PATTERN_v.matcher(out).replaceAll(v);
                vout = PATTERN_V.matcher(vout).replaceAll(V);
                vout = PATTERN_VC.matcher(vout).replaceAll(VC);
                vout = PATTERN_VT.matcher(vout).replaceAll(VT);
                vout = PATTERN_VMAX.matcher(vout).replaceAll(VMAX);
                String processed_output = PATTERN_VMIN.matcher(vout).replaceAll(VMIN);
                StringBuilder processed_replication_output = new StringBuilder();
                Map<Integer, String> replicated_blocks = findReplicatedBlocks(processed_output, processed_replication_output);
                if (replicated_blocks != null) {
                    processed_output = processReplication(processed_replication_output.toString(), replicated_blocks);
                }

                String processed_filename = PATTERN_V_UNDERBAR.matcher(out_file_name).replaceAll(V);
                writeFile(processed_output, new File(output_dir, processed_filename));
            }
        }

    }

    private static void processEMarkers(String content, File output_dir, String file_name) throws IOException {
        Generator.WrapperInfo[] wrappers;
        int length = (wrappers = WRAPPERS).length;

        for(int i = 0; i < length; ++i) {
            Generator.WrapperInfo info = wrappers[i];
            String e = info.primitive;
            String ET = info.class_name;
            String E = abbreviate(ET);
            String EC = E.toUpperCase();
            String EMAX = info.max_value;
            String EMIN = info.min_value;
            String out = PATTERN_e.matcher(content).replaceAll(e);
            out = PATTERN_E.matcher(out).replaceAll(E);
            out = PATTERN_EC.matcher(out).replaceAll(EC);
            out = PATTERN_ET.matcher(out).replaceAll(ET);
            out = PATTERN_EMAX.matcher(out).replaceAll(EMAX);
            String processed_output = PATTERN_EMIN.matcher(out).replaceAll(EMIN);
            String out_file_name = "T" + file_name;
            out_file_name = PATTERN_E_UNDERBAR.matcher(out_file_name).replaceAll(E);
            StringBuilder processed_replication_output = new StringBuilder();
            Map<Integer, String> replicated_blocks = findReplicatedBlocks(processed_output, processed_replication_output);
            if (replicated_blocks != null) {
                processed_output = processReplication(processed_replication_output.toString(), replicated_blocks);
            }

            writeFile(processed_output, new File(output_dir, out_file_name));
        }

    }

    static String processReplication(String content, Map<Integer, String> replicated_blocks) {
        Entry entry;
        StringBuilder entry_buffer;
        for(Iterator var2 = replicated_blocks.entrySet().iterator(); var2.hasNext(); content = Pattern.compile("#REPLICATED" + entry.getKey() + "#").matcher(content).replaceAll(entry_buffer.toString())) {
            entry = (Entry)var2.next();
            entry_buffer = new StringBuilder();
            boolean first_loop = true;

            for(int i = 0; i < WRAPPERS.length; ++i) {
                Generator.WrapperInfo info = WRAPPERS[i];
                String k = info.primitive;
                String KT = info.class_name;
                String K = abbreviate(KT);
                String KC = K.toUpperCase();
                String KMAX = info.max_value;
                String KMIN = info.min_value;

                for(int j = 0; j < WRAPPERS.length; ++j) {
                    Generator.WrapperInfo jinfo = WRAPPERS[j];
                    String v = jinfo.primitive;
                    String VT = jinfo.class_name;
                    String V = abbreviate(VT);
                    String VC = V.toUpperCase();
                    String VMAX = jinfo.max_value;
                    String VMIN = jinfo.min_value;
                    String before_e;
                    String out = before_e = (String)entry.getValue();
                    out = Pattern.compile("#e#").matcher(out).replaceAll(k);
                    out = Pattern.compile("#E#").matcher(out).replaceAll(K);
                    out = Pattern.compile("#ET#").matcher(out).replaceAll(KT);
                    out = Pattern.compile("#EC#").matcher(out).replaceAll(KC);
                    out = Pattern.compile("#EMAX#").matcher(out).replaceAll(KMAX);
                    out = Pattern.compile("#EMIN#").matcher(out).replaceAll(KMIN);
                    boolean uses_e = !out.equals(before_e);
                    if (uses_e && j != 0) {
                        break;
                    }

                    out = Pattern.compile("#v#").matcher(out).replaceAll(v);
                    out = Pattern.compile("#V#").matcher(out).replaceAll(V);
                    out = Pattern.compile("#VT#").matcher(out).replaceAll(VT);
                    out = Pattern.compile("#VC#").matcher(out).replaceAll(VC);
                    out = Pattern.compile("#VMAX#").matcher(out).replaceAll(VMAX);
                    out = Pattern.compile("#VMIN#").matcher(out).replaceAll(VMIN);
                    out = Pattern.compile("#k#").matcher(out).replaceAll(k);
                    out = Pattern.compile("#K#").matcher(out).replaceAll(K);
                    out = Pattern.compile("#KT#").matcher(out).replaceAll(KT);
                    out = Pattern.compile("#KC#").matcher(out).replaceAll(KC);
                    out = Pattern.compile("#KMAX#").matcher(out).replaceAll(KMAX);
                    out = Pattern.compile("#KMIN#").matcher(out).replaceAll(KMIN);
                    if (first_loop) {
                        first_loop = false;
                    } else {
                        entry_buffer.append("\n\n");
                    }

                    entry_buffer.append(out);
                }
            }
        }

        return content;
    }

    private static void writeFile(String content, File output_file) throws IOException {
        File parent = output_file.getParentFile();
        makeDirs(parent);
        File temp = File.createTempFile("trove", "gentemp", new File(System.getProperty("java.io.tmpdir")));
        Writer writer = new BufferedWriter(new FileWriter(temp));
        writer.write(content);
        writer.close();
        boolean need_to_move;
        if (output_file.exists()) {
            boolean matches;
            try {
                MessageDigest digest = MessageDigest.getInstance("MD5");
                byte[] current_file = digest(output_file, digest);
                byte[] new_file = digest(temp, digest);
                matches = Arrays.equals(current_file, new_file);
            } catch (NoSuchAlgorithmException var10) {
                System.err.println("WARNING: Couldn't load digest algorithm to compare new and old template. Generation will be forced.");
                matches = false;
            }

            need_to_move = !matches;
        } else {
            need_to_move = true;
        }

        if (need_to_move) {
            delete(output_file);
            copyFile(temp, output_file);
            System.out.println("  Wrote: " + simplifyPath(output_file));
        } else {
            System.out.println("  Skipped: " + simplifyPath(output_file));
            delete(temp);
        }

    }

    private static void delete(File output_file) {
        if (output_file.exists()) {
            if (!output_file.delete()) {
                throw new IllegalStateException("Could not delete " + output_file);
            }
        }
    }

    private static byte[] digest(File file, MessageDigest digest) throws IOException {
        digest.reset();
        byte[] buffer = new byte[1024];
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));

        try {
            for(int read = in.read(buffer); read >= 0; read = in.read(buffer)) {
                digest.update(buffer, 0, read);
            }

            byte[] var13 = digest.digest();
            return var13;
        } finally {
            try {
                in.close();
            } catch (IOException var11) {
            }

        }
    }

    private static String abbreviate(String type) {
        if (type.equals("Integer")) {
            return "Int";
        } else {
            return type.equals("Character") ? "Char" : type;
        }
    }

    private static String readFile(File input_file) throws IOException {
        if (!input_file.exists()) {
            throw new NullPointerException("Couldn't find: " + input_file);
        } else {
            BufferedReader reader = null;

            try {
                reader = new BufferedReader(new FileReader(input_file));
                StringBuilder out = new StringBuilder();

                while(true) {
                    String line = reader.readLine();
                    if (line == null) {
                        line = out.toString();
                        return line;
                    }

                    out.append(line);
                    out.append("\n");
                }
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException var10) {
                    }
                }

            }
        }
    }

    static Map<Integer, String> findReplicatedBlocks(String content_in, StringBuilder content_out) throws IOException {
        Map<Integer, String> to_return = null;
        BufferedReader reader = new BufferedReader(new StringReader(content_in));
        StringBuilder buffer = new StringBuilder();
        boolean in_replicated_block = false;
        boolean need_newline = false;

        while(true) {
            String line;
            while((line = reader.readLine()) != null) {
                if (!in_replicated_block && line.startsWith("====START_REPLICATED_CONTENT #")) {
                    in_replicated_block = true;
                    need_newline = false;
                    if (content_out.length() == 0) {
                        content_out.append(buffer.toString());
                    }

                    buffer = new StringBuilder();
                } else if (in_replicated_block && line.startsWith("=====END_REPLICATED_CONTENT #")) {
                    int number_start_index = "=====END_REPLICATED_CONTENT #".length();
                    int number_end_index = line.indexOf("=", number_start_index);
                    String number = line.substring(number_start_index, number_end_index);
                    Integer number_obj = Integer.valueOf(number);
                    if (to_return == null) {
                        to_return = new HashMap();
                    }

                    to_return.put(number_obj, buffer.toString());
                    in_replicated_block = false;
                    need_newline = false;
                } else {
                    if (need_newline) {
                        buffer.append("\n");
                    } else {
                        need_newline = true;
                    }

                    buffer.append(line);
                }
            }

            return to_return;
        }
    }

    private static String simplifyPath(File file) {
        String output_string = root_output_dir.toString();
        String file_string = file.toString();
        return file_string.substring(output_string.length() + 1);
    }

    private static void cleanDir(File directory) {
        File[] listFiles;
        int length = (listFiles = directory.listFiles()).length;

        for(int i = 0; i < length; ++i) {
            File file = listFiles[i];
            if (file.isDirectory()) {
                cleanDir(file);
                delete(file);
            }

            delete(file);
        }

    }

    private static void copyFile(File source, File dest) throws IOException {
        FileChannel srcChannel = (new FileInputStream(source)).getChannel();
        FileChannel dstChannel = (new FileOutputStream(dest)).getChannel();
        dstChannel.transferFrom(srcChannel, 0L, srcChannel.size());
        srcChannel.close();
        dstChannel.close();
    }

    private static class WrapperInfo {
        final String primitive;
        final String class_name;
        final String max_value;
        final String min_value;

        WrapperInfo(String primitive, String class_name, String max_value, String min_value) {
            this.primitive = primitive;
            this.class_name = class_name;
            this.max_value = class_name + "." + max_value;
            this.min_value = class_name + "." + min_value;
        }
    }
}
