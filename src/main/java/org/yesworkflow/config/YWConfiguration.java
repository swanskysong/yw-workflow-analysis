package org.yesworkflow.config;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.yesworkflow.exceptions.YWToolUsageException;

@SuppressWarnings("serial")
public class YWConfiguration extends HashMap<String,Object> {

    public YWConfiguration() throws Exception {        
    }

    public YWConfiguration(String... paths) throws Exception {
        for (String path : paths) {
            File configFile = new File(path);
            if(configFile.exists()) {
                applyConfigProperties(new FileReader(configFile));
                break;
            }
        }
    }

    public void applyConfigProperties(Reader reader) throws Exception {
        Properties properties = new Properties();
        properties.load(reader);
        for (Map.Entry<Object, Object> entry: properties.entrySet()) {
            applyConfigOption((String) entry.getKey(), (String) entry.getValue());
        }
    }
    
    public void applyConfigOptions(List<?> options) throws YWToolUsageException {        
        for (Object option : options) {
            applyConfigOption((String) option);
        }
    }

    public void applyConfigOption(String name, String value) {
        System.out.println(name + " = " + value);
        ConfigAddress address = configurationAddress(name, true);
        address.table.put(address.key, value);
    }
    
    public void applyConfigOption(String option) throws YWToolUsageException {
        String[] optionParts = option.split("=");
        if (optionParts.length != 2) {
            throw new YWToolUsageException(
                "Configuration options should be key-value pairs separated by equal signs.");
        }
        String name = optionParts[0];
        String value = optionParts[1];
        ConfigAddress address = configurationAddress(name, true);
        address.table.put(address.key, value);
    }

    public String getConfigOptionValue(String optionName) {
        ConfigAddress address = configurationAddress(optionName, false);
        return address == null ? null : (String)address.table.get(address.key);
    }
    
    private class ConfigAddress {
        String key;
        Map<String,Object> table;
    }
    
    @SuppressWarnings("unchecked")
    private ConfigAddress configurationAddress(String configName, boolean createMissingTables) {
        String[] configNameParts = configName.split("\\.");
        ConfigAddress address = new ConfigAddress();
        address.key = configNameParts[configNameParts.length - 1];
        address.table = this;
        if (configNameParts.length > 0) {
            for (int i = 0; i < configNameParts.length - 1; ++i) {
                String partName = configNameParts[i];
                Object tableObject =  address.table.get(partName);
                if (tableObject == null) {
                    if (!createMissingTables) return null;
                    tableObject = new HashMap<String,Object>();
                    address.table.put(partName, tableObject);
                }
                address.table = (Map<String, Object>)tableObject;
            }
        }
        return address;
    }
    
    @SuppressWarnings("unchecked")
    public Map<String,Object> getSection(String key) {
        return (Map<String,Object>) get(key);
    }
}
