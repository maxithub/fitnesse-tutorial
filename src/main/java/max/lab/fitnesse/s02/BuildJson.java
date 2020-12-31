package max.lab.fitnesse.s02;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.math.BigDecimal;
import java.util.*;

public class BuildJson {
    @SneakyThrows
    public List doTable(List<List<String>> table) {
        Map<String, Object> map = new HashMap<>();
        List<String> cols = table.get(0);
        List<String> vals = table.get(1);

        List<String> colsEx = new ArrayList<>();
        List<String> valsEx = new ArrayList<>();
        for (int i = 0; i < cols.size(); i ++) {
            String col = cols.get(i);
            int pos = col.indexOf(':');
            String val = vals.get(i);
            if (pos >= 0) {
                String name = col.substring(0, pos);
                String type = col.substring(pos + 1);
                if (type.equals("number")) {
                    map.put(name, new BigDecimal(val));
                } else {
                    map.put(name, val);
                }
            } else {
                map.put(col, val);
            }
            colsEx.add("report:" + col);
            valsEx.add("report:" + val);
        }
        String json = (new ObjectMapper()).writeValueAsString(map);
        valsEx.add("pass:"+json);
        return Arrays.asList(colsEx, valsEx);
    }
}
