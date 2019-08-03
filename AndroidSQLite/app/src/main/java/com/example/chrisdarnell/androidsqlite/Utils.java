package com.example.chrisdarnell.androidsqlite;

/**
 * Created by chrisdarnell on 7/29/17.
 */

import java.util.ArrayList;
import java.util.List;

class Utils {

    private static final String TAG = MySQLiteHelper.class.getSimpleName();

    public static List<String> splitSqlScript(String script, char delim) {
        List<String> statements = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        boolean inLiteral = false;
        char[] content = script.toCharArray();
        for (int i = 0; i < script.length(); i++) {
            if (content[i] == '"') {
                inLiteral = !inLiteral;
            }
            if (content[i] == delim && !inLiteral) {
                if (sb.length() > 0) {
                    statements.add(sb.toString().trim());
                    sb = new StringBuilder();
                }
            } else {
                sb.append(content[i]);
            }
        }

        if (sb.length() > 0) {
            statements.add(sb.toString().trim());
        }
        return statements;
    }
    }