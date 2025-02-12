package bbcdabao.pingmonitor.common.zkdataobj;

import java.util.ArrayList;
import java.util.Collection;

import lombok.Data;

@Data
public class Template {
    private Collection<TemplateField> fields = new ArrayList<>();
}
