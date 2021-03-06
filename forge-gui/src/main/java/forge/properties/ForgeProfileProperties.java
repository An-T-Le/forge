/*
 * Forge: Play Magic: the Gathering.
 * Copyright (C) 2011  Forge Team
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package forge.properties;

import forge.GuiBase;
import forge.util.FileSection;
import forge.util.FileUtil;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * Determines the user data and cache dirs, first looking at the specified file for overrides
 * then falling back to platform-specific defaults.  Resulting dir strings are guaranteed to end in a slash
 * so they can be easily appended with further path elements.
 */
public class ForgeProfileProperties {
    private static String userDir;
    private static String cacheDir;
    private static String cardPicsDir;
    private static Map<String, String> cardPicsSubDirs;
    private static int serverPort; 

    private static final String USER_DIR_KEY      = "userDir";
    private static final String CACHE_DIR_KEY     = "cacheDir";
    private static final String CARD_PICS_DIR_KEY = "cardPicsDir";
    private static final String CARD_PICS_SUB_DIRS_KEY = "cardPicsSubDirs";
    private static final String SERVER_PORT_KEY = "serverPort";

    private ForgeProfileProperties() {
        //prevent initializing static class
    }

    public static void load() {
        Properties props = new Properties();
        File propFile = new File(ForgeConstants.PROFILE_FILE);
        try {
            if (propFile.canRead()) {
                props.load(new FileInputStream(propFile));
            }
        }
        catch (IOException e) {
            System.err.println("error while reading from profile properties file");
        }

        Pair<String, String> defaults = getDefaultDirs();
        userDir     = getDir(props, USER_DIR_KEY,      defaults.getLeft());
        cacheDir    = getDir(props, CACHE_DIR_KEY,     defaults.getRight());
        cardPicsDir = getDir(props, CARD_PICS_DIR_KEY, cacheDir + "pics" + File.separator + "cards" + File.separator);
        cardPicsSubDirs = getMap(props, CARD_PICS_SUB_DIRS_KEY);
        serverPort = getInt(props, SERVER_PORT_KEY, 0);

        //ensure directories exist
        FileUtil.ensureDirectoryExists(userDir);
        FileUtil.ensureDirectoryExists(cacheDir);
        FileUtil.ensureDirectoryExists(cardPicsDir);
    }

    public static String getUserDir() {
        return userDir;
    }
    public static void setUserDir(String userDir0) {
        userDir = userDir0;
        save();
    }

    public static String getCacheDir() {
        return cacheDir;
    }
    public static void setCacheDir(String cacheDir0) {
        int idx = cardPicsDir.indexOf(cacheDir); //ensure card pics directory is updated too if within cache directory
        if (idx != -1) {
            cardPicsDir = cacheDir0 + cardPicsDir.substring(idx + cacheDir.length());
        }
        cacheDir = cacheDir0;
        save();
    }

    public static String getCardPicsDir() {
        return cardPicsDir;
    }
    public static void setCardPicsDir(String cardPicsDir0) {
        cardPicsDir = cardPicsDir0; 
        save();
    }

    public static Map<String, String> getCardPicsSubDirs() {
        return cardPicsSubDirs;
    }

    public static int getServerPort() {
        return serverPort;
    }

    private static Map<String, String> getMap(Properties props, String propertyKey) {
        String strMap = props.getProperty(propertyKey, "").trim();
        return FileSection.parseToMap(strMap, "->", "|");
    }

    private static int getInt(Properties props, String propertyKey, int defaultValue) {
        String strValue = props.getProperty(propertyKey, "").trim();
        if ( StringUtils.isNotBlank(strValue) && StringUtils.isNumeric(strValue) ) 
            return Integer.parseInt(strValue);
        return defaultValue;
    }    

    private static String getDir(Properties props, String propertyKey, String defaultVal) {
        String retDir = props.getProperty(propertyKey, defaultVal).trim();
        if (retDir.isEmpty()) {
            // use default if dir is "defined" as an empty string in the properties file
            retDir = defaultVal;
        }
        
        // canonicalize
        retDir = new File(retDir).getAbsolutePath();
        
        // ensure path ends in a slash
        if (File.separatorChar == retDir.charAt(retDir.length() - 1)) {
            return retDir;
        }
        return retDir + File.separatorChar;
    }

