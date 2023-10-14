package com.gae4coon.cloudmaestro.domain.rehost.service;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class RehostService {

    public void parsing(String jsonFile) throws ParseException {

        ArrayList<String> banNode = new ArrayList<>(Arrays.asList("IPS", "FW"));
        HashMap<String, ArrayList<String>> nodeMap = new HashMap<>();

        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(jsonFile);

        JSONArray nodeDataArray = (JSONArray) object.get("nodeDataArray");
        JSONArray linkDataArray = (JSONArray) object.get("linkDataArray");

        int nodeLen = nodeDataArray.size();

        for (Object node : nodeDataArray) {
            JSONObject currentNode = (JSONObject) node;
            String nodeAtr= (String)currentNode.get("text");

            for (String ban: banNode) {
                if (ban.equals(nodeAtr)) {

                    System.out.println(nodeAtr);
                    System.out.println(node);

                }
            }

        }

//        System.out.println(nodeDataArray);
//        System.out.println(linkDataArray);
    }
}
