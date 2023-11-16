package com.patres.homeoffice.registry;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

import java.util.Arrays;

public class RegistryManager {

    private static final String LAST_USED_TIME_STOP = "LastUsedTimeStop";
    private static final Long LAST_USED_TIME_STOP_VALUE = 0L;

    private RegistryManager() {
    }

    public static boolean isDeviceWorking(final RegistryType registryType) {
        return isDeviceWorking(registryType.getPackagePath()) || isDeviceWorking(registryType.getNonPackagePath());
    }

    public static boolean isDeviceWorking(final String keyPath) {
        final String[] folders = Advapi32Util.registryGetKeys(WinReg.HKEY_CURRENT_USER, keyPath);
        return Arrays.stream(folders)
                .map(folder -> keyPath + "\\" + folder)
                .filter(register -> Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, register, LAST_USED_TIME_STOP))
                .map(register -> Advapi32Util.registryGetLongValue(WinReg.HKEY_CURRENT_USER, register, LAST_USED_TIME_STOP))
                .anyMatch(lastUsedTimeStop -> LAST_USED_TIME_STOP_VALUE.compareTo(lastUsedTimeStop) >= 0);
    }

}