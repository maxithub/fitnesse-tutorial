package max.lab.fitnesse.s02;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.math.BigDecimal;
import java.util.*;

public class BuildJson {
    @SneakyThrows
    public List doTable(List<List<String>> table) {
        var map = new HashMap<>();
        var cols = table.get(0);
        var vals = table.get(1);

        var colsEx = new ArrayList<>();
        var valsEx = new ArrayList<>();
        for (int i = 0; i < cols.size(); i ++) {
            var col = cols.get(i);
            var pos = col.indexOf(':');
            var val = vals.get(i);
            if (pos >= 0) {
                var name = col.substring(0, pos);
                var type = col.substring(pos + 1);
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
        var json = (new ObjectMapper()).writeValueAsString(map);
        valsEx.add("pass:"+json);
        return Arrays.asList(colsEx, valsEx);
    }
}
