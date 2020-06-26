package com.ashwashing.pro.api.bean;

import java.util.ArrayList;
import java.util.List;

public class SprayInfo {
    public String ConsumeID;
    private List<List<Integer>> command;

    public List<byte[]> getCommand() {
        List<byte[]> bytesList = new ArrayList<>();
        for (List<Integer> list : command) {
            byte[] bytes = new byte[list.size()];
            for (int i = 0; i < list.size(); i++) {
                int integer = list.get(i);
                bytes[i] = (byte) integer;
            }
            bytesList.add(bytes);
        }
        return bytesList;
    }
}