    // returns a pair <userDir, cacheDir>
    private static Pair<String, String> getDefaultDirs() {
        if (!GuiBase.getInterface().isRunningOnDesktop()) { //special case for mobile devices
            String assetsDir = ForgeConstants.ASSETS_DIR;
            return Pair.of(assetsDir + "data" + File.separator, assetsDir + "cache" + File.separator);
        }

        String osName = System.getProperty("os.name");
        String homeDir = System.getProperty("user.home");

        if (StringUtils.isEmpty(osName) || StringUtils.isEmpty(homeDir)) {
            throw new RuntimeException("cannot determine OS and user home directory");
        }
        
        String fallbackDataDir = String.format("%s/.forge", homeDir);

        if (StringUtils.containsIgnoreCase(osName, "windows")) {
            // the split between appdata and localappdata on windows is relatively recent.  If
            // localappdata is not defined, use appdata for both.  and if appdata is not defined,
            // fall back to a linux-style dot dir in the home directory
            String appRoot = System.getenv().get("APPDATA");
            if (StringUtils.isEmpty(appRoot)) {
                appRoot = fallbackDataDir;
            }
            String cacheRoot = System.getenv().get("LOCALAPPDATA");
            if (StringUtils.isEmpty(cacheRoot)) {
                cacheRoot = appRoot;
            }
            // the cache dir is Forge/Cache instead of just Forge since appRoot and cacheRoot might be the
            // same directory on windows and we need to distinguish them.
            return Pair.of(appRoot + File.separator + "Forge", cacheRoot + File.separator + "Forge" + File.separator + "Cache");
        }
        else if (StringUtils.containsIgnoreCase(osName, "mac os x")) {
            return Pair.of(String.format("%s/Library/Application Support/Forge", homeDir),
                           String.format("%s/Library/Caches/Forge", homeDir));
        }

        // Linux and everything else
        return Pair.of(fallbackDataDir, String.format("%s/.cache/forge", homeDir));
    }

    private static void save() {
        Pair<String, String> defaults = getDefaultDirs();
        String defaultUserDir = defaults.getLeft() + File.separator;
        String defaultCacheDir = defaults.getRight() + File.separator;
        String defaultCardPicsDir = defaultCacheDir + "pics" + File.separator + "cards" + File.separator;

        //only append values that aren't equal to defaults
        StringBuilder sb = new StringBuilder();
        if (!userDir.equals(defaultUserDir)) { //ensure backslashes are escaped
            sb.append(USER_DIR_KEY + "=" + userDir.replace("\\", "\\\\") + "\n");
        }
        if (!cacheDir.equals(defaultCacheDir)) {
            sb.append(CACHE_DIR_KEY + "=" + cacheDir.replace("\\", "\\\\") + "\n");
        }
        if (!cardPicsDir.equals(defaultCardPicsDir)) {
            sb.append(CARD_PICS_DIR_KEY + "=" + cardPicsDir.replace("\\", "\\\\") + "\n");
        }
        if (cardPicsSubDirs.size() > 0) {
            sb.append(CARD_PICS_SUB_DIRS_KEY + "=");
            boolean needDelim = false;
            for (Map.Entry<String, String> entry : cardPicsSubDirs.entrySet()) {
                if (needDelim) {
                    sb.append("|");
                }
                sb.append(entry.getKey() + "->" + entry.getValue());
            }
            sb.append("\n");
        }
        if (serverPort != 0) {
            sb.append(SERVER_PORT_KEY + "=" + serverPort);
        }
        if (sb.length() > 0) {
            FileUtil.writeFile(ForgeConstants.PROFILE_FILE, sb.toString());
        }
        else { //delete file if empty
            try {
                File file = new File(ForgeConstants.PROFILE_FILE);
                if (file.exists()) {
                    file.delete();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
