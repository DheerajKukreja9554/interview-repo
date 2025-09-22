package com.codesbydk.interviews;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Interview {

    public static void main(String[] args) {
        Map<Integer, String> map = new HashMap<>();
        map.put(1, "a");
        map.put(2, "b");
        map.put(3, "c");
        map.put(4, "d");
        map.put(5, "e");
        map.put(6, "f");
        map.put(7, "g");

        System.out.println(
                map.entrySet()
                        .stream()
                        .filter(entry -> !entry.getValue().equals("c"))
                        .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue())));

        List<Integer> keys = map.keySet().stream().toList();

        for (int i = 0; i < keys.size(); i++) {
            if (map.get(keys.get(i)).equals("c")) {
                map.remove(keys.get(i));
            }
        }
        System.out.println("map" + map);
        String s = "my name is dheeraj";

        String[] splitted = s.split(" ");
        String reversed = "";

        for (int i = splitted.length - 1; i >= 0; i--) {
            reversed = reversed + splitted[i] + " ";
        }

        // jareehd si eman ym

        String r = "";

        for (int i = s.length() - 1; i >= 0; i--) {
            r += s.charAt(i);
        }

        System.out.println(reversed);
        System.out.println(r);

    }

}
