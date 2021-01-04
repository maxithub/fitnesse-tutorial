package max.lab.fitnesse.s02;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wnameless.json.flattener.JsonFlattener;
import com.jayway.jsonpath.JsonPath;
import lombok.SneakyThrows;

import java.util.*;

import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;

public class VerifyPayload {
    private final String payload;
    private final String jsonPath;
    private final boolean flatten;

    public VerifyPayload(String payload) {
        this(payload, "$", true);
    }

    public VerifyPayload(String payload, String jsonPath) {
        this(payload, jsonPath, true);
    }

    public VerifyPayload(String payload, String jsonPath, boolean flatten) {
        this.payload = payload;
        this.jsonPath = jsonPath;
        this.flatten = flatten;
    }

    @SneakyThrows
    public List<List<List<String>>> query() {
        var objectMapper = new ObjectMapper();
        var jsonNode = readJsonNode(objectMapper);
        var list = new ArrayList<List<List<String>>>();
        if (jsonNode.isArray()) {
            for (var subNode : jsonNode) {
                toList(list, subNode);
            }
        } else if (jsonNode.isObject()) {
            toList(list, jsonNode);
        }
        ensureSameColumnsForAllRows(list);
        return list;
    }

    private void ensureSameColumnsForAllRows(ArrayList<List<List<String>>> list) {
        var colNameForRows = new ArrayList<Set<String>>();
        var allColNames = new HashSet<String>();
        for (var row : list) {
            var colNames = new HashSet<String>();
            for (var cell : row) {
                var name = cell.get(0);
                colNames.add(name);
                allColNames.add(name);
            }
            colNameForRows.add(colNames);
        }

        for (int i = 0; i < list.size(); i ++) {
            var aRow = list.get(i);
            var colNames = colNameForRows.get(i);
            allColNames.stream().filter(s -> !colNames.contains(s))
                    .forEach(s -> aRow.add(asList(s, "")));
        }
    }

    private JsonNode readJsonNode(ObjectMapper objectMapper) throws JsonProcessingException {
        JsonNode jsonNode;
        if ("$".equals(jsonPath)) {
            jsonNode = objectMapper.readTree(payload);
        } else {
            Object object = JsonPath.read(payload, jsonPath);
            jsonNode = objectMapper.readTree(objectMapper.writeValueAsString(object));
        }
        return jsonNode;
    }

    private void toList(List<List<List<String>>> list, JsonNode node) {
        var itemList = new ArrayList<List<String>>();
        if (flatten) {
            var flattenJson = JsonFlattener.flattenAsMap(node.toString());
            flattenJson.entrySet().stream()
                    .forEach(entry -> itemList.add(asList(
                            entry.getKey(),
                            ofNullable(entry.getValue()).map(Object::toString).orElse(null)
                            )));
        } else {
            for (var iterator = node.fieldNames(); iterator.hasNext(); ) {
                var name = iterator.next();
                var subNode = node.get(name);
                var text = subNode.isObject() || subNode.isArray() ? subNode.toPrettyString() : subNode.asText();
                itemList.add(asList(name, text));
            }
        }
        list.add(itemList);
    }

    public static String stripHtml(String text) {
        return text.replace("<pre>", "")
                .replace("</pre>", "");
    }
}
